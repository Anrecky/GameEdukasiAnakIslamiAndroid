package com.giovani.game_edukasi_anak_islami

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.giovani.game_edukasi_anak_islami.data.Question

class LearnAdapter(var context: Context) : RecyclerView.Adapter<LearnAdapter.ViewHolder>() {

    private var dataList = emptyList<Question>()

    var listener: RecyclerViewClickListener? = null

    internal fun setDataList(dataList: List<Question>) {
        this.dataList = dataList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LearnAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.learn_detail_layout, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: LearnAdapter.ViewHolder, position: Int) {
        val data = dataList[position]
        holder.menuItem.text = data.answer
        holder.menuItem.setOnClickListener {
            listener?.onItemClicked(it, data)
        }
    }

    override fun getItemCount() = dataList.size


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var menuItem: TextView = itemView.findViewById(R.id.menuItem)

        init {
            menuItem.setBackgroundColor(Color.WHITE)
        }
    }
}