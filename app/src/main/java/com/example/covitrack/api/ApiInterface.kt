package com.example.covitrack.api

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface ApiInterface {
	@GET("covid19-in/stats/history")
	suspend fun getApiResponse(): Response<ApiResponseData>
	companion object {

		val BASE_URL = "https://api.rootnet.in/"
		fun getInstance(): ApiInterface {
			 return Retrofit.Builder()
					.addConverterFactory(GsonConverterFactory.create())
					.baseUrl(BASE_URL)
					.build().create(ApiInterface::class.java)

		}
	}
}