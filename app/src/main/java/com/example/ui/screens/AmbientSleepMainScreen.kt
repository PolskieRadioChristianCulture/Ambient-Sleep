package com.example.ui.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import com.example.audio.AmbientAudioService
import com.example.audio.BinauralType
import com.example.monetization.AdManager
import com.example.ui.theme.*
import com.example.viewmodel.AmbientViewModel
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Custom high-fidelity gold Logo matching the reference brand with styled "A" and crescent Moon
@Composable
fun AmbientSleepLogo(modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Image(
            painter = painterResource(id = com.example.R.drawable.logo_ambient_sleep),
            contentDescription = "Ambient Sleep Brand Logo",
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, Color(0xFFC5A059), RoundedCornerShape(12.dp))
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = stringResource(id = com.example.R.string.logo_ambient),
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                color = GoldMuted,
                letterSpacing = 1.8.sp,
                lineHeight = 14.sp
            )
            Text(
                text = stringResource(id = com.example.R.string.logo_sleep),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 4.sp,
                lineHeight = 9.sp
            )
        }
    }
}

// Gorgeous Custom App Store and Google Play Badge pills rendered programmatically
@Composable
fun StyledBadgePill(
    icon: ImageVector,
    storeName: String,
    prefix: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Black)
            .border(0.5.dp, TextMuted.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = storeName,
            tint = Color.White,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Column(verticalArrangement = Arrangement.Center) {
            Text(
                text = prefix,
                fontSize = 6.5.sp,
                color = TextMuted,
                lineHeight = 7.sp,
                fontWeight = FontWeight.Normal
            )
            Text(
                text = storeName,
                fontSize = 9.sp,
                color = Color.White,
                lineHeight = 9.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}



// Gorgeous Custom App Store and Google Play Badge pills rendered programmatically with brand colors
@Composable
fun ColorfulStoreBadge(
    icon: ImageVector,
    title: String,
    subtitle: String,
    brandColors: List<Color>,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Black.copy(alpha = 0.65f))
            .border(
                border = BorderStroke(
                    width = 1.dp,
                    brush = Brush.linearGradient(brandColors)
                ),
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = brandColors.firstOrNull() ?: Color.White,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Column(verticalArrangement = Arrangement.Center) {
            Text(
                text = subtitle,
                fontSize = 6.5.sp,
                color = TextMuted,
                lineHeight = 7.sp,
                fontWeight = FontWeight.Normal
            )
            Text(
                text = title,
                fontSize = 8.sp,
                color = Color.White,
                lineHeight = 9.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// Interactive Premium Sleep Environment Slogan Segment
@Composable
fun SleepHeroSection(
    isPlaying: Boolean,
    onTogglePlay: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "UniverseVibe")
    
    val starAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "StarsAlpha"
    )
    
    val wavyCycle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(6500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "FloatingWaves"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(310.dp)
            .clip(RoundedCornerShape(26.dp)),
        border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.15f))
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // 1. Cozy Sleep Night Illustration uploaded by the user
            Image(
                painter = painterResource(id = com.example.R.drawable.ambient_sleep_hero),
                contentDescription = "Ambient Sleep Cozy Night Scenery Banner",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // 2. High hierarchy visual contrast dark gradient scrim masking
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.45f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.75f)
                            )
                        )
                    )
            )

            // 3. Dynamic flowing soundwave pathways on top of the graphics
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                val flow1 = Path()
                val flow2 = Path()
                flow1.moveTo(0f, h * 0.44f)
                flow2.moveTo(0f, h * 0.48f)
                
                for (x in 0..w.toInt() step 8) {
                    val angle = (x.toFloat() / w) * 2.5f * Math.PI.toFloat() + wavyCycle
                    val dy1 = Math.sin(angle.toDouble()).toFloat() * 12.dp.toPx()
                    val dy2 = Math.cos(angle.toDouble()).toFloat() * 8.dp.toPx()
                    flow1.lineTo(x.toFloat(), h * 0.44f + dy1)
                    flow2.lineTo(x.toFloat(), h * 0.48f + dy2)
                }

                drawPath(flow1, SoftNeonBlue.copy(alpha = 0.35f), style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round))
                drawPath(flow2, SoftNeonPurple.copy(alpha = 0.2f), style = Stroke(width = 1.2f.dp.toPx(), cap = StrokeCap.Round))
            }

            // Symmetrically placed layout elements
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Centered Top Titles
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(id = com.example.R.string.hero_title),
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        lineHeight = 23.sp,
                        letterSpacing = 0.5.sp,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleLarge
                    )
                    
                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = stringResource(id = com.example.R.string.hero_desc),
                        fontSize = 10.5.sp,
                        color = TextMutedPurple,
                        lineHeight = 14.sp,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.fillMaxWidth(0.9f)
                    )
                }

                // Centered Button & Badges at exactly the same height as the Moon lamp
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(bottom = 10.dp)
                ) {
                    // START LISTENING FREE Sunset Orange Gradient trigger
                    Button(
                        onClick = onTogglePlay,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .height(38.dp)
                            .width(180.dp)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(GradientOrange, GradientPink, GradientPurple)
                                ),
                                shape = RoundedCornerShape(20.dp)
                             )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.VolumeUp else Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = AmmoBlack,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isPlaying) stringResource(id = com.example.R.string.hero_btn_pause) else stringResource(id = com.example.R.string.hero_btn_start),
                                color = AmmoBlack,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.4.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// Elegant sleep-themed onboarding wizard displaying customizable goals and times
