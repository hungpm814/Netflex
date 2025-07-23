package com.example.netflex.APIServices;

import com.example.netflex.model.Country;

import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface CountryAPIService {
    @GET("api/country")
    Call<List<Country>> getCountries();
    @GET("api/country/{id}")
    Call<Country> getCountryById(@Path("id") UUID id);
}
