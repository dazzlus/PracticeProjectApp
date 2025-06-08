package com.example.myapp

class RecipeFilter {
    private var searchText = ""
    private val ingredientFilter= mutableMapOf<String, FilterState>()


    //меняет состояние фильта (добавить, исключить, не трогать)
    fun toggleIngredient(ingredient: String){
        val currentState = ingredientFilter[ingredient]
        val nextState = when (currentState) {
            FilterState.NONE -> FilterState.INCLUDE
            FilterState.INCLUDE -> FilterState.EXCLUDE
            FilterState.EXCLUDE -> FilterState.NONE
            null -> FilterState.INCLUDE
        }
        if (nextState === FilterState.NONE) {
            ingredientFilter.remove(ingredient)
        } else {
            ingredientFilter[ingredient] = nextState
        }
    }

    //Чистит пробелы и пустые места в тексте
    fun setSearchText(text: String) {
        searchText = text.trim()
    }

    fun getSelectedIngredients(): Map<String, FilterState> {
        return ingredientFilter.toMap()
    }

    fun getIngredientState(ingredient: String): FilterState {
        return ingredientFilter[ingredient] ?: FilterState.NONE
    }

    fun filterRecipes(recipes: List<Recipe>): List<Recipe> {
        return recipes.filter { recipe ->
            val matchesName = if (searchText.isEmpty()) {
                true
            } else {
                recipe.title.contains(searchText, ignoreCase = true)
            }

            val ingredients = recipe.ingredientList1.split(";").map { it.trim() }.toSet()

            val matchesIngredients = ingredientFilter.entries.all { (ingredient, state) ->
                when (state) {
                    FilterState.INCLUDE -> ingredients.contains(ingredient)
                    FilterState.EXCLUDE -> !ingredients.contains(ingredient)
                    FilterState.NONE -> true
                }
            }

            matchesName && matchesIngredients
        }
    }
}