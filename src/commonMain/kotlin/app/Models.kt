package app

enum class UserRole {
    STATION_OFFICER,
    SECRETARY
}

data class UserAccount(val username: String, val password: String, val role: UserRole)

data class Personnel(
    val id: String,
    val rank: String,
    val name: String,
    val serviceNumber: String,
    val gender: String,
    val phone: String,
    val onStandby: Boolean = false
)

data class DutyPoint(val id: String, val name: String, val allowsPw: Boolean)

data class RosterAssignment(val dutyPoint: DutyPoint, val assignedPersonnel: List<Personnel>)
