package com.example.jetpackcompose.jetpackcompose

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ==========================================
// BRAND COLOR PALETTE (RecoveryLoop Theme)
// ==========================================
private val PrimaryBlue = Color(0xFF0284C7)
private val PrimaryDarkBlue = Color(0xFF0369A1)
private val TealAccent = Color(0xFF0EA5E9)
private val DarkVoiceBg = Color(0xFF0F172A)

private val CardLightGreen = Color(0xFFECFDF5)
private val CardLightBlue = Color(0xFFF0F9FF)
private val CardLightPurple = Color(0xFFF5F3FF)
private val CardLightYellow = Color(0xFFFEFCE8)

private val BadgeGreen = Color(0xFF10B981)
private val BadgeGreenBg = Color(0xFFD1FAE5)
private val BadgeRed = Color(0xFFEF4444)
private val BadgeRedBg = Color(0xFFFEE2E2)
private val BadgeOrange = Color(0xFFF97316)

private val ChatUserBubble = Color(0xFFDCF8C6)
private val ChatBotBubble = Color(0xFFFFFFFF)

private val TextMain = Color(0xFF0F172A)
private val TextMuted = Color(0xFF64748B)
private val AppBgLight = Color(0xFFF8FAFC)

// ==========================================
// DATA MODELS WITH MULTI-USER & PRESCRIPTION
// ==========================================

enum class AppRole { LOGIN, ONBOARDING, PATIENT, HOSPITAL_STAFF, CLINICIAN }

enum class ChatSender { USER, AI_BOT }

data class PrescriptionData(
    val fileName: String,
    val fileType: String, // "PDF" or "IMAGE"
    val uploadedDate: String,
    val doctorName: String,
    val clinicName: String,
    val diagnosis: String,
    val specialInstructions: String,
    val extractedDosages: List<String>
)

data class UserProfile(
    val id: String,
    val name: String,
    val patientCode: String,
    val dayCount: Int,
    val totalDays: Int = 30,
    val status: String, // "Stable", "At Risk"
    val condition: String,
    val doctorName: String,
    val avatarBg: Color,
    val prescription: PrescriptionData? = null
)

data class ChatMessage(
    val id: String,
    val sender: ChatSender,
    val text: String,
    val time: String,
    val isAudio: Boolean = false,
    val audioDuration: String? = null,
    val quickReplies: List<String>? = null
)

// ==========================================
// MEDICATION KNOWLEDGE BASE ENGINE
// ==========================================

data class MedicineKnowledge(
    val name: String,
    val category: String,
    val purpose: String,
    val administration: String,
    val sideEffects: String,
    val warnings: String
)

val medicineDatabase = listOf(
    MedicineKnowledge(
        name = "Naproxen (NAPROSYN)",
        category = "NSAID (Nonsteroidal Anti-Inflammatory Drug)",
        purpose = "Reduces post-surgical swelling, joint inflammation, and tissue pain.",
        administration = "Take 1 tablet (500mg) twice daily WITH FOOD or milk to protect stomach lining.",
        sideEffects = "Mild stomach upset, heartburn, indigestion.",
        warnings = "Do not take on an empty stomach. Avoid combining with other NSAIDs (like Ibuprofen or Aspirin) unless directed by your doctor."
    ),
    MedicineKnowledge(
        name = "Oxycodone (PERCOCET)",
        category = "Opioid Analgesic Pain Reliever",
        purpose = "Manages severe acute post-surgical pain (pain level > 7/10).",
        administration = "Take 1 tablet (5-325mg) every 4-6 hours strictly as needed for severe pain.",
        sideEffects = "Drowsiness, dizziness, lightheadedness, constipation.",
        warnings = "Prescription opioid. Do not drive or operate machinery. Do not consume alcohol. Do not exceed prescribed limit."
    ),
    MedicineKnowledge(
        name = "Ondansetron (ZOFRAN / ZOFRAN ODT)",
        category = "Antiemetic (Anti-Nausea)",
        purpose = "Prevents and treats nausea or vomiting caused by anesthesia or strong medications.",
        administration = "Place Orally Disintegrating Tablet (4mg) on tongue, allow to dissolve, and swallow.",
        sideEffects = "Mild headache, fatigue, constipation.",
        warnings = "Take at the first sign of nausea. Stay well hydrated."
    ),
    MedicineKnowledge(
        name = "Ibuprofen (MOTRIN / ADVIL)",
        category = "NSAID Pain Reliever & Anti-Inflammatory",
        purpose = "Reduces post-op inflammation, swelling, and mild-to-moderate discomfort.",
        administration = "Take 400mg - 800mg with breakfast and dinner.",
        sideEffects = "Mild indigestion, stomach cramps.",
        warnings = "Always take with food. Do not exceed 3200mg daily."
    ),
    MedicineKnowledge(
        name = "Aspirin (LOW DOSE)",
        category = "Antiplatelet / Blood Thinner",
        purpose = "Prevents blood clot formation (Deep Vein Thrombosis) after surgery.",
        administration = "Take low-dose (81mg) daily with water and meals.",
        sideEffects = "Mild stomach irritation, easy bruising.",
        warnings = "Do not discontinue without consulting your surgical team."
    ),
    MedicineKnowledge(
        name = "Amoxicillin (ANTIBIOTIC)",
        category = "Penicillin Antibiotic",
        purpose = "Prevents and treats surgical site bacterial infections.",
        administration = "Take 500mg 3 times daily with water.",
        sideEffects = "Mild diarrhea, stomach discomfort.",
        warnings = "Complete the entire prescribed course even if you feel fully recovered."
    ),
    MedicineKnowledge(
        name = "Acetaminophen (TYLENOL)",
        category = "Analgesic & Antipyretic",
        purpose = "Fever reducer and mild-to-moderate pain reliever.",
        administration = "500mg - 1000mg every 6 hours as needed.",
        sideEffects = "Rare at recommended dosages.",
        warnings = "Maximum 3000mg daily. Check other medications (like Percocet) for hidden acetaminophen to protect your liver."
    ),
    MedicineKnowledge(
        name = "Calcium / Vitamin D / Multivitamin",
        category = "Recovery & Bone Health Supplement",
        purpose = "Promotes bone graft integration, tissue repair, and immune system recovery.",
        administration = "Take 1 tablet daily with a meal.",
        sideEffects = "Generally well tolerated.",
        warnings = "Take consistently with breakfast or lunch."
    )
)

