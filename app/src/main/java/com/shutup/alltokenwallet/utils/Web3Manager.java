package com.shutup.alltokenwallet.utils;

import android.os.Build;
import android.util.Log;

import com.shutup.alltokenwallet.network.SSLSocketFactoryCompat;
import com.shutup.alltokenwallet.network.Tls12SocketFactory;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.http.HttpService;

import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.TlsVersion;

public class Web3Manager implements Constants {
    static Web3j sWeb3j;

    public static Web3j getInstance() {
        if (sWeb3j == null) {
            synchronized (Web3Manager.class) {
                if (sWeb3j == null) {
//                    sWeb3j = Web3jFactory.build(new HttpService(HTTP_NODE_URL,getNewHttpClient(),false));
                    sWeb3j = Web3jFactory.build(new HttpService(HTTP_NODE_URL));
                }
            }
        }
        return sWeb3j;
    }

    public static OkHttpClient.Builder enableTls12OnPreLollipop(OkHttpClient.Builder client) {
        if (Build.VERSION.SDK_INT >= 16 && Build.VERSION.SDK_INT < 22) {
            try {
                SSLContext sc = SSLContext.getInstance("TLSv1.2");
                sc.init(null, null, null);
                client.sslSocketFactory(new Tls12SocketFactory(sc.getSocketFactory()));

                ConnectionSpec cs = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                        .tlsVersions(TlsVersion.TLS_1_2)
                        .build();

                List<ConnectionSpec> specs = new ArrayList<>();
                specs.add(cs);
//                specs.add(ConnectionSpec.COMPATIBLE_TLS);
//                specs.add(ConnectionSpec.CLEARTEXT);

                client.connectionSpecs(specs);
            } catch (Exception exc) {
                Log.e("OkHttpTLSCompat", "Error while setting TLS 1.2", exc);
            }
        }

        return client;
    }

    public static OkHttpClient.Builder enableTls12OnPreLollipop1(OkHttpClient.Builder client) {
        if (Build.VERSION.SDK_INT >= 16 && Build.VERSION.SDK_INT < 22) {
            try {
                SSLContext sc = SSLContext.getInstance("TLSv1.2");
                sc.init(null, null, null);
                SSLSocketFactory sslSocketFactory = intiSSL();
                client.sslSocketFactory(sslSocketFactory);

//                ConnectionSpec cs = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
//                        .tlsVersions(TlsVersion.TLS_1_2)
//                        .build();
//
//                List<ConnectionSpec> specs = new ArrayList<>();
//                specs.add(cs);

//                client.connectionSpecs(specs);
            } catch (Exception exc) {
                Log.e("OkHttpTLSCompat", "Error while setting TLS 1.2", exc);
            }
        }

        return client;
    }

    private static OkHttpClient getNewHttpClient() {
        OkHttpClient.Builder client = new OkHttpClient.Builder()
                .followRedirects(true)
                .followSslRedirects(true)
                .retryOnConnectionFailure(true)
                .cache(null);

        return enableTls12OnPreLollipop1(client).build();
    }

    /*
        初始化添加ssl协议（解决安卓4.4.4中okhttp不能访问https的问题）
    */
    private static SSLSocketFactory intiSSL() {
        // 自定义一个信任所有证书的TrustManager
        final X509TrustManager trustAllCert =
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new java.security.cert.X509Certificate[]{};
                    }
                };

        final SSLSocketFactory sslSocketFactory = new SSLSocketFactoryCompat(trustAllCert);
        return sslSocketFactory;
    }
}
