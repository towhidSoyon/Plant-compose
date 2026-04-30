package com.plant.compose.data.choosetree

import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.plant.compose.domain.model.ChooseTreeInput
import com.plant.compose.domain.model.TreeRecommendation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class FirebaseChooseTreeDataSource(
    private val firebaseDatabase: FirebaseDatabase,
    private val firestore: FirebaseFirestore
) {
    fun getTreeRecommendations(input: ChooseTreeInput): Flow<Result<List<TreeRecommendation>>> = flow {
        emit(
            runCatching {
                val resultSet = resolveResultSet(input)
                val resultSnapshot = firebaseDatabase
                    .reference
                    .child(CHOOSE_TREE_RESULT_PATH)
                    .child(resultSet)
                    .get()
                    .await()

                val imageMap = firestore
                    .collection(CHOOSE_TREE_RESULT_IMAGE_COLLECTION)
                    .get()
                    .await()
                    .documents
                    .mapNotNull { document ->
                        val typeOfTree = document.getString(TYPE_OF_TREE_FIELD)?.trim()
                        val treeImage = document.getString(TREE_IMAGE_FIELD)?.trim()

                        if (typeOfTree.isNullOrBlank()) null else typeOfTree to treeImage
                    }
                    .toMap()

                val selectedCategory = input.typeOfPlant.normalizePlantTypeToCategory()
                SUPPORTED_CATEGORIES
                    .filter { category ->
                        selectedCategory == null || category == selectedCategory
                    }
                    .flatMap { category ->
                        resultSnapshot
                            .child(category)
                            .getValue(String::class.java)
                            .orEmpty()
                            .split(",")
                            .map { it.trim() }
                            .filter { it.isNotBlank() }
                            .mapIndexed { index, treeName ->
                                TreeRecommendation(
                                    id = "${category}_${treeName}".toStableId(),
                                    name = treeName,
                                    category = category,
                                    imageUrl = imageMap[category],
                                    matchPercentage = index.toMatchPercentage(),
                                    description = "Recommended ${category.lowercase()} for your selected soil, temperature, water, sunlight, and location conditions."
                                )
                            }
                    }
            }.recoverCatching { throwable ->
                throw IllegalStateException(throwable.toChooseTreeMessage(), throwable)
            }
        )
    }

    private fun String?.normalizePlantTypeToCategory(): String? {
        return when (this?.trim()?.lowercase()) {
            null, "", "all" -> null
            "fruits", "fruit", "fruit plant" -> FRUIT_PLANT
            "flowers", "flower", "flower plant" -> FLOWER_PLANT
            "medicinal", "medicine", "medicine plant" -> MEDICINE_PLANT
            "economical", "economic", "economical plant" -> ECONOMICAL_PLANT
            else -> this.trim().takeIf { it in SUPPORTED_CATEGORIES }
        }
    }

    private fun resolveResultSet(input: ChooseTreeInput): String {
        val waterSupply = input.waterSupply.lowercase()
        val sunlight = input.sunlight.lowercase()
        val hasHighWater = waterSupply.contains("high")
        val hasLowWater = waterSupply.contains("low")
        val hasHighSunlight = sunlight.contains("full sun") || sunlight.contains("high")
        val hasLowSunlight = sunlight.contains("full shade") || sunlight.contains("low")

        // TODO: Replace these temporary rules with final agronomy/business mapping.
        return when {
            hasHighSunlight && hasHighWater -> SET_ONE
            hasHighSunlight && hasLowWater -> SET_TWO
            hasLowSunlight && hasHighWater -> SET_THREE
            hasLowSunlight && hasLowWater -> SET_FOUR
            hasHighSunlight -> SET_ONE
            hasLowSunlight -> SET_THREE
            hasHighWater -> SET_ONE
            hasLowWater -> SET_FOUR
            else -> SET_ONE
        }
    }

    private fun Int.toMatchPercentage(): Int {
        return when (this) {
            0 -> 98
            1 -> 92
            2 -> 85
            else -> 80
        }
    }

    private fun String.toStableId(): String {
        return lowercase()
            .replace(Regex("[^a-z0-9]+"), "_")
            .trim('_')
    }

    private fun Throwable.toChooseTreeMessage(): String {
        return when (this) {
            is FirebaseNetworkException -> "Network error. Please check your connection."
            is FirebaseFirestoreException -> when (code) {
                FirebaseFirestoreException.Code.PERMISSION_DENIED -> {
                    "You do not have permission to view tree recommendations."
                }

                FirebaseFirestoreException.Code.UNAVAILABLE -> {
                    "Tree recommendation service is unavailable. Please try again."
                }

                else -> "Unable to load tree recommendations. Please try again."
            }

            is FirebaseException -> "Unable to load tree recommendations. Please try again."
            else -> message ?: "Something went wrong. Please try again."
        }
    }

    private companion object {
        const val CHOOSE_TREE_RESULT_PATH = "chooseTreeResult"
        const val CHOOSE_TREE_RESULT_IMAGE_COLLECTION = "chooseTreeResultImage"
        const val TREE_IMAGE_FIELD = "treeImage"
        const val TYPE_OF_TREE_FIELD = "typeOfTree"

        const val SET_ONE = "Set_One"
        const val SET_TWO = "Set_Two"
        const val SET_THREE = "Set_Three"
        const val SET_FOUR = "Set_Four"

        const val ECONOMICAL_PLANT = "Economical Plant"
        const val FLOWER_PLANT = "Flower Plant"
        const val FRUIT_PLANT = "Fruit Plant"
        const val MEDICINE_PLANT = "Medicine Plant"

        val SUPPORTED_CATEGORIES = listOf(
            ECONOMICAL_PLANT,
            FLOWER_PLANT,
            FRUIT_PLANT,
            MEDICINE_PLANT
        )
    }
}