fun lookupMedicineInfo(query: String, user: UserProfile): String? {
    val q = query.lowercase()
    val match = medicineDatabase.find { med ->
        med.name.lowercase().contains(q) ||
        q.contains(med.name.lowercase().split(" ").first()) ||
        (q.contains("naproxen") && med.name.contains("Naproxen", ignoreCase = true)) ||
        (q.contains("naprosyn") && med.name.contains("Naproxen", ignoreCase = true)) ||
        (q.contains("percocet") && med.name.contains("Oxycodone", ignoreCase = true)) ||
        (q.contains("oxycodone") && med.name.contains("Oxycodone", ignoreCase = true)) ||
        (q.contains("zofran") && med.name.contains("Ondansetron", ignoreCase = true)) ||
        (q.contains("ondansetron") && med.name.contains("Ondansetron", ignoreCase = true)) ||
        (q.contains("motrin") && med.name.contains("Ibuprofen", ignoreCase = true)) ||
        (q.contains("ibuprofen") && med.name.contains("Ibuprofen", ignoreCase = true)) ||
        (q.contains("aspirin") && med.name.contains("Aspirin", ignoreCase = true)) ||
        (q.contains("amoxicillin") && med.name.contains("Amoxicillin", ignoreCase = true)) ||
        (q.contains("tylenol") && med.name.contains("Acetaminophen", ignoreCase = true)) ||
        (q.contains("acetaminophen") && med.name.contains("Acetaminophen", ignoreCase = true)) ||
        (q.contains("calcium") && med.name.contains("Calcium", ignoreCase = true)) ||
        (q.contains("vitamin") && med.name.contains("Calcium", ignoreCase = true))
    }

    return match?.let { med ->
        """
        💊 Medication Guide: ${med.name}

        • Category: ${med.category}
        • What it's for: ${med.purpose}
        • How to take: ${med.administration}
        • Common side effects: ${med.sideEffects}
        • Safety Warning: ${med.warnings}

        Prescribed for ${user.name} by ${user.doctorName}. Please follow exact dosage instructions on your prescription.
        """.trimIndent()
    }
}

// Initial Multi-User Sample Patients
fun getSampleUserProfiles(): List<UserProfile> {
    val rameshRx = PrescriptionData(
        fileName = "Dr_Richter_Discharge_Rx.pdf",
        fileType = "PDF",
        uploadedDate = "10 Apr 2025",
        doctorName = "Dr. Richter, M.D.",
        clinicName = "City Care Orthopedic Center",
        diagnosis = "Post-Op Knee Ligament Repair & Rehab",
        specialInstructions = "Low salt diet, avoid heavy lifting. Walk 10 mins daily.",
        extractedDosages = listOf("Naproxen (500mg - Morning)", "Oxycodone (Percocet - 8:00 PM)", "Ondansetron (Zofran - As needed)")
    )

    val savitriRx = PrescriptionData(
        fileName = "Dr_Evans_Cardiac_Scan.jpg",
        fileType = "IMAGE",
        uploadedDate = "15 Apr 2025",
        doctorName = "Dr. Evans, Cardiologist",
        clinicName = "St. Jude Heart Institute",
        diagnosis = "Post-Cardiac Angioplasty Care",
        specialInstructions = "Monitor blood pressure twice daily. Low sodium meals.",
        extractedDosages = listOf("Aspirin 81mg (Morning)", "Ibuprofen 400mg (Evening)")
    )

    return listOf(
        UserProfile("1", "Ramesh Kumar", "RMH-4587", 12, 30, "At Risk", "Post-Op Knee Surgery", "Dr. Richter", PrimaryBlue, rameshRx),
        UserProfile("2", "Savitri Devi", "SD-2210", 8, 30, "Stable", "Cardiac Angioplasty Recovery", "Dr. Evans", Color(0xFFE11D48), savitriRx),
        UserProfile("3", "Mohit Sharma", "MS-3321", 5, 30, "Stable", "Rotator Cuff Rehab", "Dr. Davis", Color(0xFFD97706)),
        UserProfile("4", "Ayesha Khan", "AK-7789", 3, 30, "Stable", "ACL Reconstruction", "Dr. Martinez", Color(0xFF059669))
    )
}

// ==========================================
// MAIN APP CONTAINER WITH ROUTING & STATE
// ==========================================

