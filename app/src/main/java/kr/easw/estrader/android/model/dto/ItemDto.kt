package kr.easw.estrader.android.model.dto

data class ItemDto (
    val picture: String,
    val information: String,
    val period: String,
    val location: String,
    val reserveprice: String,
    val auctionperiod: String
)

data class ItemListDto (
    val itemDto: List<ItemDto>
)