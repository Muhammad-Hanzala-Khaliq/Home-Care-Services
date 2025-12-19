package com.example.services_app.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.services_app.R
import com.example.services_app.activities.DetailActivity
import com.example.services_app.databinding.ViewholderItemBinding
import com.example.services_app.domain.ItemModel

class ItemListCategoryAdapter(val items: MutableList<ItemModel>): RecyclerView.Adapter<ItemListCategoryAdapter.Viewholder>() {
    lateinit var context: Context
    class Viewholder(val binding: ViewholderItemBinding):
    RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemListCategoryAdapter.Viewholder {
      context=parent.context
        val binding= ViewholderItemBinding.inflate(LayoutInflater.from(context),parent,false)
        return Viewholder(binding)
    }

    override fun onBindViewHolder(
        holder: ItemListCategoryAdapter.Viewholder,
        position: Int
    ) {
        holder.binding.titleTxt.text=items[position].title
        holder.binding.subTitleTxt.text=items[position].subtitle.toString()

        Glide.with(context).load(items[position].picUrl).into((holder.binding.pic))
        val bgIndex=position%background.size
        holder.binding.mainLayout.setBackgroundResource(background[bgIndex])
        holder.itemView.setOnClickListener {
         val intent= Intent(context, DetailActivity::class.java)
            intent.putExtra("object",items[position])
            intent.putExtra("position",bgIndex)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = items.size

    private val background=listOf(
        R.drawable.pink_gradient_bg,
        R.drawable.green_gradient_bg,
        R.drawable.brown_gradient_bg,
        R.drawable.blue_gradient_bg
    )
}