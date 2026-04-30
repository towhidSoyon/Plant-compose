package com.plant.compose.presentation.choosetree

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.WbCloudy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.plant.compose.R
import com.plant.compose.core.ui.observeAsEvents
import com.plant.compose.domain.model.ChooseTreeInput
import com.plant.compose.navigation.NavigationEvent
import org.koin.androidx.compose.koinViewModel

@Composable
fun ChooseTreeScreen(
    navController: androidx.navigation.NavController
) {
    val viewModel = koinViewModel<ChooseTreeViewModel>()

    viewModel.event.observeAsEvents { event ->
        when (event) {
            is NavigationEvent.Navigate -> navController.navigate(event.route)
            is NavigationEvent.PopBackStack -> navController.popBackStack()
        }
    }
    ChooseTreeScreenContent(
        onAction = {
            viewModel.onAction(it)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseTreeScreenContent(
    onAction: (ChooseTreeAction) -> Unit
) {
    val scrollState = rememberScrollState()

    var temperature by remember { mutableFloatStateOf(22f) }
    var selectedWater by remember { mutableIntStateOf(0) }
    var selectedSunlight by remember { mutableIntStateOf(1) }
    var selectedSoilIndex by remember { mutableIntStateOf(1) }
    var showSoilTypeSheet by remember { mutableStateOf(false) }
    var location by remember { mutableStateOf("") }
    var typeOfPlant by remember { mutableStateOf("") }
    val selectedSoilType = soilTypes[selectedSoilIndex]
    val waterSupplyOptions = listOf("Low", "Moderate", "High")
    val sunlightOptions = listOf("Full Sun", "Partial Shade", "Full Shade")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0D1F0F),
                        Color(0xFF0F2A14),
                        Color(0xFF0A1A0D)
                    )
                )
            )
            .padding(horizontal = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onAction(ChooseTreeAction.OnBackClick) }
                )
                Spacer(modifier = Modifier.weight(1f))
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
            ) {
                Text(
                    text = "Find Your Perfect Tree",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tell us about your environment, and we'll suggest the most resilient species for your space.",
                    fontSize = 14.sp,
                    color = Color(0xFF8fa38f),
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Soil Type
                FormLabel("SOIL TYPE")
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF1A3322))
                        .clickable { showSoilTypeSheet = true }
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            selectedSoilType.title,
                            color = Color.White,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = Color(0xFF8fa38f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Temperature
                FormLabel("AVERAGE TEMPERATURE")
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF1A3322))
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Min: 5°C", color = Color(0xFF8fa38f), fontSize = 12.sp)
                            Text(
                                "${temperature.toInt()}°C",
                                color = Color(0xFF4FD12B),
                                fontWeight = FontWeight.Bold
                            )
                            Text("Max: 40°C", color = Color(0xFF8fa38f), fontSize = 12.sp)
                        }
                        Slider(
                            value = temperature,
                            onValueChange = { temperature = it },
                            valueRange = 5f..40f,
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFF4FD12B),
                                activeTrackColor = Color(0xFF4FD12B),
                                inactiveTrackColor = Color(0xFF0D1F0F)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Location
                FormLabel("LOCATION / PLACE")
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    placeholder = { Text("e.g. Backyard, Balcony", color = Color(0xFF5A725A)) },
                    trailingIcon = {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color(0xFF5A725A)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF1A3322),
                        unfocusedContainerColor = Color(0xFF1A3322),
                        focusedBorderColor = Color(0xFF4FD12B),
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Water Supply
                FormLabel("WATER SUPPLY")
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SelectableOption(
                        icon = Icons.Default.WaterDrop,
                        label = "Low",
                        isSelected = selectedWater == 0,
                        modifier = Modifier.weight(1f),
                        onClick = { selectedWater = 0 }
                    )
                    SelectableOption(
                        icon = Icons.Default.WaterDrop,
                        label = "Moderate",
                        isSelected = selectedWater == 1,
                        modifier = Modifier.weight(1f),
                        onClick = { selectedWater = 1 }
                    )
                    SelectableOption(
                        icon = Icons.Default.WaterDrop,
                        label = "High",
                        isSelected = selectedWater == 2,
                        modifier = Modifier.weight(1f),
                        onClick = { selectedWater = 2 }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Sunlight
                FormLabel("SUNLIGHT EXPOSURE")
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SelectableOption(
                        icon = Icons.Outlined.LightMode,
                        label = "Full Sun",
                        isSelected = selectedSunlight == 0,
                        modifier = Modifier.weight(1f),
                        onClick = { selectedSunlight = 0 }
                    )
                    SelectableOption(
                        icon = Icons.Outlined.WbCloudy,
                        label = "Partial Shade",
                        isSelected = selectedSunlight == 1,
                        modifier = Modifier.weight(1f),
                        onClick = { selectedSunlight = 1 }
                    )
                    SelectableOption(
                        icon = Icons.Default.Cloud,
                        label = "Full Shade",
                        isSelected = selectedSunlight == 2,
                        modifier = Modifier.weight(1f),
                        onClick = { selectedSunlight = 2 }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Plant Type
                FormLabel("PLANT TYPE PREFERENCES (OPTIONAL)")
                OutlinedTextField(
                    value = typeOfPlant,
                    onValueChange = { typeOfPlant = it },
                    placeholder = {
                        Text(
                            "e.g. Evergreen, Flowering, Fruit-bearing",
                            color = Color(0xFF5A725A),
                            fontSize = 12.sp
                        )
                    },
                    trailingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.plant_logo),
                            contentDescription = null,
                            tint = Color(0xFF5A725A),
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF1A3322),
                        unfocusedContainerColor = Color(0xFF1A3322),
                        focusedBorderColor = Color(0xFF4FD12B),
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Suggest Button
                Button(
                    onClick = {
                        onAction(
                            ChooseTreeAction.OnSuggestClick(
                                ChooseTreeInput(
                                soilType = selectedSoilType.title,
                                temperature = temperature.toInt().toString(),
                                location = location,
                                waterSupply = waterSupplyOptions[selectedWater],
                                sunlight = sunlightOptions[selectedSunlight],
                                typeOfPlant = typeOfPlant.takeIf { it.isNotBlank() }
                            )
                        )
                        )
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6B4226)), // Darker brown/green from design
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Suggest Trees", color = Color.White, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.Search, contentDescription = null, tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Results are calculated based on local horticultural data.",
                    fontSize = 11.sp,
                    color = Color(0xFF5A725A),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        if (showSoilTypeSheet) {
            ModalBottomSheet(
                onDismissRequest = { showSoilTypeSheet = false },
                containerColor = Color(0xFF0F1E13),
                contentColor = Color.White,
                dragHandle = {
                    BottomSheetDefaults.DragHandle(color = Color(0xFF5A725A))
                }
            ) {
                SoilTypeBottomSheetContent(
                    soilTypes = soilTypes,
                    selectedSoilIndex = selectedSoilIndex,
                    onSoilSelected = { index ->
                        selectedSoilIndex = index
                        showSoilTypeSheet = false
                    }
                )
            }
        }
    }
}

