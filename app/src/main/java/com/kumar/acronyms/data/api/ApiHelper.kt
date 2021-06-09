package com.kumar.acronyms.data.api;

class ApiHelper(private val apiService: ApiService) {

    suspend fun getAcromine(sf: String) = apiService.getAcromine(sf);
}