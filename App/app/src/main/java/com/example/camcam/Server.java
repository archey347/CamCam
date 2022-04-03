package com.example.camcam;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class Server {

    private interface API {
        @FormUrlEncoded
        @POST("")
        Call<Void> addObservation(@Field("data") String data);
    }

    API service;

    public void Server()
    {
        OkHttpClient client = new OkHttpClient();

        client.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                HttpUrl url = request.url().newBuilder().addQueryParameter("key","JayDoesn'tKnowWhatATomTomIs").build();
                request = request.newBuilder().url(url).build();
                return chain.proceed(request);
            }
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://camcam.wwlrc.co.uk/")
                .client(client)
                .build();

        this.service = retrofit.create(API.class);
    }

    public void add(String data) {

        this.service.addObservation(data);
    }


}
