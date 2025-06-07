package com.example.myapp

data class Recipe(
    val title: String,
    val mealType: String,
    val isVegan: Boolean,
    val isGlutenFree: Boolean,
    val isLactoseFree: Boolean,
    val kcal: Int,
    val protein: Int,
    val fat: Int,
    val carbs: Int,
    val gi: Int,
    val prepTime: String,
    val instructions: String,
    val ingredients: String,
    val ingredientList1: String,
    val ingredientList2: String
)

enum class FilterState{
    INCLUDE, EXCLUDE, NONE
}
