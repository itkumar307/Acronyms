package com.kumar.acronyms.data.api;


import com.kumar.acronyms.data.model.AcronymsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("software/acromine/dictionary.py")
    suspend fun getAcromine(@Query("sf") sf: String): List<AcronymsResponse>

}