package com.brainmagic.covid19helpdesk.model.api;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.brainmagic.covid19helpdesk.Constants.ROOT_URL;


public class RetroClient {

    /**
     * Get Retrofit Instance
     */
    private static Retrofit getRetrofitInstance() {
        return new Retrofit.Builder()
                .baseUrl(ROOT_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okClient())
                .build();
    }


    private static OkHttpClient okClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(45, TimeUnit.MINUTES)
                .writeTimeout(45, TimeUnit.MINUTES)
                .readTimeout(45, TimeUnit.MINUTES)
                .build();
    }


    /**
     * Get API Service
     *
     * @return API Service
     */
    public static APIService getApiService() {
        return getRetrofitInstance().create(APIService.class);
    }

}
