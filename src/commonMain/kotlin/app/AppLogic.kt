package app

object AppState {
    val accounts = mutableListOf(
        UserAccount("station", "officer123", UserRole.STATION_OFFICER),
        UserAccount("secretary", "viewonly123", UserRole.SECRETARY)
    )

    val personnel = mutableListOf(
        Personnel("p1", "C/Inspr", "Emmanuel Bruce", "13697", "M", "0243449418"),
        Personnel("p2", "Inspr", "Williams Yeboah", "00002", "M", "0244482034"),
        Personnel("p3", "Constable", "Rita A. Piatoo", "13697", "PW", "0241764509"),
        Personnel("p4", "Constable", "Ofori Joycelyn", "13824", "PW", "0559366698"),
        Personnel("p5", "Inspr", "Gillian Tetteh", "00003", "PW", "0543419209"),
        Personnel("p6", "G/Const.", "Prince Osei Yankson", "61287", "M", "0554980995"),
        Personnel("p7", "G/Const.", "Kumah Yaw Michael", "62130", "M", "0554080263"),
        Personnel("p8", "PW/Const.", "Abigail Boakye", "13796", "PW", "0547364640"),
        Personnel("p9", "Inspr", "Bismark Sarfo", "00004", "M", "0264874499")
    )

    val dutyPoints = mutableListOf(
        DutyPoint("d1", "KPK Day", true),
        DutyPoint("d2", "KPK Night", true),
        DutyPoint("d3", "Ayanfuri One", true),
        DutyPoint("d4", "Pokukrom", true),
        DutyPoint("d5", "MTN Office", true),
        DutyPoint("d6", "Station Guard", false),
        DutyPoint("d7", "Day Patrols", false)
    )

    var currentUser: UserAccount? = null
    var roster: List<RosterAssignment> = emptyList()
}

fun generateWeeklyRoster(personnel: List<Personnel>, dutyPoints: List<DutyPoint>): List<RosterAssignment> {
    val activePersonnel = personnel.filterNot { it.onStandby }
    if (activePersonnel.isEmpty()) return emptyList()

    return dutyPoints.map { dutyPoint ->
        val eligible = activePersonnel.filter { person ->
            val isPw = person.gender.equals("PW", true) || person.gender.equals("F", true)
            !isPw || dutyPoint.allowsPw
        }

        val assignment = mutableListOf<Personnel>()

        if (dutyPoint.name.contains("Pokukrom", true)) {
            val seniors = eligible.filter { it.rank.contains("C/Inspr", true) || it.rank.contains("Inspr", true) }
            val pws = eligible.filter { it.gender.equals("PW", true) || it.gender.equals("F", true) }
            val males = eligible.filter { !it.gender.equals("PW", true) && !it.gender.equals("F", true) }

            pws.firstOrNull()?.let { assignment.add(it) }
            if (assignment.size < 2) seniors.firstOrNull { it !in assignment }?.let { assignment.add(it) }
            val extra = males.filter { it !in assignment }.take(3)
            assignment.addAll(extra)
        } else {
            val sorted = eligible.sortedWith(compareBy({ !it.gender.equals("PW", true) && !it.gender.equals("F", true) }, { it.rank }, { it.name }))
            assignment.addAll(sorted.take(4))
        }

        RosterAssignment(dutyPoint, assignment.distinct().take(4))
    }
}

fun formatRoster(roster: List<RosterAssignment>): String {
    if (roster.isEmpty()) {
        return "No roster generated yet. Station Officer may generate the weekly roster."
    }
    val builder = StringBuilder()
    roster.forEach { assignment ->
        builder.append("${assignment.dutyPoint.name} (${if (assignment.dutyPoint.allowsPw) "PW allowed" else "No PW"})\n")
        if (assignment.assignedPersonnel.isEmpty()) {
            builder.append("  No personnel assigned.\n")
        } else {
            assignment.assignedPersonnel.forEachIndexed { index, person ->
                builder.append("  ${index + 1}. ${person.rank} ${person.name} (${person.gender}) - ${person.serviceNumber}\n")
            }
        }
        builder.append("\n")
    }
    return builder.toString()
}