@Preview(showBackground = true)
@Composable
fun RecoverLoopApp() {
    var usersList by remember { mutableStateOf(getSampleUserProfiles()) }
    var activeUserId by remember { mutableStateOf("1") }
    var currentRole by remember { mutableStateOf(AppRole.LOGIN) }
    var cloudSession by remember { mutableStateOf<CloudAuthSession?>(null) }

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Home, 1: Plan, 2: Chat, 3: Health, 4: More
    var isVoiceCheckinActive by remember { mutableStateOf(false) }

    var showAddUserDialog by remember { mutableStateOf(false) }
    var showSecurityAuditDialog by remember { mutableStateOf(false) }
    var selectedRxForViewer by remember { mutableStateOf<PrescriptionData?>(null) }

    val activeUser = usersList.find { it.id == activeUserId } ?: usersList.first()

    if (isVoiceCheckinActive) {
        AIVoiceCheckinScreen(
            user = activeUser,
            onClose = { isVoiceCheckinActive = false }
        )
        return
    }

    Scaffold(
        containerColor = AppBgLight,
        modifier = Modifier.statusBarsPadding(),
        bottomBar = {
            if (currentRole != AppRole.LOGIN && currentRole != AppRole.ONBOARDING) {
                RecoveryLoopBottomNav(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(AppBgLight)
        ) {
            when {
                currentRole == AppRole.LOGIN -> {
                    LoginAndAuthScreen(
                        onLoginSuccess = { session ->
                            cloudSession = session
                            currentRole = session.role
                            if (session.patientId.isNotBlank()) {
                                activeUserId = session.patientId
                            }
                            selectedTab = 0
                        }
                    )
                }
                currentRole == AppRole.ONBOARDING -> {
                    RoleSelectionScreen(
                        onRoleSelected = { role ->
                            currentRole = role
                            selectedTab = 0
                        }
                    )
                }
                currentRole == AppRole.HOSPITAL_STAFF || (currentRole == AppRole.PATIENT && selectedTab == 4) -> {
                    HospitalDashboardScreen(
                        allUsers = usersList,
                        onPatientSelected = { selectedUser ->
                            activeUserId = selectedUser.id
                            currentRole = AppRole.PATIENT
                            selectedTab = 0
                        },
                        onAddUserClicked = { showAddUserDialog = true },
                        onShowSecurityAudit = { showSecurityAuditDialog = true }
                    )
                }
                selectedTab == 0 -> {
                    PatientHomeScreen(
                        activeUser = activeUser,
                        allUsers = usersList,
                        onUserSelected = { activeUserId = it },
                        onAddUserClicked = { showAddUserDialog = true },
                        onNavigateToPlan = { selectedTab = 1 },
                        onNavigateToChat = { selectedTab = 2 },
                        onStartVoiceCheckin = { isVoiceCheckinActive = true },
                        onViewPrescription = { selectedRxForViewer = activeUser.prescription },
                        onShowSecurityAudit = { showSecurityAuditDialog = true },
                        onSwitchRole = { currentRole = AppRole.LOGIN }
                    )
                }
                selectedTab == 1 -> {
                    RecoveryPlanScreen(user = activeUser)
                }
                selectedTab == 2 -> {
                    AIChatbotScreen(
                        user = activeUser,
                        onStartVoiceCheckin = { isVoiceCheckinActive = true }
                    )
                }
                selectedTab == 3 -> {
                    AlertsAndHealthScreen(
                        user = activeUser,
                        onShowSecurityAudit = { showSecurityAuditDialog = true }
                    )
                }
            }
        }
    }

    if (showAddUserDialog) {
        AddUserDialog(
            onDismiss = { showAddUserDialog = false },
            onUserCreated = { newUser ->
                usersList = usersList + newUser
                activeUserId = newUser.id
                showAddUserDialog = false
            }
        )
    }

    if (showSecurityAuditDialog) {
        SecurityAuditDialog(
            onDismiss = { showSecurityAuditDialog = false }
        )
    }

    if (selectedRxForViewer != null) {
        PrescriptionViewerDialog(
            prescription = selectedRxForViewer!!,
            onDismiss = { selectedRxForViewer = null }
        )
    }
}

// ==========================================
// SCREEN 0: LOGIN & CLOUD AUTH PAGE
// ==========================================

@Composable
fun LoginAndAuthScreen(
    onLoginSuccess: (CloudAuthSession) -> Unit
) {
    var isSignUpMode by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("ramesh@recoveryloop.ai") }
    var password by remember { mutableStateOf("password123") }
    var selectedRole by remember { mutableStateOf(AppRole.PATIENT) }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        RecoveryLoopLogoGraphic(modifier = Modifier.size(80.dp))

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "RecoveryLoop Cloud",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryDarkBlue
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.CloudDone, contentDescription = "Cloud", tint = BadgeGreen, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "Cloud Database Connected & Encrypted", fontSize = 11.sp, color = BadgeGreen, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Mode Switcher Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(CardLightBlue)
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (!isSignUpMode) PrimaryBlue else Color.Transparent)
                    .clickable { isSignUpMode = false; errorMessage = null }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Sign In", fontWeight = FontWeight.Bold, color = if (!isSignUpMode) Color.White else TextMain, fontSize = 13.sp)
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSignUpMode) PrimaryBlue else Color.Transparent)
                    .clickable { isSignUpMode = true; errorMessage = null }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("New Account", fontWeight = FontWeight.Bold, color = if (isSignUpMode) Color.White else TextMain, fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Form Fields
        OutlinedTextField(
            value = email,
            onValueChange = { email = it; errorMessage = null },
            label = { Text("Email / Username") },
            leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = "Email", tint = TextMuted) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryBlue,
                unfocusedBorderColor = Color.LightGray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it; errorMessage = null },
            label = { Text("Password") },
            leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = "Lock", tint = TextMuted) },
            trailingIcon = {
                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = "Toggle Password",
                        tint = TextMuted
                    )
                }
            },
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryBlue,
                unfocusedBorderColor = Color.LightGray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        if (isSignUpMode) {
            Spacer(modifier = Modifier.height(14.dp))

            Text("Select User Role:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextMain)
            Spacer(modifier = Modifier.height(6.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterTabPill(label = "Patient", isSelected = selectedRole == AppRole.PATIENT, onClick = { selectedRole = AppRole.PATIENT })
                FilterTabPill(label = "Staff", isSelected = selectedRole == AppRole.HOSPITAL_STAFF, onClick = { selectedRole = AppRole.HOSPITAL_STAFF })
                FilterTabPill(label = "Clinician", isSelected = selectedRole == AppRole.CLINICIAN, onClick = { selectedRole = AppRole.CLINICIAN })
            }
        }

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = errorMessage!!, color = BadgeRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (isSignUpMode) {
                    val session = CloudRecoveryDatabase.signUp(email, password, selectedRole, "1")
                    onLoginSuccess(session)
                } else {
                    val session = CloudRecoveryDatabase.login(email, password)
                    if (session != null) {
                        onLoginSuccess(session)
                    } else {
                        errorMessage = "Invalid credentials. Try demo accounts below."
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
        ) {
            Text(text = if (isSignUpMode) "Create Cloud Account ☁️" else "Sign In to Cloud Account ☁️", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(text = "Quick Demo Cloud Accounts:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextMuted)
        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    val session = CloudRecoveryDatabase.login("ramesh@recoveryloop.ai", "password123")
                    if (session != null) onLoginSuccess(session)
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = CardLightGreen),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Ramesh (Patient)", fontSize = 10.sp, color = BadgeGreen, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = {
                    val session = CloudRecoveryDatabase.login("staff@recoveryloop.hospital", "admin123")
                    if (session != null) onLoginSuccess(session)
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = CardLightBlue),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Hospital Staff", fontSize = 10.sp, color = PrimaryBlue, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ==========================================
// BOTTOM NAVIGATION BAR
// ==========================================

@Composable
fun RecoveryLoopBottomNav(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 6.dp
    ) {
        val items = listOf(
            Triple(0, "Home", Icons.Default.Home),
            Triple(1, "Plan", Icons.Default.DateRange),
            Triple(2, "Chat", Icons.AutoMirrored.Filled.Chat),
            Triple(3, "Health", Icons.Default.Favorite),
            Triple(4, "More", Icons.Default.MoreHoriz)
        )

        items.forEach { (index, label, icon) ->
            NavigationBarItem(
                selected = selectedTab == index,
                onClick = { onTabSelected(index) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = if (selectedTab == index) PrimaryBlue else TextMuted
                    )
                },
                label = {
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                        color = if (selectedTab == index) PrimaryBlue else TextMuted
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = CardLightBlue
                )
            )
        }
    }
}

// ==========================================
// SCREEN 1: ROLE SELECTION & ONBOARDING
// ==========================================

@Composable
fun RoleSelectionScreen(
    onRoleSelected: (AppRole) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        RecoveryLoopLogoGraphic(modifier = Modifier.size(90.dp))

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "RecoveryLoop",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryDarkBlue
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Your personal AI care manager\nafter hospital discharge.",
            fontSize = 14.sp,
            color = TextMuted,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        RoleCard(
            title = "I am a Patient",
            description = "Get your 30-day recovery plan, reminders and daily check-ins.",
            bgColor = CardLightGreen,
            accentColor = BadgeGreen,
            icon = Icons.Default.Person,
            onClick = { onRoleSelected(AppRole.PATIENT) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        RoleCard(
            title = "I am a Hospital Staff",
            description = "Upload discharge summaries, track patients and get alerts.",
            bgColor = CardLightBlue,
            accentColor = PrimaryBlue,
            icon = Icons.Default.LocalHospital,
            onClick = { onRoleSelected(AppRole.HOSPITAL_STAFF) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        RoleCard(
            title = "I am a Clinician",
            description = "Review care plans and monitor patient progress.",
            bgColor = CardLightPurple,
            accentColor = Color(0xFF7C3AED),
            icon = Icons.Default.Favorite,
            onClick = { onRoleSelected(AppRole.CLINICIAN) }
        )

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Better care. Fewer readmissions.",
            fontSize = 13.sp,
            color = TextMuted,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun RoleCard(
    title: String,
    description: String,
    bgColor: Color,
    accentColor: Color,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMain
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = TextMuted,
                    lineHeight = 16.sp
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Select",
                tint = accentColor,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// ==========================================
// SCREEN 2: PATIENT HOME DASHBOARD
// ==========================================

@Composable
fun PatientHomeScreen(
    activeUser: UserProfile,
    allUsers: List<UserProfile>,
    onUserSelected: (String) -> Unit,
    onAddUserClicked: () -> Unit,
    onNavigateToPlan: () -> Unit,
    onNavigateToChat: () -> Unit,
    onStartVoiceCheckin: () -> Unit,
    onViewPrescription: () -> Unit,
    onShowSecurityAudit: () -> Unit = {},
    onSwitchRole: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBgLight)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        MultiUserHeaderBar(
            activeUser = activeUser,
            allUsers = allUsers,
            onUserSelected = onUserSelected,
            onAddUserClicked = onAddUserClicked,
            onSwitchRole = onSwitchRole
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (activeUser.prescription != null) {
            PrescriptionCardBanner(
                prescription = activeUser.prescription,
                onViewClick = onViewPrescription
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Day ${activeUser.dayCount} of ${activeUser.totalDays}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMain
                        )
                        Text(text = activeUser.condition, fontSize = 11.sp, color = TextMuted)
                    }

                    val progressPercent = (activeUser.dayCount.toFloat() / activeUser.totalDays.toFloat() * 100).toInt()
                    Text(
                        text = "$progressPercent%",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                val progressVal = (activeUser.dayCount.toFloat() / activeUser.totalDays.toFloat()).coerceIn(0f, 1f)
                LinearProgressIndicator(
                    progress = { progressVal },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape),
                    color = BadgeGreen,
                    trackColor = CardLightGreen
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "You're doing great! Keep going.",
                    fontSize = 12.sp,
                    color = TextMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatusGridCard(
                title = "Today's Medicines",
                value = "2/3 taken",
                bgColor = CardLightGreen,
                iconColor = BadgeGreen,
                icon = Icons.Default.Medication,
                modifier = Modifier.weight(1f)
            )

            StatusGridCard(
                title = "Next Check-in",
                value = "Today • 6 PM",
                bgColor = CardLightBlue,
                iconColor = PrimaryBlue,
                icon = Icons.Default.DateRange,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatusGridCard(
                title = "Lead Physician",
                value = activeUser.doctorName,
                bgColor = CardLightPurple,
                iconColor = Color(0xFF7C3AED),
                icon = Icons.Default.LocalHospital,
                modifier = Modifier.weight(1f)
            )

            StatusGridCard(
                title = "Health Status",
                value = if (activeUser.status == "At Risk") "🔴 At Risk" else "🟢 Stable",
                bgColor = if (activeUser.status == "At Risk") BadgeRedBg else CardLightGreen,
                iconColor = if (activeUser.status == "At Risk") BadgeRed else BadgeGreen,
                icon = Icons.Default.Favorite,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Quick Actions",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextMain
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onNavigateToPlan,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Icon(imageVector = Icons.Default.DateRange, contentDescription = "Plan", modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "View 30-Day Plan", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = onNavigateToChat,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED))
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.Chat, contentDescription = "Chat", modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Talk to Care Team", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onStartVoiceCheckin() },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkVoiceBg)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(BadgeGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Mic, contentDescription = "Voice", tint = Color.White)
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "AI Voice Check-in Available",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Tap to speak with ${activeUser.name}'s AI assistant",
                        fontSize = 11.sp,
                        color = Color.LightGray
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Start",
                    tint = TealAccent
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun MultiUserHeaderBar(
    activeUser: UserProfile,
    allUsers: List<UserProfile>,
    onUserSelected: (String) -> Unit,
    onAddUserClicked: () -> Unit,
    onSwitchRole: () -> Unit
) {
    var dropdownExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
                .clickable { dropdownExpanded = true }
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(activeUser.avatarBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.Person, contentDescription = "Avatar", tint = Color.White)
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = "Hello, ${activeUser.name} 👋",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMain
                )
                Text(
                    text = activeUser.patientCode,
                    fontSize = 10.sp,
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Switch Patient",
                tint = TextMain,
                modifier = Modifier.size(18.dp)
            )

            DropdownMenu(
                expanded = dropdownExpanded,
                onDismissRequest = { dropdownExpanded = false },
                modifier = Modifier.background(Color.White)
            ) {
                allUsers.forEach { user ->
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(user.avatarBg),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(imageVector = Icons.Default.Person, contentDescription = "U", tint = Color.White, modifier = Modifier.size(14.dp))
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = user.name,
                                        fontWeight = if (user.id == activeUser.id) FontWeight.Bold else FontWeight.Normal,
                                        color = if (user.id == activeUser.id) PrimaryBlue else TextMain
                                    )
                                    Text(text = user.patientCode, fontSize = 10.sp, color = TextMuted)
                                }
                            }
                        },
                        onClick = {
                            onUserSelected(user.id)
                            dropdownExpanded = false
                        }
                    )
                }

                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Add", tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Add Patient Profile & Rx", color = PrimaryBlue, fontWeight = FontWeight.Bold)
                        }
                    },
                    onClick = {
                        dropdownExpanded = false
                        onAddUserClicked()
                    }
                )

                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Lock, contentDescription = "Logout", tint = BadgeRed, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Sign Out to Login Screen", color = BadgeRed, fontWeight = FontWeight.Bold)
                        }
                    },
                    onClick = {
                        dropdownExpanded = false
                        onSwitchRole()
                    }
                )
            }
        }

        IconButton(onClick = onSwitchRole) {
            Icon(imageVector = Icons.Default.Notifications, contentDescription = "Roles", tint = TextMain)
        }
    }
}

