package com.iflytek.mscv5plusdemo.retrofit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by taoxingyu on 2018/12/26.
 */

public interface RetrofitApi {

    //@Headers("apikey:81bf9da930c7f9825a3c3383f1d8d766")
    @GET("book/{id}")
    Call<ResponseBody> getNews(@Path("id") int id);

    @Headers({"Content-Type: application/json", "Accept:  application/json"})
    @POST("/iot/taoxingyu")
    Call<Task> createTask(@Body Task task);

}
