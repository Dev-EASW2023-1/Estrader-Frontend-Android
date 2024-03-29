package kr.easw.estrader.android.model.dto

data class MainItem(
    val iconDrawable: String = "",
    val auctionHouse: String = "",
    val caseNumber: String = "",
    val location: String = "",
    val reservePrice: String = "",
    val auctionPeriod: String = "",
    val xcoordinate: String = "",
    val ycoordinate: String = "",
    val district: String = "",
    val itemType: String = "",
    val note: String = ""
)

data class TitleItem(
    val titleName: String = "",
    var isSelected: Boolean = false
)

data class DelegateItem(
    val username: String,
    val auctionHouse: String,
    val caseNumber: String,
    val location: String,
    val reservePrice: String,
    val auctionPeriod: String
)

data class DelegateCompletionItem(
    val username: String,
    val auctionHouse: String,
    val caseNumber: String,
    val location: String,
    val reservePrice: String,
    val auctionPeriod: String
)

data class NotificationItem(
    val auctionHouse: String,
    val location: String,
    val reservePrice: String,
    val auctionPeriod: String,
    val userId: String,
    val targetId: String,
    val itemImage: String,
    val phase: String
)