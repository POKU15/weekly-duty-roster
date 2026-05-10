package app

import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.util.*
import javax.swing.*
import javax.swing.border.EmptyBorder

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

fun main() {
    SwingUtilities.invokeLater { createAndShowGui() }
}

private fun createAndShowGui() {
    val frame = JFrame("Dunkwa FPU Weekly Duty Roster")
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    frame.minimumSize = Dimension(900, 600)

    val contentPane = JPanel(CardLayout())
    val loginPanel = createLoginPanel(frame, contentPane)
    val mainPanel = createMainPanel(frame, contentPane)

    contentPane.add(loginPanel, "login")
    contentPane.add(mainPanel, "main")
    frame.contentPane = contentPane
    (contentPane.layout as CardLayout).show(contentPane, "login")

    frame.pack()
    frame.setLocationRelativeTo(null)
    frame.isVisible = true
}

private fun createLoginPanel(frame: JFrame, contentPane: JPanel): JPanel {
    val panel = JPanel(BorderLayout())
    panel.border = EmptyBorder(24, 24, 24, 24)

    val title = JLabel("Dunkwa FPU Weekly Duty Roster", SwingConstants.CENTER)
    title.font = title.font.deriveFont(Font.BOLD, 24f)
    panel.add(title, BorderLayout.NORTH)

    val center = JPanel(GridBagLayout())
    val gbc = GridBagConstraints().apply {
        gridx = 0
        weightx = 1.0
        anchor = GridBagConstraints.WEST
        fill = GridBagConstraints.HORIZONTAL
        insets = Insets(8, 8, 8, 8)
    }

    val usernameField = JTextField(24)
    val passwordField = JPasswordField(24)
    val roleCombo = JComboBox(UserRole.values())
    val modeButton = JButton("Switch to Sign Up")
    val actionButton = JButton("Login")
    val statusLabel = JLabel(" ")

    val signUpMode = booleanArrayOf(false)
    roleCombo.isEnabled = false

    center.add(JLabel("Username:"), gbc)
    gbc.gridx = 1
    center.add(usernameField, gbc)
    gbc.gridx = 0
    gbc.gridy = 1
    center.add(JLabel("Password:"), gbc)
    gbc.gridx = 1
    center.add(passwordField, gbc)
    gbc.gridx = 0
    gbc.gridy = 2
    center.add(JLabel("Role:"), gbc)
    gbc.gridx = 1
    center.add(roleCombo, gbc)
    gbc.gridx = 0
    gbc.gridy = 3
    gbc.gridwidth = 2
    center.add(actionButton, gbc)
    gbc.gridy = 4
    center.add(modeButton, gbc)
    gbc.gridy = 5
    center.add(statusLabel, gbc)

    actionButton.addActionListener {
        val username = usernameField.text.trim()
        val password = String(passwordField.password).trim()
        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.text = "Please enter both username and password."
            return@addActionListener
        }
        if (signUpMode[0]) {
            if (AppState.accounts.any { it.username.equals(username, true) }) {
                statusLabel.text = "Username already exists."
                return@addActionListener
            }
            val newUser = UserAccount(username, password, roleCombo.selectedItem as UserRole)
            AppState.accounts.add(newUser)
            AppState.currentUser = newUser
            statusLabel.text = "Account created. Logging in..."
            switchToMain(contentPane)
        } else {
            val user = AppState.accounts.find { it.username.equals(username, true) && it.password == password }
            if (user == null) {
                statusLabel.text = "Invalid username or password."
            } else {
                AppState.currentUser = user
                switchToMain(contentPane)
            }
        }
    }

    modeButton.addActionListener {
        signUpMode[0] = !signUpMode[0]
        roleCombo.isEnabled = signUpMode[0]
        actionButton.text = if (signUpMode[0]) "Sign Up" else "Login"
        modeButton.text = if (signUpMode[0]) "Switch to Login" else "Switch to Sign Up"
        statusLabel.text = " "
    }

    panel.add(center, BorderLayout.CENTER)
    return panel
}

