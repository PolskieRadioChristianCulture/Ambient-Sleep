package com.example.audio

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.MainActivity
import com.example.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

enum class BinauralType {
    NONE, DELTA, THETA
}

class AmbientAudioService : android.app.Service() {

    private val binder = LocalBinder()

    private val players = mutableMapOf<String, ExoPlayer>()
    private val scope = CoroutineScope(Dispatchers.Main + Job())

    // Player channel identifiers
    companion object {
        const val CHANNEL_ID = "ambient_sleep_fgs_channel"
        const val NOTIFICATION_ID = 557

        const val TRACK_BROWN_NOISE = "brown_noise"
        const val TRACK_RAIN = "rain"
        const val TRACK_FIREPLACE = "fireplace"
        const val TRACK_AMBIENT_PAD = "ambient_pad"
        const val TRACK_BINAURAL = "binaural"

        const val ACTION_TOGGLE_PLAY = "com.example.action.TOGGLE_PLAY"
        const val ACTION_STOP_SERVICE = "com.example.action.STOP_SERVICE"
    }

    // Live States
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _volumes = MutableStateFlow(
        mapOf(
            TRACK_BROWN_NOISE to 0.4f,
            TRACK_RAIN to 0.4f,
            TRACK_FIREPLACE to 0.3f,
            TRACK_AMBIENT_PAD to 0.5f,
            TRACK_BINAURAL to 0.0f
        )
    )
    val volumes: StateFlow<Map<String, Float>> = _volumes.asStateFlow()

    private val _binauralType = MutableStateFlow(BinauralType.NONE)
    val binauralType: StateFlow<BinauralType> = _binauralType.asStateFlow()

    private val _timerSecondsRemaining = MutableStateFlow(-1)
    val timerSecondsRemaining: StateFlow<Int> = _timerSecondsRemaining.asStateFlow()

    private var timerJob: Job? = null
    private var fadeMultiplier = 1.0f

    inner class LocalBinder : Binder() {
        fun getService(): AmbientAudioService = this@AmbientAudioService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        Log.d("AmbientAudioService", "Initializing background audio service...")
        createNotificationChannel()
        initializePlayers()
        startServiceForeground()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Ambient Sleep Sync"
            val descriptionText = "Kontroler odtwarzania dźwięków relaksacyjnych w tle"
            val importance = NotificationManager.IMPORTANCE_LOW // No beep
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                setShowBadge(false)
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun initializePlayers() {
        val tracks = listOf(
            TRACK_BROWN_NOISE to "brown_noise.wav",
            TRACK_RAIN to "rain.wav",
            TRACK_FIREPLACE to "fireplace.wav",
            TRACK_AMBIENT_PAD to "ambient_pad.wav"
        )

        val contextForPlayers = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            createAttributionContext("ambient_sleep_audio")
        } else {
            this
        }

        for ((trackId, fileName) in tracks) {
            try {
                val player = ExoPlayer.Builder(contextForPlayers).build().apply {
                    repeatMode = Player.REPEAT_MODE_ALL
                    volume = 0f // Start silent until played
                }
                val file = AudioSynthesizer.getAudioFile(contextForPlayers, fileName)
                if (file.exists()) {
                    player.setMediaItem(MediaItem.fromUri(Uri.fromFile(file)))
                    player.prepare()
                } else {
                    Log.e("AmbientAudioService", "Audio file $fileName not found!")
                }
                players[trackId] = player
            } catch (e: Exception) {
                Log.e("AmbientAudioService", "Failed to initialize player for $trackId", e)
            }
        }

        // Setup individual Binaural Beat player
        try {
            val binauralPlayer = ExoPlayer.Builder(contextForPlayers).build().apply {
                repeatMode = Player.REPEAT_MODE_ALL
                volume = 0f
            }
            players[TRACK_BINAURAL] = binauralPlayer
        } catch (e: Exception) {
            Log.e("AmbientAudioService", "Failed to initialize binaural player", e)
        }
    }

