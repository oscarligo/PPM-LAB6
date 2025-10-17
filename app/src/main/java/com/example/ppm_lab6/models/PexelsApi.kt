package com.example.ppm_lab6.models

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface PexelsApi {
    @GET("v1/curated")
    fun getCurated(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20
    ): Call<PexelsResponse>

    @GET("v1/search")
    fun search(
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20
    ): Call<PexelsResponse>
}
