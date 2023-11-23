package com.inness.shoppinglistapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.inness.shoppinglistapp.entities.*
import kotlinx.coroutines.flow.Flow


@Dao
interface Dao {

    @Query("SELECT * FROM shopping_list_names")
    fun getAllShopListNames(): Flow<List<ShopListNameItem>>

    @Query("SELECT * FROM shop_list_item WHERE listId LIKE :listId")
    fun getAllShopListItems(listId: Int): Flow<List<ShopListItem>>

    @Query("SELECT * FROM library WHERE name LIKE :name")
    suspend fun getAllLibraryItems(name: String): List<LibraryItem>

    @Query("SELECT * FROM note_list")
    fun getAllNotes(): Flow<List<NoteItem>>

    @Query("DELETE FROM note_list WHERE id IS :id")
    suspend fun deleteNote(id: Int)

    @Query("DELETE FROM shop_list_item WHERE id IS :id")
    suspend fun deleteItem(id: Int)

    @Query("DELETE FROM shopping_list_names WHERE id IS :id")
    suspend fun deleteShopListName(id: Int)

    @Query("DELETE FROM shop_list_item WHERE listId LIKE :listId")
    suspend fun deleteShopItemByListId(listId: Int)

    @Query("DELETE FROM library WHERE id IS :id")
    suspend fun deleteLibraryItem(id: Int)

    @Insert
    suspend fun insertNote(note: NoteItem)

    @Insert
    suspend fun insertShopItem(shopListItem: ShopListItem)

    @Insert
    suspend fun insertShopListName(name: ShopListNameItem)

    @Insert
    suspend fun insertLibraryItem(libraryItem: LibraryItem)

    @Update
    suspend fun updateNote(note: NoteItem)

    @Update
    suspend fun updateLibraryItem(libraryItem: LibraryItem)

    @Update
    suspend fun updateListItem(shopListItem: ShopListItem)

    @Update
    suspend fun updateShopListName(shopListName: ShopListNameItem)



    @Query("SELECT * FROM alarm_item")
    fun getAllAlarmItem(): Flow<List<AlarmItem>>

    @Query("DELETE FROM alarm_item WHERE id IS :id")
    suspend fun deleteAlarmItem(id: Int)

    @Insert
    suspend fun insertAlarmItem(alarm: AlarmItem)

    @Update
    suspend fun updateAlarmItem(alarm: AlarmItem)


}