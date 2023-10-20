package com.chillle.chillmusic.data.repository

import android.util.Log
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.media3.common.MediaItem
import com.chillle.chillmusic.models.Song

object MusicStore {
    val songs: MutableLiveData<Set<Song>> = MutableLiveData(setOf())
    val shortMusic = MediatorLiveData<Set<Song>>(setOf())
    val longMusic = MediatorLiveData<Set<Song>>(setOf())

    init {
        shortMusic.addSource(songs) { data ->
            shortMusic.postValue(data.filter { (it.duration ?: 0) < 600000 }.toSet())
        }

        longMusic.addSource(songs) { data ->
            longMusic.postValue(data.filter { (it.duration ?: 0) >= 600000 }.toSet())
        }
    }

    fun setSongs(listSong: Collection<Song>) {
        songs.postValue(listSong.toSet())
    }

    fun getSong(id: String): Song? {
        return songs.value?.find { it.id == id }
    }
}