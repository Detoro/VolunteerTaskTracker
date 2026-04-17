package tofunmi.volunteer.volunteertasktracker.models

import com.google.gson.annotations.SerializedName
import java.util.Date

enum class UserRole { ORGANIZATION, SUBSCRIBER }

data class UserProfile(
    val id: String = "",
    val name: String = "",
    val role: UserRole = UserRole.SUBSCRIBER
)

data class LoginCredentials(
    val email: String,
    val password: String
)

data class SignUpPayload(
    val id: String,
    val name: String,
    @SerializedName("org_id")
    val orgId: String? = null,
    @SerializedName("org_name")
    val orgName: String? = null,
    val email: String,
    val password: String,
    val role: UserRole
)
data class VolunteerGroup(
    val id: String,
    val orgId: String,
    val name: String,
    val memberIds: List<String>
)