private fun switchToMain(contentPane: JPanel) {
    val layout = contentPane.layout as CardLayout
    layout.show(contentPane, "main")
}

private fun createMainPanel(frame: JFrame, contentPane: JPanel): JPanel {
    val panel = JPanel(BorderLayout())
    panel.border = EmptyBorder(16, 16, 16, 16)

    val userLabel = JLabel("Logged in as: ")
    userLabel.font = userLabel.font.deriveFont(Font.BOLD, 14f)
    val logoutButton = JButton("Logout")
    val header = JPanel(BorderLayout())
    header.add(userLabel, BorderLayout.WEST)
    header.add(logoutButton, BorderLayout.EAST)
    panel.add(header, BorderLayout.NORTH)

    val tabs = JTabbedPane()
    val personnelPanel = createPersonnelPanel()
    val dutyPointPanel = createDutyPointPanel()
    val rosterPanel = createRosterPanel()

    tabs.addTab("Personnel", personnelPanel)
    tabs.addTab("Duty Points", dutyPointPanel)
    tabs.addTab("Preview Roster", rosterPanel)
    panel.add(tabs, BorderLayout.CENTER)

    logoutButton.addActionListener {
        AppState.currentUser = null
        (contentPane.layout as CardLayout).show(contentPane, "login")
    }

    val currentUser = AppState.currentUser
    if (currentUser != null) {
        userLabel.text = "Logged in as: ${currentUser.username} [${currentUser.role}]"
    }

    return panel
}

private fun createPersonnelPanel(): JPanel {
    val panel = JPanel(BorderLayout())
    panel.border = EmptyBorder(12, 12, 12, 12)

    val listModel = DefaultListModel<String>()
    val personnelList = JList(listModel)
    personnelList.selectionMode = ListSelectionModel.SINGLE_SELECTION
    panel.add(JScrollPane(personnelList), BorderLayout.CENTER)

    val addButton = JButton("Add Personnel")
    val removeButton = JButton("Remove Selected")
    val buttons = JPanel(FlowLayout(FlowLayout.LEFT))
    buttons.add(addButton)
    buttons.add(removeButton)
    panel.add(buttons, BorderLayout.SOUTH)

    addButton.addActionListener {
        val dialog = AddPersonnelDialog()
        if (dialog.showDialog(frame = null)) {
            AppState.personnel.add(dialog.result!!)
            refreshPersonnelPanel(panel)
        }
    }

    removeButton.addActionListener {
        val selected = personnelList.selectedIndex
        if (selected >= 0) {
            AppState.personnel.removeAt(selected)
            refreshPersonnelPanel(panel)
        }
    }

    refreshPersonnelPanel(panel)
    return panel
}

private fun refreshPersonnelPanel(panel: JPanel) {
    val scrollPane = panel.getComponent(0) as JScrollPane
    val listModel = scrollPane.viewport.view as JList<*>
    val model = listModel.model as DefaultListModel<String>
    model.clear()
    AppState.personnel.forEach { model.addElement("${it.rank} ${it.name} (${it.gender}) - ${it.serviceNumber}") }
    val currentUser = AppState.currentUser
    val buttons = panel.getComponent(1) as JPanel
    buttons.getComponent(0).isEnabled = currentUser?.role == UserRole.STATION_OFFICER
    buttons.getComponent(1).isEnabled = currentUser?.role == UserRole.STATION_OFFICER
}

