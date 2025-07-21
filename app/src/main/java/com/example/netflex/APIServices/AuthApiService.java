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

    @POST("api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("api/auth/register")
    Call<MessageResponse> register(@Body RegisterRequest request);

    @POST("api/auth/logout")
    Call<MessageResponse> logout();


    @GET("api/auth/profile")
    Call<ProfileResponse> getProfile(@Query("userId") String userId);

    @PUT("api/auth/profile")
    Call<MessageResponse> updateProfile(@Body UpdateProfileRequest request);

    @POST("api/auth/change-password")
    Call<MessageResponse> changePassword(@Body ChangePasswordRequest request);

    @POST("api/auth/forgot-password")
    Call<ForgotPasswordOtpResponse> forgotPassword(@Body ForgotPasswordRequest request);

    @POST("api/auth/reset-password")
    Call<MessageResponse> resetPassword(@Body ResetPasswordRequest request);
}
