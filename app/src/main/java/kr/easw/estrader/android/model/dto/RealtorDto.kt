package kr.easw.estrader.android.model.dto


data class RealtorSignInRequest(
    val realtorId: String,
    val password: String,
    val fcmToken: String
)

data class RealtorSignInResponse(
    val isSuccess: Boolean,
    val message: String
)