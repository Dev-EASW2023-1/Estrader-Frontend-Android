package kr.easw.estrader.android.model.dto

import android.graphics.drawable.Drawable

data class MainItem(
    val iconDrawable: Drawable,
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