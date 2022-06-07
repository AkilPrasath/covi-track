package com.example.covitrack

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.covitrack.api.ApiInterface
import com.example.covitrack.api.ApiResponseData
import com.example.covitrack.api.Day
import com.example.covitrack.api.FetchResult
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class MainActivityViewModel( application: Application) : AndroidViewModel(application) {

	val SHAREDPREF_NAME = "SP"
	val API_RESULT = "api_result"
	private var fullData: ApiResponseData? = null
	var progressBarVisibility = MutableLiveData(false)
	var filteredDays: MutableLiveData<MutableList<Day>> = MutableLiveData(mutableListOf<Day>())
	var startDate = MutableLiveData(Calendar.getInstance().apply {
		set(2020, 2, 10)
	})
	var endDate = MutableLiveData(Calendar.getInstance().apply {
		set(2020, 2, 15)
	})
	private val TAG = "mainactivity_viewmodel"
	private fun storeToCache(){
		getApplication<Application>()
			.applicationContext
			.getSharedPreferences(SHAREDPREF_NAME,Context.MODE_PRIVATE)
			.edit {
			val gson = Gson()
			val json = gson.toJson(fullData)
			this.putString(API_RESULT,json)
			commit()
		}
	}
	private suspend fun fetchCacheData():Boolean{
		val application:Application = getApplication()
		val sharedPref = application.applicationContext.getSharedPreferences("SP", Context.MODE_PRIVATE)
		val json = sharedPref.getString(API_RESULT,null) ?: return false
		val gson = Gson()
		fullData = gson.fromJson(json, ApiResponseData::class.java)
	    withContext(Dispatchers.Main){
			filteredDays.value = fullData!!.data
		}
		return true
	}
	fun deleteCache(){
		getApplication<Application>()
			.applicationContext
			.getSharedPreferences(SHAREDPREF_NAME,Context.MODE_PRIVATE)
			.edit {
				this.clear()
				commit()
			}
		filteredDays.value = mutableListOf()
		fullData = null
	}
	suspend fun fetchData(): FetchResult
	{
		var result : FetchResult = FetchResult.Error
		if (fetchCacheData()) {//check for local database
			result = FetchResult.FromCache
		} else {
				fullData = apiFetch()
				if (fullData != null) {
					 withContext(Dispatchers.Main){
						filteredDays.value = fullData!!.data
					 }
						storeToCache()
						result = FetchResult.FromAPI
				}
		}
		return result
	}

	fun filterDate() {
		if (fullData == null) {
			return
		}
		val dateFormat = SimpleDateFormat("yyyy-MM-dd")
		filteredDays.value = fullData?.data?.filter {
			val currentCal = Calendar.getInstance()
			currentCal.time = dateFormat.parse(it.day)
			currentCal.before(endDate.value) && currentCal.after(startDate.value)
		} as MutableList<Day>
	}

	private suspend fun apiFetch(): ApiResponseData? {
		withContext(Dispatchers.Main) {
			progressBarVisibility.value = true
		}
		var response: Response<ApiResponseData>? = null
		try {
			response = ApiInterface.getInstance().getApiResponse()
		} catch (e: Exception) {
			Log.e(TAG, e.toString())
		}
		withContext(Dispatchers.Main) {
			progressBarVisibility.value = false
		}
		return response!!.body()
	}

}