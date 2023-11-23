package com.inness.shoppinglistapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.MenuItem.OnActionExpandListener
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.inness.shoppinglistapp.R
import com.inness.shoppinglistapp.database.ShopListItemAdapter
import com.inness.shoppinglistapp.databinding.ActivityShopListBinding
import com.inness.shoppinglistapp.dialogs.DeleteDialog
import com.inness.shoppinglistapp.dialogs.EditListItemDialog
import com.inness.shoppinglistapp.entities.LibraryItem
import com.inness.shoppinglistapp.entities.ShopListItem
import com.inness.shoppinglistapp.entities.ShopListNameItem
import com.inness.shoppinglistapp.utils.ShareHelper
import com.inness.shoppinglistapp.viewmodel.MainViewModel

class ShopListActivity : AppCompatActivity(), ShopListItemAdapter.Listener {
    private lateinit var binding: ActivityShopListBinding
    private var shopListNameItem: ShopListNameItem? = null
    private lateinit var saveItem: MenuItem
    private var edItem: EditText? = null
    private var adapter: ShopListItemAdapter? = null
    private lateinit var textWatcher: TextWatcher

    private val mainViewModel: MainViewModel by viewModels {
        MainViewModel.MainViewModelFactory((applicationContext as MainApp).database)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShopListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        initRcView()
        listItemObserver()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.shop_list_menu,menu)
        saveItem = menu?.findItem(R.id.save_item_list)!!
        val newItem = menu.findItem(R.id.new_item)
        edItem = newItem.actionView?.findViewById(R.id.edNewShopItem) as EditText
        newItem.setOnActionExpandListener(expandActionView())
        saveItem.isVisible = false
        textWatcher = textWatcher()
        return true
    }

    private fun textWatcher(): TextWatcher {
        return object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mainViewModel.getAllLibraryItems("%$s%")
            }
            override fun afterTextChanged(s: Editable?) {
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save_item_list -> {
                addNewShopItem(edItem?.text.toString())
            }
            R.id.delete_list -> {
                mainViewModel.deleteShopList(shopListNameItem?.id!!, true)
                finish()
            }
            R.id.clear_list -> {
                mainViewModel.deleteShopList(shopListNameItem?.id!!, false)
            }
            R.id.share_list -> {
                startActivity(Intent.createChooser(
                    ShareHelper.shareShopList(adapter?.currentList!!,shopListNameItem?.name!!),
                    getString(R.string.share_by)))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun addNewShopItem(name: String) {
        if(name.isEmpty()) return
        val item = ShopListItem(
            null,
            name,
            "",
            false,
            shopListNameItem?.id!!,
            0
        )
        edItem?.setText("")
        mainViewModel.insertShopItem(item)
    }

    private fun listItemObserver(){
        mainViewModel.getAllItemsFromList(shopListNameItem?.id!!).observe(this) {
            adapter?.submitList(it)
            binding.tvEmply.visibility = if (it.isEmpty()) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    private fun libraryItemObserver(){
        mainViewModel.libraryItems.observe(this,({
            val tempShopList = ArrayList<ShopListItem>()
            it.forEach { item->
                val shopItem = ShopListItem(
                    item.id,
                    item.name,
                    "",
                    false,
                    0,
                    1
                )
                tempShopList.add(shopItem)
            }
            adapter?.submitList(tempShopList)
            binding.tvEmply.visibility = if(it.isEmpty()){
                View.VISIBLE
            } else { View.GONE }
        }))
    }

    private fun initRcView() = with(binding){
        adapter = ShopListItemAdapter(this@ShopListActivity)
        rcViewShopList.layoutManager = LinearLayoutManager(this@ShopListActivity)
        rcViewShopList.adapter = adapter
    }

    private fun expandActionView(): OnActionExpandListener {
       return object : OnActionExpandListener {
           override fun onMenuItemActionExpand(item: MenuItem): Boolean {
              saveItem.isVisible = true
              edItem?.addTextChangedListener(textWatcher)
              libraryItemObserver()
              mainViewModel.getAllItemsFromList(shopListNameItem?.id!!).removeObservers(this@ShopListActivity)
              mainViewModel.getAllLibraryItems("%%")
              return true
           }

           override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
               saveItem.isVisible = false
               edItem?.removeTextChangedListener(textWatcher)
               invalidateOptionsMenu()
               mainViewModel.libraryItems.removeObservers(this@ShopListActivity)
               edItem?.setText("")
               listItemObserver()
               return true
           }
       }
    }

    private fun init(){
        shopListNameItem = intent.getSerializableExtra(SHOP_LIST_NAME) as ShopListNameItem
    }

    override fun onClickItem(shopListItem: ShopListItem,state: Int) {
        when(state){
            ShopListItemAdapter.CHECK_BOX -> mainViewModel.updateListItem(shopListItem)
            ShopListItemAdapter.EDIT -> editListItem(shopListItem)
            ShopListItemAdapter.EDIT_LIBRARY_ITEM -> editLibraryItem(shopListItem)
            ShopListItemAdapter.ADD -> addNewShopItem(shopListItem.name)
            ShopListItemAdapter.DELETE_LIBRARY_ITEM ->{
                mainViewModel.deleteLibraryItem(shopListItem.id!!)
                mainViewModel.getAllLibraryItems("%${edItem?.text.toString()}%")
            }
        }
    }

    override fun deleteListItem(id: Int) {
        DeleteDialog.showDialog(this, object : DeleteDialog.Listener {
            override fun onClick() {
                mainViewModel.deleteListItem(id)
                //Toast.makeText(this,"Удалено", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun editListItem (shopListItem : ShopListItem) {
        EditListItemDialog.showDialog(this, shopListItem, object : EditListItemDialog.Listener{
            override fun onClick(shopListItem: ShopListItem) {
             mainViewModel.updateListItem(shopListItem)
            }
        })
    }

    private fun editLibraryItem (shopListItem : ShopListItem) {
        EditListItemDialog.showDialog(this, shopListItem, object : EditListItemDialog.Listener{
            override fun onClick(shopListItem: ShopListItem) {
                mainViewModel.updateLibraryItem(LibraryItem(shopListItem.id,shopListItem.name))
                mainViewModel.getAllLibraryItems("%${edItem?.text.toString()}%")
            }
        })
    }

    private fun saveItemCount(){
        var checkedItemCounter = 0
        adapter?.currentList?.forEach {
            if(it.itemChecked) checkedItemCounter++
        }
        val tempShopListNameItem = shopListNameItem?.copy(
            allItemCounter = adapter?.itemCount!!,
            checkedItemsCounter = checkedItemCounter
        )
        tempShopListNameItem?.let { mainViewModel.updateShopListName(it) }
    }

    override fun onBackPressed() {
        saveItemCount()
        super.onBackPressed()
    }

    companion object{
        const val SHOP_LIST_NAME = "shop_list_name"
    }
}