package com.kumar.acronyms.data.serviceRepository

import com.kumar.acronyms.data.api.ApiHelper


class MainRepository(private val apiHelper: ApiHelper) {

    suspend fun getAcromine(sf: String) = apiHelper.getAcromine(sf);
}