package kr.easw.estrader.android.model.dto

data class MainItem(
    val iconDrawable: String,
    val auctionHouse: String,
    val caseNumber: String,
    val location: String,
    val reservePrice: String,
    val auctionPeriod: String
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