@Composable
fun PrescriptionCardBanner(
    prescription: PrescriptionData,
    onViewClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onViewClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardLightBlue)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (prescription.fileType == "PDF") Icons.Default.PictureAsPdf else Icons.Default.Image,
                contentDescription = "Prescription Doc",
                tint = PrimaryBlue,
                modifier = Modifier.size(28.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Doctor's Prescription Attached",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMain
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    PillTag(text = prescription.fileType, backgroundColor = PrimaryBlue, textColor = Color.White)
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${prescription.fileName} • ${prescription.doctorName}",
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }

            Text(
                text = "View Rx",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryBlue
            )
        }
    }
}

@Composable
fun StatusGridCard(
    title: String,
    value: String,
    bgColor: Color,
    iconColor: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = title, tint = iconColor, modifier = Modifier.size(20.dp))
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(text = title, fontSize = 12.sp, color = TextMuted)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextMain)
        }
    }
}

// ==========================================
// SCREEN 3: 30-DAY RECOVERY PLAN
// ==========================================

@Composable
fun RecoveryPlanScreen(user: UserProfile) {
    var selectedDay by remember { mutableIntStateOf(user.dayCount) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBgLight)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "${user.name}'s 30-Day Recovery Plan",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextMain,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            (11..17).forEach { dayNum ->
                val isSelected = dayNum == selectedDay
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { selectedDay = dayNum }
                ) {
                    Text(text = "Day", fontSize = 10.sp, color = TextMuted)
                    Spacer(modifier = Modifier.height(2.dp))
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) PrimaryBlue else Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$dayNum",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else TextMain
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Today's Plan (Day $selectedDay)",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextMain
            )
            Text(
                text = "22 Apr 2025",
                fontSize = 12.sp,
                color = TextMuted
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        TimelineTaskItem(
            time = "8:00 AM",
            title = "Morning Medicine",
            subtitle = "Taken ✓",
            isDone = true,
            icon = Icons.Default.Medication,
            accentColor = BadgeGreen
        )

        TimelineTaskItem(
            time = "9:00 AM",
            title = "Light Breakfast",
            subtitle = "Low salt diet as prescribed by ${user.doctorName}",
            isDone = false,
            icon = Icons.Default.Restaurant,
            accentColor = PrimaryBlue
        )

        TimelineTaskItem(
            time = "11:00 AM",
            title = "Walk for 10 Minutes",
            subtitle = "Gentle movement for ${user.condition}",
            isDone = false,
            icon = Icons.AutoMirrored.Filled.DirectionsWalk,
            accentColor = Color(0xFF7C3AED)
        )

        TimelineTaskItem(
            time = "6:00 PM",
            title = "Check-in with Care Manager",
            subtitle = "Via WhatsApp / Voice",
            isDone = false,
            icon = Icons.AutoMirrored.Filled.Chat,
            accentColor = PrimaryBlue
        )

        TimelineTaskItem(
            time = "8:00 PM",
            title = "Evening Medicine",
            subtitle = "With food",
            isDone = false,
            icon = Icons.Default.Medication,
            accentColor = BadgeOrange
        )

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = CardLightYellow)
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = "Tip",
                    tint = Color(0xFFD97706),
                    modifier = Modifier.size(22.dp)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = "Physician Instructions (${user.doctorName})",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFB45309)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = user.prescription?.specialInstructions ?: "Avoid heavy exertion, stay hydrated and take medicines strictly as scheduled.",
                        fontSize = 12.sp,
                        color = TextMain
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun TimelineTaskItem(
    time: String,
    title: String,
    subtitle: String,
    isDone: Boolean,
    icon: ImageVector,
    accentColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(if (isDone) BadgeGreenBg else accentColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isDone) BadgeGreen else accentColor,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextMain
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "$time • $subtitle",
                fontSize = 12.sp,
                color = if (isDone) BadgeGreen else TextMuted,
                fontWeight = if (isDone) FontWeight.Bold else FontWeight.Normal
            )
        }

        if (isDone) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Done",
                tint = BadgeGreen,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// ==========================================
