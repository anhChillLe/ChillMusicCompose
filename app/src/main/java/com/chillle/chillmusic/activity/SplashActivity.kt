package com.chillle.chillmusic.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.chillle.chillmusic.data.loader.MusicLoader
import com.chillle.chillmusic.data.repository.DefaultResource
import com.chillle.chillmusic.data.repository.MusicStore
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : ComponentActivity() {
    private val loader by lazy { MusicLoader(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkPermission()
    }

    private val contract = ActivityResultContracts.RequestPermission()
    private val requestPermissionLauncher = registerForActivityResult(contract) { isGranted ->
        if (isGranted) loadData()
        else finish()
    }

    private fun loadData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                val songs = loader.getListSong()
                MusicStore.setSongs(songs)
                DefaultResource.getInstance(this@SplashActivity.resources)
                val intent = Intent(this@SplashActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun checkPermission() {
        val permission = Manifest.permission.READ_EXTERNAL_STORAGE
        requestPermissionLauncher.launch(permission)
    }
}