private fun createDutyPointPanel(): JPanel {
    val panel = JPanel(BorderLayout())
    panel.border = EmptyBorder(12, 12, 12, 12)

    val listModel = DefaultListModel<String>()
    val dutyList = JList(listModel)
    dutyList.selectionMode = ListSelectionModel.SINGLE_SELECTION
    panel.add(JScrollPane(dutyList), BorderLayout.CENTER)

    val addButton = JButton("Add Duty Point")
    val removeButton = JButton("Remove Selected")
    val buttons = JPanel(FlowLayout(FlowLayout.LEFT))
    buttons.add(addButton)
    buttons.add(removeButton)
    panel.add(buttons, BorderLayout.SOUTH)

    addButton.addActionListener {
        val dialog = AddDutyPointDialog()
        if (dialog.showDialog(frame = null)) {
            AppState.dutyPoints.add(dialog.result!!)
            refreshDutyPointPanel(panel)
        }
    }

    removeButton.addActionListener {
        val selected = dutyList.selectedIndex
        if (selected >= 0) {
            AppState.dutyPoints.removeAt(selected)
            refreshDutyPointPanel(panel)
        }
    }

    refreshDutyPointPanel(panel)
    return panel
}

private fun refreshDutyPointPanel(panel: JPanel) {
    val scrollPane = panel.getComponent(0) as JScrollPane
    val listModel = scrollPane.viewport.view as JList<*>
    val model = listModel.model as DefaultListModel<String>
    model.clear()
    AppState.dutyPoints.forEach { model.addElement("${it.name} - PW allowed: ${if (it.allowsPw) "Yes" else "No"}") }
    val currentUser = AppState.currentUser
    val buttons = panel.getComponent(1) as JPanel
    buttons.getComponent(0).isEnabled = currentUser?.role == UserRole.STATION_OFFICER
    buttons.getComponent(1).isEnabled = currentUser?.role == UserRole.STATION_OFFICER
}

private fun createRosterPanel(): JPanel {
    val panel = JPanel(BorderLayout())
    panel.border = EmptyBorder(12, 12, 12, 12)

    val textArea = JTextArea()
    textArea.isEditable = false
    textArea.font = Font(Font.MONOSPACED, Font.PLAIN, 12)
    panel.add(JScrollPane(textArea), BorderLayout.CENTER)

    val generateButton = JButton("Generate Weekly Roster")
    val buttons = JPanel(FlowLayout(FlowLayout.LEFT))
    buttons.add(generateButton)
    panel.add(buttons, BorderLayout.SOUTH)

    generateButton.addActionListener {
        AppState.roster = generateWeeklyRoster(AppState.personnel, AppState.dutyPoints)
        textArea.text = formatRoster(AppState.roster)
    }

    refreshRosterPanel(panel)
    return panel
}

private fun refreshRosterPanel(panel: JPanel) {
    val scrollPane = panel.getComponent(0) as JScrollPane
    val textArea = scrollPane.viewport.view as JTextArea
    textArea.text = formatRoster(AppState.roster)
    val buttons = panel.getComponent(1) as JPanel
    val generateButton = buttons.getComponent(0) as JButton
    generateButton.isEnabled = AppState.currentUser?.role == UserRole.STATION_OFFICER
}

