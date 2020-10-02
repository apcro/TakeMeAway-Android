package io.takemeaway.takemeaway.services;

import android.util.Log;

import java.security.SignatureException;
import java.util.Date;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/*
 * TakeMeAway
 *
 * (C) 2017-present Tom Gordon
 */
public class ApiClient {

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {

            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClient.addInterceptor(interceptor);

            httpClient.addInterceptor(chain -> {
                Request original = chain.request();

                String authHeader = getAuthorisationHeaderValue();

                Request request = original.newBuilder()
                      .header("Authorisation", authHeader)
                      .method(original.method(), original.body())
                      .build();

                Response response = chain.proceed(request);
                if (response.code() != 200) {

                    return response;
                }
                return response;
            }
            );

            OkHttpClient client = httpClient.build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(Backend.getBaseUrl())
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    private static String getTimestamp() {
        long timestamp = new Date().getTime();
        return String.valueOf(timestamp);
    }

    private static String getAuthorisationHeaderValue() {
        String timestamp = getTimestamp();
        String hashSource = timestamp + Backend.getPublicKey() + Backend.getSalt();
        String hash = null;
        try {
            hash = Backend.hashMac(hashSource, Backend.getPrivateKey());
        } catch (SignatureException e) {
            Log.e("SignatureException", e.getMessage());
        }

        if (hash == null) {
            hash = "";
        }

        return "KeyAuth publicKey=" + Backend.getPublicKey() + " hash=" + hash + " ts=" + timestamp;
    }

}