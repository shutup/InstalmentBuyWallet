package com.shutup.alltokenwallet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shutup.alltokenwallet.R;
import com.shutup.alltokenwallet.db.RealmManager;
import com.shutup.alltokenwallet.model.AccountInfo;
import com.shutup.alltokenwallet.utils.Constants;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity implements Constants {

    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout mSwipeRefresh;
    @BindView(R.id.left_icon)
    ImageView mLeftIcon;
    @BindView(R.id.title_text)
    TextView mTitleText;
    @BindView(R.id.right_icon)
    ImageView mRightIcon;
    @BindView(R.id.totalAccount)
    TextView mTotalAccount;
    @BindView(R.id.addBtn)
    ImageView mAddBtn;
    @BindView(R.id.createAccountBtn)
    Button mCreateAccountBtn;
    @BindView(R.id.importAccountBtn)
    Button mImportAccountBtn;
    @BindView(R.id.bottomBtnLayout)
    LinearLayout mBottomBtnLayout;

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    List<AccountInfo> mAccountInfos;
    AccountRecyclerViewAdapter mAccountRecyclerViewAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initToolBar();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshView();
    }

    private void refreshView() {
        RealmResults<AccountInfo> accountInfoRealmResults = RealmManager.getRealmInstance().where(AccountInfo.class).findAll();
        mAccountInfos = RealmManager.getRealmInstance().copyFromRealm(accountInfoRealmResults);
        mAccountRecyclerViewAdapter.notifyDataSetChanged();
    }

    private void initView() {

        mTotalAccount.setText("456.233223");
        mTitleText.setText("AllCoin Wallet");

        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshView();
                mSwipeRefresh.setRefreshing(false);
            }
        });

        RealmResults<AccountInfo> accountInfoRealmResults = RealmManager.getRealmInstance().where(AccountInfo.class).findAll();
        mAccountInfos = RealmManager.getRealmInstance().copyFromRealm(accountInfoRealmResults);
        mAccountRecyclerViewAdapter = new AccountRecyclerViewAdapter(getApplicationContext(), mAccountInfos);
        mRecyclerView.addOnItemTouchListener(new RecyclerViewTouchListener(mRecyclerView.getContext(), mRecyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                //jump to detail
                Intent intent = new Intent(MainActivity.this, AccountDetailsActivity.class);
                intent.putExtra(ACCOUNT_INFO_KEY, mAccountInfos.get(position));
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mRecyclerView.getContext()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAccountRecyclerViewAdapter);
    }

    private void initToolBar() {
        mLeftIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_scan));
        mRightIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_setting));
        mTitleText.setText("Wallet");

    }

    @OnClick(R.id.addBtn)
    public void onViewClicked() {
        mBottomBtnLayout.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.createAccountBtn)
    public void onMCreateAccountBtnClicked() {
        mBottomBtnLayout.setVisibility(View.GONE);
        Intent intent = new Intent(this, CreateAccountActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.importAccountBtn)
    public void onMImportAccountBtnClicked() {
        Toast.makeText(this, "import account ", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.bottomBtnLayout)
    public void onMBottomBtnLayoutClicked() {
        mBottomBtnLayout.setVisibility(View.GONE);
    }

    @OnClick(R.id.left_icon)
    public void onMLeftIconClicked() {
    }

    @OnClick(R.id.right_icon)
    public void onMRightIconClicked() {
    }
}