private fun formatRoster(roster: List<RosterAssignment>): String {
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

private fun generateWeeklyRoster(personnel: List<Personnel>, dutyPoints: List<DutyPoint>): List<RosterAssignment> {
    val activePersonnel = personnel.filterNot { it.onStandby }
    if (activePersonnel.isEmpty()) return emptyList()

    val byDutyPoint = dutyPoints.map { dutyPoint ->
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

    return byDutyPoint
}

private class AddPersonnelDialog {
    var result: Personnel? = null

    fun showDialog(frame: JFrame?): Boolean {
        val dialog = JDialog(frame, "Add Personnel", true)
        dialog.layout = GridBagLayout()
        dialog.defaultCloseOperation = JDialog.DISPOSE_ON_CLOSE
        val gbc = GridBagConstraints().apply {
            gridx = 0
            gridy = 0
            fill = GridBagConstraints.HORIZONTAL
            insets = Insets(8, 8, 8, 8)
        }

        val nameField = JTextField(20)
        val rankField = JTextField(20)
        val serviceField = JTextField(12)
        val genderCombo = JComboBox(arrayOf("M", "F", "PW"))
        val phoneField = JTextField(16)

        dialog.add(JLabel("Full Name:"), gbc)
        gbc.gridx = 1
        dialog.add(nameField, gbc)
        gbc.gridx = 0
        gbc.gridy++
        dialog.add(JLabel("Rank:"), gbc)
        gbc.gridx = 1
        dialog.add(rankField, gbc)
        gbc.gridx = 0
        gbc.gridy++
        dialog.add(JLabel("Service Number:"), gbc)
        gbc.gridx = 1
        dialog.add(serviceField, gbc)
        gbc.gridx = 0
        gbc.gridy++
        dialog.add(JLabel("Gender:"), gbc)
        gbc.gridx = 1
        dialog.add(genderCombo, gbc)
        gbc.gridx = 0
        gbc.gridy++
        dialog.add(JLabel("Phone:"), gbc)
        gbc.gridx = 1
        dialog.add(phoneField, gbc)

        val buttonPanel = JPanel(FlowLayout(FlowLayout.RIGHT))
        val saveButton = JButton("Save")
        val cancelButton = JButton("Cancel")
        buttonPanel.add(saveButton)
        buttonPanel.add(cancelButton)
        gbc.gridx = 0
        gbc.gridy++
        gbc.gridwidth = 2
        dialog.add(buttonPanel, gbc)

        var confirmed = false
        saveButton.addActionListener {
            val name = nameField.text.trim().ifEmpty { return@addActionListener }
            val rank = rankField.text.trim().ifEmpty { "Constable" }
            val service = serviceField.text.trim().ifEmpty { "000" }
            val gender = genderCombo.selectedItem as String
            val phone = phoneField.text.trim().ifEmpty { "N/A" }
            result = Personnel(UUID.randomUUID().toString(), rank, name, service, gender, phone)
            confirmed = true
            dialog.dispose()
        }
        cancelButton.addActionListener {
            dialog.dispose()
        }

        dialog.pack()
        dialog.setLocationRelativeTo(frame)
        dialog.isVisible = true
        return confirmed
    }
}

private class AddDutyPointDialog {
    var result: DutyPoint? = null

    fun showDialog(frame: JFrame?): Boolean {
        val dialog = JDialog(frame, "Add Duty Point", true)
        dialog.layout = GridBagLayout()
        dialog.defaultCloseOperation = JDialog.DISPOSE_ON_CLOSE
        val gbc = GridBagConstraints().apply {
            gridx = 0
            gridy = 0
            fill = GridBagConstraints.HORIZONTAL
            insets = Insets(8, 8, 8, 8)
        }

        val nameField = JTextField(20)
        val pwCheck = JCheckBox("Allow PW")

        dialog.add(JLabel("Duty Point Name:"), gbc)
        gbc.gridx = 1
        dialog.add(nameField, gbc)
        gbc.gridx = 0
        gbc.gridy++
        dialog.add(pwCheck, gbc)

        val buttonPanel = JPanel(FlowLayout(FlowLayout.RIGHT))
        val saveButton = JButton("Save")
        val cancelButton = JButton("Cancel")
        buttonPanel.add(saveButton)
        buttonPanel.add(cancelButton)
        gbc.gridx = 0
        gbc.gridy++
        gbc.gridwidth = 2
        dialog.add(buttonPanel, gbc)

        var confirmed = false
        saveButton.addActionListener {
            val name = nameField.text.trim().ifEmpty { return@addActionListener }
            result = DutyPoint(UUID.randomUUID().toString(), name, pwCheck.isSelected)
            confirmed = true
            dialog.dispose()
        }
        cancelButton.addActionListener { dialog.dispose() }

        dialog.pack()
        dialog.setLocationRelativeTo(frame)
        dialog.isVisible = true
        return confirmed
    }
}
