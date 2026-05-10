package app

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class Screen {
    LOGIN, MAIN
}

data class UIState(
    val currentScreen: Screen = Screen.LOGIN,
    val isSignUpMode: Boolean = false,
    val currentTab: Int = 0,
    val username: String = "",
    val password: String = "",
    val selectedRole: UserRole = UserRole.STATION_OFFICER,
    val statusMessage: String = "",
    val personnel: List<Personnel> = AppState.personnel,
    val dutyPoints: List<DutyPoint> = AppState.dutyPoints,
    val roster: List<RosterAssignment> = emptyList()
)

@Composable
fun App() {
    val isDarkTheme = isSystemInDarkTheme()
    val colorScheme = if (isDarkTheme) darkColorScheme() else lightColorScheme()
    
    MaterialTheme(colorScheme = colorScheme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            DutyRosterApp()
        }
    }
}

@Composable
fun DutyRosterApp() {
    var uiState by remember { mutableStateOf(UIState()) }
    
    when (uiState.currentScreen) {
        Screen.LOGIN -> {
            LoginScreen(uiState) { newState ->
                uiState = newState
            }
        }
        Screen.MAIN -> {
            MainScreen(uiState) { newState ->
                uiState = newState
            }
        }
    }
}

@Composable
fun LoginScreen(state: UIState, onStateChange: (UIState) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Dunkwa FPU Weekly Duty Roster",
            fontSize = 24.sp,
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        var username by remember { mutableStateOf(state.username) }
        var password by remember { mutableStateOf(state.password) }
        var selectedRole by remember { mutableStateOf(state.selectedRole) }
        var statusMessage by remember { mutableStateOf(state.statusMessage) }
        var isSignUpMode by remember { mutableStateOf(state.isSignUpMode) }

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(vertical = 8.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(vertical = 8.dp)
        )

        if (isSignUpMode) {
            var selectedRoleLocal by remember { mutableStateOf(UserRole.STATION_OFFICER) }
            var expandedRole by remember { mutableStateOf(false) }

            Box {
                OutlinedButton(
                    onClick = { expandedRole = true },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .padding(vertical = 8.dp)
                ) {
                    Text("Role: ${selectedRoleLocal.name}")
                }
                DropdownMenu(
                    expanded = expandedRole,
                    onDismissRequest = { expandedRole = false }
                ) {
                    UserRole.values().forEach { role ->
                        DropdownMenuItem(
                            text = { Text(role.name) },
                            onClick = {
                                selectedRoleLocal = role
                                expandedRole = false
                            }
                        )
                    }
                }
            }
            selectedRole = selectedRoleLocal
        }

        if (statusMessage.isNotEmpty()) {
            Text(
                text = statusMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        Button(
            onClick = {
                val uname = username.trim()
                val pwd = password.trim()
                if (uname.isEmpty() || pwd.isEmpty()) {
                    statusMessage = "Please enter both username and password."
                    return@Button
                }

                if (isSignUpMode) {
                    if (AppState.accounts.any { it.username.equals(uname, true) }) {
                        statusMessage = "Username already exists."
                        return@Button
                    }
                    val newUser = UserAccount(uname, pwd, selectedRole)
                    AppState.accounts.add(newUser)
                    AppState.currentUser = newUser
                    onStateChange(UIState(currentScreen = Screen.MAIN))
                } else {
                    val user = AppState.accounts.find { it.username.equals(uname, true) && it.password == pwd }
                    if (user == null) {
                        statusMessage = "Invalid username or password."
                    } else {
                        AppState.currentUser = user
                        onStateChange(UIState(currentScreen = Screen.MAIN))
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .padding(vertical = 16.dp)
        ) {
            Text(if (isSignUpMode) "Sign Up" else "Login")
        }

        OutlinedButton(
            onClick = {
                isSignUpMode = !isSignUpMode
                statusMessage = ""
            },
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .padding(vertical = 8.dp)
        ) {
            Text(if (isSignUpMode) "Switch to Login" else "Switch to Sign Up")
        }
    }
}

@Composable
fun MainScreen(state: UIState, onStateChange: (UIState) -> Unit) {
    var currentTab by remember { mutableStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Logged in as: ${AppState.currentUser?.username ?: "Unknown"} [${AppState.currentUser?.role ?: ""}]",
                style = MaterialTheme.typography.bodyMedium
            )
            Button(onClick = {
                AppState.currentUser = null
                onStateChange(UIState(currentScreen = Screen.LOGIN))
            }) {
                Text("Logout")
            }
        }

        Divider()

        // Tabs
        TabRow(selectedTabIndex = currentTab) {
            Tab(selected = currentTab == 0, onClick = { currentTab = 0 }, text = { Text("Personnel") })
            Tab(selected = currentTab == 1, onClick = { currentTab = 1 }, text = { Text("Duty Points") })
            Tab(selected = currentTab == 2, onClick = { currentTab = 2 }, text = { Text("Preview Roster") })
        }

        // Tab Content
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
        ) {
            when (currentTab) {
                0 -> PersonnelTab(state, onStateChange)
                1 -> DutyPointsTab(state, onStateChange)
                2 -> RosterTab(state, onStateChange)
            }
        }
    }
}

@Composable
fun PersonnelTab(state: UIState, onStateChange: (UIState) -> Unit) {
    val isStationOfficer = AppState.currentUser?.role == UserRole.STATION_OFFICER
    var showAddDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        if (isStationOfficer) {
            Button(
                onClick = { showAddDialog = true },
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text("Add Personnel")
            }
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(AppState.personnel) { person ->
                PersonnelItem(person = person, isStationOfficer = isStationOfficer, onDelete = {
                    AppState.personnel.remove(person)
                    onStateChange(state.copy(personnel = AppState.personnel.toList()))
                })
            }
        }
    }

    if (showAddDialog) {
        AddPersonnelDialog(onDismiss = { showAddDialog = false }) { personnel ->
            AppState.personnel.add(personnel)
            onStateChange(state.copy(personnel = AppState.personnel.toList()))
            showAddDialog = false
        }
    }
}

