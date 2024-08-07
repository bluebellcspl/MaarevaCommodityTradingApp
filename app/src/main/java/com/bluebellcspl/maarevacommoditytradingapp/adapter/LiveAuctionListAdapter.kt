package com.bluebellcspl.maarevacommoditytradingapp.adapter

import android.content.Context
import android.icu.text.NumberFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bluebellcspl.maarevacommoditytradingapp.commonFunction.PrefUtil
import com.bluebellcspl.maarevacommoditytradingapp.database.DatabaseManager
import com.bluebellcspl.maarevacommoditytradingapp.database.Query
import com.bluebellcspl.maarevacommoditytradingapp.databinding.LiveAuctionAdapterBinding
import com.bluebellcspl.maarevacommoditytradingapp.model.ExpandableObject
import com.bluebellcspl.maarevacommoditytradingapp.model.LiveAuctionPCAListModel
import com.bluebellcspl.maarevacommoditytradingapp.model.LiveAuctionShopListModel
import com.bluebellcspl.maarevacommoditytradingapp.model.NotificationRTRMasterModelItem
import com.bluebellcspl.maarevacommoditytradingapp.recyclerViewHelper.RecyclerViewHelper


class LiveAuctionListAdapter(
    var context: Context,
    var dataList: ArrayList<LiveAuctionPCAListModel>,
    var expandableList: ArrayList<ExpandableObject>,
    var recyclerViewHelper: RecyclerViewHelper
) : RecyclerView.Adapter<LiveAuctionListAdapter.MyViewHolder>() {
    val TAG = "LiveAuctionListAdapter"
    private var oldAuctionList= ArrayList<LiveAuctionPCAListModel>()
    private var newAuctionList = dataList

    private var diffUtilCallBack = object : DiffUtil.Callback(){
        override fun getOldListSize(): Int {
            return oldAuctionList.size
        }

        override fun getNewListSize(): Int {
            return newAuctionList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldAuctionList[oldItemPosition].PCAAuctionMasterId==newAuctionList[newItemPosition].PCAAuctionMasterId
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldAuctionList[oldItemPosition]==newAuctionList[newItemPosition]
        }
    }

    fun submitList(newLiveAuctionList:List<LiveAuctionPCAListModel>){
        oldAuctionList = ArrayList(newLiveAuctionList)
        val differ = DiffUtil.calculateDiff(diffUtilCallBack)
        oldAuctionList.clear()
        oldAuctionList.addAll(newLiveAuctionList)
        differ.dispatchUpdatesTo(this)
    }
    inner class MyViewHolder(var binding: LiveAuctionAdapterBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.llHeaderLiveAuctionAdapter.setOnClickListener {
                val model = expandableList[adapterPosition]
                model.Expandable = !model.isExpandable()
                notifyItemChanged(adapterPosition)
            }

            binding.llHeaderLiveAuctionAdapter.setOnLongClickListener {
                val model = dataList[adapterPosition]
                recyclerViewHelper.getLiveAuctionPCAData(adapterPosition,model)
                true
            }
        }

        fun bindShopList(shopList: ArrayList<LiveAuctionShopListModel>) {
            val adapter = ShopListAdatper(context, shopList)
            binding.rcViewPCAShopListLiveAuctionAdapter.adapter = adapter
            adapter.submitList(shopList)
        }

        fun calcutionTotalPCAAmount(dataModel: LiveAuctionPCAListModel) {
            try {

                var currentPCABasic = 0.0
                var CURRENT_pcaMarketCess = 0.0
                var CURRENT_pcaCommCharge = 0.0
                var CURRENT_gcaCommCharge = 0.0
                var CURRENT_pcaTransportationCharge = 0.0
                var CURRENT_pcaLabourCharge = 0.0
                var CURRENT_Shop_Amount = 0.0
                var CURRENT_TOTAL_COST = 0.0
                var CURRENT_pcaExpense = 0.0
                var TOTAL_pcaBasic=0.0
                for (ShopData in dataModel.ShopList) {
                    currentPCABasic = ShopData.Amount.toDouble()
                    var SHOP_CURRENT_PRICE = ShopData.CurrentPrice.toDouble()
                    var SHOP_CURRENT_BAGS = ShopData.Bags.toFloat()

                    var pcaMarketCess =
                        (((SHOP_CURRENT_BAGS * dataModel.CommodityBhartiPrice.toDouble()) / 20) * (SHOP_CURRENT_PRICE) * dataModel.MarketCessCharge.toDouble()) / 100.00
                    var pcaCommCharge =
                        (((SHOP_CURRENT_BAGS * dataModel.CommodityBhartiPrice.toDouble()) / 20) * (SHOP_CURRENT_PRICE) * dataModel.PCACommCharge.toDouble()) / 100.00
                    var gcaCommCharge =
                        (((SHOP_CURRENT_BAGS * dataModel.CommodityBhartiPrice.toDouble()) / 20) * (SHOP_CURRENT_PRICE) * dataModel.GCACommCharge.toDouble()) / 100.00
                    if (dataModel.TransportationCharge.isEmpty()) {
                        dataModel.TransportationCharge = "0"
                    }
                    if (dataModel.LabourCharge.isEmpty()) {
                        dataModel.LabourCharge = "0"
                    }
                    var pcaLabourCharge = SHOP_CURRENT_BAGS * dataModel.LabourCharge.toDouble()
                    var pcaTransportationCharge =
                        SHOP_CURRENT_BAGS * dataModel.TransportationCharge.toDouble()
                    var amount =
                        ((SHOP_CURRENT_BAGS * dataModel.CommodityBhartiPrice.toDouble()) / 20) * SHOP_CURRENT_PRICE

                    CURRENT_Shop_Amount += amount
                    CURRENT_pcaCommCharge += pcaCommCharge
                    CURRENT_gcaCommCharge += gcaCommCharge
                    CURRENT_pcaMarketCess += pcaMarketCess
                    CURRENT_pcaLabourCharge += pcaLabourCharge
                    CURRENT_pcaTransportationCharge += pcaTransportationCharge
                    TOTAL_pcaBasic += currentPCABasic
                    CURRENT_pcaExpense += pcaCommCharge + gcaCommCharge + pcaMarketCess +pcaLabourCharge + pcaTransportationCharge
                    CURRENT_TOTAL_COST += amount + pcaCommCharge + gcaCommCharge + pcaMarketCess +pcaLabourCharge + pcaTransportationCharge
                }
                val PCATOTALNF = NumberFormat.getCurrencyInstance().format(CURRENT_TOTAL_COST).substring(1)
                binding.tvPCATotalAmount.setText(PCATOTALNF)
                var pcaAvgRate = CURRENT_TOTAL_COST/((dataModel.TotalPurchasedBags.toFloat()*dataModel.CommodityBhartiPrice.toDouble())/20)
                val AvgRateNF = NumberFormat.getCurrencyInstance().format(pcaAvgRate).substring(1)
                binding.tvPCAAvgRate.setText(AvgRateNF)

            } catch (e: Exception) {
                e.printStackTrace()
                Log.e(TAG, "calcutionTotalPCAAmount: ${e.message}")

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LiveAuctionAdapterBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return newAuctionList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = dataList[holder.adapterPosition]
        Log.d(TAG, "onBindViewHolder: LIVE_AUCTION_MODEL : $model")
        val expanable = expandableList[holder.adapterPosition]
        holder.binding.tvPCAName.setText(model.PCAName)
        if (!PrefUtil.getSystemLanguage().equals("en")) {
            if (model.GujaratiPCAShortName.isNotEmpty()){
                holder.binding.tvPCAName.setText(model.GujaratiPCAShortName)
            }else
            {
                holder.binding.tvPCAName.setText(model.PCAShortName)
            }
        } else {
            holder.binding.tvPCAName.setText(model.PCAShortName)
        }
        holder.bindShopList(model.ShopList)
        if (model.TotalPurchasedBags.split(".")[1].startsWith("5"))
        {
            holder.binding.tvPCATotalBags.setText(model.TotalPurchasedBags)
        }else
        {
            holder.binding.tvPCATotalBags.setText(model.TotalPurchasedBags.split(".")[0])
        }
        holder.calcutionTotalPCAAmount(model)

        if (model.IsAuctionStop.equals("False", true)) {
            holder.binding.fabPauseAuctionLiveAucionFragment.visibility = View.VISIBLE
            holder.binding.fabStartAuctionLiveAucionFragment.visibility = View.GONE
        } else {
            holder.binding.fabPauseAuctionLiveAucionFragment.visibility = View.GONE
            holder.binding.fabStartAuctionLiveAucionFragment.visibility = View.VISIBLE
        }

        holder.binding.fabStartAuctionLiveAucionFragment.setOnClickListener {
            holder.binding.fabPauseAuctionLiveAucionFragment.visibility = View.VISIBLE
            holder.binding.fabStartAuctionLiveAucionFragment.visibility = View.GONE
            recyclerViewHelper.onItemClick(holder.adapterPosition, "start")
        }
        holder.binding.fabPauseAuctionLiveAucionFragment.setOnClickListener {
            holder.binding.fabPauseAuctionLiveAucionFragment.visibility = View.GONE
            holder.binding.fabStartAuctionLiveAucionFragment.visibility = View.VISIBLE
            recyclerViewHelper.onItemClick(holder.adapterPosition, "stop")
        }
        val isExpandable: Boolean = expanable.isExpandable()
        holder.binding.llExpandableLiveAuctionAdapter.visibility =
            if (isExpandable) View.VISIBLE else View.GONE
    }
}