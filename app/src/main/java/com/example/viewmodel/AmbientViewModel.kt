package com.example.viewmodel

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AmbientAudioService
import com.example.audio.BinauralType
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AmbientViewModel(application: Application) : AndroidViewModel(application) {

    private val _isServiceBound = MutableStateFlow(false)
    val isServiceBound: StateFlow<Boolean> = _isServiceBound.asStateFlow()

    private var audioService: AmbientAudioService? = null

    // Exposed Reactive StateFlows for the Jetpack Compose layer
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _volumes = MutableStateFlow<Map<String, Float>>(
        mapOf(
            AmbientAudioService.TRACK_BROWN_NOISE to 0.4f,
            AmbientAudioService.TRACK_RAIN to 0.4f,
            AmbientAudioService.TRACK_FIREPLACE to 0.3f,
            AmbientAudioService.TRACK_AMBIENT_PAD to 0.5f,
            AmbientAudioService.TRACK_BINAURAL to 0.0f
        )
    )
    val volumes: StateFlow<Map<String, Float>> = _volumes.asStateFlow()

    private val _binauralType = MutableStateFlow(BinauralType.NONE)
    val binauralType: StateFlow<BinauralType> = _binauralType.asStateFlow()

    private val _timerSecondsRemaining = MutableStateFlow(-1)
    val timerSecondsRemaining: StateFlow<Int> = _timerSecondsRemaining.asStateFlow()

    // Premium Unlock states stored in persistent SharedPreferences
    private val sharedPrefs = application.getSharedPreferences("ambient_sleep_prefs", Context.MODE_PRIVATE)
    
    private val _premiumUnlockedUntil = MutableStateFlow(0L)
    val premiumUnlockedUntil: StateFlow<Long> = _premiumUnlockedUntil.asStateFlow()

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            val localBinder = binder as? AmbientAudioService.LocalBinder
            if (localBinder != null) {
                val serviceInstance = localBinder.getService()
                audioService = serviceInstance
                _isServiceBound.value = true
                Log.d("AmbientViewModel", "Successfully bound to AmbientAudioService")

                // Wire up state flows of service directly into ViewModel states
                viewModelScope.launch {
                    serviceInstance.isPlaying.collect { _isPlaying.value = it }
                }
                viewModelScope.launch {
                    serviceInstance.volumes.collect { _volumes.value = it }
                }
                viewModelScope.launch {
                    serviceInstance.binauralType.collect { _binauralType.value = it }
                }
                viewModelScope.launch {
                    serviceInstance.timerSecondsRemaining.collect { _timerSecondsRemaining.value = it }
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            _isServiceBound.value = false
            audioService = null
            Log.d("AmbientViewModel", "Service connection severed unexpectedly.")
        }
    }

    init {
        // Sync premium expiration from disk
        _premiumUnlockedUntil.value = sharedPrefs.getLong("premium_unlocked_until", 0L)

        // Bind & Keep service alive persistently in the background
        val intent = Intent(application, AmbientAudioService::class.java)
        try {
            application.startService(intent)
            application.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        } catch (e: Exception) {
            Log.e("AmbientViewModel", "Failed starting/binding Audio Service", e)
        }
    }

    fun togglePlay() {
        audioService?.setPlaying(!_isPlaying.value)
    }

    fun setPlaying(playing: Boolean) {
        audioService?.setPlaying(playing)
    }

    fun setChannelVolume(channel: String, volume: Float) {
        audioService?.setChannelVolume(channel, volume)
    }

    fun selectBinauralWaves(type: BinauralType) {
        audioService?.setBinauralWaves(type)
    }

    fun setSleepTimer(minutes: Int) {
        audioService?.setSleepTimer(minutes)
    }

    fun stopSleepTimer() {
        audioService?.stopTimer()
    }

    fun isPremiumActive(): Boolean {
        return System.currentTimeMillis() < _premiumUnlockedUntil.value
    }

    fun unlockPremiumFor24Hours() {
        val unlockTime = System.currentTimeMillis() + (24L * 60L * 60L * 1000L) // 24 hours
        sharedPrefs.edit().putLong("premium_unlocked_until", unlockTime).apply()
        _premiumUnlockedUntil.value = unlockTime
        Log.d("AmbientViewModel", "Premium delta/theta waves unlocked until: $unlockTime")
    }

    override fun onCleared() {
        try {
            if (_isServiceBound.value) {
                getApplication<Application>().unbindService(serviceConnection)
            }
        } catch (e: Exception) {
            Log.w("AmbientViewModel", "Unbinding error skipped during VM cleared phase", e)
        }
        super.onCleared()
    }
}
