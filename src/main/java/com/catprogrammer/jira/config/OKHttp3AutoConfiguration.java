package com.catprogrammer.jira.config;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.catprogrammer.jira.constant.Auth;

import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

@Configuration
public class OKHttp3AutoConfiguration {

	@Bean
	public OkHttpClient okHttpClient() {
		OkHttpClient.Builder builder = new OkHttpClient.Builder();
		
		builder.authenticator(new Authenticator() {

			@Override
			public Request authenticate(Route route, Response response) throws IOException {
				if (response.request().header("Authorization") != null) {
					return null; // Give up, we've already failed to authenticate.
				}

				final String credential = Credentials.basic(Auth.getUser(), Auth.getPw());
				return response.request().newBuilder().header("Authorization", credential).build();
			}
		});
		
		return builder.build();
	}
}
