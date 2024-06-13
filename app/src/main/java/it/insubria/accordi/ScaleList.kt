package it.insubria.accordi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ScaleList(private val scales: List<Scale>) : RecyclerView.Adapter<ScaleList.ScaleViewHolder>() {

    class ScaleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val scaleText: TextView = view.findViewById(R.id.scale_text)
        val scaleDescr: TextView = view.findViewById(R.id.scale_description)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScaleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_scale_list, parent, false)
        return ScaleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScaleViewHolder, position: Int) {
        val scale = scales[position]
        holder.scaleText.text = scale.scale
        holder.scaleDescr.text = scale.notes.joinToString(", ") { it.note }
    }

    override fun getItemCount() = scales.size
}