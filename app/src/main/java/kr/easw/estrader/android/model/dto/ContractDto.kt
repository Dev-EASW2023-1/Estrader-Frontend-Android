package kr.easw.estrader.android.model.dto

data class ContractRequest(
    val userId: String,
    val representativeId: String,
    val itemId: String
)

data class ContractResponse(
    val isSuccess: Boolean,
    val message: String
)

data class ItemInContractDto(
    val userId: String,
    val representativeId: String,
    val itemId: String
)