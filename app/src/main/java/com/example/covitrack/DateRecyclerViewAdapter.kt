package com.example.covitrack

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.covitrack.api.Day
import com.example.covitrack.databinding.ExpandableCardInnerContentBinding
import com.example.covitrack.databinding.ExpandableCardListItemBinding

class DateRecyclerViewAdapter(
	private val context:Context,
	private var dayList:   MutableList<Day>
) : RecyclerView.Adapter<DateRecyclerViewAdapter.DayViewHolder>() {

	class DayViewHolder( val binding: ExpandableCardListItemBinding) :
		RecyclerView.ViewHolder(binding.root) {
		lateinit var innerBinding: ExpandableCardInnerContentBinding
		var titleDate = binding.dayListItem
		init {
			val view = binding.dayListItem.findViewById<LinearLayoutCompat>(R.id.innerView)
			innerBinding = ExpandableCardInnerContentBinding.bind(view)
		}
	}
	fun setNewData(newList: MutableList<Day>){
		dayList = newList
		notifyDataSetChanged()
	}
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
		val binding = ExpandableCardListItemBinding.inflate(
			LayoutInflater.from(parent.context),
			parent,
			false
		)
		return DayViewHolder(binding)
	}


	override fun onViewRecycled(holder: DayViewHolder) {
		super.onViewRecycled(holder)
		if(holder.binding.dayListItem.isExpanded)
		holder.binding.dayListItem.collapse()
	}
	override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
		val binding = holder.innerBinding
		val dayItem = dayList[position]
		binding.tvDeaths.text = "${dayItem.summary.deaths}"
		binding.tvDischarged.text = "${dayItem.summary.discharged}"
		binding.tvForeign.text = "${dayItem.summary.confirmedCasesForeign}"
		binding.tvIndian.text = "${dayItem.summary.confirmedCasesIndian}"
		binding.tvTotal.text = "${dayItem.summary.total}"
		holder.titleDate.setTitle("${dayItem.day}")
		holder.innerBinding.btnDetails.setOnClickListener {
			val listener = context as DayItemListener
			listener.onclickDayItem(dayItem)
		}
	}

	override fun getItemCount(): Int {
		return dayList.size
	}
}