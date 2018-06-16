package com.shutup.alltokenwallet.activity;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.shutup.alltokenwallet.R;
import com.shutup.alltokenwallet.model.AccountInfo;
import com.shutup.alltokenwallet.model.RPCRequestModel;
import com.shutup.alltokenwallet.model.RPCResponseModel;
import com.shutup.alltokenwallet.network.WalletAPI;
import com.shutup.alltokenwallet.utils.Constants;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        Call<RPCResponseModel> call = WalletAPI.getInstance().getBalance(new RPCRequestModel(RPC_METHOD_GET_BALANCE, accountInfo.getAddress(), "56"));
        call.enqueue(new Callback<RPCResponseModel>() {
            @Override
            public void onResponse(Call<RPCResponseModel> call, Response<RPCResponseModel> response) {
                holder.mBalanceText.setText(response.body().getResult());
            }

            @Override
            public void onFailure(Call<RPCResponseModel> call, Throwable t) {
                Toast.makeText(mContext, "网络请求失败", Toast.LENGTH_SHORT).show();
            }
        });
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
}
