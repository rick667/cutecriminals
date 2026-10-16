package com.example.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GameSaveRepository(private val gameSaveDao: GameSaveDao) {

    suspend fun getSavedGame(): GameSaveEntity? = withContext(Dispatchers.IO) {
        gameSaveDao.getGameSave()
    }

    suspend fun saveGame(entity: GameSaveEntity) = withContext(Dispatchers.IO) {
        gameSaveDao.saveGame(entity)
    }

    suspend fun clearSave() = withContext(Dispatchers.IO) {
        gameSaveDao.clearActiveSave()
    }
}
