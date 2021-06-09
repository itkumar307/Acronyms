package com.kumar.acronyms.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.kumar.acronyms.R
import com.kumar.acronyms.utils.Logger
import kotlinx.android.synthetic.main.item_layout.view.*



class AcronymsAdapter(private val acronyms: ArrayList<String>) :
    RecyclerView.Adapter<AcronymsAdapter.DataViewHolder>() {

    class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(acronym: String) {
            itemView.apply {
             Logger.info("Adapter","${acronym}")
                txt_view_acronyms.text = acronym
                txt_view_acronyms.setTextColor(Color.parseColor("#bdbdbd"))

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder =
        DataViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        )

    override fun getItemCount(): Int = acronyms.size

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.bind(acronyms[position])
    }

    fun addAcronym(acronymsResponse: List<String>) {
        this.acronyms.apply {
            clear()
            addAll(acronyms)
        }

    }
}