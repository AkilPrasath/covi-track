package com.example.covitrack.api

sealed interface FetchResult {
	object FromAPI : FetchResult
	object FromCache: FetchResult
	object Error: FetchResult
}