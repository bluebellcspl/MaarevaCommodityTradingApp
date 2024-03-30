package com.bluebellcspl.maarevacommoditytradingapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bluebellcspl.maarevacommoditytradingapp.databinding.ChatlistItemAdapterBinding
import com.bluebellcspl.maarevacommoditytradingapp.model.PCAListModelItem

class BuyerChatListAdapter(var context: Context, var chatlist: ArrayList<PCAListModelItem>) :
    RecyclerView.Adapter<BuyerChatListAdapter.MyViewHolder>() {
    inner class MyViewHolder(var binding: ChatlistItemAdapterBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            ChatlistItemAdapterBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return chatlist.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = chatlist[holder.adapterPosition]
        holder.binding.tvPCANameChatListAdapter.setText(model.PCAShortName)
        holder.binding.iconPCATextChatListAdapter.setText(getInitialLetter(model.PCAShortName!!).toString())
    }

    fun getInitialLetter(inputString: String): Char? {
        val words = inputString.trim().split("\\s+".toRegex())

        if (words.isNotEmpty()) {
            val firstWord = words[0]

            if (firstWord.isNotEmpty()) {
                return firstWord[0].toUpperCase()
            }
        }
        return null
    }

}