@Composable
fun OnboardingOverlay(
    onComplete: (goal: String, targetHours: Int) -> Unit
) {
    val context = LocalContext.current
    var selectedGoalResId by remember { mutableStateOf(com.example.R.string.onboarding_goal_noise) }
    var targetHours by remember { mutableStateOf(8) }
    
    val goals = listOf(
        com.example.R.string.onboarding_goal_god to Icons.Default.Star,
        com.example.R.string.onboarding_goal_noise to Icons.Default.GraphicEq,
        com.example.R.string.onboarding_goal_insomnia to Icons.Default.Bedtime,
        com.example.R.string.onboarding_goal_student to Icons.Default.AutoAwesome
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(CosmicDeepBlack, CosmicMidnightNavy, Color.Black)
                )
            )
            .statusBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f)
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            AmbientSleepLogo()
            
            Spacer(modifier = Modifier.height(30.dp))
            
            Text(
                text = stringResource(id = com.example.R.string.onboarding_welcome),
                fontSize = 22.sp,
                color = Color.White,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = stringResource(id = com.example.R.string.onboarding_desc),
                fontSize = 11.5.sp,
                color = TextMutedPurple,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Choose goal section
            Text(
                text = stringResource(id = com.example.R.string.onboarding_section_goal),
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Bold,
                color = GoldAccent,
                letterSpacing = 1.sp,
                modifier = Modifier.align(Alignment.Start)
            )
            
            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                goals.forEach { (goalResId, icon) ->
                    val isSelected = selectedGoalResId == goalResId
                    val goalText = stringResource(id = goalResId)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedGoalResId = goalResId },
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, if (isSelected) GoldAccent else GoldAccent.copy(alpha = 0.1f)),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) Color(0xFF191B2F) else Color(0xFF0C0E1E)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = goalText,
                                tint = if (isSelected) GoldAccent else TextMuted
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = goalText,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) GoldAccent else TextLight
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Time Selector section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = com.example.R.string.onboarding_section_time),
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldAccent,
                    letterSpacing = 1.sp
                )
                Text(
                    text = stringResource(id = com.example.R.string.onboarding_hours_suffix, targetHours),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                listOf(4, 6, 8, 10).forEach { hr ->
                    val isSelected = targetHours == hr
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) GoldAccent else Color(0xFF0F1124))
                            .clickable { targetHours = hr }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${hr}h",
                            color = if (isSelected) AmmoBlack else TextLight,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Action Trigger
        Button(
            onClick = { onComplete(context.getString(selectedGoalResId), targetHours) },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(GradientOrange, GradientPink)
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
        ) {
            Text(
                text = stringResource(id = com.example.R.string.onboarding_btn_start),
                color = AmmoBlack,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp
            )
        }
    }
}

// Capsule vertical slider modeled precisely after the premium mockup design
@Composable
fun SleekVerticalSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .height(160.dp)
            .width(42.dp)
            .clip(RoundedCornerShape(22.dp)),
        contentAlignment = Alignment.Center
    ) {
        val maxH = maxHeight
        val maxHPx = with(androidx.compose.ui.platform.LocalDensity.current) { maxH.toPx() }
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        val pct = 1f - (offset.y / maxHPx).coerceIn(0f, 1f)
                        onValueChange(pct)
                    }
                }
                .pointerInput(Unit) {
                    detectDragGestures { change, _ ->
                        change.consume()
                        val pct = 1f - (change.position.y / maxHPx).coerceIn(0f, 1f)
                        onValueChange(pct)
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            // Raw Custom Canvas drawing the gold dotted/dispersed particle line track instead of solid
            Canvas(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(16.dp)
            ) {
                val cw = size.width
                val ch = size.height
                
                // Draw thin base axis line
                drawLine(
                    color = Color(0xFF151928),
                    start = Offset(cw / 2f, 0f),
                    end = Offset(cw / 2f, ch),
                    strokeWidth = 1.dp.toPx()
                )
                
                // Draw dotted gold particle track
                val particleCount = 18
                val spacing = ch / (particleCount - 1)
                
                for (i in 0 until particleCount) {
                    val y = ch - (i * spacing)
                    val particleVal = i.toFloat() / (particleCount - 1)
                    val isFilled = particleVal <= value.coerceIn(0f, 1f)
                    
                    if (isFilled) {
                        // Glowing filled gold/amber particle
                        drawCircle(
                            color = Color(0xFFE5A97E),
                            radius = if (i % 3 == 0) 3.5f.dp.toPx() else 2.5f.dp.toPx(),
                            center = Offset(cw / 2f, y)
                        )
                        // Tiny glow aura on larger nodes
                        if (i % 3 == 0) {
                            drawCircle(
                                color = Color(0xFFE5A97E).copy(alpha = 0.25f),
                                radius = 6.dp.toPx(),
                                center = Offset(cw / 2f, y)
                            )
                        }
                    } else {
                        // Empty/stifled track dot
                        drawCircle(
                            color = Color(0xFF1E243A),
                            radius = 2.dp.toPx(),
                            center = Offset(cw / 2f, y)
                        )
                    }
                }
            }
            
            // Neon glowing circular slider Thumb Node sliding on the dotted axis
            val pctOffset = -((160 * value.coerceIn(0f, 1f)) - 80).dp
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = pctOffset)
                    .size(28.dp),
                contentAlignment = Alignment.Center
            ) {
                // Intensive outer radial golden glow blur/halo
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val r = size.width / 2f
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFFF4C39F).copy(alpha = 0.82f), Color(0xFFE5A97E).copy(alpha = 0.35f), Color.Transparent),
                            center = Offset(r, r),
                            radius = r
                        ),
                        radius = r,
                        center = Offset(r, r)
                    )
                }
                
                // White glowing core light node
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(Color.White, shape = CircleShape)
                        .border(1.5.dp, Color(0xFFE5A97E), shape = CircleShape)
                )
            }
        }
    }
}

// Individual mixer slider node columns
@Composable
fun MixerColumn(
    label: String,
    volume: Float,
    icon: ImageVector,
    onVolumeChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.width(76.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(
                    if (volume > 0.05f) Color(0xFF131427) else Color(0xFF0D0E1C),
                    shape = CircleShape
                )
                .border(
                    0.5.dp,
                    if (volume > 0.05f) SoftNeonBlue else Color.Transparent,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (volume > 0.05f) SoftNeonBlue else TextMuted,
                modifier = Modifier.size(18.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(10.dp))

        // Custom Capsule Slider
        SleekVerticalSlider(
            value = volume,
            onValueChange = onVolumeChange,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = label,
            fontSize = 11.sp,
            color = if (volume > 0.05f) TextLight else TextMuted,
            fontWeight = if (volume > 0.05f) FontWeight.Bold else FontWeight.Normal,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        
        Text(
            text = "${(volume * 100).toInt()}%",
            fontSize = 10.sp,
            color = if (volume > 0.05f) SoftNeonBlue else TextMuted
        )
    }
}

// Ultimate interactive sound grid layouts card
@Composable
fun SoundPadCard(
    title: String,
    sub: String,
    icon: ImageVector,
    isEnabled: Boolean,
    onToggle: () -> Unit,
    volume: Float,
    onVolChange: (Float) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(115.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = 1.dp,
                color = if (isEnabled) SoftNeonBlue else TextMuted.copy(alpha = 0.12f),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onToggle),
        colors = CardDefaults.cardColors(
            containerColor = if (isEnabled) Color(0xFF101229) else Color(0xFF080918)
        )
    ) {
        val infiniteTransition = rememberInfiniteTransition(label = "Visualizers")
        val pulseH by infiniteTransition.animateFloat(
            initialValue = 2.dp.value,
            targetValue = 12.dp.value,
            animationSpec = infiniteRepeatable(
                animation = tween(400, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "PadBars"
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isEnabled) SoftNeonBlue else TextMuted,
                    modifier = Modifier.size(20.dp)
                )
                
                if (isEnabled) {
                    // Small active audio equalizer frequency animation lines
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalAlignment = Alignment.Bottom,
                        modifier = Modifier.height(16.dp)
                    ) {
                        listOf(0.4f, 1.0f, 0.7f).forEach { scale ->
                            Box(
                                modifier = Modifier
                                    .width(2.dp)
                                    .height((pulseH * scale).dp)
                                    .background(SoftNeonBlue, shape = RoundedCornerShape(1.dp))
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(TextMuted.copy(alpha = 0.4f), shape = CircleShape)
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isEnabled) Color.White else TextLight,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = sub,
                    fontSize = 9.5.sp,
                    color = TextMutedPurple,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Slim quick inline volume slider for instant access in Library Grid
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            val pct = (offset.x / size.width).coerceIn(0f, 1f)
                            onVolChange(pct)
                        }
                    }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(volume.coerceIn(0f, 1f))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(GradientOrange, GradientPink)
                            )
                        )
                )
            }
        }
    }
}

