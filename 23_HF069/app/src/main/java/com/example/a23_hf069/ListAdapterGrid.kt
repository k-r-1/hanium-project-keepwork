package com.example.a23_hf069

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.a23_hf069.databinding.ListGridItemBinding

class ListAdapterGrid(var list: ArrayList<String>): RecyclerView.Adapter<ListAdapterGrid.GridAdapter>() {

    // 기본 생성자 추가
    constructor() : this(ArrayList())

    class GridAdapter(internal val binding: ListGridItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridAdapter {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListGridItemBinding.inflate(inflater, parent, false)

        return GridAdapter(binding)
    }

    override fun onBindViewHolder(holder: GridAdapter, position: Int) {
        val item = list[position]
        holder.binding.textListTitle.text = item

        holder.binding.layoutListItem.setOnClickListener {
            Toast.makeText(holder.itemView.context, "$item Click!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}
