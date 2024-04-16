package com.example.todolist

import android.content.Context
import android.inputmethodservice.Keyboard.Row
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.databinding.RowTaskBinding

class TaskAdapter: RecyclerView.Adapter<TaskAdapter.HolderTask> {

    val context: Context
    private lateinit var arrayList: ArrayList<ModelTask>

    private lateinit var binding: RowTaskBinding

    constructor(context: Context, arrayList: ArrayList<ModelTask>) : super() {
        this.context = context
        this.arrayList = arrayList
    }




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderTask {
        binding=RowTaskBinding.inflate(LayoutInflater.from(context),parent,false)
        return HolderTask(binding.root)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: HolderTask, position: Int) {
        val model=arrayList[position]
        val task=model.title
        val date=model.date
        val time=model.time

        holder.task.text=task
        holder.date.text=date
        holder.time.text=time
    }

    inner class HolderTask(itemView: View): RecyclerView.ViewHolder(itemView){
        val task=binding.task
        val date=binding.date
        val time=binding.time
    }
}