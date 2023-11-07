package com.bluebellcspl.maarevacommoditytradingapp.adapter

import android.content.Context
import android.icu.text.NumberFormat
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bluebellcspl.maarevacommoditytradingapp.databinding.BuyerAuctionItemAdapterBinding
import com.bluebellcspl.maarevacommoditytradingapp.model.AuctionDetailsModel
import com.bluebellcspl.maarevacommoditytradingapp.recyclerViewHelper.RecyclerViewHelper

class BuyerAuctionListAdapter(
    var context: Context,
    var dataList: ArrayList<AuctionDetailsModel>,
    var recyclerViewHelper: RecyclerViewHelper,
    var commodityBhartiPrice: String
) : RecyclerView.Adapter<BuyerAuctionListAdapter.MyViewHolder>() {

    private val TAG = "BuyerAuctionListAdapter"

    inner class MyViewHolder(var binding: BuyerAuctionItemAdapterBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun calcutateData(model: AuctionDetailsModel) {
            try {
                var upperLimit = binding.tvUpperLimitBuyerAuctionItemAdapter.text.toString().trim()
                var lowerLimit = binding.tvLowerLimitBuyerAuctionItemAdapter.text.toString().trim()
                var bags = binding.tvBagsBuyerAuctionItemAdapter.text.toString().trim()
                if (upperLimit.isNotEmpty() && lowerLimit.isNotEmpty() && bags.isNotEmpty()) {
                    val BasicAmount =
                        ((bags.toDouble() * commodityBhartiPrice.toDouble()) / 20) * ((upperLimit.toDouble() + lowerLimit.toDouble()) / 2)
                    Log.d(TAG, "afterTextChanged: BAGS_AMOUNT : $BasicAmount")
                    var totalAmount = 0.0

                    var transportCharge =(bags.toDouble() * model.UpdPerBoriRate.toDouble())
                    var labourCharge = model.UpdLabourCharge.toDouble()

                    val gcaCommission = ((BasicAmount * model.UpdGCACommRate.toDouble()) / 100.0)
                    val pcaCommission = (BasicAmount * model.UpdPCACommRate.toDouble()) / 100.0
                    val marketCess = (BasicAmount * model.UpdMarketCessRate.toDouble()) / 100.0

                    Log.d(TAG, "afterTextChanged: PCA_NAME_MODEL : ${model.PCAName}")
                    Log.d(TAG, "afterTextChanged: MARKETCESS : $marketCess")
                    Log.d(TAG, "afterTextChanged: PCACOMISSION : $pcaCommission")
                    Log.d(TAG, "afterTextChanged: GCACOMISSION : $gcaCommission")
                    Log.d(TAG,"afterTextChanged: TRANSPORTATION_CHARGE at $adapterPosition : $transportCharge")
                    Log.d(TAG, "afterTextChanged: LABOURCHARGES : $labourCharge")
                    if (upperLimit.toInt()!=0 && lowerLimit.toInt()!=0) {
                        totalAmount =BasicAmount + gcaCommission + pcaCommission + marketCess + transportCharge + labourCharge
                    }else
                    {
                        totalAmount = 0.0
                    }

                    Log.d(TAG, "afterTextChanged: TOTAL_AMOUNT : $totalAmount")
                    Log.d(
                        TAG,
                        "afterTextChanged: ================================================================================"
                    )

//                    binding.tvAmountBuyerAuctionItemAdapter.setText("%.2f".format(totalAmount))
                    val nf = NumberFormat.getCurrencyInstance().format(totalAmount)
                    binding.tvAmountBuyerAuctionItemAdapter.setText(nf.toString())
                    model.Bags = bags
                    model.Amount = "%.2f".format(totalAmount)
                    model.LowerLimit = lowerLimit
                    model.UpperLimit = upperLimit
                    model.Basic = BasicAmount.toString()
                    model.TransportationCharge = "%.2f".format(transportCharge)
                    model.PerBoriRate = model.UpdPerBoriRate
                    model.PCACommCharge = pcaCommission.toString()
                    model.PCACommRate = model.UpdPCACommRate
                    model.GCACommCharge = gcaCommission.toString()
                    model.GCACommRate = model.UpdGCACommRate
                    model.UpdLabourCharge = labourCharge.toString()
                    model.MarketCessCharge = marketCess.toString()
                    recyclerViewHelper.getBuyerAuctionDataList(dataList)
                } else {
                    binding.tvAmountBuyerAuctionItemAdapter.setText("")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e(TAG, "calcutateData: ${e.message}")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            BuyerAuctionItemAdapterBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = dataList[holder.adapterPosition]
        holder.binding.tvPCANameBuyerAuctionItemAdapter.setText(model.PCAName)
        val nf = NumberFormat.getCurrencyInstance().format(model.Amount.toDouble())
        holder.binding.tvAmountBuyerAuctionItemAdapter.setText(nf.toString())
        holder.binding.tvBagsBuyerAuctionItemAdapter.setText(model.Bags)
        var pcaLowerLimit = ""
        var pcaUpperLimit = ""
        if (model.PCAUpperLimit.equals("0") && model.PCALowerLimit.equals("0")) {
            if ((model.LowerLimit.isNotEmpty() && model.LowerLimit.toDouble() > 0.0) && (model.UpperLimit.isNotEmpty() && model.UpperLimit.toDouble() > 0.0)) {
                pcaLowerLimit = model.LowerLimit
                pcaUpperLimit = model.UpperLimit
            } else {
                pcaLowerLimit = "0"
                pcaUpperLimit = "0"
            }
        } else {
            pcaLowerLimit = model.PCALowerLimit
            pcaUpperLimit = model.PCAUpperLimit
        }
        holder.binding.tvLowerLimitBuyerAuctionItemAdapter.setText(pcaLowerLimit)
        holder.binding.tvUpperLimitBuyerAuctionItemAdapter.setText(pcaUpperLimit)
//        holder.binding.tvLastDayPriceBuyerAuctionItemAdapter.setText(model.LastDayPrice)
        model.Basic = "0.0"
        if (model.Bags.isEmpty() || model.Bags.equals("") || model.Bags.toInt() < 1) {
            holder.binding.cvAuctionDetailsBuyerAuctionItemAdapter.visibility = View.GONE
            holder.binding.cvBagCountBuyerAuctionItemAdapter.visibility = View.GONE
        }
        holder.calcutateData(model)
        holder.binding.cvAuctionDetailsBuyerAuctionItemAdapter.setOnClickListener {
            recyclerViewHelper.onItemClick(holder.adapterPosition,"")
        }
        //TextWatcher
        val calculationTextWatcher: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                if (holder.binding.tvBagsBuyerAuctionItemAdapter.text.toString().isNullOrBlank()) {
                    holder.binding.tvBagsBuyerAuctionItemAdapter.setText("0")
                    holder.binding.tvBagsBuyerAuctionItemAdapter.setSelection(1)
                }
                if (holder.binding.tvBagsBuyerAuctionItemAdapter.text.toString().length >= 2 && holder.binding.tvBagsBuyerAuctionItemAdapter.text.toString()
                        .startsWith("0")
                ) {
                    val subStr =
                        holder.binding.tvBagsBuyerAuctionItemAdapter.text.toString().substring(1)
                    holder.binding.tvBagsBuyerAuctionItemAdapter.setText(subStr)
                    holder.binding.tvBagsBuyerAuctionItemAdapter.setSelection(1)
                }
                holder.calcutateData(model)
            }
        }
        holder.binding.tvUpperLimitBuyerAuctionItemAdapter.addTextChangedListener(calculationTextWatcher)
        holder.binding.tvBagsBuyerAuctionItemAdapter.addTextChangedListener(calculationTextWatcher)
        holder.binding.tvLowerLimitBuyerAuctionItemAdapter.addTextChangedListener(object :TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                holder.binding.tvUpperLimitBuyerAuctionItemAdapter.setText("")
            }
        })
    }
}