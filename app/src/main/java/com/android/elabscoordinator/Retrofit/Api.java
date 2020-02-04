package com.android.elabscoordinator.Retrofit;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface Api {
    @Headers("Content-Type:application/json")
    @POST("api/mobile/attendance")
    Call<AllStudent> request(@Body AllStudent data);


}
