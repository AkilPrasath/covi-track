package com.example.covitrack

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.covitrack.api.Day
import com.example.covitrack.databinding.ActivityDetailsBinding

class DetailsActivity : AppCompatActivity() {
	lateinit var viewmodel: DetailsViewModel
	lateinit var binding: ActivityDetailsBinding
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityDetailsBinding.inflate(layoutInflater)
		setContentView(binding.root)
		initViewModel()
		initRecyclerView()
		initListeners()
	}

	private fun initViewModel() {
		viewmodel = ViewModelProvider(this).get(DetailsViewModel::class.java)
		val day = intent.getSerializableExtra("day") as Day
		supportActionBar?.title = "Stats for " + day.day
		viewmodel.init(day)
	}

	private fun initListeners() {
		binding.etvSearch.doOnTextChanged { text, _, _, _ ->
			if (text.toString() == "") {
				viewmodel.reset()
			} else {
				viewmodel.filter(text.toString())
			}
		}
		binding.btnClear.setOnClickListener {
			binding.etvSearch.setText("")
			binding.etvSearch.clearFocus()
			it.hideKeyboard()
		}
	}

	private fun initRecyclerView() {
		val rvAdapter = StateRecyclerViewAdapter(viewmodel.statesFilteredList.value!!)
		binding.rvStates.apply {
			adapter = rvAdapter
			layoutManager = LinearLayoutManager(this@DetailsActivity)
		}
		viewmodel.statesFilteredList.observe(this, {
			rvAdapter.updateList(it)
		})
	}

	private fun View.hideKeyboard() {
		val inputManager =
			context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
		inputManager.hideSoftInputFromWindow(windowToken, 0)
	}
}
