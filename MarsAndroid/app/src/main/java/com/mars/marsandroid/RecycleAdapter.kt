package com.mars.marsandroid

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class RecycleAdapter(private val dataSet: List<Face>) :
    RecyclerView.Adapter<RecycleAdapter.MyViewHolder>() {

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val success = view.findViewById<TextView>(R.id.success)
        val found_name = view.findViewById<TextView>(R.id.found_name)
        val id = view.findViewById<TextView>(R.id.id)
        val image = view.findViewById<ImageView>(R.id.sel_img)
        val progress = view.findViewById<ProgressBar>(R.id.progressBar)
        val good = view.findViewById<ImageView>(R.id.good)
        val bad = view.findViewById<ImageView>(R.id.bad)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.id.text = (position + 1).toString()
        holder.image.setImageBitmap(dataSet.get(itemCount - position - 1).getBitmap())
        holder.progress.visibility = ProgressBar.INVISIBLE
        holder.good.visibility = ProgressBar.INVISIBLE
        holder.bad.visibility = ProgressBar.INVISIBLE
        if (dataSet.get(itemCount - position - 1).success == 1) {
            holder.success.text = "Успешно"
            holder.success.setTextColor(Color.parseColor("#1C8801"))
            holder.found_name.text = dataSet.get(itemCount - position - 1).name
            holder.good.visibility = View.VISIBLE
        }
        else if (dataSet.get(itemCount - position - 1).success == -1) {
            holder.success.text = "Неопознан"
            holder.success.setTextColor(Color.parseColor("#FF0000"))
            holder.found_name.text = "Неопознан"
            holder.bad.visibility = View.VISIBLE
        }
        else {
            holder.success.text = "Обработка"
            holder.success.setTextColor(Color.parseColor("#AAAAAA"))
            holder.found_name.text = "Обработка"
            holder.progress.visibility = View.VISIBLE
        }
    }
}