    private fun startServiceForeground() {
        val notification = buildServiceNotification()
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun buildServiceNotification(): Notification {
        val mainIntent = Intent(this, MainActivity::class.java)
        val mainPendingIntent = PendingIntent.getActivity(
            this, 0, mainIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val toggleIntent = Intent(this, AmbientAudioService::class.java).apply {
            action = ACTION_TOGGLE_PLAY
        }
        val togglePendingIntent = PendingIntent.getService(
            this, 1, toggleIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val stopIntent = Intent(this, AmbientAudioService::class.java).apply {
            action = ACTION_STOP_SERVICE
        }
        val stopPendingIntent = PendingIntent.getService(
            this, 2, stopIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val playPauseText = if (_isPlaying.value) "Wstrzymaj" else "Odtwarzaj"

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Ambient Sleep")
            .setContentText(getNotificationText())
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setColor(0xC5A059) // Deep matte Gold
            .setContentIntent(mainPendingIntent)
            .setOngoing(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(android.R.drawable.ic_media_pause, playPauseText, togglePendingIntent)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Zamknij", stopPendingIntent)
            .build()
    }

    private fun getNotificationText(): String {
        if (!_isPlaying.value) return "Wyciszone sesje snu"
        val activeList = mutableListOf<String>()
        val vols = _volumes.value
        if ((vols[TRACK_BROWN_NOISE] ?: 0f) > 0.05f) activeList.add("Szum")
        if ((vols[TRACK_RAIN] ?: 0f) > 0.05f) activeList.add("Deszcz")
        if ((vols[TRACK_FIREPLACE] ?: 0f) > 0.05f) activeList.add("Kominek")
        if ((vols[TRACK_AMBIENT_PAD] ?: 0f) > 0.05f) activeList.add("Pad")
        if (_binauralType.value != BinauralType.NONE) {
            activeList.add(if (_binauralType.value == BinauralType.DELTA) "Delta" else "Theta")
        }

        return if (activeList.isEmpty()) "Odtwarzanie w tle..." else "Odtwarza: ${activeList.joinToString(", ")}"
    }

    private fun updateNotification() {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, buildServiceNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.action?.let { action ->
            when (action) {
                ACTION_TOGGLE_PLAY -> {
                    setPlaying(!_isPlaying.value)
                }
                ACTION_STOP_SERVICE -> {
                    stopSelfWithGrace()
                }
            }
        }
        return START_NOT_STICKY
    }

    // Global toggle for all sounds
    fun setPlaying(playing: Boolean) {
        _isPlaying.value = playing
        applyAudioStates()
        updateNotification()
    }

    fun setChannelVolume(track: String, volume: Float) {
        if (track == TRACK_BINAURAL) {
            Log.w("AmbientAudioService", "Binaural must be enabled via setBinauralWaves()")
            return
        }
        val updated = _volumes.value.toMutableMap().apply {
            this[track] = volume.coerceIn(0f, 1f)
        }
        _volumes.value = updated
        applyAudioStates()
        updateNotification()
    }

    fun setBinauralWaves(type: BinauralType) {
        _binauralType.value = type
        val binauralPlayer = players[TRACK_BINAURAL] ?: return

        try {
            binauralPlayer.stop()
            if (type != BinauralType.NONE) {
                val fileName = if (type == BinauralType.DELTA) "binaural_delta.wav" else "binaural_theta.wav"
                val file = AudioSynthesizer.getAudioFile(this, fileName)
                if (file.exists()) {
                    binauralPlayer.setMediaItem(MediaItem.fromUri(Uri.fromFile(file)))
                    binauralPlayer.prepare()
                    // Sync play state
                    if (_isPlaying.value) {
                        binauralPlayer.play()
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("AmbientAudioService", "Error toggling binaural $type", e)
        }

        applyAudioStates()
        updateNotification()
    }

    /**
     * Set sleep timer in minutes.
     * Decrements the seconds, and in the final 30 seconds triggers a linear fade out.
     */
    fun setSleepTimer(minutes: Int) {
        timerJob?.cancel()
        fadeMultiplier = 1.0f
        
        if (minutes <= 0) {
            _timerSecondsRemaining.value = -1
            applyAudioStates()
            return
        }

        val totalSeconds = minutes * 60
        _timerSecondsRemaining.value = totalSeconds

        timerJob = scope.launch {
            var remaining = totalSeconds
            while (remaining > 0) {
                delay(1000)
                remaining--
                _timerSecondsRemaining.value = remaining

                // Linear smooth fade-out during final 30 seconds
                if (remaining <= 30) {
                    fadeMultiplier = remaining.toFloat() / 30f
                    applyAudioStates()
                } else {
                    fadeMultiplier = 1.0f
                }
            }

            // Timer expired - Fade accomplished. Stop!
            stopSelfWithGrace()
        }
    }

    fun stopTimer() {
        timerJob?.cancel()
        _timerSecondsRemaining.value = -1
        fadeMultiplier = 1.0f
        applyAudioStates()
    }

    /**
     * Orchestrates player states according to user options.
     * To save battery, CPU, and memory, if the user turns a slider down to 0%
     * or the app playback is globally paused, we explicitly `pause()` ExoPlayer.
     * If volume is positive (>0), we `play()` and set precise scaled volume.
     */
    private fun applyAudioStates() {
        val isGlobPlay = _isPlaying.value
        val vols = _volumes.value

        for ((trackId, player) in players) {
            if (trackId == TRACK_BINAURAL) {
                // Binaural is managed separately by type configuration
                val isBinauralActive = _binauralType.value != BinauralType.NONE
                if (isGlobPlay && isBinauralActive) {
                    val targetVol = 0.5f * fadeMultiplier // binaural beats are warm & balanced at 50% max
                    player.volume = targetVol
                    if (!player.isPlaying) player.play()
                } else {
                    if (player.isPlaying) player.pause()
                }
                continue
            }

            val baseVol = vols[trackId] ?: 0f
            val finalVol = baseVol * fadeMultiplier

            if (isGlobPlay && finalVol > 0.001f) {
                player.volume = finalVol
                if (!player.isPlaying) {
                    player.play()
                }
            } else {
                if (player.isPlaying) {
                    player.pause()
                }
                player.volume = 0f
            }
        }
    }

    private fun stopSelfWithGrace() {
        Log.d("AmbientAudioService", "Stopping audio service cleanly...")
        timerJob?.cancel()
        _isPlaying.value = false
        _timerSecondsRemaining.value = -1
        
        for (player in players.values) {
            try {
                player.stop()
                player.release()
            } catch (e: Exception) {
                // Ignore during cleanup
            }
        }
        players.clear()
        
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        timerJob?.cancel()
        for (player in players.values) {
            try {
                player.release()
            } catch (e: Exception) {
                // Ignore during cleanup
            }
        }
        super.onDestroy()
    }
}
