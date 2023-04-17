package kr.easw.estrader.android.definitions

import com.android.volley.Request
import kr.easw.estrader.android.model.dto.*
import kr.easw.estrader.android.util.RestRequestTemplate


object ApiDefinition {
    val LOGIN_PROCESS = RestRequestTemplate.Builder<SignInRequest, SignInResponse>()
        .setRequestHeaders(mutableMapOf("Content-Type" to "application/json"))
        .setRequestUrl("http://172.17.0.30:8060/user/login")
        .setResponseParams(SignInResponse::class.java)
        .setRequestMethod(Request.Method.POST)

    val CHECK_ID_DUPLICATED = RestRequestTemplate.Builder<SignupCheckRequest, SignupCheckResponse>()
        .setRequestHeaders(mutableMapOf("Content-Type" to "application/json"))
        .setRequestUrl("http://172.17.0.30:8060/user/account-exists")
        .setResponseParams(SignupCheckResponse::class.java)
        .setRequestMethod(Request.Method.POST)

    val REGISTER_PROCESS = RestRequestTemplate.Builder<RegisterDataRequest, RegisterDataResponse>()
        .setRequestHeaders(mutableMapOf("Content-Type" to "application/json"))
        .setRequestUrl("http://172.17.0.30:8060/user/register")
        .setResponseParams(RegisterDataResponse::class.java)
        .setRequestMethod(Request.Method.POST)

    val GET_ITEM_LIST = RestRequestTemplate.Builder<Void, ItemListDto>()
        .setRequestHeaders(mutableMapOf("Content-Type" to "application/json"))
        .setRequestUrl("http://172.17.0.30:8060/item/show-list")
        .setRequestParams(null)
        .setResponseParams(ItemListDto::class.java)
        .setRequestMethod(Request.Method.GET)
}