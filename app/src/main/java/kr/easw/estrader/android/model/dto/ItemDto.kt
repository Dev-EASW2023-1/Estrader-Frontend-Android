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
    val xcoordinate: String,
    val ycoordinate: String,
    val district: String
)
data class ItemPageRequestDTO (
    val district: String,
    val page: Int,
    val size: Int
)

data class ItemListDto (
    val itemDto: List<ItemDto>
)

data class LookUpItemRequest(
    val photo: String
)