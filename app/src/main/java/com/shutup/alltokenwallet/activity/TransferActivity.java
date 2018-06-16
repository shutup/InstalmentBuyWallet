package com.shutup.alltokenwallet.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.shutup.alltokenwallet.BuildConfig;
import com.shutup.alltokenwallet.R;
import com.shutup.alltokenwallet.base.BaseActivity;
import com.shutup.alltokenwallet.model.AccountInfo;
import com.shutup.alltokenwallet.model.RPCRequestModel;
import com.shutup.alltokenwallet.model.RPCResponseModel;
import com.shutup.alltokenwallet.network.WalletAPI;
import com.shutup.alltokenwallet.utils.XPermissionUtils;

import org.spongycastle.util.encoders.Hex;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.crypto.WalletUtils;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransferActivity extends BaseActivity {

    @BindView(R.id.left_icon)
    ImageView mLeftIcon;
    @BindView(R.id.title_text)
    TextView mTitleText;
    @BindView(R.id.availableCount)
    TextView mAvailableCount;
    @BindView(R.id.currentWalletName)
    TextView mCurrentWalletName;
    @BindView(R.id.selectWallet)
    ImageView mSelectWallet;
    @BindView(R.id.transferWalletAddrText)
    TextView mTransferWalletAddrText;
    @BindView(R.id.showHideAddrImage)
    ImageView mShowHideAddrImage;
    @BindView(R.id.receiveWalletAddrText)
    EditText mReceiveWalletAddrText;
    @BindView(R.id.scanWalletAddrImage)
    ImageView mScanWalletAddrImage;
    @BindView(R.id.transferAmountEditText)
    EditText mTransferAmountEditText;
    @BindView(R.id.transferBtn)
    Button mTransferBtn;

    AccountInfo mAccountInfo;
    boolean isShowAddr = false;

    IntentIntegrator qrScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);
        ButterKnife.bind(this);
        mAccountInfo = (AccountInfo) getIntent().getSerializableExtra(ACCOUNT_INFO_KEY);
        initToolBar();
        initViews();
    }

    private void initViews() {
        qrScan = new IntentIntegrator(this);
        mShowHideAddrImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_content_hide));
        mTransferWalletAddrText.setText("**** **** **** " + mAccountInfo.getAddress().substring(mAccountInfo.getAddress().length() - 4));
        mScanWalletAddrImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_scan));
    }

    private void initToolBar() {
        Call<RPCResponseModel> call = WalletAPI.getInstance().getBalance(new RPCRequestModel(RPC_METHOD_GET_BALANCE, mAccountInfo.getAddress(), "56"));
        call.enqueue(new Callback<RPCResponseModel>() {
            @Override
            public void onResponse(Call<RPCResponseModel> call, Response<RPCResponseModel> response) {
                mAvailableCount.setText(response.body().getResult());
            }

            @Override
            public void onFailure(Call<RPCResponseModel> call, Throwable t) {
                Toast.makeText(TransferActivity.this, "网络请求失败", Toast.LENGTH_SHORT).show();
            }
        });
        mLeftIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_left_back));
        mTitleText.setText(mAccountInfo.getAccountName());
    }

    private void updateAddrtextStatus() {
        if (isShowAddr) {
            mShowHideAddrImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_content_show));
            mTransferWalletAddrText.setText(mAccountInfo.getAddress());
        } else {
            mShowHideAddrImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_content_hide));
            mTransferWalletAddrText.setText("**** **** **** " + mAccountInfo.getAddress().substring(mAccountInfo.getAddress().length() - 4));
        }
        isShowAddr = !isShowAddr;
    }

    @OnClick({R.id.left_icon, R.id.selectWallet, R.id.showHideAddrImage, R.id.scanWalletAddrImage, R.id.transferBtn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.left_icon:
                finish();
                break;
            case R.id.selectWallet:
                break;
            case R.id.showHideAddrImage:
                updateAddrtextStatus();
                break;
            case R.id.scanWalletAddrImage:
                processScan();

                break;
            case R.id.transferBtn:
                //
                processTransfer();
                break;
        }
    }

    private void processScan() {
        XPermissionUtils.requestPermissions(this, REQUEST_CODE_USE_CAMERA, new String[]{Manifest.permission.CAMERA}, new XPermissionUtils.OnPermissionListener() {
            @Override
            public void onPermissionGranted() {
                qrScan.setOrientationLocked(false);
                qrScan.setBarcodeImageEnabled(true);
                qrScan.initiateScan();
            }

            @Override
            public void onPermissionDenied() {
                Toast.makeText(TransferActivity.this, "请授予使用摄像头的权限", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /**
         * 处理二维码扫描结果
         */
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void processTransfer() {
        final String toAddr = mReceiveWalletAddrText.getText().toString().trim();
        final BigInteger sendValue = Convert.toWei(mTransferAmountEditText.getText().toString(), Convert.Unit.ETHER).toBigInteger();

        Call<RPCResponseModel> getTransactionCountCall = WalletAPI.getInstance().getTransactionCount(new RPCRequestModel(RPC_METHOD_GET_TRANSACTION_COUNT, mAccountInfo.getAddress(), "pending"));
        getTransactionCountCall.enqueue(new Callback<RPCResponseModel>() {
            @Override
            public void onResponse(Call<RPCResponseModel> call, Response<RPCResponseModel> response) {
                RPCResponseModel rpcResponseModel = response.body();
                BigInteger nonce = BigInteger.valueOf(Long.valueOf(rpcResponseModel.getResult()));
                Credentials credentials = null;
                try {
                    credentials = WalletUtils.loadCredentials(mAccountInfo.getPassword(), mAccountInfo.getPath());
                } catch (IOException e) {
                    if (BuildConfig.DEBUG) Log.d("TransferActivity", "e:" + e);
                    e.printStackTrace();
                } catch (CipherException e) {
                    if (BuildConfig.DEBUG) Log.d("TransferActivity", "e:" + e);
                    e.printStackTrace();
                }

                final BigInteger gasLimit = BigInteger.valueOf(51675);
                final BigInteger gasPrice = Convert.toWei(BigDecimal.valueOf(2), Convert.Unit.GWEI).toBigInteger();
                RawTransaction rawTransaction = RawTransaction.createEtherTransaction(nonce, gasPrice, gasLimit, toAddr, sendValue);
                byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
                String hexValue = "0x" + Hex.toHexString(signedMessage);
                if (BuildConfig.DEBUG) Log.d("TransferActivity", hexValue);
                Call<RPCResponseModel> sendRawTransactionCall = WalletAPI.getInstance().sendRawTransaction(new RPCRequestModel(RPC_METHOD_SEND_RAW_TRANACTION, hexValue));
                sendRawTransactionCall.enqueue(new Callback<RPCResponseModel>() {
                    @Override
                    public void onResponse(Call<RPCResponseModel> call, Response<RPCResponseModel> response) {
                        finish();
                    }

                    @Override
                    public void onFailure(Call<RPCResponseModel> call, Throwable t) {
                        Toast.makeText(TransferActivity.this, "网络请求失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(Call<RPCResponseModel> call, Throwable t) {
                Toast.makeText(TransferActivity.this, "网络请求失败", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