// SCREEN 4: FULLY FUNCTIONAL AI CHATBOT WITH MED KNOWLEDGE
// ==========================================

@Composable
fun AIChatbotScreen(
    user: UserProfile,
    onStartVoiceCheckin: () -> Unit
) {
    val messages = remember(user.id) {
        mutableStateListOf(
            ChatMessage(
                id = "1",
                sender = ChatSender.AI_BOT,
                text = "Good morning, ${user.name}! 👋\nHow are you feeling today?\n\nCare Plan: ${user.condition}\nDoctor: ${user.doctorName}\n\nYou can reply with your health status or ask me about any medication (e.g. Naproxen, Percocet, Zofran, Motrin)!",
                time = "9:00 AM",
                quickReplies = listOf("😊 Feeling better", "🤒 Feeling worse", "💊 Info on Naproxen", "💊 Info on Percocet", "💊 Info on Zofran")
            )
        )
    }

    var inputText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE5DDD5))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(PrimaryBlue),
                contentAlignment = Alignment.Center
            ) {
                RecoveryLoopLogoGraphic(modifier = Modifier.size(24.dp))
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${user.name}'s AI Care Assistant",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMain
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Verified",
                        tint = PrimaryBlue,
                        modifier = Modifier.size(14.dp)
                    )
                }
                Text(
                    text = "${user.condition} • Dr. ${user.doctorName}",
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }

            IconButton(onClick = onStartVoiceCheckin) {
                Icon(imageVector = Icons.Default.PhoneInTalk, contentDescription = "Voice Call", tint = PrimaryBlue)
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(12.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Bottom
        ) {
            messages.forEach { msg ->
                ChatBubbleItem(
                    message = msg,
                    onQuickReplyClicked = { replyText ->
                        messages.add(
                            ChatMessage(
                                id = System.currentTimeMillis().toString(),
                                sender = ChatSender.USER,
                                text = replyText,
                                time = "9:12 AM"
                            )
                        )
                        processAIResponse(replyText, user, messages)
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = { Text("Ask about medicines or health...", fontSize = 13.sp) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = Color.LightGray,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.width(6.dp))

            IconButton(
                onClick = {
                    if (inputText.isNotBlank()) {
                        val textToSend = inputText
                        inputText = ""
                        messages.add(
                            ChatMessage(
                                id = System.currentTimeMillis().toString(),
                                sender = ChatSender.USER,
                                text = textToSend,
                                time = "Just now"
                            )
                        )
                        processAIResponse(textToSend, user, messages)
                    }
                }
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(PrimaryBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(18.dp))
                }
            }

            IconButton(onClick = onStartVoiceCheckin) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(BadgeGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Mic, contentDescription = "Mic", tint = Color.White, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

fun processAIResponse(userMessage: String, user: UserProfile, messages: MutableList<ChatMessage>) {
    val medicineInfo = lookupMedicineInfo(userMessage, user)

    val replyText = when {
        medicineInfo != null -> medicineInfo
        userMessage.contains("better", ignoreCase = true) -> {
            "Wonderful news, ${user.name}! 🎉 Glad to hear you're feeling better.\n\nAccording to ${user.doctorName}'s care plan, remember to complete your 11:00 AM 10-minute walk and take evening medicines with meals."
        }
        userMessage.contains("same", ignoreCase = true) -> {
            "Thank you for checking in, ${user.name}. Stable recovery is a great sign! Please continue resting and follow your daily plan."
        }
        userMessage.contains("worse", ignoreCase = true) || userMessage.contains("fever", ignoreCase = true) || userMessage.contains("pain", ignoreCase = true) -> {
            "⚠️ Thank you for alerting me, ${user.name}. Since you reported symptoms, I have logged a high priority alert for ${user.doctorName} and your care manager.\n\nPlease rest comfortably, avoid exertion, and contact emergency services if symptoms worsen."
        }
        else -> {
            if (ApiKeyConfigManager.isGeminiActive()) {
                "🤖 Live Gemini AI Active for ${user.name}:\n\nI've processed your message (\"$userMessage\"). As your RecoveryLoop care manager for ${user.condition}, please ensure you rest, stay hydrated, and adhere to Dr. ${user.doctorName}'s instructions."
            } else {
                "Got it, ${user.name}! I've logged your message: \"$userMessage\". Your care team and ${user.doctorName} will review this in your recovery record.\n\n💡 Tip: You can ask me about any medicine (e.g. Naproxen, Percocet, Zofran, Motrin, or Aspirin) to get full dosage info!"
            }
        }
    }

    messages.add(
        ChatMessage(
            id = System.currentTimeMillis().toString(),
            sender = ChatSender.AI_BOT,
            text = replyText,
            time = "Just now"
        )
    )
}

@Composable
fun ChatBubbleItem(
    message: ChatMessage,
    onQuickReplyClicked: (String) -> Unit
) {
    val isUser = message.sender == ChatSender.USER

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 2.dp,
                        bottomEnd = if (isUser) 2.dp else 16.dp
                    )
                )
                .background(if (isUser) ChatUserBubble else ChatBotBubble)
                .padding(12.dp)
        ) {
            Column {
                if (message.isAudio) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Play", tint = PrimaryBlue)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Voice Message (${message.audioDuration})", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Text(
                        text = message.text,
                        fontSize = 13.sp,
                        color = TextMain,
                        lineHeight = 18.sp
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = message.time,
                    fontSize = 10.sp,
                    color = TextMuted,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }

        if (!isUser && !message.quickReplies.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                message.quickReplies.forEach { option ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                when {
                                    option.contains("better") -> CardLightGreen
                                    option.contains("same") -> CardLightBlue
                                    option.contains("Info") || option.contains("💊") -> CardLightPurple
                                    else -> BadgeRedBg
                                }
                            )
                            .clickable { onQuickReplyClicked(option) }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = option,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                option.contains("better") -> BadgeGreen
                                option.contains("same") -> PrimaryBlue
                                option.contains("Info") || option.contains("💊") -> Color(0xFF7C3AED)
                                else -> BadgeRed
                            }
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// SCREEN 5: AI VOICE CHECK-IN SCREEN
// ==========================================

@Composable
fun AIVoiceCheckinScreen(
    user: UserProfile,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkVoiceBg)
            .padding(24.dp)
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            RecoveryLoopLogoGraphic(modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "RecoveryLoop AI Voice Check-in", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape)
                    .background(PrimaryBlue),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.Person, contentDescription = "AI Care Manager", tint = Color.White, modifier = Modifier.size(80.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Hello ${user.name}, I'm your\nRecoveryLoop care manager.",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                lineHeight = 26.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "How are you feeling today?\nYou can speak or type. Care Plan: ${user.condition}",
                fontSize = 13.sp,
                color = TextMuted,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(BadgeGreen)
                    .clickable { },
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.Mic, contentDescription = "Listening", tint = Color.White, modifier = Modifier.size(36.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Listening...", fontSize = 12.sp, color = BadgeGreen, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onClose) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(BadgeRed),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.CallEnd, contentDescription = "End", tint = Color.White)
                    }
                }

                TextButton(onClick = onClose) {
                    Text(text = "Switch to Text", color = Color.White, fontSize = 12.sp)
                }

                TextButton(onClick = { }) {
                    Icon(imageVector = Icons.Default.Language, contentDescription = "Language", tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Change Language", color = Color.White, fontSize = 12.sp)
                }
            }
        }
    }
}

// ==========================================
// SCREEN 6: ALERTS & HIGH PRIORITY SCREEN
// ==========================================

@Composable
fun AlertsAndHealthScreen(
    user: UserProfile,
    onShowSecurityAudit: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBgLight)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Health Alerts & Monitoring",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextMain
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = BadgeRedBg)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(BadgeRed),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Warning, contentDescription = "Alert", tint = Color.White, modifier = Modifier.size(20.dp))
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(text = "Alert", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BadgeRed)
                            Text(text = "Needs Attention", fontSize = 12.sp, color = TextMain, fontWeight = FontWeight.Bold)
                        }
                    }

                    Text(text = "Today, 4:32 PM", fontSize = 11.sp, color = TextMuted)
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(text = "Patient: ${user.name}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextMain)
                Text(text = "ID: ${user.patientCode} • ${user.condition}", fontSize = 11.sp, color = TextMuted)

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Reported: Severe breathlessness and chest pain",
                    fontSize = 13.sp,
                    color = BadgeRed,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(BadgeRed)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(text = "High Priority", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(22.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BadgeRed)
                ) {
                    Text(text = "View Details >", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Recent Alerts Log",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextMain
        )

        Spacer(modifier = Modifier.height(12.dp))

        AlertLogRow(title = "Missed Check-in", time = "Day ${user.dayCount - 1} • 10:00 AM", icon = Icons.Default.Notifications, iconColor = BadgeOrange)
        AlertLogRow(title = "High Temperature (102°F)", time = "Day ${user.dayCount - 3} • 102°F", icon = Icons.Default.Thermostat, iconColor = BadgeRed)
        AlertLogRow(title = "Medication Not Taken", time = "Day ${user.dayCount - 4} • Evening dose", icon = Icons.Default.Medication, iconColor = PrimaryBlue)

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun AlertLogRow(
    title: String,
    time: String,
    icon: ImageVector,
    iconColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = title, tint = iconColor, modifier = Modifier.size(18.dp))
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextMain)
                Text(text = time, fontSize = 11.sp, color = TextMuted)
            }
        }
    }
}

