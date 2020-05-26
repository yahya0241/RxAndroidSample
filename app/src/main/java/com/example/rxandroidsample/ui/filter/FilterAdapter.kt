package com.example.rxandroidsample.ui.filter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rxandroidsample.R

class FilterAdapter: RecyclerView.Adapter<FilterAdapter.IViewHolder>() {
    var taskList = ArrayList<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.filter_list_item, null)
        return IViewHolder(view)

    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    override fun onBindViewHolder(holder: IViewHolder, position: Int) {
        val task = taskList[position]
        holder.bind(task)
    }

    public fun addItem(s:String){
        taskList.add(s)
        notifyDataSetChanged()
    }

    public fun setTask(list:ArrayList<String>){
        taskList.clear()
        taskList = ArrayList<String>(list)
    }
    public fun getTasks():ArrayList<String>{
        return taskList
    }

    fun clearData() {
        taskList.clear()
        notifyDataSetChanged()
    }

    inner class IViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView = itemView.findViewById<TextView>(R.id.filter_textView_item)

        fun bind(task: String){
            textView.text = task.toString()
        }

    }
}