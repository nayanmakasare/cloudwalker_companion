package api;

import android.content.Context;
import android.webkit.URLUtil;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import tv.cloudwalker.cwnxt.cloudwalkercompanion.BuildConfig;

/**
 * Created by cognoscis on 8/3/18.
 */

public class CustomHttpClient {

    private static String[] userAgentList = {
            "ro.build.version.release",
            "ro.product.model",
            "ro.cvte.ota.version",
            "ro.cloudwalker.cota.version"
    };
    private static int TIMEOUT = 30;

    private static boolean isUrlHTTPS(String url) {
        return URLUtil.isHttpsUrl(url);
    }



    private static OkHttpClient getOkHttps(Context context) {
        SSLContext sslContext;
        TrustManager[] trustManagers;
        try {
            trustManagers = getTrustManagers(context);
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagers, null);
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.connectTimeout(TIMEOUT, TimeUnit.SECONDS);
            builder.readTimeout(TIMEOUT, TimeUnit.SECONDS);
            builder.writeTimeout(TIMEOUT, TimeUnit.SECONDS);
            builder.sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustManagers[0]);

            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession sslSession) {
                    HostnameVerifier hv =
                            HttpsURLConnection.getDefaultHostnameVerifier();
                    boolean verified = hv.verify("*.cloudwalker.tv", sslSession);
                    return verified;
                }
            });

            builder.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();
                    Request request = original.newBuilder()
                            .header("emac", "70:2E:D9:55:44:1A")
                            .header("mboard", "CWT32SHX214")
                            .header("panel", "PT320AT01-1")
                            .header("model", "TP.MT5510I.PB805")
                            .method(original.method(), original.body())
                            .build();

                    return chain.proceed(request);
                }
            });


//            if (BuildConfig.DEBUG) {
//                HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
//                logging.setLevel(HttpLoggingInterceptor.Level.BODY);
//                builder.addInterceptor(logging);
//            }

            return builder.build();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static OkHttpClient getOkHttp(Context context) {
        try {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.connectTimeout(TIMEOUT, TimeUnit.SECONDS);
            builder.readTimeout(TIMEOUT, TimeUnit.SECONDS);
            builder.writeTimeout(TIMEOUT, TimeUnit.SECONDS);

//            final PreferenceManager preferenceManager = new PreferenceManager(context);
//            builder.addInterceptor(new Interceptor() {
//                @Override
//                public Response intercept(Chain chain) throws IOException {
//                    Request original = chain.request();
//
//                    Request request = original.newBuilder()
//                            .header("User-Agent", tvUserAgent)
//                            .header("emac", preferenceManager.getEthMacAddress())
//                            .header("mboard", preferenceManager.getModelNumber())
//                            .header("panel", getSystemProperty("ro.cvte.panelname"))
//                            .header("model", getSystemProperty("ro.product.model"))
//                            .header("cotaversion", getSystemProperty("ro.cloudwalker.cota.version"))
//                            .header("fotaversion", getSystemProperty("ro.cvte.ota.version"))
//                            .header("lversion", BuildConfig.VERSION_NAME)
//                            .method(original.method(), original.body())
//                            .build();
//
//                    return chain.proceed(request);
//                }
//            });

            builder.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();

                    Request request = original.newBuilder()
                            .header("emac", "70:2E:D9:55:44:1A")
                            .header("mboard", "CWT32SHX214")
                            .header("panel", "PT320AT01-1")
                            .header("model", "TP.MT5510I.PB805")
                            .method(original.method(), original.body())
                            .build();

                    return chain.proceed(request);
                }
            });


//            if (BuildConfig.DEBUG) {
//                HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
//                logging.setLevel(HttpLoggingInterceptor.Level.BODY);
//                builder.addInterceptor(logging);
//            }

            return builder.build();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static OkHttpClient getHttpClient(Context context, String url) {
        if (isUrlHTTPS(url)) {
            return getOkHttps(context);
        } else {
            return getOkHttp(context);
        }
    }

    private static TrustManager[] getTrustManagers(Context context) {
        TrustManager[] trustManagers;
        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            InputStream certInputStream = context.getAssets().open("server.crt");
            BufferedInputStream bis = new BufferedInputStream(certInputStream);
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            while (bis.available() > 0) {
                Certificate cert = certificateFactory.generateCertificate(bis);
                keyStore.setCertificateEntry("ca", cert);
            }
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            trustManagers = trustManagerFactory.getTrustManagers();
            return trustManagers;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
