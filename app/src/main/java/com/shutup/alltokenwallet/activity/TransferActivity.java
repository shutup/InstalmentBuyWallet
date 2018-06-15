package com.shutup.alltokenwallet.activity;

import android.Manifest;
import android.content.Intent;
import android.os.AsyncTask;
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
import com.shutup.alltokenwallet.contract_wrapper.RXToken;
import com.shutup.alltokenwallet.model.AccountInfo;
import com.shutup.alltokenwallet.utils.Web3Manager;
import com.shutup.alltokenwallet.utils.XPermissionUtils;

import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
        new GetBalanceTask().execute(mAccountInfo);
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
        String toAddr = mReceiveWalletAddrText.getText().toString().trim();
        Float sendValueFloat = Float.valueOf(String.valueOf(mTransferAmountEditText.getText())) * 10000;
        BigInteger sendValue = BigInteger.valueOf(sendValueFloat.longValue());
        new SendTokenTask().execute(mAccountInfo, toAddr, sendValue);
    }

    class GetBalanceTask extends AsyncTask<Object, Void, String> {

        @Override
        protected String doInBackground(Object... params) {
            AccountInfo accountInfo = (AccountInfo) params[0];
            Credentials credentials = null;
            try {
                credentials = WalletUtils.loadCredentials(accountInfo.getPassword(), accountInfo.getPath());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (CipherException e) {
                e.printStackTrace();
            }
            RXToken rxToken = RXToken.load(Token_Address, Web3Manager.getInstance(), credentials, ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
            BigInteger balance = null;
            try {
                balance = rxToken.balanceOf(accountInfo.getAddress()).send();
            } catch (Exception e) {
                e.printStackTrace();
            }
            BigDecimal bigDecimal = BigDecimal.valueOf(balance.floatValue() / 10000);
            return bigDecimal.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (BuildConfig.DEBUG) Log.d("GetBalanceTask", s);
            mAvailableCount.setText(s);
        }
    }

    class SendTokenTask extends AsyncTask<Object, Void, String> {

        @Override
        protected String doInBackground(Object... params) {
            AccountInfo accountInfo = (AccountInfo) params[0];
            String toAddr = (String) params[1];
            BigInteger value = (BigInteger) params[2];
            Credentials credentials = null;
            try {
                credentials = WalletUtils.loadCredentials(accountInfo.getPassword(), accountInfo.getPath());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (CipherException e) {
                e.printStackTrace();
            }
            BigInteger gasLimit = BigInteger.valueOf(51675);
            BigInteger gasPrice = Convert.toWei(BigDecimal.valueOf(2), Convert.Unit.GWEI).toBigInteger();

            RXToken rxToken = RXToken.load(Token_Address, Web3Manager.getInstance(), credentials, gasPrice, gasLimit);
            TransactionReceipt transactionReceipt = null;
            try {
                transactionReceipt = rxToken.transfer(toAddr, value).send();
                return transactionReceipt.getTransactionHash();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "some thing wrong";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (BuildConfig.DEBUG) Log.d("GetBalanceTask", s);
            mAvailableCount.setText(s);
        }
    }
}
