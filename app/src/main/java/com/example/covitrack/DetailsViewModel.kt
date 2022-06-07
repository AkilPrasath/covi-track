package com.example.covitrack

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.covitrack.api.Day
import com.example.covitrack.api.State

class DetailsViewModel:ViewModel() {
	private var statesRawList = mutableListOf<State>()
	val statesFilteredList = MutableLiveData(mutableListOf<State>())
	fun init(day:Day){
		day.regional.sortBy {
			it.name
		}
		statesRawList.clear()
		statesRawList.addAll(day.regional)
		statesFilteredList.value!!.clear()
		statesFilteredList.value!!.addAll(day.regional)
	}
	fun filter(filterText:String){
		statesFilteredList.value = statesRawList.filter {
			it.name.startsWith(filterText,ignoreCase = true)
		} as MutableList<State>
	}
	fun reset(){
		statesFilteredList.value = statesRawList
	}
}