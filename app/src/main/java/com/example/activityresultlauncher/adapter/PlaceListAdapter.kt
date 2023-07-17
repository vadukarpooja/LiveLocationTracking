package com.example.activityresultlauncher.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.activityresultlauncher.R
import com.example.activityresultlauncher.model.PlaceModel

class PlaceListAdapter (private val placeList: List<PlaceModel>,var onClick:(List<PlaceModel>)->Unit ) :
    RecyclerView.Adapter<PlaceListAdapter.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val  locale: TextView = view.findViewById(R.id.locale)
        private val subLocality: TextView = view.findViewById(R.id.subLocality)
       private val  postalCode:TextView = view.findViewById(R.id.postalCode)


        fun onBind(model: PlaceModel) {
            locale.text = model.locale
            subLocality.text = model.subLocality
            postalCode.text = model.postalCode


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceListAdapter.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.raw_near_place, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PlaceListAdapter.ViewHolder, position: Int) {
        holder.onBind(placeList[position])

    }

    override fun getItemCount(): Int {
        return placeList.size
    }
}