package com.example.myapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import java.io.BufferedReader
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var recipeAdapter: RecipeAdapter
    private lateinit var recipeFilter: RecipeFilter
    private lateinit var allRecipes: List<Recipe>
    private lateinit var filterSuggestionAdapter: FilterSuggestionAdapter
    private lateinit var etFilterSearch: EditText
    private lateinit var rvFilterResults: RecyclerView
    private lateinit var allIngredients: List<String>

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
        val chipGroup = findViewById<ChipGroup>(R.id.chipGroup)
        val ingredients = loadIngredientsFromCSV()

        for (ingredient in ingredients) {
            val chip = Chip(this).apply {
                text = ingredient
                isCheckable = true
                isClickable = true
                isCloseIconVisible = false
                setEnsureMinTouchTargetSize(false)
                isChecked = false // Start unselected
            }

            // Set click listener
            chip.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    recipeFilter.toggleIngredient(ingredient)
                } else {
                    recipeFilter.toggleIngredient(ingredient)
                }
                applyFilter()
                updateChipAppearance(chip, recipeFilter.getIngredientState(ingredient))
            }

            // Initially set appearance based on current state
            updateChipAppearance(chip, recipeFilter.getIngredientState(ingredient))

            chipGroup.addView(chip)
        }



        // Search bar for recipes
        val etSearch = findViewById<EditText>(R.id.etSearch)
        etSearch.addTextChangedListener {
            recipeFilter.setSearchText(it.toString())
            applyFilter()
        }

        //search bar for ingredients (filters)
        allIngredients = loadIngredientsFromCSV().distinct()
        etFilterSearch = findViewById(R.id.etFilterSearch)
        rvFilterResults = findViewById(R.id.rvFilterResults)
        filterSuggestionAdapter = FilterSuggestionAdapter(allIngredients) { ingredient ->
            toggleChip(ingredient)
            etFilterSearch.setText("") // Clear search after selecting
            rvFilterResults.visibility = View.GONE
        }

        rvFilterResults.adapter = filterSuggestionAdapter
        rvFilterResults.layoutManager = LinearLayoutManager(this)

// Set up search listener
        etFilterSearch.addTextChangedListener { editable ->
            val query = editable.toString()
            filterSuggestionAdapter.filter(query)
            rvFilterResults.visibility = if (query.isNotEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun loadIngredientsFromCSV(): List<String> {
        val ingredients = mutableListOf<String>()
        try {
            val inputStream = assets.open("types.csv")
            val reader = BufferedReader(InputStreamReader(inputStream))
            var line: String?
            var isFirstLine = true

            while (reader.readLine().also { line = it } != null) {
                if (isFirstLine) {
                    isFirstLine = false
                    continue
                }

                // Split the line by comma and trim each value
                val lineSplit = line!!.split(",").map { it.trim() }
                for (item in lineSplit) {
                    if (item.isNotEmpty()) {
                        ingredients.add(item)
                    }
                }
            }

            reader.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ingredients
    }



    private fun updateChipAppearance(chip: Chip, state: FilterState) {
        when (state) {
            FilterState.INCLUDE -> {
                chip.chipBackgroundColor = ContextCompat.getColorStateList(this, R.color.include_color)
                chip.isChecked = true
            }
            FilterState.EXCLUDE -> {
                chip.chipBackgroundColor = ContextCompat.getColorStateList(this, R.color.exclude_color)
                chip.isChecked = true
            }
            FilterState.NONE -> {
                chip.chipBackgroundColor = ContextCompat.getColorStateList(this, R.color.none_color)
                chip.isChecked = false
            }
        }
    }

    private fun toggleChip(ingredient: String) {
        recipeFilter.toggleIngredient(ingredient)

        // Find if chip already exists
        val chipGroup = findViewById<ChipGroup>(R.id.chipGroup)
        val existingChipIndex = (0 until chipGroup.childCount).map { chipGroup.getChildAt(it) }
            .indexOfFirst { (it as? Chip)?.text == ingredient }

        if (existingChipIndex == -1) {
            // Not found — create a new one
            val chip = Chip(this).apply {
                text = ingredient
                isCheckable = true
                isClickable = true
                setEnsureMinTouchTargetSize(false)
            }

            chip.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    recipeFilter.toggleIngredient(ingredient)
                } else {
                    recipeFilter.resetIngredient(ingredient)
                }
                applyFilter()
                updateChipAppearance(chip, recipeFilter.getIngredientState(ingredient))
            }

            updateChipAppearance(chip, recipeFilter.getIngredientState(ingredient))
            chipGroup.addView(chip)
        } else {
            // Already exists — just update its state
            val chip = chipGroup.getChildAt(existingChipIndex) as Chip
            updateChipAppearance(chip, recipeFilter.getIngredientState(ingredient))
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

