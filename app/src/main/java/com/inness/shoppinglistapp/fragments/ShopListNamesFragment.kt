package com.inness.shoppinglistapp.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.inness.shoppinglistapp.activities.MainApp
import com.inness.shoppinglistapp.activities.ShopListActivity
import com.inness.shoppinglistapp.database.ShopListNameAdapter
import com.inness.shoppinglistapp.databinding.FragmentShopListNamesBinding
import com.inness.shoppinglistapp.dialogs.DeleteDialog
import com.inness.shoppinglistapp.dialogs.NewListDialog
import com.inness.shoppinglistapp.entities.ShopListNameItem
import com.inness.shoppinglistapp.utils.TimeManager
import com.inness.shoppinglistapp.viewmodel.MainViewModel

class ShopListNamesFragment : BaseFragment(), ShopListNameAdapter.Listener {
    private lateinit var binding: FragmentShopListNamesBinding
    private lateinit var adapter: ShopListNameAdapter

    private val mainViewModel: MainViewModel by activityViewModels {
        MainViewModel.MainViewModelFactory((context?.applicationContext as MainApp).database)
    }

    override fun onClickNew() {
        binding.newItemFab.setOnClickListener {
            NewListDialog.showDialog(
                activity as AppCompatActivity, object : NewListDialog.Listener {
                    override fun onClick(name: String) {
                        val shopListName = ShopListNameItem(
                            null,
                            name,
                            TimeManager.getCurrentTime(),
                            0,
                            0,
                            ""
                        )
                        mainViewModel.insertShopListName(shopListName)
                }
            },"")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentShopListNamesBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRcView()
        observer()
        onClickNew()
    }

    private fun initRcView() = with(binding) {
        rcView.layoutManager = LinearLayoutManager(activity)
        adapter = ShopListNameAdapter(this@ShopListNamesFragment)
        rcView.adapter = adapter
    }

    private fun observer() {
        mainViewModel.allShopListNames.observe(
            viewLifecycleOwner, {
                adapter.submitList(it)
        })
    }

    override fun deleteItem(id: Int) {
        DeleteDialog.showDialog(context as AppCompatActivity, object : DeleteDialog.Listener {
            override fun onClick() {
                mainViewModel.deleteShopList(id, true)
            }
        })
    }

    override fun editItem(shopListName: ShopListNameItem) {
        NewListDialog.showDialog(
            activity as AppCompatActivity, object : NewListDialog.Listener{
                override fun onClick(name: String) {
                    mainViewModel.updateShopListName(shopListName.copy(name = name))
                }
            },shopListName.name)
    }

    override fun onClickItem(shopListNameItem: ShopListNameItem) {
        val intent = Intent(activity, ShopListActivity::class.java).apply {
            putExtra(ShopListActivity.SHOP_LIST_NAME, shopListNameItem)
        }
        startActivity(intent)
    }

    companion object {
        @JvmStatic
        fun newInstance() = ShopListNamesFragment()
    }
}