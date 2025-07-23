package com.example.netflex.APIServices;

import com.example.netflex.model.Review;
import com.example.netflex.requestAPI.auth.RatingRequest;
import com.example.netflex.responseAPI.ReviewResponse;
import com.example.netflex.viewModels.ReviewEditModel;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ReviewAPIService {
    @GET("api/review/serie/{serieId}/rating")
    Call<ReviewResponse> getSeriePublicRating(@Path("serieId") String serieId);

    @POST("api/review/serie/{id}")
    Call<ResponseBody> submitSerieRating(@Path("id") String serieId, @Body ReviewEditModel model);

}
