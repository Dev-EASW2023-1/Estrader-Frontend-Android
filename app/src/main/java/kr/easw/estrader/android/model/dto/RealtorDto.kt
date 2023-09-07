package kr.easw.estrader.android.model.dto


data class RealtorSignInRequest(
    val realtorId: String,
    val password: String,
    val fcmToken: String
)

data class RealtorSignInResponse(
    val isSuccess: Boolean,
    val message: String,
    val token: String
)

data class RealtorRegisterDataRequest(
    val realtorId: String,
    val password: String,
    val name: String,
    val residentNumber: String,
    val phoneNumber: String,
    val address: String,
    val corporateRegistrationNumber: String,
    val fcmToken: String,
    val region: String
)

data class RealtorRegisterDataResponse(
    val isSuccess: Boolean,
    val message: String,
    val token: String
)

data class RealtorSignupCheckRequest(
    val realtorId: String
)

data class RealtorSignupCheckResponse(
    val isDuplicated: Boolean,
    val message: String
)