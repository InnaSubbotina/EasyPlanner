package com.inness.shoppinglistapp.viewmodel

import androidx.lifecycle.*
import com.inness.shoppinglistapp.database.MainDataBase
import com.inness.shoppinglistapp.entities.*
import kotlinx.coroutines.launch

class MainViewModel(database: MainDataBase) : ViewModel() {
    private val dao = database.getDao()
    val libraryItems = MutableLiveData <List<LibraryItem>>()
    val allNotes: LiveData<List<NoteItem>> = dao.getAllNotes().asLiveData()
    val allAlarm: LiveData<List<AlarmItem>> = dao.getAllAlarmItem().asLiveData()
    val allShopListNames: LiveData<List<ShopListNameItem>> = dao.getAllShopListNames().asLiveData()

    fun getAllItemsFromList(listId: Int): LiveData<List<ShopListItem>>{
        return dao.getAllShopListItems(listId).asLiveData()
    }
    fun getAllLibraryItems(name: String) = viewModelScope.launch {
        libraryItems.postValue(dao.getAllLibraryItems(name))
    }
    fun insertNote(note: NoteItem) = viewModelScope.launch {
        dao.insertNote(note)
    }
    fun insertShopListName(name: ShopListNameItem) = viewModelScope.launch {
        dao.insertShopListName(name)
    }
    fun insertShopItem(shopListItem: ShopListItem) = viewModelScope.launch {
        dao.insertShopItem(shopListItem)
        if(!isLibraryItemExists(shopListItem.name))
        dao.insertLibraryItem(LibraryItem(null, shopListItem.name))
    }
    fun deleteNote(id: Int) = viewModelScope.launch {
        dao.deleteNote(id)
    }
    fun deleteLibraryItem(id: Int) = viewModelScope.launch {
        dao.deleteLibraryItem(id)
    }
    fun deleteListItem(id: Int) = viewModelScope.launch {
        dao.deleteItem(id)
    }
    fun deleteShopList(id: Int, deleteList: Boolean) = viewModelScope.launch {
        if(deleteList)dao.deleteShopListName(id)
        dao.deleteShopItemByListId(id)
    }
    fun updateNote(note: NoteItem) = viewModelScope.launch {
        dao.updateNote(note)
    }
    fun updateLibraryItem(libraryItem: LibraryItem) = viewModelScope.launch {
        dao.updateLibraryItem(libraryItem)
    }
    fun updateShopListName(shopListName: ShopListNameItem) = viewModelScope.launch {
        dao.updateShopListName(shopListName)
    }
    fun updateListItem(shopListItem: ShopListItem) = viewModelScope.launch {
        dao.updateListItem(shopListItem)
    }
    private suspend fun isLibraryItemExists(name: String) : Boolean {
        return dao.getAllLibraryItems(name).isNotEmpty()
    }
    fun insertAlarmItem(alarm: AlarmItem) = viewModelScope.launch {
        dao.insertAlarmItem(alarm)
    }
    fun deleteAlarmItem(id: Int) = viewModelScope.launch {
        dao.deleteAlarmItem(id)
    }
    fun updateAlarmItem(alarm: AlarmItem) = viewModelScope.launch {
        dao.updateAlarmItem(alarm)
    }




    class MainViewModelFactory(val database: MainDataBase) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(MainViewModel::class.java)) {
                return MainViewModel(database) as T
            }
            throw IllegalArgumentException("Unknown ViewModelClass")
        }
    }
}
