package kr.easw.estrader.android.model.dto

data class UserDto (
    val picture: String,
    val information: String,
    val period: String
)

data class UserListDto (
    val userDto: List<UserDto>
)