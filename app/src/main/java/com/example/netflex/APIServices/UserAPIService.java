package com.example.netflex.APIServices;

import com.example.netflex.model.User;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserAPIService {
    @GET("api/User/{userId}")
    Call<User> getUser(@Path("userId") String userId);
    
    @PUT("api/User/{userId}")
    Call<ResponseBody> updateUser(@Path("userId") String userId, @Body User user);
}
