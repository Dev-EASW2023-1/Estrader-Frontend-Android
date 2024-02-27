package kr.easw.estrader.android.model.dto

data class DelegatedDto(
    val feeid : Long,
    val user : Long,
    val realtor : Long
)
data class RealtorfeeDto(
    val itemid : Long,
    val realtor : String,
    val fee : String,
    val isdealing : Boolean
)

data class UserHopePriceDto(
    val casenumber : String,
    val user : String,
    val price : String
)