private data class SoilType(
    val title: String,
    val description: String
)

private val soilTypes = listOf(
    SoilType(
        title = "Sandy",
        description = "Fast-draining soil that warms quickly and suits drought-tolerant trees."
    ),
    SoilType(
        title = "Loamy (Ideal mix)",
        description = "Balanced texture with strong drainage, aeration, and nutrient retention."
    ),
    SoilType(
        title = "Clay",
        description = "Dense, moisture-holding soil that supports hardy species with strong roots."
    ),
    SoilType(
        title = "Silty",
        description = "Smooth, fertile soil that holds moisture and benefits from steady drainage."
    ),
    SoilType(
        title = "Peaty",
        description = "Organic-rich, acidic soil that stays damp and favors moisture-loving trees."
    )
)

@Composable
private fun SoilTypeBottomSheetContent(
    soilTypes: List<SoilType>,
    selectedSoilIndex: Int,
    onSoilSelected: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp)
            .padding(bottom = 24.dp)
    ) {
        Text(
            text = "Choose Soil Type",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Select the soil that best matches your planting space.",
            fontSize = 14.sp,
            color = Color(0xFF8fa38f),
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            soilTypes.forEachIndexed { index, soilType ->
                SoilTypeOption(
                    soilType = soilType,
                    isSelected = selectedSoilIndex == index,
                    onClick = { onSoilSelected(index) }
                )
            }
        }
    }
}

@Composable
private fun SoilTypeOption(
    soilType: SoilType,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) Color(0xFF1A3322) else Color(0xFF142718))
            .then(
                if (isSelected) Modifier.border(1.dp, Color(0xFF4FD12B), RoundedCornerShape(16.dp))
                else Modifier
            )
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = soilType.title,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = soilType.description,
                color = Color(0xFF8fa38f),
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
        }
        if (isSelected) {
            Spacer(modifier = Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF4FD12B)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color(0xFF0D1F0F),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun FormLabel(text: String) {
    Text(
        text = text,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF5A725A),
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun SelectableOption(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(80.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1A3322))
            .then(
                if (isSelected) Modifier.border(1.dp, Color(0xFF4FD12B), RoundedCornerShape(16.dp))
                else Modifier
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) Color(0xFF4FD12B) else Color(0xFF8fa38f),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                color = if (isSelected) Color.White else Color(0xFF8fa38f)
            )
        }
    }
}
