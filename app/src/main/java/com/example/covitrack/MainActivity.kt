package com.example.covitrack

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.view.Window
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.covitrack.api.Day
import com.example.covitrack.api.FetchResult
import com.example.covitrack.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


class MainActivity : AppCompatActivity(), DayItemListener {
	private lateinit var binding: ActivityMainBinding
	private lateinit var viewModel: MainActivityViewModel
	private var loadingDialog: Dialog? = null
	private val TAG = "akil"
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(binding.root)
		viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
		initListeners()
		initRecyclerView()
	}

	private fun initListeners() {
		viewModel.progressBarVisibility.observe(this, {
			if (it) showLoadingDialog() else hideLoadingDialog()
		})
		viewModel.startDate.observe(this, {
			val dateText =
				"${it.get(Calendar.DATE)}-${it.get(Calendar.MONTH) + 1}-${it.get(Calendar.YEAR)}"
			binding.etvStartDate.setText(dateText)
		})
		viewModel.endDate.observe(this, {
			val dateText =
				"${it.get(Calendar.DATE)}-${it.get(Calendar.MONTH) + 1}-${it.get(Calendar.YEAR)}"
			Log.d(TAG, dateText);
			binding.etvEndDate.setText(dateText)
		})
		binding.etvStartDate.setOnClickListener {
			handleDatePick(viewModel.startDate)
		}
		binding.etvEndDate.setOnClickListener {
			handleDatePick(viewModel.endDate)
		}
		binding.imgBtnDone.setOnClickListener {
			viewModel.filterDate()
		}
		binding.btnFetch1.setOnClickListener {
			viewModel.deleteCache()
			Toast.makeText(this, "Cache Cleared", Toast.LENGTH_SHORT).show()
		}
		binding.btnFetch.setOnClickListener {
			lifecycleScope.launch(Dispatchers.IO) {
				val displayText = when (viewModel.fetchData()) {
					is FetchResult.Error -> "Error occurred!"
					is FetchResult.FromCache -> "Fetched from Local Cache"
					is FetchResult.FromAPI -> "Fetched from Network"
				}
				withContext(Dispatchers.Main) {
					Toast.makeText(this@MainActivity, displayText, Toast.LENGTH_SHORT).show()
				}
			}
		}
	}

	private fun initRecyclerView() {
		val rvAdapter = DateRecyclerViewAdapter(this, viewModel.filteredDays.value!!)
		binding.rvDates.adapter = rvAdapter
		binding.rvDates.layoutManager = LinearLayoutManager(this)
		viewModel.filteredDays.observe(this, {
			rvAdapter.setNewData(it)
		})
	}

	private fun handleDatePick(viewModelDate: MutableLiveData<Calendar>) {
		val selectedDate = Calendar.getInstance()
		DatePickerDialog(
			this,
			{ _, year, monthOfYear, dayOfMonth ->
				selectedDate.set(year, monthOfYear, dayOfMonth)
				viewModelDate.value = selectedDate
			},
			2020,
			Calendar.MARCH,
			10
		).show()

	}

	private fun showLoadingDialog() {
		loadingDialog = Dialog(this)
		loadingDialog?.window?.requestFeature(Window.FEATURE_ACTION_BAR)
		loadingDialog?.setCancelable(false)
		val relativeLayout = RelativeLayout(this)
		relativeLayout.layoutParams = RelativeLayout.LayoutParams(
			ViewGroup.LayoutParams.MATCH_PARENT,
			ViewGroup.LayoutParams.MATCH_PARENT
		)
		val progressBar = ProgressBar(this)
		val params = RelativeLayout.LayoutParams(150, 150)
		params.addRule(RelativeLayout.CENTER_IN_PARENT)
		relativeLayout.addView(progressBar)
		loadingDialog?.window?.setContentView(relativeLayout, relativeLayout.layoutParams)
		loadingDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
		loadingDialog?.show()
	}

	private fun hideLoadingDialog() {
		loadingDialog?.hide()
	}

	override fun onclickDayItem(day: Day) {
		Intent(this, DetailsActivity::class.java).apply {
			putExtra("day", day)
			startActivity(this)
		}

	}
}