package com.shutup.alltokenwallet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shutup.alltokenwallet.R;
import com.shutup.alltokenwallet.base.BaseActivity;
import com.shutup.alltokenwallet.model.AccountInfo;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AccountDetailsActivity extends BaseActivity {

    @BindView(R.id.left_icon)
    ImageView mLeftIcon;
    @BindView(R.id.title_text)
    TextView mTitleText;
    @BindView(R.id.right_icon)
    ImageView mRightIcon;
    @BindView(R.id.balanceText)
    TextView mBalanceText;
    @BindView(R.id.qrCodeImage)
    ImageView mQrCodeImage;
    @BindView(R.id.walletAddrText)
    TextView mWalletAddrText;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.backupAccountBtn)
    Button mBackupAccountBtn;
    @BindView(R.id.renameAccountBtn)
    Button mRenameAccountBtn;
    @BindView(R.id.delAccountBtn)
    Button mDelAccountBtn;
    @BindView(R.id.bottomBtnLayout)
    LinearLayout mBottomBtnLayout;
    @BindView(R.id.rewardBtn)
    Button mRewardBtn;
    @BindView(R.id.showHideAddrImage)
    ImageView mShowHideAddrImage;
    @BindView(R.id.copyAddrImage)
    ImageView mCopyAddrImage;

    AccountInfo mAccountInfo;
    boolean isShowAddr = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_details);
        ButterKnife.bind(this);
        initToolBar();
        initViews();
    }

    private void initViews() {
        Intent intent = getIntent();
        mAccountInfo = (AccountInfo) intent.getSerializableExtra(ACCOUNT_INFO_KEY);
        mRewardBtn.setVisibility(View.VISIBLE);
        mCopyAddrImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_content_copy));
        mCopyAddrImage.setVisibility(View.VISIBLE);
        mShowHideAddrImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_content_hide));
        mShowHideAddrImage.setVisibility(View.VISIBLE);
        if (mAccountInfo != null) {
            mWalletAddrText.setText("**** **** **** " + mAccountInfo.getAddress().substring(mAccountInfo.getAddress().length() - 4));
        }
    }

    private void initToolBar() {
        mLeftIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_left_back));
        mRightIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_more));
    }

    @OnClick({R.id.rewardBtn, R.id.showHideAddrImage, R.id.copyAddrImage,
            R.id.left_icon, R.id.right_icon, R.id.qrCodeImage,
            R.id.backupAccountBtn, R.id.renameAccountBtn, R.id.delAccountBtn, R.id.bottomBtnLayout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.left_icon:
                finish();
                break;
            case R.id.right_icon:
                mBottomBtnLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.qrCodeImage:
                break;
            case R.id.backupAccountBtn:
                break;
            case R.id.renameAccountBtn:
                break;
            case R.id.delAccountBtn:
                break;
            case R.id.bottomBtnLayout:
                mBottomBtnLayout.setVisibility(View.GONE);
                break;
            case R.id.rewardBtn:
                //jump to detail
                Intent intent = new Intent(AccountDetailsActivity.this, TransferActivity.class);
                intent.putExtra(ACCOUNT_INFO_KEY, mAccountInfo);
                startActivity(intent);
                break;
            case R.id.showHideAddrImage:
                updateAddrtextStatus();
                break;
            case R.id.copyAddrImage:
                break;
        }
    }

    private void updateAddrtextStatus() {
        if (isShowAddr) {
            mShowHideAddrImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_content_show));
            mWalletAddrText.setText(mAccountInfo.getAddress());
        } else {
            mShowHideAddrImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_content_hide));
            mWalletAddrText.setText("**** **** **** " + mAccountInfo.getAddress().substring(mAccountInfo.getAddress().length() - 4));
        }
        isShowAddr = !isShowAddr;
    }
}
