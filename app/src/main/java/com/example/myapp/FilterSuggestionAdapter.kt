package com.example.myapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FilterSuggestionAdapter(private val allIngredients: List<String>,
                              private val onItemClickListener: (String) -> Unit)
    : RecyclerView.Adapter<FilterSuggestionAdapter.ViewHolder>() {
    var filteredList = allIngredients

    fun filter(query: String) {
        filteredList = if (query.isEmpty()) {
            allIngredients
        } else {
            allIngredients.filter { it.contains(query, true) }
        }
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.tvFilterName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_filter, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = filteredList[position]
        holder.textView.text = item
        holder.itemView.setOnClickListener {
            onItemClickListener(item)
        }
    }

    override fun getItemCount() = filteredList.size

}