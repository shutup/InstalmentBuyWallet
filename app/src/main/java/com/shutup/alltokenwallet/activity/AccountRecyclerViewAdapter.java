package com.shutup.alltokenwallet.activity;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.shutup.alltokenwallet.BuildConfig;
import com.shutup.alltokenwallet.R;
import com.shutup.alltokenwallet.contract_wrapper.RXToken;
import com.shutup.alltokenwallet.model.AccountInfo;
import com.shutup.alltokenwallet.utils.Constants;
import com.shutup.alltokenwallet.utils.Web3Manager;

import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AccountRecyclerViewAdapter extends RecyclerView.Adapter<AccountRecyclerViewAdapter.ViewHolder> implements Constants {

    Context mContext;
    List<AccountInfo> mAccountInfos;
    LayoutInflater mLayoutInflater;

    public AccountRecyclerViewAdapter(Context context, List<AccountInfo> accountInfos) {
        mContext = context;
        mAccountInfos = accountInfos;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.account_item_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final AccountInfo accountInfo = mAccountInfos.get(position);
        holder.mWalletAddrText.setText(accountInfo.getAddress());
        holder.mQrCodeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(view.getContext());
                dialog.setContentView(R.layout.qr_view);
                ImageView qr_big = (ImageView) dialog.findViewById(R.id.qr_big);
                qr_big.setImageBitmap(QRGen(accountInfo.getAddress(), 600, 600));
                dialog.show();
            }
        });
        new GetBalanceTask().execute(accountInfo, holder);
    }

    @Override
    public int getItemCount() {
        return mAccountInfos.size();
    }

    public Bitmap QRGen(String Value, int Width, int Heigth) {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        Bitmap bitmap = null;
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(Value, BarcodeFormat.DATA_MATRIX.QR_CODE, Width, Heigth);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            bitmap = barcodeEncoder.createBitmap(bitMatrix);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.balanceText)
        TextView mBalanceText;
        @BindView(R.id.qrCodeImage)
        ImageView mQrCodeImage;
        @BindView(R.id.walletAddrText)
        TextView mWalletAddrText;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    class GetBalanceTask extends AsyncTask<Object, Void, String> {

        ViewHolder mViewHolder;

        @Override
        protected String doInBackground(Object... params) {
            mViewHolder = (ViewHolder) params[1];
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
            mViewHolder.mBalanceText.setText(s);
        }
    }

}
