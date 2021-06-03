package com.nemesiss.dev.crossingcontainermovement.manager

import android.util.Log
import com.nemesiss.dev.crossingcontainermovement.GameConfig
import com.nemesiss.dev.crossingcontainermovement.model.GameBoardMap
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.actor

class SaveDataManager private constructor() {

    companion object {
        val INSTANCE by lazy { SaveDataManager() }
    }

    private val mmkv = MMKV.defaultMMKV()!!

    private val saveScope = CoroutineScope(Dispatchers.IO + Job())

    @ObsoleteCoroutinesApi
    private val saver = saveScope.actor<GameBoardMap> {
        for (map in channel) {
            mmkv.encode(GameConfig.SaveKey, map)
            Log.w("SaveDataManager", "Game map saved!")
        }
    }

    @ObsoleteCoroutinesApi
    fun saveGameMap(map: GameBoardMap) {
        saveScope.launch {
            saver.send(map)
        }
    }

    fun savedMapExists() = mmkv.containsKey(GameConfig.SaveKey)

    fun removeSavedMap() {
        mmkv.remove(GameConfig.SaveKey)
    }

    fun getSavedMap(): GameBoardMap? {
        return mmkv.decodeParcelable(GameConfig.SaveKey, GameBoardMap::class.java)
    }
}
