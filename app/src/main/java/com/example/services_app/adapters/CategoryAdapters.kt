package com.example.services_app.adapters


import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.services_app.R
import com.example.services_app.activities.ItemListActivity
import com.example.services_app.databinding.ViewholderCategoryBinding
import com.example.services_app.domain.CategoryModel

class CategoryAdapters(private val items: MutableList<CategoryModel>): RecyclerView.Adapter<CategoryAdapters.Viewholder>() {
    private lateinit var context: Context
    private var selectedPosition = -1
    private var lastSelectedPosition = -1
    inner class  Viewholder(val binding: ViewholderCategoryBinding): RecyclerView.ViewHolder(binding.root){

    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoryAdapters.Viewholder {
        context=parent.context
        val binding= ViewholderCategoryBinding.inflate(LayoutInflater.from(context),parent,false)
        return Viewholder(binding)
    }

    override fun onBindViewHolder(holder: CategoryAdapters.Viewholder, position: Int) {
        val item=items[position]
        holder.binding.titleTxt.text=item.title
        holder.binding.root.setOnClickListener {
            if(selectedPosition!=position){
                lastSelectedPosition=selectedPosition
                selectedPosition=position
                if(lastSelectedPosition!=-1) notifyItemChanged(lastSelectedPosition)
                notifyItemChanged(selectedPosition)
            }
            Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(context, ItemListActivity::class.java).apply{
                putExtra("id",item.id.toString())
                putExtra("title",item.title)

            }
                ContextCompat.startActivity(context,intent,null)
            },500)
        }
        val isSelected=selectedPosition==position
        holder.binding.cat.setBackgroundResource(
            if(isSelected) R.drawable.black_bg else R.drawable.gradient_bg2
        )
        holder.binding.titleTxt.setTextColor(if(isSelected) holder.itemView.context.resources.getColor(R.color.white)else holder.itemView.context.resources.getColor(R.color.black))
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newData: List<CategoryModel>){
        items.clear()
        items.addAll(newData)
        notifyDataSetChanged()

    }
}