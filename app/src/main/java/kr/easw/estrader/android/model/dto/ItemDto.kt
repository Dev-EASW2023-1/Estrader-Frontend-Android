package kr.easw.estrader.android.model.dto

data class ItemDto (
    val caseNumber: String,
    val court: String,
    val location: String,
    val minimumBidPrice: String,
    val photo: String,
    val biddingPeriod: String,
    val itemType: String,
    val note: String,
    val managementNumber: String,
    val xcoordinate : String,
    val ycoordinate : String
)

data class ItemListDto (
    val itemDto: List<ItemDto>
)

data class LookUpItemRequest(
    val photo: String
)