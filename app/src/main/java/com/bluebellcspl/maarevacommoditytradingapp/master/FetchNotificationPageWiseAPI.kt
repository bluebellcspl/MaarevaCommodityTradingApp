package com.bluebellcspl.maarevacommoditytradingapp.master

import android.content.ContentValues
import android.content.Context
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.bluebellcspl.maarevacommoditytradingapp.commonFunction.CommonUIUtility
import com.bluebellcspl.maarevacommoditytradingapp.commonFunction.PrefUtil
import com.bluebellcspl.maarevacommoditytradingapp.constants.Constants
import com.bluebellcspl.maarevacommoditytradingapp.database.DatabaseManager
import com.bluebellcspl.maarevacommoditytradingapp.fragment.NotificationFragment
import com.bluebellcspl.maarevacommoditytradingapp.fragment.buyer.BuyerDashboardFragment
import com.bluebellcspl.maarevacommoditytradingapp.fragment.pca.PCADashboardFragment
import com.bluebellcspl.maarevacommoditytradingapp.retrofitApi.OurRetrofit
import com.bluebellcspl.maarevacommoditytradingapp.retrofitApi.RetrofitHelper
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FetchNotificationPageWiseAPI(var context:Context, var fragment:Fragment,var pageNo:Int,var itemCount:Int) {
    val job = Job()
    val scope = CoroutineScope(job)
    val commonUIUtility = CommonUIUtility(context)
    val TAG = "FetchNotificationPageWiseAPI"

    init {
        getNotification()
    }

    private fun getNotification() {
        try {
//            commonUIUtility.showProgress()
            var typeOfUser = ""
            if (PrefUtil.getString(PrefUtil.KEY_ROLE_NAME,"").toString().equals("PCA",true))
            {
                typeOfUser = "3"
            }else
            {
                typeOfUser="2"
            }
            val JO = JsonObject()
            JO.addProperty("CompanyCode", PrefUtil.getString(PrefUtil.KEY_COMPANY_CODE,"").toString())
            JO.addProperty("RegId", PrefUtil.getString(PrefUtil.KEY_REGISTER_ID,"").toString())
            JO.addProperty("Typeofuser", typeOfUser)
            JO.addProperty("Language", PrefUtil.getSystemLanguage())
            Log.d(TAG, "getNotification: JSON : ${JO.toString()}")

            val APICall = RetrofitHelper.getInstance().create(OurRetrofit::class.java)
            scope.launch(Dispatchers.IO){
                val result = APICall.getNotificationListPageWise(JO,pageNo,itemCount)
                if (result.isSuccessful)
                {
                    val notificationModel = result.body()!!
//                    DatabaseManager.deleteData(Constants.TBL_NotificationMaster)
                    val list = ContentValues()
                    for (model in notificationModel)
                    {
                        list.put("NotificationId",model.NotificationId)
                        list.put("ShortMsg",model.ShortMsg)
                        list.put("FullMsg",model.FullMsg)
                        list.put("FromRoleId",model.FromRoleId)
                        list.put("RoleName",model.RoleName)
                        list.put("ToRoleId",model.ToRoleId)
                        list.put("ToUserId",model.ToUserId)
                        list.put("Name",model.Name)
                        list.put("Link",model.Link)
                        list.put("ISRead",model.ISRead)
//                        list.put("ISSeen",model.ISSeen)
                        list.put("CreateUser",model.CreateUser)
                        list.put("Cdate",model.Cdate)

//                        DatabaseManager.commonInsert(list, Constants.TBL_NotificationMaster)
                    }
                    if (fragment is NotificationFragment){
                        withContext(Dispatchers.Main)
                        {
//                            commonUIUtility.dismissProgress()
//                            (fragment as NotificationFragment).binding.progressBarNotificationFragment.visibility = View.GONE
//                            (fragment as NotificationFragment).newBindNotificationList(notificationModel)
                        }
                    }else if (fragment is PCADashboardFragment){
                        withContext(Dispatchers.Main)
                        {
                            commonUIUtility.dismissProgress()
                            (fragment as PCADashboardFragment).updateNotificationCount()
                        }
                    }
                }else
                {
                    withContext(Dispatchers.Main){
                        Log.e(TAG, "getNotification: ERROR : ${result.errorBody()}", )
                        commonUIUtility.dismissProgress()
                    }
                }
            }
        }catch (e:Exception)
        {
            commonUIUtility.dismissProgress()
            e.printStackTrace()
            Log.e(TAG, "getNotification ERROR : ${e.message}", )
        }
    }
}