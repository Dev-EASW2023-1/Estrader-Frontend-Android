package kr.easw.estrader.android.definitions

import com.android.volley.Request
import kr.easw.estrader.android.model.dto.*
import kr.easw.estrader.android.util.RestRequestTemplate

object ApiDefinition {
    val LOGIN_PROCESS = RestRequestTemplate.Builder<SignInRequest, SignInResponse>()
        .setRequestHeaders(mutableMapOf("Content-Type" to "application/json"))
        .setRequestUrl("$SERVER_URL/user/login")
        .setResponseParams(SignInResponse::class.java)
        .setRequestMethod(Request.Method.PATCH)

    val CHECK_ID_DUPLICATED = RestRequestTemplate.Builder<SignupCheckRequest, SignupCheckResponse>()
        .setRequestHeaders(mutableMapOf("Content-Type" to "application/json"))
        .setRequestUrl("$SERVER_URL/user/account-exists")
        .setResponseParams(SignupCheckResponse::class.java)
        .setRequestMethod(Request.Method.POST)

    val REGISTER_PROCESS = RestRequestTemplate.Builder<RegisterDataRequest, RegisterDataResponse>()
        .setRequestHeaders(mutableMapOf("Content-Type" to "application/json"))
        .setRequestUrl("$SERVER_URL/user/register")
        .setResponseParams(RegisterDataResponse::class.java)
        .setRequestMethod(Request.Method.POST)

    val GET_ITEM_LIST = RestRequestTemplate.Builder<Void, ItemListDto>()
        .setRequestHeaders(mutableMapOf("Content-Type" to "application/json"))
        .setRequestUrl("$SERVER_URL/item/show-list")
        .setRequestParams(null)
        .setResponseParams(ItemListDto::class.java)
        .setRequestMethod(Request.Method.GET)

    val SEND_FCM = RestRequestTemplate.Builder<FcmRequest, FcmResponse>()
        .setRequestHeaders(mutableMapOf("Content-Type" to "application/json"))
        .setRequestUrl("$SERVER_URL/user/fcm")
        .setResponseParams(FcmResponse::class.java)
        .setRequestMethod(Request.Method.POST)

    val REALTOR_LOGIN_PROCESS = RestRequestTemplate.Builder<RealtorSignInRequest, RealtorSignInResponse>()
        .setRequestHeaders(mutableMapOf("Content-Type" to "application/json"))
        .setRequestUrl("$SERVER_URL/realtor/login")
        .setResponseParams(RealtorSignInResponse::class.java)
        .setRequestMethod(Request.Method.PATCH)

    val REALTOR_SEND_FCM = RestRequestTemplate.Builder<FcmRequest, FcmResponse>()
        .setRequestHeaders(mutableMapOf("Content-Type" to "application/json"))
        .setRequestUrl("$SERVER_URL/realtor/fcm")
        .setResponseParams(FcmResponse::class.java)
        .setRequestMethod(Request.Method.POST)

    val REALTOR_SHOW_ITEM = RestRequestTemplate.Builder<LookUpItemRequest, ItemDto>()
        .setRequestHeaders(mutableMapOf("Content-Type" to "application/json"))
        .setRequestUrl("$SERVER_URL/item/show")
        .setResponseParams(ItemDto::class.java)
        .setRequestMethod(Request.Method.POST)

    val SHOW_ITEM = RestRequestTemplate.Builder<LookUpItemRequest, ItemDto>()
        .setRequestHeaders(mutableMapOf("Content-Type" to "application/json"))
        .setRequestUrl("$SERVER_URL/item/show")
        .setResponseParams(ItemDto::class.java)
        .setRequestMethod(Request.Method.POST)

    val CONTRACT_COMPLETE = RestRequestTemplate.Builder<ContractRequest, ContractResponse>()
        .setRequestHeaders(mutableMapOf("Content-Type" to "application/json"))
        .setRequestUrl("$SERVER_URL/contract/complete")
        .setResponseParams(ContractResponse::class.java)
        .setRequestMethod(Request.Method.POST)

    val GET_CONTRACT_ITEM = RestRequestTemplate.Builder<ItemInContractDto, ItemDto>()
        .setRequestHeaders(mutableMapOf("Content-Type" to "application/json"))
        .setRequestUrl("$SERVER_URL/contract/item")
        .setResponseParams(ItemDto::class.java)
        .setRequestMethod(Request.Method.POST)

    val GET_CONTRACT_INFO = RestRequestTemplate.Builder<ContractInfoRequest, ContractInfoResponse>()
        .setRequestHeaders(mutableMapOf("Content-Type" to "application/json"))
        .setRequestUrl("$SERVER_URL/contract/find-info")
        .setResponseParams(ContractInfoResponse::class.java)
        .setRequestMethod(Request.Method.POST)
}