// Synchronized mockup smartphone viewport recreating layout from reference design
@Composable
fun DeviceMixerMockup(
    volumes: Map<String, Float>,
    isPlaying: Boolean,
    onTogglePlay: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(135.dp)
            .height(230.dp)
            .clip(RoundedCornerShape(22.dp))
            .border(2.5.dp, Color(0xFF1E2138), RoundedCornerShape(22.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF030409))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Dynamic island / Camera capsule notch mockup
            Box(
                modifier = Modifier
                    .width(36.dp)
                    .height(6.dp)
                    .background(Color.Black, shape = CircleShape)
            )

            // Live viewport sliders replication corresponding to actual volume sliders state
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 14.dp, horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.Bottom
            ) {
                val mockTracks = listOf(
                    AmbientAudioService.TRACK_BROWN_NOISE,
                    AmbientAudioService.TRACK_RAIN,
                    AmbientAudioService.TRACK_AMBIENT_PAD,
                    AmbientAudioService.TRACK_FIREPLACE
                )

                mockTracks.forEach { trackKey ->
                    val volumeValue = volumes[trackKey] ?: 0f
                    
                    // Small capsule mockup slider
                    Box(
                        modifier = Modifier
                            .height(90.dp)
                            .width(8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF080918)),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight(volumeValue.coerceIn(0f, 1f))
                                .width(8.dp)
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(GradientPurple, GradientOrange)
                                    )
                                )
                        )
                    }
                }
            }

            // Tiny phone media controls mockup
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(if (isPlaying) GradientPink else Color(0xFF16192E), shape = CircleShape)
                        .clickable { onTogglePlay() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = if (isPlaying) AmmoBlack else Color.White,
                        modifier = Modifier.size(11.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AmbientSleepMainScreen(
    viewModel: AmbientViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isPlaying by viewModel.isPlaying.collectAsState()
    val volumes by viewModel.volumes.collectAsState()
    val binauralType by viewModel.binauralType.collectAsState()
    val secondsRemaining by viewModel.timerSecondsRemaining.collectAsState()
    val premiumUntil by viewModel.premiumUnlockedUntil.collectAsState()

    // Local-first persistence parameters on client-side
    val sharedPrefs = context.getSharedPreferences("ambient_sleep_prefs", Context.MODE_PRIVATE)
    var onboardingCompleted by remember { mutableStateOf(sharedPrefs.getBoolean("onboarding_done", false)) }
    var activeTab by remember { mutableStateOf(0) } // 0 = Home, 1 = Mixer, 2 = Extra/Premium

    val cosmicGradient = Brush.verticalGradient(
        colors = listOf(
            CosmicDeepBlack,
            CosmicMidnightNavy,
            Color.Black
        )
    )

    // 1. Onboarding Flow Checklist Implementation
    if (!onboardingCompleted) {
        OnboardingOverlay(
            onComplete = { goal, hours ->
                sharedPrefs.edit().putBoolean("onboarding_done", true).apply()
                onboardingCompleted = true
                viewModel.setSleepTimer(hours * 60)
                viewModel.setPlaying(true)
                Toast.makeText(context, context.getString(com.example.R.string.onboarding_welcome_toast, goal), Toast.LENGTH_LONG).show()
            }
        )
    } else {
        Scaffold(
            modifier = modifier
                .fillMaxSize()
                .background(cosmicGradient),
            bottomBar = {
                Column(
                    modifier = Modifier
                        .background(Color.Black)
                        .navigationBarsPadding()
                ) {
                    // Luxurious material design 3 NavigationBar
                    NavigationBar(
                        containerColor = CosmicDeepBlack,
                        tonalElevation = 10.dp,
                        modifier = Modifier.height(72.dp)
                    ) {
                        NavigationBarItem(
                            selected = activeTab == 0,
                            onClick = { activeTab = 0 },
                            label = { Text(stringResource(id = com.example.R.string.nav_home), fontSize = 10.sp) },
                            icon = { Icon(Icons.Default.Home, contentDescription = stringResource(id = com.example.R.string.nav_home)) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = AmmoBlack,
                                selectedTextColor = SoftNeonBlue,
                                indicatorColor = SoftNeonBlue,
                                unselectedIconColor = TextMuted,
                                unselectedTextColor = TextMuted
                            )
                        )
                        NavigationBarItem(
                            selected = activeTab == 1,
                            onClick = { activeTab = 1 },
                            label = { Text(stringResource(id = com.example.R.string.nav_mixer), fontSize = 10.sp) },
                            icon = { Icon(Icons.Default.Tune, contentDescription = stringResource(id = com.example.R.string.nav_mixer)) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = AmmoBlack,
                                selectedTextColor = SoftNeonBlue,
                                indicatorColor = SoftNeonBlue,
                                unselectedIconColor = TextMuted,
                                unselectedTextColor = TextMuted
                            )
                        )
                        NavigationBarItem(
                            selected = false,
                            enabled = false,
                            onClick = {},
                            label = { Text(stringResource(id = com.example.R.string.nav_videos), fontSize = 10.sp) },
                            icon = { Icon(Icons.Default.Star, contentDescription = stringResource(id = com.example.R.string.nav_videos)) },
                            colors = NavigationBarItemDefaults.colors(
                                disabledIconColor = TextMuted.copy(alpha = 0.35f),
                                disabledTextColor = TextMuted.copy(alpha = 0.35f)
                            )
                        )
                    }

                    // Adaptive Banner
                    AdmobBanner(modifier = Modifier.fillMaxWidth())
                }
            },
            containerColor = Color.Transparent
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .background(cosmicGradient)
                    .statusBarsPadding()
            ) {
                // High-fidelity branding Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    AmbientSleepLogo()

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // DOWNLOAD NOW Golden pill button po prawej z mockup'u
                        Button(
                            onClick = {
                                 try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=pl.cclite.app&hl=pl"))
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, context.getString(com.example.R.string.toast_no_link_open), Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            contentPadding = PaddingValues(),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .height(30.dp)
                                .width(115.dp)
                                .border(
                                    BorderStroke(0.5.dp, GoldAccent.copy(alpha = 0.5f)),
                                    RoundedCornerShape(12.dp)
                                )
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(GradientOrange, GradientPink)
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                )
                        ) {
                            Text(
                                text = stringResource(id = com.example.R.string.download_now),
                                color = AmmoBlack,
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.2.sp
                            )
                        }

                        // Sleep Active timer flag
                        if (secondsRemaining > 0) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF13152B)),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(0.5.dp, SoftNeonBlue.copy(alpha = 0.3f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Timer, contentDescription = null, tint = SoftNeonBlue, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = formatSeconds(secondsRemaining),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SoftNeonBlue
                                    )
                                }
                            }
                        }
                    }
                }

                // Screens Router based on Navigation tabs
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                ) {
                    when (activeTab) {
                        0 -> HomeScreen(viewModel, isPlaying, volumes, premiumUntil)
                        1 -> MixerScreen(viewModel, volumes, isPlaying, secondsRemaining)
                        2 -> ExtraScreen(context, premiumUntil, viewModel, binauralType)
                    }
                }
            }
        }
    }
}

