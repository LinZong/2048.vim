package com.nemesiss.dev.vim2048.manager

import android.util.Log
import com.nemesiss.dev.vim2048.GameConfig
import com.nemesiss.dev.vim2048.model.GameBoardMap
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
            mmkv.encode(GameConfig.SaveMapKey, map)
            Log.w("SaveDataManager", "Game map saved!")
        }
    }

    @ObsoleteCoroutinesApi
    fun saveGameMap(map: GameBoardMap) {
        saveScope.launch {
            saver.send(map)
        }
    }

    fun savedMapExists() = mmkv.containsKey(GameConfig.SaveMapKey)

    fun removeSavedMap() {
        mmkv.remove(GameConfig.SaveMapKey)
    }

    fun getSavedMap(): GameBoardMap? {
        return mmkv.decodeParcelable(GameConfig.SaveMapKey, GameBoardMap::class.java)
    }

    fun saveCurrentScore(score: Int) {
        mmkv.encode(GameConfig.SaveCurrentScoreKey, score)
    }

    fun saveHighestScore(highest: Int) {
        mmkv.encode(GameConfig.SaveHighestScoreKey, highest)
    }

    fun getCurrentScore(): Int {
        return mmkv.decodeInt(GameConfig.SaveCurrentScoreKey, 0)
    }

    fun getHighestScore(): Int {
        return mmkv.decodeInt(GameConfig.SaveHighestScoreKey, 0)
    }
}
