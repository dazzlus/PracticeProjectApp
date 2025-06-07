package com.example.myapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.BufferedReader
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var recipeAdapter: RecipeAdapter
    private lateinit var recipeFilter: RecipeFilter
    private lateinit var allRecipes: List<Recipe>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Load recipes from CSV
        allRecipes = loadRecipesFromCSV()

        // Initialize filter
        recipeFilter = RecipeFilter()

        // Setup RecyclerView
        recyclerView = findViewById(R.id.rvRecipes)
        recipeAdapter = RecipeAdapter(this, allRecipes)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = recipeAdapter

        // Buttons for ingredients
        findViewById<Button>(R.id.btnTomato)?.setOnClickListener {
            recipeFilter.toggleIngredient("tomato")
            applyFilter()
        }

        findViewById<Button>(R.id.btnCheese)?.setOnClickListener {
            recipeFilter.toggleIngredient("cheese")
            applyFilter()
        }

        // Search bar
        val etSearch = findViewById<EditText>(R.id.etSearch)
        etSearch.addTextChangedListener {
            recipeFilter.setSearchText(it.toString())
            applyFilter()
        }
    }

    private fun applyFilter() {
        val filteredList = recipeFilter.filterRecipes(allRecipes)
        recipeAdapter.updateList(filteredList)
    }

    @SuppressLint("Recycle")
    private fun loadRecipesFromCSV(): List<Recipe> {
        val recipes = mutableListOf<Recipe>()
        try {
            val inputStream = assets.open("recipes.csv")
            val reader = BufferedReader(InputStreamReader(inputStream, Charsets.UTF_8))
            var line: String?
            var isFirstLine = true

            while (reader.readLine().also { line = it } != null) {
                if (isFirstLine) {
                    isFirstLine = false
                    continue
                }

                val tokens = line!!.split(",") // Adjust if needed (e.g., split("\t"))

                if (tokens.size >= 17) {
                    val recipe = Recipe(
                        title = tokens[0],
                        mealType = tokens[1],
                        isVegan = tokens[3] == "1",
                        isGlutenFree = tokens[4] == "1",
                        isLactoseFree = tokens[5] == "1",
                        kcal = tokens[10].toIntOrNull() ?: 0,
                        protein = tokens[11].toIntOrNull() ?: 0,
                        fat = tokens[12].toIntOrNull() ?: 0,
                        carbs = tokens[13].toIntOrNull() ?: 0,
                        gi = tokens[14].toIntOrNull() ?: 0,
                        prepTime = tokens[15],
                        instructions = tokens[16],
                        ingredients = if (tokens.size > 17) tokens[17] else "",
                        ingredientList1 = if (tokens.size > 18) tokens[18] else "",
                        ingredientList2 = if (tokens.size > 19) tokens[19] else ""
                    )
                    recipes.add(recipe)
                }
            }

            reader.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return recipes
    }
}

