package kr.easw.estrader.android.model.dto

data class RegisterDataRequest(
    val userId: String,
    val password: String,
    val name: String,
    val residentNumber: String,
    val phoneNumber: String,
    val address: String,
    val corporateRegistrationNumber: String,
    val fcmToken: String,
    val region: String
)

data class RegisterDataResponse(
    val isSuccess: Boolean,
    val message: String
)

data class SignupCheckRequest(
    val userId: String
)

data class SignupCheckResponse(
    val isDuplicated: Boolean,
    val message: String
)

data class SignInRequest(
    val userId: String,
    val password: String,
    val fcmToken: String
)

data class SignInResponse(
    val isSuccess: Boolean,
    val message: String
)

data class FcmRequest(
    val userId: String,
    val targetId: String,
    val itemImage: String,
    val phase: String,
    val title: String,
    val body: String
)

data class FcmResponse(
    val isSuccess: Boolean,
    val message: String
)