package tofunmi.volunteer.volunteertasktracker.models

import com.google.gson.annotations.SerializedName
import java.util.Date

sealed class DashboardItem {
    abstract var id: String?
    abstract val orgId: String?
    abstract val title: String?
    abstract val description: String?
    abstract val isCompleted: Boolean
    abstract val assigner: UserProfile?
    abstract val assignee: UserProfile?
    abstract val assignedDateTime: Date?
    abstract val expiryDate: Date?
    abstract var startTimestamp: Date?
    abstract var endTimestamp: Date?

    data class Task(
        override var id: String? = "",
        @SerializedName("org_id")
        override val orgId: String? = "",
        override val title: String? = "",
        val goal: String? = "",
        override val description: String? = "",
        @SerializedName("is_completed")
        override val isCompleted: Boolean = false,
        @SerializedName("assigner_id")
        override val assigner: UserProfile? = null,
        @SerializedName("assignee_id")
        override val assignee: UserProfile? = null,
        override val assignedDateTime: Date? = Date(),
        override val expiryDate: Date? = null,
        @SerializedName("start_time")
        override var startTimestamp: Date? = null,
        @SerializedName("end_time")
        override var endTimestamp: Date? = null
    ) : DashboardItem()

    // 3. The Goal: Inherits the shared fields PLUS its 1 unique field
    data class Goal(
        override var id: String? = "",
        override val title: String? = "",
        override val description: String? = "",
        override val isCompleted: Boolean = false,
        val progressPercentage: Int,
        val assignedGroupId: String?,
        override val orgId: String?,
        override val assigner: UserProfile?,
        override val assignee: UserProfile?,
        override val assignedDateTime: Date?,
        override val expiryDate: Date?,
        override var startTimestamp: Date? = null,
        override var endTimestamp: Date? = null
    ) : DashboardItem()
}