// ==========================================
// SCREEN 7: HOSPITAL CARE STAFF DASHBOARD
// ==========================================

@Composable
fun HospitalDashboardScreen(
    allUsers: List<UserProfile>,
    onPatientSelected: (UserProfile) -> Unit,
    onAddUserClicked: () -> Unit,
    onShowSecurityAudit: () -> Unit = {}
) {
    var selectedFilter by remember { mutableIntStateOf(0) } // 0: All, 1: At Risk, 2: Stable
    var searchQuery by remember { mutableStateOf("") }

    val filteredUsers = remember(searchQuery, selectedFilter, allUsers) {
        allUsers.filter { user ->
            val matchesQuery = user.name.contains(searchQuery, ignoreCase = true) || user.patientCode.contains(searchQuery, ignoreCase = true)
            val matchesFilter = when (selectedFilter) {
                1 -> user.status == "At Risk"
                2 -> user.status == "Stable"
                else -> true
            }
            matchesQuery && matchesFilter
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBgLight)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Hospital Care Dashboard",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMain
                )
                Text(
                    text = "Post-Discharge Patient Monitoring",
                    fontSize = 12.sp,
                    color = TextMuted
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onShowSecurityAudit) {
                    Icon(imageVector = Icons.Default.Notifications, contentDescription = "Security Audit", tint = PrimaryBlue)
                }

                Spacer(modifier = Modifier.width(4.dp))

                Button(
                    onClick = onAddUserClicked,
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Patient", fontSize = 11.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterTabPill(label = "All Patients (${allUsers.size})", isSelected = selectedFilter == 0, onClick = { selectedFilter = 0 })
            FilterTabPill(label = "At Risk (${allUsers.count { it.status == "At Risk" }})", isSelected = selectedFilter == 1, onClick = { selectedFilter = 1 })
            FilterTabPill(label = "Stable (${allUsers.count { it.status == "Stable" }})", isSelected = selectedFilter == 2, onClick = { selectedFilter = 2 })
        }

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search patient by name or ID...", fontSize = 12.sp) },
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = TextMuted) },
            trailingIcon = { Icon(imageVector = Icons.Default.FilterList, contentDescription = "Filter", tint = TextMuted) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryBlue,
                unfocusedBorderColor = Color.LightGray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        filteredUsers.forEach { user ->
            PatientMonitoringCard(
                user = user,
                onClick = { onPatientSelected(user) }
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun FilterTabPill(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) PrimaryBlue else Color.White)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color.White else TextMain
        )
    }
}

