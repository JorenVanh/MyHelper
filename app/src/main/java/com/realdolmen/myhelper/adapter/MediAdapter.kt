package com.realdolmen.myhelper.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.realdolmen.myhelper.R
import com.realdolmen.myhelper.data.Medi

class MediAdapter (private val list: ArrayList<Medi>, private val context: Context): RecyclerView.Adapter<MediAdapter.ViewHolder>() {
    inner class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItem(pill: Medi) {
            var name: TextView = itemView.findViewById(R.id.tvName) as TextView
            name.text = pill.naam
            var dose: TextView = itemView.findViewById(R.id.tvDose) as TextView
            dose.text = pill.dose
            var day: TextView = itemView.findViewById(R.id.tvday) as TextView
            day.text = pill.day
            var period: TextView = itemView.findViewById(R.id.tvtime) as TextView
            period.text = pill.time
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, position: Int): MediAdapter.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.recyclerview_medi, parent, false)
        return ViewHolder(view)
    }
    override fun getItemCount(): Int {return list.size    }
    override fun onBindViewHolder(holder: MediAdapter.ViewHolder, position: Int) {        holder.bindItem(list[position])    }
}