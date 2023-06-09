package kr.easw.estrader.android.model.dto

data class ContractRequest(
    val userId: String,
    val realtorId: String,
    val itemId: String,
    val phase: String,
    val title: String,
    val body: String
)

data class ContractResponse(
    val isSuccess: Boolean,
    val message: String,
    val name: String
)

data class ItemInContractDto(
    val userId: String,
    val realtorId: String,
    val itemId: String
)

data class ContractInfoRequest(
    val userId: String,
    val realtorId: String,
    val itemId: String
)

data class ContractInfoResponse(
    val biddingPeriod: String,
    val caseNumber: String,
    val minimumBidPrice: String,
    val managementNumber: String
)