package com.bluebellcspl.maarevacommoditytradingapp.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bluebellcspl.maarevacommoditytradingapp.databinding.LiveAuctionAdapterBinding
import com.bluebellcspl.maarevacommoditytradingapp.model.ExpandableObject
import com.bluebellcspl.maarevacommoditytradingapp.model.LiveAuctionPCAListModel
import com.bluebellcspl.maarevacommoditytradingapp.model.LiveAuctionShopListModel
import com.bluebellcspl.maarevacommoditytradingapp.recyclerViewHelper.RecyclerViewHelper


class LiveAuctionListAdapter(var context: Context,var dataList:ArrayList<LiveAuctionPCAListModel>,var expandableList:ArrayList<ExpandableObject>,var recyclerViewHelper: RecyclerViewHelper):RecyclerView.Adapter<LiveAuctionListAdapter.MyViewHolder>() {
        val TAG = "LiveAuctionListAdapter"
    inner class MyViewHolder(var binding: LiveAuctionAdapterBinding):
        RecyclerView.ViewHolder(binding.root){
        init {
            binding.llHeaderLiveAuctionAdapter.setOnClickListener {
                val model = expandableList[adapterPosition]
                model.Expandable = !model.isExpandable()
                notifyItemChanged(adapterPosition)
            }
        }
            fun bindShopList(shopList:ArrayList<LiveAuctionShopListModel>){
                val adapter = ShopListAdatper(context,shopList)
                binding.rcViewPCAShopListLiveAuctionAdapter.adapter = adapter
                binding.rcViewPCAShopListLiveAuctionAdapter.invalidate()
            }

            fun calcutionTotalPCAAmount(dataList: LiveAuctionPCAListModel)
            {
                try {
                    var pcaBasic = 0.0
                    var pcaExpense = 0.0
                    for(pcaData in dataList.ShopList)
                    {
                        pcaBasic+=pcaData.Amount.toDouble()
                    }
                    pcaExpense = dataList.PCACommCharge.toDouble()+dataList.GCACommCharge.toDouble()+dataList.TransportationCharge.toDouble()+dataList.LabourCharge.toDouble()

                    binding.tvPCATotalAmount.setText(String.format("%.2f",pcaBasic))
                }catch (e:Exception)
                {
                    e.printStackTrace()
                    Log.e(TAG, "calcutionTotalPCAAmount: ${e.message}")

                }
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LiveAuctionAdapterBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = dataList[holder.adapterPosition]
        val expanable = expandableList[holder.adapterPosition]
        holder.binding.tvPCAName.setText(model.PCAName)
        holder.bindShopList(model.ShopList)
        holder.binding.tvPCAAvgRate.setText(model.AvgPrice)
        holder.binding.tvPCATotalBags.setText(model.TotalPurchasedBags)
        holder.calcutionTotalPCAAmount(model)
        if (model.IsAuctionStop.equals("False",true))
        {
            holder.binding.fabPauseAuctionLiveAucionFragment.visibility = View.VISIBLE
            holder.binding.fabStartAuctionLiveAucionFragment.visibility = View.GONE
        }else
        {
            holder.binding.fabPauseAuctionLiveAucionFragment.visibility = View.GONE
            holder.binding.fabStartAuctionLiveAucionFragment.visibility = View.VISIBLE
        }

        holder.binding.fabStartAuctionLiveAucionFragment.setOnClickListener {
            holder.binding.fabPauseAuctionLiveAucionFragment.visibility = View.VISIBLE
            holder.binding.fabStartAuctionLiveAucionFragment.visibility = View.GONE
            recyclerViewHelper.onItemClick(holder.adapterPosition,"start")
        }
        holder.binding.fabPauseAuctionLiveAucionFragment.setOnClickListener {
            holder.binding.fabPauseAuctionLiveAucionFragment.visibility = View.GONE
            holder.binding.fabStartAuctionLiveAucionFragment.visibility = View.VISIBLE
            recyclerViewHelper.onItemClick(holder.adapterPosition,"stop")
        }
        val isExpandable: Boolean = expanable.isExpandable()
        holder.binding.llExpandableLiveAuctionAdapter.visibility = if (isExpandable) View.VISIBLE else View.GONE
    }
}