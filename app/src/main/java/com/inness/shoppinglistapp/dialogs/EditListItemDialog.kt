package com.inness.shoppinglistapp.dialogs

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.inness.shoppinglistapp.R
import com.inness.shoppinglistapp.databinding.EditListItemDialogBinding
import com.inness.shoppinglistapp.databinding.NewListDialogBinding
import com.inness.shoppinglistapp.entities.ShopListItem

object EditListItemDialog {
    fun showDialog(context: Context, shopListItem: ShopListItem, listener : Listener) {
        var dialog: AlertDialog? = null
        val builder = AlertDialog.Builder(context)
        val binding = EditListItemDialogBinding.inflate(LayoutInflater.from(context))
        builder.setView(binding.root)
        binding.apply {
           edName.setText(shopListItem.name)
           edInfo.setText(shopListItem.itemInfo)
           if(shopListItem.itemType ==1) edInfo.visibility = View.GONE
           buttonUpdate.setOnClickListener {
               if(edName.text.toString().isNotEmpty()) {
                   listener.onClick(shopListItem.copy(
                   name = edName.text.toString(), itemInfo = edInfo.text.toString()))
               }
               dialog?.dismiss()
           }

            }

        dialog = builder.create()
        dialog.window?.setBackgroundDrawable(null)
        dialog.show()
    }


    interface Listener {
        fun onClick(shopListItem: ShopListItem)
    }
}