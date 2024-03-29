package com.bluebellcspl.maarevacommoditytradingapp.master

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.bluebellcspl.maarevacommoditytradingapp.LoginActivity
import com.bluebellcspl.maarevacommoditytradingapp.commonFunction.CommonUIUtility
import com.bluebellcspl.maarevacommoditytradingapp.constants.Constants
import com.bluebellcspl.maarevacommoditytradingapp.database.DatabaseManager
import com.bluebellcspl.maarevacommoditytradingapp.retrofitApi.OurRetrofit
import com.bluebellcspl.maarevacommoditytradingapp.retrofitApi.RetrofitHelper
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FetchCommodityMasterAPI(var context: Context, var activity: Activity) {
    val job = Job()
    val scope = CoroutineScope(job)
    val commonUIUtility = CommonUIUtility(context)
    val TAG = "FetchCommodityMasterAPI"

    init {
        DatabaseManager.initializeInstance(context)
        getCommodityMaster()
    }

    private fun getCommodityMaster() {
        try {
            commonUIUtility.showProgress()
            val JO = JsonObject()
            JO.addProperty("CompanyCode", "MAT189")
            JO.addProperty("Action", "All")
            Log.d(TAG, "getStateMaster: JSON : ${JO.toString()}")

            val APICall = RetrofitHelper.getInstance().create(OurRetrofit::class.java)

            scope.launch(Dispatchers.IO){
                val result = APICall.getCommodityMaster(JO)

                if (result.isSuccessful)
                {
                    val commodityMasterModel = result.body()!!
                    val list = ContentValues()
                    DatabaseManager.deleteData(Constants.TBL_CommodityMaster)
                    for (model in commodityMasterModel)
                    {
                        list.put("CommodityId",model.CommodityId)
                        list.put("CommodityName",model.CommodityName)
                        list.put("StateId",model.StateId)
                        list.put("StateName",model.StateName)
                        list.put("DistrictId",model.DistrictId)
                        list.put("DistrictName",model.DistrictName)
                        list.put("APMCId",model.APMCId)
                        list.put("APMCName",model.APMCName)
                        list.put("CompanyCode",model.CompanyCode)
                        list.put("IsActive",model.IsActive)
                        list.put("CreateUser",model.CreateUser)
                        list.put("CreateDate",model.CreateDate)
                        list.put("UpdateDate",model.UpdateDate)
                        list.put("UpdateUser",model.UpdateUser)

                        DatabaseManager.commonInsert(list,Constants.TBL_CommodityMaster)
                    }
                    withContext(Dispatchers.Main){
                        commonUIUtility.dismissProgress()
                        if (activity is LoginActivity){
                            (activity as LoginActivity).bindCommodityDropDown()
                        }
                    }
                }else{
                    withContext(Dispatchers.Main) {
                        commonUIUtility.dismissProgress()
                    }
                    Log.e(TAG, "getCommodityMaster: ${result.errorBody().toString()}", )
                }
            }
        }catch (e:Exception){
            commonUIUtility.dismissProgress()
            e.printStackTrace()
            Log.e(TAG, "getCommodityMaster: ${e.message}")
        }
    }
}