package com.example.nutritionapp.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.nutritionapp.data.model.NutritionDataF
import com.example.nutritionapp.databinding.ItemMealBinding
import java.text.SimpleDateFormat

class MealsAdapter(
    val onItemClicked: (Int, NutritionDataF) -> Unit,
) : RecyclerView.Adapter<MealsAdapter.MyViewHolder>() {

    val sdf = SimpleDateFormat("dd MMM yyyy")
    private var list: MutableList<NutritionDataF> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = ItemMealBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }

    fun updateList(list: MutableList<NutritionDataF>) {
        this.list = list
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        if (list.isNotEmpty() && position < list.size) {
            list.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, list.size)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MyViewHolder(private val binding: ItemMealBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: NutritionDataF) {
            binding.foodName.text = item.foodName
            binding.textCalories.text = item.calories
            binding.textCarb.text = item.carbs
            binding.textProtein.text = item.protein
            binding.textFat.text = item.fat
            binding.textDate.text = sdf.format(item.date)

            binding.itemLayout.setOnClickListener {
                onItemClicked?.invoke(position, item)
            }
        }

    }
}

