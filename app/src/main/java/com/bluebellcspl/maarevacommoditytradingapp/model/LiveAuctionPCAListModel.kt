package com.bluebellcspl.maarevacommoditytradingapp.model

data class LiveAuctionPCAListModel(
    var AvgPrice: String,
    var BuyerBori: String,
    var BuyerId: String,
    var CommodityBhartiPrice: String,
    var CommodityId: String,
    var CommodityName: String,
    var CompanyCode: String,
    var Date: String,
    var Ddate: String,
    var GCACommCharge: String,
    var IsAuctionStop: String,
    var LabourCharge: String,
    var MarketCessCharge: String,
    var PCAAuctionHeaderId: String,
    var PCAAuctionMasterId: String,
    var PCACommCharge: String,
    var PCAId: String,
    var PCAName: String,
    var PCARegId: String,
    var ShopList: ArrayList<LiveAuctionShopListModel>,
    var TotalPurchasedBags: String,
    var TransportationCharge: String
)