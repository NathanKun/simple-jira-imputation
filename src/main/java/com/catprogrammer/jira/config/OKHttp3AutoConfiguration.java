package com.catprogrammer.jira.config;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.catprogrammer.jira.constant.Auth;
import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Configuration class for OKHttp3 client
 * 
 * @author NathanKun
 *
 */
@Configuration
public class OKHttp3AutoConfiguration {

    @Bean
    public OkHttpClient okHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] chain,
                        String authType) throws CertificateException {}

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] chain,
                        String authType) throws CertificateException {}

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[] {};
                }
            }};

            // Install the all-trusting trust manager
            SSLContext sslContext;
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            final String credential = Credentials.basic(Auth.getUser(), Auth.getPw());
            OkHttpClient.Builder builder = new OkHttpClient.Builder();

            // Okhttp's Authenticator only get call when request response 401
            // but we need add Authorization header to all request
            // because some rest API don't require authentication
            // but it will return less resource if not authenticated
            builder.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request request = chain.request()
                            .newBuilder()
                            .addHeader("Authorization", credential)
                            .build();
                    return chain.proceed(request);
                }
            });

            // do not validate ssl certificate
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            builder.readTimeout(120, TimeUnit.SECONDS);

            return builder.build();

        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (KeyManagementException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }
}