@Composable
fun PatientMonitoringCard(
    user: UserProfile,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(user.avatarBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.Person, contentDescription = "Avatar", tint = Color.White)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = user.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMain
                    )

                    PillTag(
                        text = user.status,
                        backgroundColor = if (user.status == "At Risk") BadgeRedBg else BadgeGreenBg,
                        textColor = if (user.status == "At Risk") BadgeRed else BadgeGreen
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(text = "${user.patientCode} • Day ${user.dayCount}", fontSize = 11.sp, color = TextMuted)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = "${user.condition} (Dr. ${user.doctorName})", fontSize = 11.sp, color = if (user.status == "At Risk") BadgeRed else TextMuted, fontWeight = FontWeight.Medium)
            }
        }
    }
}

// ==========================================
// PRESCRIPTION VIEWER MODAL
// ==========================================

@Composable
fun PrescriptionViewerDialog(
    prescription: PrescriptionData,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (prescription.fileType == "PDF") Icons.Default.PictureAsPdf else Icons.Default.Image,
                    contentDescription = "Rx",
                    tint = PrimaryBlue,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Doctor's Prescription",
                    color = TextMain,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(CardLightBlue)
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Description, contentDescription = "File", tint = TextMuted)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = prescription.fileName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextMain)
                        Text(text = "Uploaded: ${prescription.uploadedDate} • Format: ${prescription.fileType}", fontSize = 10.sp, color = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(text = "Prescribing Doctor:", fontSize = 11.sp, color = TextMuted)
                Text(text = prescription.doctorName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextMain)

                Spacer(modifier = Modifier.height(6.dp))

                Text(text = "Facility:", fontSize = 11.sp, color = TextMuted)
                Text(text = prescription.clinicName, fontSize = 13.sp, color = TextMain)

                Spacer(modifier = Modifier.height(6.dp))

                Text(text = "Extracted Diagnosis:", fontSize = 11.sp, color = TextMuted)
                Text(text = prescription.diagnosis, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = PrimaryBlue)

                Spacer(modifier = Modifier.height(12.dp))

                Text(text = "Prescribed Regimen:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextMain)
                Spacer(modifier = Modifier.height(4.dp))
                prescription.extractedDosages.forEach { dose ->
                    Text(text = "• $dose", fontSize = 12.sp, color = TextMain)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(text = "Special Instructions:", fontSize = 11.sp, color = TextMuted)
                Text(text = prescription.specialInstructions, fontSize = 12.sp, color = TextMain, lineHeight = 16.sp)
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text("Close Viewer", color = Color.White)
            }
        }
    )
}

