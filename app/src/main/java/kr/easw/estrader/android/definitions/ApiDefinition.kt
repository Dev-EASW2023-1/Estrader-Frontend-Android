package kr.easw.estrader.android.definitions

import com.android.volley.Request
import kr.easw.estrader.android.model.dto.ContractInfoRequest
import kr.easw.estrader.android.model.dto.ContractInfoResponse
import kr.easw.estrader.android.model.dto.ContractRequest
import kr.easw.estrader.android.model.dto.ContractResponse
import kr.easw.estrader.android.model.dto.DistrictRequest
import kr.easw.estrader.android.model.dto.DistrictResponse
import kr.easw.estrader.android.model.dto.FcmRequest
import kr.easw.estrader.android.model.dto.FcmResponse
import kr.easw.estrader.android.model.dto.ItemDto
import kr.easw.estrader.android.model.dto.ItemInContractDto
import kr.easw.estrader.android.model.dto.ItemListDto
import kr.easw.estrader.android.model.dto.LookUpItemRequest
import kr.easw.estrader.android.model.dto.RealtorRegisterDataRequest
import kr.easw.estrader.android.model.dto.RealtorRegisterDataResponse
import kr.easw.estrader.android.model.dto.RealtorSignInRequest
import kr.easw.estrader.android.model.dto.RealtorSignInResponse
import kr.easw.estrader.android.model.dto.RealtorSignupCheckRequest
import kr.easw.estrader.android.model.dto.RealtorSignupCheckResponse
import kr.easw.estrader.android.model.dto.RegisterDataRequest
import kr.easw.estrader.android.model.dto.RegisterDataResponse
import kr.easw.estrader.android.model.dto.SignInRequest
import kr.easw.estrader.android.model.dto.SignInResponse
import kr.easw.estrader.android.model.dto.SignupCheckRequest
import kr.easw.estrader.android.model.dto.SignupCheckResponse
import kr.easw.estrader.android.util.RestRequestTemplate

object ApiDefinition {
    val LOGIN_PROCESS = RestRequestTemplate.Builder<SignInRequest, SignInResponse>()
        .setRequestHeaders(mutableMapOf("Content-Type" to "application/json"))
        .setRequestUrl("$SERVER_URL/auth/user/login")
        .setResponseParams(SignInResponse::class.java)
        .setRequestMethod(Request.Method.PATCH)

    val CHECK_ID_DUPLICATED = RestRequestTemplate.Builder<SignupCheckRequest, SignupCheckResponse>()
        .setRequestHeaders(mutableMapOf("Content-Type" to "application/json"))
        .setRequestUrl("$SERVER_URL/auth/user/account-exists")
        .setResponseParams(SignupCheckResponse::class.java)
        .setRequestMethod(Request.Method.POST)

    val REGISTER_PROCESS = RestRequestTemplate.Builder<RegisterDataRequest, RegisterDataResponse>()
        .setRequestHeaders(mutableMapOf("Content-Type" to "application/json"))
        .setRequestUrl("$SERVER_URL/auth/user/register")
        .setResponseParams(RegisterDataResponse::class.java)
        .setRequestMethod(Request.Method.POST)

    fun GET_ITEM_LIST(district: String, page: Int, size: Int) = RestRequestTemplate.Builder<Void, ItemListDto>()
        .setRequestHeaders(mutableMapOf("Content-Type" to "application/json"))
        .setRequestUrl("$SERVER_URL/item/show-list?district=$district&page=$page&size=$size")
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
        .setRequestUrl("$SERVER_URL/auth/realtor/login")
        .setResponseParams(RealtorSignInResponse::class.java)
        .setRequestMethod(Request.Method.PATCH)

    val REALTOR_CHECK_ID_DUPLICATED = RestRequestTemplate.Builder<RealtorSignupCheckRequest, RealtorSignupCheckResponse>()
        .setRequestHeaders(mutableMapOf("Content-Type" to "application/json"))
        .setRequestUrl("$SERVER_URL/auth/realtor/account-exists")
        .setResponseParams(RealtorSignupCheckResponse::class.java)
        .setRequestMethod(Request.Method.POST)

    val REALTOR_REGISTER_PROCESS = RestRequestTemplate.Builder<RealtorRegisterDataRequest, RealtorRegisterDataResponse>()
        .setRequestHeaders(mutableMapOf("Content-Type" to "application/json"))
        .setRequestUrl("$SERVER_URL/auth/realtor/register")
        .setResponseParams(RealtorRegisterDataResponse::class.java)
        .setRequestMethod(Request.Method.POST)

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

//     val GET_LOCATION_INFO = RestRequestTemplate.Builder<Unit, DistrictResponse>() -> Unit은  Kotlin에서 아무 값도 갖지 않는, 특별한 타입
    val GET_LOCATION_INFO = RestRequestTemplate.Builder<DistrictRequest, DistrictResponse>()
        .setRequestHeaders(mutableMapOf("Content-Type" to "application/json"))
        .setRequestUrl("$SERVER_URL/item/map")
        .setResponseParams(DistrictResponse::class.java)
        .setRequestMethod(Request.Method.POST)
}
