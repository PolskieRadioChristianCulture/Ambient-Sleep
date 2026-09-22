package com.example

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.example.audio.AudioSynthesizer
import com.example.monetization.AdManager
import com.example.ui.screens.AmbientSleepMainScreen
import com.example.ui.theme.AmmoBlack
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TextMuted
import com.example.viewmodel.AmbientViewModel

class MainActivity : ComponentActivity() {

    private val showContent = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1. Check and generate loop audio tracks as standard WAV programmatically
        AudioSynthesizer.checkAndGenerateAll(applicationContext)

        // 2. Initialize Mobile Ads SDK
        AdManager.initialize(applicationContext)

        // 3. Coordinate App Open Ad display with loading states
        val mainHandler = Handler(Looper.getMainLooper())
        var processed = false

        val proceedToMainScreen = {
            if (!processed) {
                processed = true
                showContent.value = true
            }
        }

        // Load and try to show the App Open ad
        AdManager.loadAppOpenAd(applicationContext) {
            mainHandler.post {
                if (AdManager.appOpenAd != null) {
                    AdManager.showAppOpenAd(this) {
                        proceedToMainScreen()
                    }
                } else {
                    proceedToMainScreen()
                }
            }
        }

        // Secure a 2500ms maximum wait timer for outstanding UX even if offline or network delays occur
        mainHandler.postDelayed({
            proceedToMainScreen()
        }, 2500)

        // Instantiate ViewModel
        val viewModel = ViewModelProvider(this)[AmbientViewModel::class.java]

        setContent {
            MyApplicationTheme {
                if (showContent.value) {
                    AmbientSleepMainScreen(viewModel = viewModel)
                } else {
                    SplashScreen()
                }
            }
        }
    }
}

@Composable
fun SplashScreen() {
    val cosmicBackground = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF030509), // deepest midnight
            Color(0xFF090E1C), // cosmic navy
            Color(0xFF020305)  // absolute space black
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(cosmicBackground),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.MusicNote,
            contentDescription = "Cosmic Music Note Logo",
            tint = GoldAccent,
            modifier = Modifier.size(76.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "AMBIENT SLEEP",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = GoldAccent,
            letterSpacing = 2.sp
        )
        
        Text(
            text = "by Christian Culture Network",
            fontSize = 12.sp,
            color = TextMuted,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(44.dp))

        CircularProgressIndicator(
            color = GoldAccent,
            strokeWidth = 3.dp,
            modifier = Modifier.size(36.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Przygotowywanie sesji snu...",
            fontSize = 11.sp,
            color = TextMuted
        )
    }
}
