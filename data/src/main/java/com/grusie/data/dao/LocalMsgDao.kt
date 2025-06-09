package com.grusie.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.grusie.data.data.LocalMsgEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalMsgDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMessage(message: LocalMsgEntity)

    @Query("SELECT * FROM message_table WHERE menuId = :menuId ORDER BY timeStamp DESC LIMIT 200")
    fun getRecentMessages(menuId: Int): Flow<List<LocalMsgEntity>>

    @Query("SELECT * FROM message_table ORDER BY timeStamp DESC LIMIT 10")
    fun getRecentMessagesAll(): Flow<List<LocalMsgEntity>>

    @Query(
        """
        DELETE FROM message_table 
        WHERE id NOT IN (
            SELECT id FROM message_table 
            WHERE menuId = :menuId 
            ORDER BY timeStamp DESC 
            LIMIT 200
        ) AND menuId = :menuId
    """
    )
    suspend fun trimOldMessages(menuId: Int)

    @Query("DELETE From message_table WHERE menuId = :menuId")
    suspend fun deleteMessages(menuId: Int)
}