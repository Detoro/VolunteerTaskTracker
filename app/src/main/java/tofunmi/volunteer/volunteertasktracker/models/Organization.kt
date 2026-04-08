package tofunmi.volunteer.volunteertasktracker.models

enum class CardType { ORGANIZATION, TASK }

data class Organization(
    val id: String,
    val title: String,
    val name: String,
    val description: String = ""
)