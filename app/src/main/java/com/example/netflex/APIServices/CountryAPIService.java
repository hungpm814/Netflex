package com.example.netflex.APIServices;

import com.example.netflex.model.Country;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface CountryAPIService {
    @GET("api/country")
    Call<List<Country>> getCountries();
}
