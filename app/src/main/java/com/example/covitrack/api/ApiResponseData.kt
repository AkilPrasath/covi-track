package com.example.covitrack.api

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ApiResponseData(
	var success: Boolean,
	@SerializedName("data")
	var data: MutableList<Day>
)

data class Day(
	var day: String,
	@SerializedName("summary")
	var summary: Summary,
	@SerializedName("regional")
	var regional: MutableList<State>
) : Serializable

data class Summary(
	var total: Int,
	var confirmedCasesIndian: Int,
	var confirmedCasesForeign: Int,
	var deaths: Int,
	var discharged: Int,
	var confirmedButLocationUnidentified: Int
) : Serializable

data class State(
	@SerializedName("loc")
	var name: String,
	var confirmedCasesIndian: Int,
	var confirmedCasesForeign: Int,
	var deaths: Int,
	var discharged: Int,
	var totalConfirmed: Int
) : Serializable

