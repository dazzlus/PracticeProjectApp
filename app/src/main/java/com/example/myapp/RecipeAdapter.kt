package com.example.myapp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecipeAdapter(
    private val context: Context,
    private var recipeList: List<Recipe>
) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvMealType: TextView = itemView.findViewById(R.id.tvDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recipe, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipeList[position]
        holder.tvTitle.text = recipe.title
        holder.tvMealType.text = recipe.mealType

        holder.itemView.setOnClickListener {
            val intent = Intent(context, RecipeDetailActivity::class.java).apply {
                putExtra("title", recipe.title)
                putExtra("instructions", recipe.instructions)
                putExtra("prepTime", recipe.prepTime)
                putExtra("ingredients", recipe.ingredients)
                putExtra("kcal", recipe.kcal)
                putExtra("protein", recipe.protein)
                putExtra("fat", recipe.fat)
                putExtra("carbs", recipe.carbs)
                putExtra("gi", recipe.gi)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = recipeList.size
    fun updateList(newList: List<Recipe>) {
        recipeList = newList
        notifyDataSetChanged()
    }
}



