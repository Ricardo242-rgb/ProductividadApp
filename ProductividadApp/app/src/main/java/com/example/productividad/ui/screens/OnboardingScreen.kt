package com.example.productividad.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.productividad.data.AppColors
import com.example.productividad.ui.components.RifitLogo
import com.example.productividad.ui.components.appChipColors
import com.example.productividad.ui.components.primaryButtonColors
import com.example.productividad.viewmodel.GymViewModel

@Composable
fun OnboardingScreen(vm: GymViewModel, t: (String, String) -> String) {
    var nameInput by rememberSaveable { mutableStateOf(vm.preferredName) }
    var weightInput by rememberSaveable { mutableStateOf(vm.bodyWeight) }
    var unitInput by rememberSaveable { mutableStateOf(vm.weightUnit) }
    var ageInput by rememberSaveable { mutableStateOf(vm.userAge) }
    var heightInput by rememberSaveable { mutableStateOf(vm.userHeight) }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = AppColors.CARD),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, AppColors.BORDER, RoundedCornerShape(28.dp)),
                shape = RoundedCornerShape(28.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    RifitLogo(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp),
                        contentScale = ContentScale.Fit
                    )

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            t("BIENVENIDO A RIFIT", "WELCOME TO RIFIT"),
                            color = AppColors.ACCENT,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.4.sp,
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                        Text(
                            t("Vamos a dejar tu app lista desde el primer minuto", "Let's set up your app from minute one"),
                            color = AppColors.TEXT,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                        Text(
                            t(
                                "Cuéntame cómo quieres que te llamemos y tus datos base. Después entras directo a la app y lo podrás cambiar cuando quieras en Perfil.",
                                "Tell me what you want us to call you and your base stats. After that, you go straight into the app and can change it anytime in Profile."
                            ),
                            color = AppColors.TEXT_SECONDARY,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                        )
                        Spacer(Modifier.height(6.dp))
                    }
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = AppColors.CARD),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, AppColors.BORDER, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text(t("Cómo quieres que te llamemos", "How should we call you")) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AppColors.ACCENT,
                            unfocusedBorderColor = AppColors.BORDER,
                            focusedTextColor = AppColors.TEXT,
                            unfocusedTextColor = AppColors.TEXT
                        )
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = weightInput,
                            onValueChange = { weightInput = it },
                            label = { Text(t("Peso", "Weight")) },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AppColors.ACCENT,
                                unfocusedBorderColor = AppColors.BORDER,
                                focusedTextColor = AppColors.TEXT,
                                unfocusedTextColor = AppColors.TEXT
                            )
                        )
                        Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.Center) {
                            FilterChip(
                                selected = unitInput == "kg",
                                onClick = { unitInput = "kg" },
                                label = { Text("KG") },
                                colors = appChipColors()
                            )
                            Spacer(Modifier.width(8.dp))
                            FilterChip(
                                selected = unitInput == "lb",
                                onClick = { unitInput = "lb" },
                                label = { Text("LB") },
                                colors = appChipColors()
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = heightInput,
                            onValueChange = { heightInput = it },
                            label = { Text(t("Estatura (cm)", "Height (cm)")) },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AppColors.ACCENT,
                                unfocusedBorderColor = AppColors.BORDER,
                                focusedTextColor = AppColors.TEXT,
                                unfocusedTextColor = AppColors.TEXT
                            )
                        )
                        OutlinedTextField(
                            value = ageInput,
                            onValueChange = { ageInput = it },
                            label = { Text(t("Edad", "Age")) },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AppColors.ACCENT,
                                unfocusedBorderColor = AppColors.BORDER,
                                focusedTextColor = AppColors.TEXT,
                                unfocusedTextColor = AppColors.TEXT
                            )
                        )
                    }

                    errorMessage?.let { message ->
                        Text(
                            message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Button(
                        onClick = {
                            if (
                                nameInput.trim().isBlank() ||
                                weightInput.trim().isBlank() ||
                                heightInput.trim().isBlank() ||
                                ageInput.trim().isBlank()
                            ) {
                                errorMessage = t(
                                    "Completa tu nombre, peso, estatura y edad para continuar.",
                                    "Complete your name, weight, height, and age to continue."
                                )
                            } else {
                                errorMessage = null
                                vm.completeOnboarding(
                                    name = nameInput,
                                    weight = weightInput,
                                    unit = unitInput,
                                    age = ageInput,
                                    height = heightInput
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors = primaryButtonColors(),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(t("ENTRAR A RIFIT", "ENTER RIFIT"), fontWeight = FontWeight.Black)
                    }
                }
            }
        }
    }
}
