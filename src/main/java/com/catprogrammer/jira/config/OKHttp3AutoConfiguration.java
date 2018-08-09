package com.catprogrammer.jira.config;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
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
 * @author NathanKun
 *
 */
@Configuration
public class OKHttp3AutoConfiguration {

	@Bean
	public OkHttpClient okHttpClient() {
        final String credential = Credentials.basic(Auth.getUser(), Auth.getPw());
		OkHttpClient.Builder builder = new OkHttpClient.Builder();
		
		// Okhttp's Authenticator only get call when request response 401
		// but we need add Authorization header to all request
		// because some rest API don't require authentication
		// but it will return less resource if not authenticated
		builder.addInterceptor(new Interceptor() { 
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request().newBuilder().addHeader("Authorization", credential).build();
                return chain.proceed(request);
            }
        });
		
		builder.readTimeout(30, TimeUnit.SECONDS);
		
		return builder.build();
	}
}
