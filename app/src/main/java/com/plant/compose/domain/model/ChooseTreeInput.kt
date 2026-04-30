package com.plant.compose.domain.model

data class ChooseTreeInput(
    val soilType: String,
    val temperature: String,
    val location: String,
    val waterSupply: String,
    val sunlight: String,
    val typeOfPlant: String?
)
