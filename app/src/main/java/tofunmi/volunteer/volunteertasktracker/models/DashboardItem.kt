package tofunmi.volunteer.volunteertasktracker.models

import java.util.Date

data class LoginCredentials(
    val email: String,
    val password: String
)

sealed class DashboardItem {
    abstract var id: String
    abstract val orgId: String
    abstract val title: String
    abstract val description: String
    abstract val isCompleted: Boolean
    abstract val assigner: UserProfile
    abstract val assignee: UserProfile?
    abstract val assignedDateTime: Date
    abstract val expiryDate: Date?

    data class Task(
        override var id: String = "",
        override val orgId: String = "",
        override val title: String = "",
        val goal: String? = "",
        override val description: String = "",
        override val isCompleted: Boolean = false,
        override val assigner: UserProfile,
        override val assignee: UserProfile? = null,
        override val assignedDateTime: Date = Date(),
        override val expiryDate: Date? = null,
    ) : DashboardItem()

    // 3. The Goal: Inherits the shared fields PLUS its 1 unique field
    data class Goal(
        override var id: String = "",
        override val title: String = "",
        override val description: String = "",
        override val isCompleted: Boolean = false,
        val progressPercentage: Int,
        val assignedGroupId: String?,
        override val orgId: String,
        override val assigner: UserProfile,
        override val assignee: UserProfile?,
        override val assignedDateTime: Date,
        override val expiryDate: Date?,
    ) : DashboardItem()
}