@Composable
fun PersonnelItem(person: Personnel, isStationOfficer: Boolean, onDelete: () -> Unit) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("${person.rank} ${person.name}")
                Text("${person.gender} - ${person.serviceNumber}", style = MaterialTheme.typography.bodySmall)
                Text(person.phone, style = MaterialTheme.typography.bodySmall)
            }
            if (isStationOfficer) {
                Button(onClick = onDelete) {
                    Text("Delete")
                }
            }
        }
    }
}

@Composable
fun AddPersonnelDialog(onDismiss: () -> Unit, onAdd: (Personnel) -> Unit) {
    var name by remember { mutableStateOf("") }
    var rank by remember { mutableStateOf("") }
    var serviceNumber by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("M") }
    var phone by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Personnel") },
        text = {
            Column {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Full Name") })
                OutlinedTextField(value = rank, onValueChange = { rank = it }, label = { Text("Rank") })
                OutlinedTextField(value = serviceNumber, onValueChange = { serviceNumber = it }, label = { Text("Service Number") })
                OutlinedTextField(value = gender, onValueChange = { gender = it }, label = { Text("Gender (M/F/PW)") })
                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone") })
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotEmpty()) {
                        val id = "p${AppState.personnel.size + 1}"
                        onAdd(Personnel(id, rank.ifEmpty { "Constable" }, name, serviceNumber.ifEmpty { "000" }, gender.ifEmpty { "M" }, phone.ifEmpty { "N/A" }))
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun DutyPointsTab(state: UIState, onStateChange: (UIState) -> Unit) {
    val isStationOfficer = AppState.currentUser?.role == UserRole.STATION_OFFICER
    var showAddDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        if (isStationOfficer) {
            Button(
                onClick = { showAddDialog = true },
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text("Add Duty Point")
            }
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(AppState.dutyPoints) { dutyPoint ->
                DutyPointItem(dutyPoint = dutyPoint, isStationOfficer = isStationOfficer, onDelete = {
                    AppState.dutyPoints.remove(dutyPoint)
                    onStateChange(state.copy(dutyPoints = AppState.dutyPoints.toList()))
                })
            }
        }
    }

    if (showAddDialog) {
        AddDutyPointDialog(onDismiss = { showAddDialog = false }) { dutyPoint ->
            AppState.dutyPoints.add(dutyPoint)
            onStateChange(state.copy(dutyPoints = AppState.dutyPoints.toList()))
            showAddDialog = false
        }
    }
}

@Composable
fun DutyPointItem(dutyPoint: DutyPoint, isStationOfficer: Boolean, onDelete: () -> Unit) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(dutyPoint.name)
                Text("PW Allowed: ${if (dutyPoint.allowsPw) "Yes" else "No"}", style = MaterialTheme.typography.bodySmall)
            }
            if (isStationOfficer) {
                Button(onClick = onDelete) {
                    Text("Delete")
                }
            }
        }
    }
}

@Composable
fun AddDutyPointDialog(onDismiss: () -> Unit, onAdd: (DutyPoint) -> Unit) {
    var name by remember { mutableStateOf("") }
    var allowsPw by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Duty Point") },
        text = {
            Column {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Duty Point Name") })
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = allowsPw, onCheckedChange = { allowsPw = it })
                    Text("Allow PW")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotEmpty()) {
                        val id = "d${AppState.dutyPoints.size + 1}"
                        onAdd(DutyPoint(id, name, allowsPw))
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun RosterTab(state: UIState, onStateChange: (UIState) -> Unit) {
    val isStationOfficer = AppState.currentUser?.role == UserRole.STATION_OFFICER
    var roster by remember { mutableStateOf(AppState.roster) }

    Column(modifier = Modifier.fillMaxSize()) {
        if (isStationOfficer) {
            Button(
                onClick = {
                    roster = generateWeeklyRoster(AppState.personnel, AppState.dutyPoints)
                    AppState.roster = roster
                    onStateChange(state.copy(roster = roster))
                },
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text("Generate Weekly Roster")
            }
        }

        Text(
            text = formatRoster(roster),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(8.dp)
        )
    }
}

@Composable
expect fun isSystemInDarkTheme(): Boolean
