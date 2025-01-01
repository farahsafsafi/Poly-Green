package com.example.green


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SuggestionsAdapter(private val onClick: (LocationSuggestion) -> Unit) :
    RecyclerView.Adapter<SuggestionsAdapter.SuggestionViewHolder>() {

    private var suggestions: List<LocationSuggestion> = emptyList()

    fun updateSuggestions(newSuggestions: List<LocationSuggestion>) {
        suggestions = newSuggestions
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        return SuggestionViewHolder(view)
    }

    override fun onBindViewHolder(holder: SuggestionViewHolder, position: Int) {
        val suggestion = suggestions[position]
        holder.bind(suggestion)
    }

    override fun getItemCount(): Int = suggestions.size

    inner class SuggestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(android.R.id.text1)

        fun bind(suggestion: LocationSuggestion) {
            textView.text = suggestion.display_name
            itemView.setOnClickListener { onClick(suggestion) }
        }
    }
}
