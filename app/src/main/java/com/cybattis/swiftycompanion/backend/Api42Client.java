package com.cybattis.swiftycompanion.backend;

import com.cybattis.swiftycompanion.BuildConfig;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Api42Client {
    private static final String TAG = "Api42Client";
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClient.addInterceptor(logging).build();
        }

        retrofit = new Retrofit.Builder()
                .baseUrl("https://api.intra.42.fr/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        return retrofit;
    }

    public static Api42Service createService() {
        return getClient().create(Api42Service.class);
    }
}
