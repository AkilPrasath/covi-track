package com.example.covitrack

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.covitrack.api.State
import com.example.covitrack.databinding.ItemStateCardBinding

class StateRecyclerViewAdapter(
	var statesList: MutableList<State>
) : RecyclerView.Adapter<StateRecyclerViewAdapter.StateViewHolder>() {
	class StateViewHolder(val binding: ItemStateCardBinding) :
		RecyclerView.ViewHolder(binding.root) {

	}
	fun updateList(newStateList:MutableList<State>){

		statesList= newStateList
		notifyDataSetChanged()
	}
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StateViewHolder {
		val binding =
			ItemStateCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
		return StateViewHolder(binding)
	}

	override fun onBindViewHolder(holder: StateViewHolder, position: Int) {
		val stateItem = statesList[position]
		holder.binding.apply {
			startLetter.text = stateItem.name.substring(0,1)
			if(!checkFirstInTheList(position)){
				startLetter.visibility = View.GONE
			}
			else{
				startLetter.visibility = View.VISIBLE
			}
			tvState.text = stateItem.name.toString()
			tvTotalCases.text = stateItem.totalConfirmed.toString()
			tvConfirmedForeigners.text = stateItem.confirmedCasesForeign.toString()
			tvConfirmedIndians.text = stateItem.confirmedCasesIndian.toString()
			tvDischargedCases.text = stateItem.discharged.toString()
			tvDeathCases.text = stateItem.deaths.toString()
		}
	}
	private fun checkFirstInTheList(index:Int): Boolean{
		if(index==0) return true
		val prev = statesList[index-1].name.substring(0,1)
		val current = statesList[index].name.substring(0,1)
		if(prev == current){
			return false
		}
		return true
	}
	override fun getItemCount(): Int {
		return statesList.size
	}
}