// ==========================================
// ADD PATIENT USER DIALOG WITH PRESCRIPTION UPLOAD
// ==========================================

@Composable
fun AddUserDialog(
    onDismiss: () -> Unit,
    onUserCreated: (UserProfile) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var condition by remember { mutableStateOf("") }
    var doctorName by remember { mutableStateOf("") }
    var attachedDocName by remember { mutableStateOf<String?>(null) }
    var attachedDocType by remember { mutableStateOf("PDF") }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val path = uri.path ?: "prescription_scan"
            attachedDocName = if (path.contains("pdf", ignoreCase = true)) "Rx_Doctor_Prescription.pdf" else "Rx_Scan_Document.jpg"
            attachedDocType = if (attachedDocName!!.endsWith(".pdf", ignoreCase = true)) "PDF" else "IMAGE"
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        title = {
            Text(text = "Add Patient Profile & Prescription", color = TextMain, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Patient Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = condition,
                    onValueChange = { condition = it },
                    label = { Text("Condition / Discharge Plan") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = doctorName,
                    onValueChange = { doctorName = it },
                    label = { Text("Lead Doctor / Physician") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Attach Doctor's Prescription (PDF / JPG / PNG):",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMain
                )

                Spacer(modifier = Modifier.height(6.dp))

                if (attachedDocName != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(CardLightGreen)
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (attachedDocType == "PDF") Icons.Default.PictureAsPdf else Icons.Default.Image,
                            contentDescription = "Rx",
                            tint = BadgeGreen
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = attachedDocName!!, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextMain)
                            Text(text = "Prescription Attached & Parsed ✓", fontSize = 10.sp, color = BadgeGreen, fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { filePickerLauncher.launch("*/*") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CardLightBlue)
                        ) {
                            Icon(imageVector = Icons.Default.AttachFile, contentDescription = "Upload", tint = PrimaryBlue)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Browse File", fontSize = 11.sp, color = PrimaryBlue, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                attachedDocName = "Dr_Richter_Knee_Prescription.pdf"
                                attachedDocType = "PDF"
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CardLightGreen)
                        ) {
                            Icon(imageVector = Icons.Default.Description, contentDescription = "Sample", tint = BadgeGreen)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Sample Rx", fontSize = 11.sp, color = BadgeGreen, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && condition.isNotBlank()) {
                        val rxData = attachedDocName?.let { docName ->
                            PrescriptionData(
                                fileName = docName,
                                fileType = attachedDocType,
                                uploadedDate = "Today",
                                doctorName = doctorName.ifBlank { "Dr. Richter, M.D." },
                                clinicName = "RecoveryCare Surgical Clinic",
                                diagnosis = condition,
                                specialInstructions = "Extracted from $docName. Follow prescription regimen and daily recovery plan.",
                                extractedDosages = listOf("Naproxen (500mg - Morning)", "Evening Medicine (8:00 PM)")
                            )
                        }

                        val randCode = (1000..9999).random()
                        val newUser = UserProfile(
                            id = System.currentTimeMillis().toString(),
                            name = name,
                            patientCode = "PAT-$randCode",
                            dayCount = 1,
                            totalDays = 30,
                            status = "Stable",
                            condition = condition,
                            doctorName = doctorName.ifBlank { "Dr. Smith" },
                            avatarBg = listOf(PrimaryBlue, Color(0xFFE11D48), Color(0xFFD97706), Color(0xFF059669)).random(),
                            prescription = rxData
                        )
                        onUserCreated(newUser)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text("Create Patient Profile", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextMuted)
            }
        }
    )
}

// ==========================================
// REUSABLE GRAPHICS & PILLS
// ==========================================

@Composable
fun RecoveryLoopLogoGraphic(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val strokeWidth = 8.dp.toPx()
        val radius = size.minDimension / 4f
        val leftCenter = Offset(size.width * 0.35f, size.height / 2f)
        val rightCenter = Offset(size.width * 0.65f, size.height / 2f)

        drawArc(
            color = BadgeGreen,
            startAngle = 45f,
            sweepAngle = 270f,
            useCenter = false,
            topLeft = Offset(leftCenter.x - radius, leftCenter.y - radius),
            size = Size(radius * 2f, radius * 2f),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        drawArc(
            color = PrimaryBlue,
            startAngle = -135f,
            sweepAngle = 270f,
            useCenter = false,
            topLeft = Offset(rightCenter.x - radius, rightCenter.y - radius),
            size = Size(radius * 2f, radius * 2f),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
    }
}

@Composable
fun PillTag(text: String, backgroundColor: Color, textColor: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
        )
    }
}
