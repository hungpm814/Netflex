package com.example.netflex.APIServices;


import com.example.netflex.requestAPI.auth.*;
import com.example.netflex.resonseAPI.MessageResponse;
import com.example.netflex.resonseAPI.auth.*;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface AuthApiService {

    @POST("api/Auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("api/Auth/register")
    Call<MessageResponse> register(@Body RegisterRequest request);

    @POST("api/Auth/logout")
    Call<MessageResponse> logout();


    @GET("api/Auth/profile")
    Call<ProfileResponse> getProfile(@Query("userId") String userId);

    @PUT("api/Auth/profile")
    Call<MessageResponse> updateProfile(@Body UpdateProfileRequest request);

    @POST("api/Auth/change-password")
    Call<MessageResponse> changePassword(@Body ChangePasswordRequest request);

    @POST("api/Auth/forgot-password")
    Call<ForgotPasswordTokenResponse> forgotPassword(@Body ForgotPasswordRequest request);

    @POST("api/Auth/reset-password")
    Call<MessageResponse> resetPassword(@Body ResetPasswordRequest request);
}