// Custom high-fidelity Premium Banner opening Radio CC stream application on Play Store
@Composable
fun RadioCCBanner() {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=pl.cclite.app&hl=pl"))
                    context.startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(context, context.getString(com.example.R.string.toast_no_link_open), Toast.LENGTH_SHORT).show()
                }
            },
        colors = CardDefaults.cardColors(containerColor = AmmoBlack),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.5.dp, GoldAccent.copy(alpha = 0.8f))
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Brush.radialGradient(colors = listOf(GoldAccent.copy(alpha = 0.2f), Color.Transparent)))
                    .border(1.dp, GoldAccent.copy(alpha = 0.4f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Radio,
                    contentDescription = null,
                    tint = GoldAccent,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(id = com.example.R.string.listen_radio_cc),
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldAccent,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = stringResource(id = com.example.R.string.listen_radio_cc_desc),
                    fontSize = 9.5.sp,
                    color = TextLight.copy(alpha = 0.85f),
                    lineHeight = 13.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(GoldAccent)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = AmmoBlack,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

// 2. Home Tab: Slogan segment + Discover summary + beautiful sound grid
@Composable
fun HomeScreen(
    viewModel: AmbientViewModel,
    isPlaying: Boolean,
    volumes: Map<String, Float>,
    premiumUntil: Long
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val isPremiumActive = viewModel.isPremiumActive()
    val binauralType by viewModel.binauralType.collectAsState()
    
    val soundItems = listOf(
        SoundPadItem(AmbientAudioService.TRACK_BROWN_NOISE, stringResource(id = com.example.R.string.sound_white_noise_title), stringResource(id = com.example.R.string.sound_white_noise_desc), Icons.Default.GraphicEq),
        SoundPadItem(AmbientAudioService.TRACK_RAIN, stringResource(id = com.example.R.string.sound_rain_title), stringResource(id = com.example.R.string.sound_rain_desc), Icons.Default.WaterDrop),
        SoundPadItem(AmbientAudioService.TRACK_FIREPLACE, stringResource(id = com.example.R.string.sound_fireplace_title), stringResource(id = com.example.R.string.sound_fireplace_desc), Icons.Default.LocalFireDepartment),
        SoundPadItem(AmbientAudioService.TRACK_AMBIENT_PAD, stringResource(id = com.example.R.string.sound_pad_title), stringResource(id = com.example.R.string.sound_pad_desc), Icons.Default.Waves)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SleepHeroSection(isPlaying = isPlaying, onTogglePlay = { viewModel.togglePlay() })
        
        Spacer(modifier = Modifier.height(24.dp))

        // Polish unique title and info section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = com.example.R.string.discover_title),
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Bold,
                color = GoldAccent,
                letterSpacing = 1.2.sp
            )
            Icon(
                imageVector = Icons.Default.Waves,
                contentDescription = null,
                tint = GoldMuted,
                modifier = Modifier.size(16.dp)
            )
        }
        
        Text(
            text = stringResource(id = com.example.R.string.discover_info),
            fontSize = 10.sp,
            color = TextMuted,
            lineHeight = 14.sp,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(top = 4.dp, bottom = 14.dp)
        )

        // 2-Column Row-paired layouts for sounds (prevents nested scrolling conflicts)
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Row 1: Deep White Noise & Rainfall on Glass
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    val s = soundItems[0]
                    val vol = volumes[s.id] ?: 0f
                    val active = vol > 0.05f
                    SoundPadCard(
                        title = s.title,
                        sub = s.sub,
                        icon = s.icon,
                        isEnabled = active,
                        volume = vol,
                        onToggle = {
                            if (active) {
                                viewModel.setChannelVolume(s.id, 0f)
                            } else {
                                viewModel.setChannelVolume(s.id, 0.45f)
                                viewModel.setPlaying(true)
                            }
                        },
                        onVolChange = { viewModel.setChannelVolume(s.id, it) }
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    val s = soundItems[1]
                    val vol = volumes[s.id] ?: 0f
                    val active = vol > 0.05f
                    SoundPadCard(
                        title = s.title,
                        sub = s.sub,
                        icon = s.icon,
                        isEnabled = active,
                        volume = vol,
                        onToggle = {
                            if (active) {
                                viewModel.setChannelVolume(s.id, 0f)
                            } else {
                                viewModel.setChannelVolume(s.id, 0.45f)
                                viewModel.setPlaying(true)
                            }
                        },
                        onVolChange = { viewModel.setChannelVolume(s.id, it) }
                    )
                }
            }

            // Row 2: Cracking Fireplace & Calming Ambient Pad
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    val s = soundItems[2]
                    val vol = volumes[s.id] ?: 0f
                    val active = vol > 0.05f
                    SoundPadCard(
                        title = s.title,
                        sub = s.sub,
                        icon = s.icon,
                        isEnabled = active,
                        volume = vol,
                        onToggle = {
                            if (active) {
                                viewModel.setChannelVolume(s.id, 0f)
                            } else {
                                viewModel.setChannelVolume(s.id, 0.45f)
                                viewModel.setPlaying(true)
                            }
                        },
                        onVolChange = { viewModel.setChannelVolume(s.id, it) }
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    val s = soundItems[3]
                    val vol = volumes[s.id] ?: 0f
                    val active = vol > 0.05f
                    SoundPadCard(
                        title = s.title,
                        sub = s.sub,
                        icon = s.icon,
                        isEnabled = active,
                        volume = vol,
                        onToggle = {
                            if (active) {
                                viewModel.setChannelVolume(s.id, 0f)
                            } else {
                                viewModel.setChannelVolume(s.id, 0.45f)
                                viewModel.setPlaying(true)
                            }
                        },
                        onVolChange = { viewModel.setChannelVolume(s.id, it) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Card 5: Binaural Delta Waves - Custom Wide Premium Layout inside discovery
        val isBinDelta = binauralType == BinauralType.DELTA
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(
                    width = 1.dp,
                    color = if (isBinDelta) GoldAccent else GoldMuted.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(16.dp)
                )
                .clickable {
                    if (!isPremiumActive) {
                        Toast.makeText(context, context.getString(com.example.R.string.binaural_premium_locked), Toast.LENGTH_SHORT).show()
                    } else {
                        if (isBinDelta) {
                            viewModel.selectBinauralWaves(BinauralType.NONE)
                        } else {
                            viewModel.selectBinauralWaves(BinauralType.DELTA)
                            viewModel.setPlaying(true)
                        }
                    }
                },
            colors = CardDefaults.cardColors(
                containerColor = if (isBinDelta) Color(0xFF14100E) else Color(0xFF070B15)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(if (isBinDelta) Color(0xFF2E2015) else Color(0xFF131525), shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Hearing,
                            contentDescription = null,
                            tint = if (isBinDelta) GoldAccent else GoldMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = stringResource(id = com.example.R.string.binaural_title_delta),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isBinDelta) GoldAccent else Color.White
                        )
                        Text(
                            text = stringResource(id = com.example.R.string.binaural_desc_delta),
                            fontSize = 9.5.sp,
                            color = TextMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                if (isBinDelta) {
                    Text(
                        text = "DELTA 3Hz",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = GoldAccent,
                        modifier = Modifier
                            .background(Color(0xFF28190B), shape = RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                } else {
                    Icon(
                        imageVector = if (isPremiumActive) Icons.Default.PlayArrow else Icons.Default.Lock,
                        tint = if (isPremiumActive) GoldAccent else Color(0xFFE5A97E),
                        contentDescription = "Premium lock active",
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Radio CC premium announcement Banner
        RadioCCBanner()
        
        Spacer(modifier = Modifier.height(30.dp))
    }
}

// 3. Library Tab: Gorgeous list containing all sounds
@Composable
fun LibraryScreen(
    viewModel: AmbientViewModel,
    volumes: Map<String, Float>,
    premiumUntil: Long
) {
    val context = LocalContext.current
    val isPremiumActive = viewModel.isPremiumActive()

    val soundItems = listOf(
        SoundPadItem(AmbientAudioService.TRACK_BROWN_NOISE, stringResource(id = com.example.R.string.sound_white_noise_title), stringResource(id = com.example.R.string.sound_white_noise_desc), Icons.Default.GraphicEq),
        SoundPadItem(AmbientAudioService.TRACK_RAIN, stringResource(id = com.example.R.string.sound_rain_title), stringResource(id = com.example.R.string.sound_rain_desc), Icons.Default.WaterDrop),
        SoundPadItem(AmbientAudioService.TRACK_FIREPLACE, stringResource(id = com.example.R.string.sound_fireplace_title), stringResource(id = com.example.R.string.sound_fireplace_desc), Icons.Default.LocalFireDepartment),
        SoundPadItem(AmbientAudioService.TRACK_AMBIENT_PAD, stringResource(id = com.example.R.string.sound_pad_title), stringResource(id = com.example.R.string.sound_pad_desc), Icons.Default.Waves)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        Text(
            text = stringResource(id = com.example.R.string.discover_title),
            fontSize = 14.sp,
            fontWeight = FontWeight.Black,
            color = SoftNeonBlue,
            letterSpacing = 1.2.sp
        )
        Text(
            text = stringResource(id = com.example.R.string.discover_info),
            fontSize = 10.5.sp,
            color = TextMutedPurple,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        // Custom Glassmorphic Card grid (2 Columns)
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(soundItems) { sound ->
                val vol = volumes[sound.id] ?: 0f
                val active = vol > 0.05f

                SoundPadCard(
                    title = sound.title,
                    sub = sound.sub,
                    icon = sound.icon,
                    isEnabled = active,
                    volume = vol,
                    onToggle = {
                        if (active) {
                            viewModel.setChannelVolume(sound.id, 0f)
                        } else {
                            viewModel.setChannelVolume(sound.id, 0.45f)
                            viewModel.setPlaying(true)
                        }
                    },
                    onVolChange = { viewModel.setChannelVolume(sound.id, it) }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Large Premium Wide Card
        val binauralType by viewModel.binauralType.collectAsState()
        val hasBin = binauralType != BinauralType.NONE

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(95.dp)
                .clip(RoundedCornerShape(18.dp))
                .border(
                    width = 1.dp,
                    color = if (hasBin) SoftNeonPurple else SoftNeonPurple.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(18.dp)
                )
                .clickable {
                    if (!isPremiumActive) {
                        Toast.makeText(context, context.getString(com.example.R.string.binaural_premium_locked), Toast.LENGTH_SHORT).show()
                    } else {
                        if (hasBin) {
                            viewModel.selectBinauralWaves(BinauralType.NONE)
                        } else {
                            viewModel.selectBinauralWaves(BinauralType.DELTA)
                            viewModel.setPlaying(true)
                        }
                    }
                },
            colors = CardDefaults.cardColors(
                containerColor = if (hasBin) Color(0xFF161026) else Color(0xFF0C071C)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(if (hasBin) Color(0xFF281C47) else Color(0xFF140D2B), shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Hearing,
                            contentDescription = null,
                            tint = if (hasBin) SoftNeonPurple else TextMutedPurple,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = stringResource(id = com.example.R.string.binaural_title_delta),
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (hasBin) SoftNeonPurple else Color.White
                        )
                        Text(
                            text = stringResource(id = com.example.R.string.binaural_desc_delta),
                            fontSize = 10.sp,
                            color = TextMutedPurple
                        )
                    }
                }

                if (hasBin) {
                    Text(
                        text = if (binauralType == BinauralType.DELTA) "DELTA 3Hz" else "THETA 6Hz",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = SoftNeonPurple
                    )
                } else {
                    Icon(
                        imageVector = if (isPremiumActive) Icons.Default.PlayArrow else Icons.Default.Lock,
                        tint = if (isPremiumActive) SoftNeonPurple else Color(0xFFFF6B9D),
                        contentDescription = "Premium Mode locked",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

// 4. Mixer Screen: Vertical capsule sliders matching design aesthetic side-by-side with device mockups
@Composable
fun MixerScreen(
    viewModel: AmbientViewModel,
    volumes: Map<String, Float>,
    isPlaying: Boolean,
    secondsRemaining: Int
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "YOUR PERSONAL SOUND MIXER",
            fontSize = 15.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White,
            letterSpacing = 1.6.sp,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Compose your ideal bedtime soundscapes with premium controllers",
            fontSize = 10.sp,
            color = TextMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp, bottom = 18.dp)
        )

        // Panel Miksera - Dark card with luxury glowing golden neon outline
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.2.dp,
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFFF4C39F), Color(0xFFC5A059).copy(alpha = 0.3f), Color.Transparent),
                        radius = 280.dp.value
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .border(1.dp, GoldAccent.copy(alpha = 0.25f), RoundedCornerShape(24.dp)),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF070B15)),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 18.dp, horizontal = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Sliders Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    MixerColumn(
                        label = "White Noise",
                        volume = volumes[AmbientAudioService.TRACK_BROWN_NOISE] ?: 0.4f,
                        icon = Icons.Default.GraphicEq,
                        onVolumeChange = { viewModel.setChannelVolume(AmbientAudioService.TRACK_BROWN_NOISE, it) },
                        modifier = Modifier.weight(1f)
                    )
                    MixerColumn(
                        label = "Rain",
                        volume = volumes[AmbientAudioService.TRACK_RAIN] ?: 0.4f,
                        icon = Icons.Default.WaterDrop,
                        onVolumeChange = { viewModel.setChannelVolume(AmbientAudioService.TRACK_RAIN, it) },
                        modifier = Modifier.weight(1f)
                    )
                    MixerColumn(
                        label = "Ambient Music",
                        volume = volumes[AmbientAudioService.TRACK_AMBIENT_PAD] ?: 0.5f,
                        icon = Icons.Default.Waves,
                        onVolumeChange = { viewModel.setChannelVolume(AmbientAudioService.TRACK_AMBIENT_PAD, it) },
                        modifier = Modifier.weight(1f)
                    )
                    MixerColumn(
                        label = "Fire",
                        volume = volumes[AmbientAudioService.TRACK_FIREPLACE] ?: 0.3f,
                        icon = Icons.Default.LocalFireDepartment,
                        onVolumeChange = { viewModel.setChannelVolume(AmbientAudioService.TRACK_FIREPLACE, it) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Timer and Playback Control Cockpit Area
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(0.5.dp, GoldAccent.copy(alpha = 0.15f), RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF070B15).copy(alpha = 0.62f)),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Play/Pause button
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { viewModel.togglePlay() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isPlaying) GoldAccent else Color(0xFF151928)
                            ),
                            shape = CircleShape,
                            modifier = Modifier.size(46.dp),
                            contentPadding = PaddingValues()
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = if (isPlaying) AmmoBlack else Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        
                        Column {
                            Text(
                                text = if (isPlaying) "PLAYING" else "STANDBY",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isPlaying) GoldAccent else TextMuted
                            )
                            val activeCount = volumes.values.count { it > 0.05f }
                            Text(
                                text = if (isPlaying) "$activeCount Sounds Active" else "Ready to play",
                                fontSize = 9.5.sp,
                                color = TextMuted
                            )
                        }
                    }

                    // Countdown representation if active
                    if (secondsRemaining > 0) {
                        val mins = secondsRemaining / 60
                        val secs = secondsRemaining % 60
                        val fmt = String.format("%02d:%02d", mins, secs)
                        
                        Text(
                            text = fmt,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = GoldAccent,
                            modifier = Modifier
                                .background(Color(0xFF28190B), shape = RoundedCornerShape(10.dp))
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    } else {
                        Text(
                            text = "Timer Off",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMuted,
                            modifier = Modifier
                                .background(Color(0xFF151928), shape = RoundedCornerShape(10.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Fast Sleep timers selectors (Wył | 15m | 30m | 45m | 60m)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val timerOptions = listOf(
                        "Off" to 0,
                        "15m" to 15,
                        "30m" to 30,
                        "45m" to 45,
                        "60m" to 60
                    )
                    
                    timerOptions.forEach { (lbl, minutes) ->
                        val currentActive = if (minutes == 0) secondsRemaining == 0 else {
                            val targetSecs = minutes * 60
                            Math.abs(secondsRemaining - targetSecs) < 15
                        }
                        
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(32.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (currentActive) GoldAccent else Color(0xFF121523)
                                )
                                .border(
                                    width = 0.5.dp,
                                    color = if (currentActive) Color.Transparent else GoldMuted.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { 
                                    if (minutes == 0) {
                                        viewModel.stopSleepTimer()
                                    } else {
                                        viewModel.setSleepTimer(minutes)
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = lbl,
                                color = if (currentActive) AmmoBlack else Color.White,
                                fontSize = 10.5.sp,
                                fontWeight = if (currentActive) FontWeight.Black else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(30.dp))
    }
}

// 5. Immersive Full Screen Player Tab: animated cosmic frequency rings, and sleep timer capsules
@Composable
fun PlayerScreen(
    viewModel: AmbientViewModel,
    isPlaying: Boolean,
    volumes: Map<String, Float>,
    binauralType: BinauralType,
    secondsRemaining: Int
) {
    val infiniteTransition = rememberInfiniteTransition(label = "RadarBreath")
    
    // Smooth breathing cosmic sphere modifier animation scale
    val breathingRate by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "MoonBreath"
    )

    // Twinkling glowing background waves contours
    val radarPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "RadarConcentric"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(id = com.example.R.string.player_title),
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Bold,
            color = SoftNeonBlue,
            letterSpacing = 2.sp
        )

        // Immense Beautiful Animated Soundwave & Moon breathing cosmic node representation
        Box(
            modifier = Modifier
                .size(240.dp)
                .graphicsLayer {
                    scaleX = if (isPlaying) breathingRate else 1.0f
                    scaleY = if (isPlaying) breathingRate else 1.0f
                },
            contentAlignment = Alignment.Center
        ) {
            // Live concentric soundwave lines emitted from glowing sphere
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val baseR = 75.dp.toPx()
                
                if (isPlaying) {
                    for (i in 1..3) {
                        val pulseFactor = ((radarPhase + i * 120f) % 360f) / 360f
                        val waveRadius = baseR + pulseFactor * 46.dp.toPx()
                        drawCircle(
                            color = SoftNeonBlue.copy(alpha = 0.38f * (1f - pulseFactor)),
                            radius = waveRadius,
                            center = center,
                            style = Stroke(width = 1.5.dp.toPx())
                        )
                    }
                }
            }

            // Central Glowing lunar sphere representing the bedroom moon light matching design mockup!
            Box(
                modifier = Modifier
                    .size(135.dp)
                    .drawBehind {
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val r = size.minDimension / 2f
                        
                        // Radiant backlight beam
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0xFFFFF7AC), Color(0xFFFFB236).copy(alpha = 0.3f), Color.Transparent),
                                center = center,
                                radius = r * 1.5f
                            ),
                            radius = r * 1.5f,
                            center = center
                        )

                        // Outer moon shape
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(Color.White, Color(0xFFFFFEE9), Color(0xFFFFC04D)),
                                center = center - Offset(6.dp.toPx(), 6.dp.toPx()),
                                radius = r
                            ),
                            radius = r,
                            center = center
                        )

                        // Crater textures
                        drawCircle(Color(0xFFDC9E1B).copy(alpha = 0.18f), r * 0.22f, center + Offset(-24.dp.toPx(), -20.dp.toPx()))
                        drawCircle(Color(0xFFDC9E1B).copy(alpha = 0.14f), r * 0.25f, center + Offset(22.dp.toPx(), -8.dp.toPx()))
                        drawCircle(Color(0xFFDC9E1B).copy(alpha = 0.18f), r * 0.20f, center + Offset(-10.dp.toPx(), 26.dp.toPx()))
                    }
            )
        }

        // Active playing channels list
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val activeChannels = mutableListOf<String>()
            if ((volumes[AmbientAudioService.TRACK_BROWN_NOISE] ?: 0f) > 0.05f) activeChannels.add(stringResource(id = com.example.R.string.sound_white_noise_title))
            if ((volumes[AmbientAudioService.TRACK_RAIN] ?: 0f) > 0.05f) activeChannels.add(stringResource(id = com.example.R.string.sound_rain_title))
            if ((volumes[AmbientAudioService.TRACK_AMBIENT_PAD] ?: 0f) > 0.05f) activeChannels.add(stringResource(id = com.example.R.string.sound_pad_title))
            if ((volumes[AmbientAudioService.TRACK_FIREPLACE] ?: 0f) > 0.05f) activeChannels.add(stringResource(id = com.example.R.string.sound_fireplace_title))
            if (binauralType != BinauralType.NONE) activeChannels.add(stringResource(id = com.example.R.string.binaural_title_delta))

            val tracksText = if (activeChannels.isEmpty()) {
                stringResource(id = com.example.R.string.player_no_tracks)
            } else {
                activeChannels.joinToString(" + ")
            }

            Text(
                text = if (isPlaying) stringResource(id = com.example.R.string.player_active_ambient) else stringResource(id = com.example.R.string.player_standby_ambient),
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = SoftNeonBlue,
                letterSpacing = 1.sp
            )
            Text(
                text = tracksText,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }

        // Sleep Timer Custom Capsules Control board (Wył. | 15 m | 30 m | 45 m | 60 m)
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(id = com.example.R.string.player_timer_title),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = TextMutedPurple,
                letterSpacing = 0.8.sp,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val timerOptions = listOf(
                    stringResource(id = com.example.R.string.player_timer_off) to 0,
                    "15 m" to 15,
                    "30 m" to 30,
                    "45 m" to 45,
                    "60 m" to 60
                )

                timerOptions.forEach { (lbl, min) ->
                    val isSelected = if (min == 0) secondsRemaining <= 0 else {
                        val matchingSec = min * 60
                        secondsRemaining in (matchingSec - 5)..(matchingSec + 5)
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) SoftNeonBlue else Color(0xFF0F1122))
                            .border(
                                0.5.dp,
                                if (isSelected) SoftNeonBlue else TextMuted.copy(alpha = 0.15f),
                                RoundedCornerShape(10.dp)
                            )
                            .clickable {
                                if (min == 0) {
                                    viewModel.stopSleepTimer()
                                } else {
                                    viewModel.setSleepTimer(min)
                                }
                            }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = lbl,
                            color = if (isSelected) AmmoBlack else TextLight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }


    }
}

// Extra YouTube & CCN guidelines tab
@Composable
fun ExtraScreen(
    context: Context,
    premiumUntil: Long,
    viewModel: AmbientViewModel,
    binauralType: BinauralType
) {
    val scrollState = rememberScrollState()
    val isPremiumUnlocked = System.currentTimeMillis() < premiumUntil

    val mockYouTubeSessions = listOf(
        YouTubeSession(stringResource(id = com.example.R.string.yt_session_1_title), "https://youtube.com/results?search_query=10+hours+brown+noise+relax+deep", stringResource(id = com.example.R.string.yt_session_1_desc), Icons.Default.GraphicEq),
        YouTubeSession(stringResource(id = com.example.R.string.yt_session_2_title), "https://youtube.com/results?search_query=10+hours+rain+on+window+cozy", stringResource(id = com.example.R.string.yt_session_2_desc), Icons.Default.WaterDrop),
        YouTubeSession(stringResource(id = com.example.R.string.yt_session_3_title), "https://youtube.com/results?search_query=10+hours+fireplace+crackling+cozy", stringResource(id = com.example.R.string.yt_session_3_desc), Icons.Default.LocalFireDepartment),
        YouTubeSession(stringResource(id = com.example.R.string.yt_session_4_title), "https://youtube.com/results?search_query=10+hours+solfeggio+ambient+meditation", stringResource(id = com.example.R.string.yt_session_4_desc), Icons.Default.Waves)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 6.dp)
    ) {
        Text(
            text = stringResource(id = com.example.R.string.binaural_card_title),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = SoftNeonBlue,
            letterSpacing = 1.2.sp
        )
        Text(
            text = stringResource(id = com.example.R.string.binaural_card_desc),
            fontSize = 10.5.sp,
            color = TextMutedPurple,
            modifier = Modifier.padding(top = 4.dp, bottom = 10.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0C071C)),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, if (isPremiumUnlocked) SoftNeonPurple.copy(alpha = 0.5f) else Color(0xFFFF6B9D).copy(alpha = 0.2f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                if (!isPremiumUnlocked) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "locked",
                            tint = Color(0xFFFF6B9D),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(id = com.example.R.string.binaural_lock_header),
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextLight
                            )
                            Text(
                                text = stringResource(id = com.example.R.string.binaural_lock_desc),
                                fontSize = 10.sp,
                                color = TextMutedPurple
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    val contextActivity = context as? Activity
                    Button(
                        onClick = {
                            if (contextActivity != null) {
                                Toast.makeText(context, context.getString(com.example.R.string.loading_ad), Toast.LENGTH_SHORT).show()
                                AdManager.showRewardedInterstitial(
                                    activity = contextActivity,
                                    onRewardEarned = {
                                        viewModel.unlockPremiumFor24Hours()
                                        Toast.makeText(context, context.getString(com.example.R.string.ad_reward_earned), Toast.LENGTH_LONG).show()
                                    },
                                    onDismissedOrError = {
                                        // keeping correct flow state on dismissed ads
                                    }
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(GradientOrange, GradientPink)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AdsClick, contentDescription = null, tint = AmmoBlack, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = stringResource(id = com.example.R.string.binaural_btn_unlock),
                                color = AmmoBlack,
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp
                            )
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LockOpen,
                            contentDescription = "unlocked",
                            tint = SoftNeonPurple,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = stringResource(id = com.example.R.string.binaural_active_header),
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = SoftNeonPurple
                            )
                            val dateStr = SimpleDateFormat("dd.MM HH:mm", Locale.getDefault()).format(Date(premiumUntil))
                            Text(
                                text = stringResource(id = com.example.R.string.binaural_active_until, dateStr),
                                fontSize = 10.5.sp,
                                color = TextMutedPurple
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val optionDeltaSelected = binauralType == BinauralType.DELTA
                        val optionThetaSelected = binauralType == BinauralType.THETA
                        val optionNoneSelected = binauralType == BinauralType.NONE

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (optionNoneSelected) SoftNeonPurple else Color(0xFF161026))
                                .clickable { viewModel.selectBinauralWaves(BinauralType.NONE) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(stringResource(id = com.example.R.string.binaural_btn_off), color = if (optionNoneSelected) AmmoBlack else TextLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Box(
                            modifier = Modifier
                                .weight(1.3f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (optionDeltaSelected) SoftNeonPurple else Color(0xFF161026))
                                .clickable { viewModel.selectBinauralWaves(BinauralType.DELTA) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(stringResource(id = com.example.R.string.binaural_btn_delta), color = if (optionDeltaSelected) AmmoBlack else TextLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Box(
                            modifier = Modifier
                                .weight(1.3f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (optionThetaSelected) SoftNeonPurple else Color(0xFF161026))
                                .clickable { viewModel.selectBinauralWaves(BinauralType.THETA) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(stringResource(id = com.example.R.string.binaural_btn_theta), color = if (optionThetaSelected) AmmoBlack else TextLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = stringResource(id = com.example.R.string.yt_section_title),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = SoftNeonBlue,
            letterSpacing = 1.2.sp
        )
        Text(
            text = stringResource(id = com.example.R.string.yt_section_desc),
            fontSize = 10.sp,
            color = TextMutedPurple,
            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
        )

        // TV Graphic Frame container representing high end YouTube viewport
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(165.dp)
                .clip(RoundedCornerShape(20.dp))
                .clickable { launchYouTubeIntent(context, "https://youtube.com/results?search_query=christian+culture+network+sleep") },
            border = BorderStroke(1.5.dp, SoftNeonBlue.copy(alpha = 0.35f)),
            colors = CardDefaults.cardColors(containerColor = Color.Black)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF030509), Color(0xFF0D0F22))
                        ),
                        size = size
                    )
                    
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(SoftNeonPurple.copy(alpha = 0.12f), Color.Transparent),
                            center = Offset(w / 2f, h / 2f),
                            radius = w * 0.45f
                        ),
                        radius = w * 0.45f,
                        center = Offset(w / 2f, h / 2f)
                    )
                    
                    val path = Path().apply {
                        moveTo(0f, h * 0.72f)
                        quadraticTo(w * 0.3f, h * 0.5f, w * 0.6f, h * 0.75f)
                        quadraticTo(w * 0.85f, h * 0.9f, w, h * 0.6f)
                    }
                    drawPath(path, SoftNeonPurple.copy(alpha = 0.18f), style = Stroke(width = 2.dp.toPx()))
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .background(SoftNeonPurple.copy(alpha = 0.25f), shape = CircleShape)
                                .border(1.5.dp, SoftNeonPurple, shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = SoftNeonPurple,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(stringResource(id = com.example.R.string.youtube_card_sessions), fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = SoftNeonBlue)
                        Text("1080p HD", fontSize = 8.5.sp, fontWeight = FontWeight.Black, color = TextMuted)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Large WATCH ON YOUTUBE CHANNEL action button
        Button(
            onClick = { launchYouTubeIntent(context, "https://youtube.com/@ChristianCultureNetwork") },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            contentPadding = PaddingValues(),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .border(1.dp, SoftNeonBlue.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(GradientOrange, GradientPink)
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = AmmoBlack, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(id = com.example.R.string.yt_btn_watch),
                    color = AmmoBlack,
                    fontWeight = FontWeight.Black,
                    fontSize = 11.5.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        mockYouTubeSessions.forEach { yr ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { launchYouTubeIntent(context, yr.url) },
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0C0D1A)),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, SoftNeonBlue.copy(alpha = 0.08f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(Color(0xFF14172C), shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(yr.icon, contentDescription = null, tint = SoftNeonBlue, modifier = Modifier.size(16.dp))
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(yr.title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextLight, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(yr.description, fontSize = 10.sp, color = TextMuted)
                    }

                    Icon(Icons.Default.OpenInNew, contentDescription = null, tint = SoftNeonBlue.copy(alpha = 0.6f), modifier = Modifier.size(14.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(26.dp))

        // Sabat clause & Legal information
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F0B18)),
            border = BorderStroke(1.dp, SoftNeonPurple.copy(alpha = 0.15f)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = stringResource(id = com.example.R.string.sabbath_policy_title),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SoftNeonPurple,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = stringResource(id = com.example.R.string.sabbath_policy_desc),
                    fontSize = 10.sp,
                    color = TextMuted,
                    lineHeight = 14.sp
                )
                
                Spacer(modifier = Modifier.height(14.dp))
                
                // Centered Slogan Segment
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "“Creating Tranquility: Night after Night”",
                        fontSize = 12.sp,
                        color = GoldAccent,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Legal links grid row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val links = listOf("Legal", "Privacy", "Contact", "Help")
                    links.forEachIndexed { index, name ->
                        Text(
                            text = name,
                            fontSize = 10.5.sp,
                            color = GoldMuted,
                            fontWeight = FontWeight.Normal,
                            modifier = Modifier
                                .clickable {
                                    Toast.makeText(context, "$name policy is loaded locally.", Toast.LENGTH_SHORT).show()
                                }
                                .padding(horizontal = 6.dp)
                        )
                        if (index < links.size - 1) {
                            Text(
                                text = "•",
                                fontSize = 10.sp,
                                color = TextMuted.copy(alpha = 0.3f),
                                modifier = Modifier.padding(horizontal = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                
                Text(
                    text = stringResource(id = com.example.R.string.credits_text),
                    fontSize = 9.sp,
                    color = TextMutedPurple,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

// ==========================================
// HELPER MODELS & UTILITIES
// ==========================================

data class SoundPadItem(
    val id: String,
    val title: String,
    val sub: String,
    val icon: ImageVector
)

data class YouTubeSession(
    val title: String,
    val url: String,
    val description: String,
    val icon: ImageVector
)

fun formatSeconds(total: Int): String {
    val m = total / 60
    val s = total % 60
    return String.format(Locale.getDefault(), "%02d:%02d", m, s)
}

fun launchYouTubeIntent(context: Context, url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, context.getString(com.example.R.string.toast_no_link_open), Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun AdmobBanner(modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            AdView(context).apply {
                adUnitId = "ca-app-pub-3940256099942544/6300978111" // AdMob Test Banner ID
                setAdSize(AdSize.BANNER)
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}
