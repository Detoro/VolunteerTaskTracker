package tofunmi.volunteer.volunteertasktracker.models

enum class UserRole { ORGANIZATION, SUBSCRIBER }

data class UserProfile(
    val id: String = "",
    val name: String = "",
    val role: UserRole = UserRole.SUBSCRIBER
)

data class VolunteerGroup(
    val id: String,
    val orgId: String,             // Which organization owns this group
    val name: String,              // e.g., "Marketing Team" or "Weekend Crew"
    val memberIds: List<String>    // We store IDs, not full profiles, to keep the database fast and clean!
)