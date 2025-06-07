package com.example.myapp

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class RecipeDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_detail)

        val title = intent.getStringExtra("title")
        val instructions = intent.getStringExtra("instructions")
        val ingredients = intent.getStringExtra("ingredients")
        val prepTime = intent.getStringExtra("prepTime")
        val kcal = intent.getIntExtra("kcal", 0)
        val protein = intent.getIntExtra("protein", 0)
        val fat = intent.getIntExtra("fat", 0)
        val carbs = intent.getIntExtra("carbs", 0)
        val gi = intent.getIntExtra("gi", 0)

        findViewById<TextView>(R.id.tvDetailTitle).text = title
        findViewById<TextView>(R.id.tvDetailIngredients).text = "Ингредиенты: $ingredients"
        findViewById<TextView>(R.id.tvDetailInstructions).text = instructions
        findViewById<TextView>(R.id.tvDetailTime).text = "Время: $prepTime"
        findViewById<TextView>(R.id.tvDetailKcal).text = "Ккал: $kcal, Б: $protein, Ж: $fat, У: $carbs, ГИ: $gi"
    }
}

