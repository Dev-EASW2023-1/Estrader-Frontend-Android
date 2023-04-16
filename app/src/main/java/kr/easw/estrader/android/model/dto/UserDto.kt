package kr.easw.estrader.android.model.dto

data class RegisterDataRequest(
    val userid: String,
    val password: String,
    val residentid: String,
    val phonenum: String,
    val address: String
)

data class RegisterDataResponse(
    val isSuccess: Boolean,
    val message: String
)

data class SignupCheckRequest(
    val userid: String
)

data class SignupCheckResponse(
    val isDuplicated: Boolean,
    val message: String
)

data class SignInRequest(
    val userid: String,
    val password: String
)

data class SignInResponse(
    val isSuccess: Boolean,
    val message: String
)