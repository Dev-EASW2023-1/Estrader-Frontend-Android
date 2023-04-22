package kr.easw.estrader.android.model.dto


data class RepresentativeSignInRequest(
    val username: String,
    val password: String,
    val fcmToken: String
)

data class RepresentativeSignInResponse(
    val isSuccess: Boolean,
    val message: String
)