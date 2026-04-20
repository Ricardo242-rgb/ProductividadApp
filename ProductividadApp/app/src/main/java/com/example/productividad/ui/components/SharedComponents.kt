package com.example.productividad.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import coil.compose.AsyncImage
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.productividad.R
import com.example.productividad.data.AppColors
import com.example.productividad.data.AppUtils
import com.example.productividad.data.ChatMessage
import com.example.productividad.data.ExerciseCatalogItem

@Composable
fun RifitLogo(modifier: Modifier = Modifier, contentScale: ContentScale = ContentScale.Fit) {
    Image(
        painter = painterResource(id = R.drawable.logo),
        contentDescription = "RIFIT",
        modifier = modifier,
        contentScale = contentScale
    )
}

@Composable
fun KPI(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = AppColors.CARD), 
        modifier = modifier.border(1.dp, AppColors.BORDER, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp), 
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(title.uppercase(), color = AppColors.MUTED, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Text(
                value, 
                color = AppColors.TEXT, 
                style = MaterialTheme.typography.headlineSmall, 
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
fun appChipColors() = FilterChipDefaults.filterChipColors(
    containerColor = AppColors.CARD_SOFT,
    labelColor = AppColors.MUTED,
    selectedContainerColor = AppColors.ACCENT,
    selectedLabelColor = Color.White
)

@Composable
fun primaryButtonColors() = ButtonDefaults.buttonColors(
    containerColor = AppColors.ACCENT,
    contentColor = Color.White
)

@Composable
fun accentVariantButtonColors() = ButtonDefaults.buttonColors(
    containerColor = AppColors.ACCENT_VARIANT,
    contentColor = Color.White
)

@Composable
fun neutralFilledButtonColors() = ButtonDefaults.buttonColors(
    containerColor = AppColors.CARD_SOFT,
    contentColor = AppColors.TEXT
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseTutorialDialog(item: ExerciseCatalogItem, t: (String, String) -> String, onDismiss: () -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = AppColors.CARD,
        dragHandle = { BottomSheetDefaults.DragHandle(color = AppColors.BORDER) },
        windowInsets = WindowInsets(0, 0, 0, 0) // Ensures background covers the bottom
    ) {
        Column(
            modifier = Modifier
                .padding(start = 24.dp, end = 24.dp, top = 0.dp, bottom = 32.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(4.dp))
            Text(item.name.uppercase(), color = AppColors.TEXT, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
            Text(item.muscle.uppercase(), color = AppColors.ACCENT, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            
            item.tutorial?.let { tut ->
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(tut.description, color = AppColors.TEXT_SECONDARY)
                    
                    Text(t("CLAVES DE EJECUCIÓN", "KEY FORM POINTS"), color = AppColors.ACCENT, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    tut.steps.forEach { point ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(6.dp).background(AppColors.ACCENT, CircleShape))
                            Spacer(Modifier.width(12.dp))
                            Text(point, color = AppColors.TEXT_SECONDARY, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
            
            val imageLoader = AppUtils.gifLoader()

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White)
                    .border(1.dp, AppColors.ACCENT.copy(alpha = 0.3f), RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (item.tutorial?.gifUrl != null) {
                    var isLoading by remember { mutableStateOf(true) }
                    var isError by remember { mutableStateOf(false) }
                    
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = AppColors.ACCENT.copy(alpha = 0.5f),
                            modifier = Modifier.size(32.dp),
                            strokeWidth = 2.dp
                        )
                    }
                    
                    if (isError) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.FitnessCenter, contentDescription = null, modifier = Modifier.size(48.dp), tint = AppColors.BORDER)
                            Text(t("ERROR AL CARGAR", "ERROR LOADING"), color = AppColors.MUTED, style = MaterialTheme.typography.labelSmall)
                        }
                    }

                    AsyncImage(
                        model = item.tutorial.gifUrl,
                        contentDescription = item.name,
                        imageLoader = imageLoader,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(24.dp)),
                        contentScale = ContentScale.Fit,
                        onState = { state ->
                            isLoading = state is coil.compose.AsyncImagePainter.State.Loading
                            isError = state is coil.compose.AsyncImagePainter.State.Error
                        }
                    )
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FitnessCenter,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = AppColors.ACCENT.copy(alpha = 0.5f)
                        )
                        Text(
                            t("SIN VISUALIZACIÓN", "NO VISUAL AVAILABLE"),
                            color = AppColors.MUTED,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
            
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = primaryButtonColors()
            ) {
                Text(t("ENTENDIDO", "GOT IT"), color = Color.White, fontWeight = FontWeight.Black)
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val isUser = message.isFromUser
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (isUser) AppColors.ACCENT else AppColors.CARD
            ),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 0.dp,
                bottomEnd = if (isUser) 0.dp else 16.dp
            ),
            modifier = Modifier
                .widthIn(max = 280.dp)
                .border(
                    if (isUser) 0.dp else 1.dp,
                    AppColors.BORDER,
                    RoundedCornerShape(
                        topStart = 16.dp, topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 0.dp,
                        bottomEnd = if (isUser) 0.dp else 16.dp
                    )
                )
        ) {
            Text(
                text = message.text,
                color = if (isUser) Color.White else AppColors.TEXT,
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
