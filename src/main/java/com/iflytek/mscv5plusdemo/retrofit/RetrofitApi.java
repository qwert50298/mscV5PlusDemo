package com.iflytek.mscv5plusdemo.retrofit;

import com.iflytek.mscv5plusdemo.retrofit.bean.SearchResult;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by taoxingyu on 2018/12/26.
 */

public interface RetrofitApi {

    @GET("/discovery/app/list")
    Call<SearchResult> createTask(@Query("name") String name, @Query("page") int page,
                                  @Query("pageSize") int pageSize);
}
