package com.shutup.alltokenwallet.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.shutup.alltokenwallet.R;
import com.shutup.alltokenwallet.db.RealmManager;
import com.shutup.alltokenwallet.model.AccountInfo;
import com.shutup.alltokenwallet.utils.FileManager;

import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;

import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

public class CreateAccountActivity extends AppCompatActivity {

    @BindView(R.id.left_icon)
    ImageView mLeftIcon;
    @BindView(R.id.title_text)
    TextView mTitleText;
    @BindView(R.id.passwordText)
    EditText mPasswordText;
    @BindView(R.id.passwordConfirmText)
    EditText mPasswordConfirmText;
    @BindView(R.id.createAccountBtn)
    Button mCreateAccountBtn;
    @BindView(R.id.importAccountBtn)
    TextView mImportAccountBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        ButterKnife.bind(this);
        initToolBar();
    }

    private void initToolBar() {
        mLeftIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_left_back));
        mTitleText.setText("创建账户");
    }

    @OnClick({R.id.createAccountBtn, R.id.importAccountBtn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.createAccountBtn:
                createAccount();
                break;
            case R.id.importAccountBtn:
                break;
        }
    }

    private void createAccount() {
        String password = mPasswordText.getText().toString();
        String passwordConfirm = mPasswordConfirmText.getText().toString();
        if (password.trim().length() < 8) {
            Toast.makeText(this, "密码长度不足8位", Toast.LENGTH_SHORT).show();
        } else if (passwordConfirm.trim().length() < 8) {
            Toast.makeText(this, "密码长度不足8位", Toast.LENGTH_SHORT).show();
        } else if (!password.trim().contentEquals(passwordConfirm.trim())) {
            Toast.makeText(this, "两次输入密码不一致", Toast.LENGTH_SHORT).show();
        } else {
            new WalletGenerateTask().execute(password.trim());
        }
    }

    @OnClick(R.id.left_icon)
    public void onViewClicked() {
        finish();
    }

    private class WalletGenerateTask extends AsyncTask<String, Integer, AccountInfo> {

        @Override
        protected AccountInfo doInBackground(String... strings) {
            try {
                String password = strings[0];
                String filename = WalletUtils.generateLightNewWalletFile(password, FileManager.getAccounts_DIR());
                Credentials credentials = WalletUtils.loadCredentials(password, new File(FileManager.getAccounts_DIR(), filename));
                if (credentials != null) {
                    AccountInfo accountInfo = new AccountInfo();
                    accountInfo.setAddress(credentials.getAddress());
                    accountInfo.setFileName(filename);
                    accountInfo.setPath(new File(FileManager.getAccounts_DIR(), filename).getAbsolutePath());
                    accountInfo.setPassword(password);
                    return accountInfo;
                }
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (NoSuchProviderException e) {
                e.printStackTrace();
            } catch (InvalidAlgorithmParameterException e) {
                e.printStackTrace();
            } catch (CipherException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final AccountInfo accountInfo) {
            RealmManager.getRealmInstance().executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.insertOrUpdate(accountInfo);
                    finish();
                }
            });
        }
    }
}
