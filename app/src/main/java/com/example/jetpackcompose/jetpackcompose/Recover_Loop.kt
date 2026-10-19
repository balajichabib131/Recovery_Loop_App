package com.example.jetpackcompose.jetpackcompose

import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

// ==========================================
// BRAND COLOR PALETTE (RecoveryLoop Theme)
// ==========================================
private val PrimaryBlue = Color(0xFF0284C7)
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
// ENUMS & DATA MODELS
// ==========================================

enum class AppRole { ONBOARDING, PATIENT, HOSPITAL_STAFF, CLINICIAN }

enum class ChatSender { USER, AI_BOT }

data class ChatMessage(
    val id: String,
    val sender: ChatSender,
    val text: String,
    val time: String,
    val isAudio: Boolean = false,
    val audioDuration: String? = null,
    val quickReplies: List<String>? = null,
    val isLoading: Boolean = false
)

data class HealthAlertItem(
    val id: String,
    val title: String,
    val time: String,
    val detailText: String,
    val isHighPriority: Boolean,
    val icon: ImageVector,
    val color: Color
)

data class PatientRecord(
    val id: String,
    val name: String,
    val patientCode: String,
    val dayCount: Int,
    val status: String, // "At Risk", "Stable"
    val subtitle: String,
    val avatarBg: Color
)

// ==========================================
// MEDICINE DATASET FROM medicine_dataset.csv
// ==========================================

data class MedicineItem(
    val name: String,
    val category: String,
    val dosageForm: String,
    val strength: String,
    val manufacturer: String,
    val indication: String,
    val classification: String
)

object MedicineRepository {
    val dataset: List<MedicineItem> = listOf(
        MedicineItem("Amoxicillin", "Antibiotic", "Tablet", "802 mg", "Teva Pharmaceutical", "Wound & Infection", "Over-the-Counter"),
        MedicineItem("Acetocillin", "Antidiabetic", "Cream", "938 mg", "Roche Holding AG", "Virus", "Over-the-Counter"),
        MedicineItem("Ibuprocillin", "Antiviral", "Injection", "337 mg", "CSL Limited", "Infection", "Over-the-Counter"),
        MedicineItem("Dextrophen", "Antibiotic", "Ointment", "333 mg", "Johnson & Johnson", "Wound", "Prescription"),
        MedicineItem("Clarinazole", "Antifungal", "Syrup", "362 mg", "AbbVie Inc.", "Pain", "Prescription"),
        MedicineItem("Ibupromycin", "Antibiotic", "Injection", "140 mg", "Takeda Pharmaceutical", "Infection", "Prescription"),
        MedicineItem("Metovir", "Antipyretic", "Ointment", "641 mg", "Takeda Pharmaceutical", "Infection", "Over-the-Counter"),
        MedicineItem("Cefcillin", "Antipyretic", "Tablet", "954 mg", "Takeda Pharmaceutical", "Fungus", "Over-the-Counter"),
        MedicineItem("Acetomycin", "Analgesic", "Inhaler", "838 mg", "Novo Nordisk A/S", "Wound", "Prescription"),
        MedicineItem("Acetonazole", "Antipyretic", "Ointment", "575 mg", "Bayer AG", "Wound", "Prescription"),
        MedicineItem("Dolocillin", "Antiviral", "Ointment", "451 mg", "Novo Nordisk A/S", "Diabetes", "Prescription"),
        MedicineItem("Cefmet", "Antifungal", "Tablet", "440 mg", "AstraZeneca plc", "Depression", "Prescription"),
        MedicineItem("Dolophen", "Antibiotic", "Capsule", "469 mg", "Johnson & Johnson", "Depression", "Prescription"),
        MedicineItem("Ibupronazole", "Analgesic", "Injection", "720 mg", "Novartis AG", "Fungus", "Prescription"),
        MedicineItem("Amoxiprofen", "Antiviral", "Ointment", "861 mg", "Novo Nordisk A/S", "Diabetes", "Prescription"),
        MedicineItem("Amoxistatin", "Antiviral", "Inhaler", "46 mg", "Sanofi S.A.", "Fever", "Over-the-Counter"),
        MedicineItem("Dextrocillin", "Antidiabetic", "Inhaler", "392 mg", "Sanofi S.A.", "Pain", "Over-the-Counter"),
        MedicineItem("Clariphen", "Antidiabetic", "Syrup", "300 mg", "GlaxoSmithKline plc", "Fungus", "Over-the-Counter"),
        MedicineItem("Dolomet", "Antifungal", "Drops", "335 mg", "Amgen Inc.", "Diabetes", "Prescription"),
        MedicineItem("Acetoprofen", "Antibiotic", "Inhaler", "909 mg", "CSL Limited", "Fever", "Prescription"),
        MedicineItem("Amoximet", "Analgesic", "Ointment", "795 mg", "Gilead Sciences, Inc.", "Pain", "Prescription"),
        MedicineItem("Doloprofen", "Antibiotic", "Inhaler", "981 mg", "Eli Lilly and Company", "Fungus", "Over-the-Counter"),
        MedicineItem("Clariprofen", "Antifungal", "Tablet", "123 mg", "Pfizer Inc.", "Infection", "Prescription"),
        MedicineItem("Metocillin", "Analgesic", "Tablet", "892 mg", "Eli Lilly and Company", "Pain", "Over-the-Counter"),
        MedicineItem("Cefstatin", "Antipyretic", "Ointment", "19 mg", "Roche Holding AG", "Fever", "Prescription"),
        MedicineItem("Metomycin", "Antifungal", "Capsule", "718 mg", "Pfizer Inc.", "Wound", "Over-the-Counter"),
        MedicineItem("Clarivir", "Analgesic", "Tablet", "14 mg", "Bristol-Myers Squibb", "Pain", "Prescription"),
        MedicineItem("Ibuproprofen", "Analgesic", "Cream", "178 mg", "CSL Limited", "Pain", "Over-the-Counter"),
        MedicineItem("Cefprofen", "Antiseptic", "Tablet", "245 mg", "Novartis AG", "Pain", "Over-the-Counter"),
        MedicineItem("Dextromycin", "Antipyretic", "Capsule", "986 mg", "Takeda Pharmaceutical", "Wound", "Over-the-Counter")
    )

    fun search(query: String): List<MedicineItem> {
        if (query.isBlank()) return dataset
        val q = query.trim().lowercase()
        return dataset.filter {
            it.name.lowercase().contains(q) ||
            it.category.lowercase().contains(q) ||
            it.indication.lowercase().contains(q) ||
            it.manufacturer.lowercase().contains(q) ||
            it.dosageForm.lowercase().contains(q)
        }
    }
}

// ==========================================
// RECOVERYLOOP GEMINI AI ENGINE (SINGLE PROMPT ARCHITECTURE)
// ==========================================

object RecoveryLoopAIEngine {
    var userApiKey by mutableStateOf("")

    private const val SYSTEM_PROMPT = """
You are Dr. RecoveryLoop, an expert clinical care manager and empathetic post-operative physician caring for patient Ramesh Kumar (Day 12 post total knee arthroplasty / surgery).

Clinical Persona & Communication Guidelines:
1. THINK & RESPOND LIKE A HUMANE DOCTOR: Speak warmly, professionally, and thoughtfully ("Hello Ramesh", "I understand", "That is reassuring"). Show genuine medical insight into post-op recovery.
2. MEDICAL TRIAGE RULES:
   - RED FLAGS (Fever >101°F / 38.3°C, sudden chest pain, shortness of breath, calf tenderness/DVT signs, heavy surgical bleeding, pus or expanding redness at incision): Immediately begin response with '[ALERT: HIGH RISK]'. Advise Ramesh to rest, stay calm, and contact emergency care or Dr. Richter right away.
   - MODERATE SYMPTOMS (Pain 4-6/10, mild stiffness, slight swelling after walking): Reassure that post-op Day 12 stiffness is normal. Recommend RICE protocol (Rest, Ice 15-20 mins, Compression, Elevation) and gentle range-of-motion knee extensions.
   - STABLE / PROGRESSING (Pain 0-3/10, completed walks, took meds): Commend Ramesh for compliance. Remind him to keep incision clean and dry, avoid heavy lifting, and drink fluids.
3. CONVERSATIONAL CONTINUITY: ALWAYS acknowledge Ramesh's specific input before providing advice, and end with a single, relevant clinical follow-up question.
4. BREVITY: Keep your answer under 100 words—warm, crisp, and medical.
"""

    /**
     * Converts everything (system instructions, conversation history, language preference, and current user message)
     * into a single comprehensive prompt string sent to Gemini.
     */
    suspend fun generateStreamResponse(
        userMessage: String,
        selectedLanguage: String = "English",
        historyMessages: List<ChatMessage> = emptyList(),
        onChunkReceived: (String) -> Unit
    ): String = withContext(Dispatchers.IO) {
        val apiKeyToUse = userApiKey.ifBlank { System.getenv("GEMINI_API_KEY") ?: "AQ.Ab8RN6Jkr5-eipj34eMHErHwGMqcnej-icRqtusgMKpld8X3JQ" }
        val fullTextBuilder = StringBuilder()

        // Build a single unified prompt containing system prompt, recent history, language, and current message
        val singlePromptBuilder = StringBuilder().apply {
            append(SYSTEM_PROMPT.trimIndent())
            append("\n\nTarget Response Language: $selectedLanguage\n")
            append("--- Conversation History ---\n")
            
            // Include up to last 8 messages for robust multi-turn context within the single prompt
            val recentHistory = historyMessages.takeLast(8)
            for (msg in recentHistory) {
                val roleLabel = if (msg.sender == ChatSender.USER) "Patient Ramesh" else "Dr. RecoveryLoop AI"
                append("$roleLabel: ${msg.text}\n")
            }
            
            append("--- Current Interaction ---\n")
            append("Patient Ramesh says: $userMessage\n")
            append("Dr. RecoveryLoop AI response:")
        }

        val finalSinglePrompt = singlePromptBuilder.toString()

        if (apiKeyToUse.isNotBlank()) {
            try {
                val model = GenerativeModel(
                    modelName = "gemini-1.5-flash",
                    apiKey = apiKeyToUse
                )

                model.generateContentStream(finalSinglePrompt).collect { chunkResponse ->
                    val chunkText = chunkResponse.text
                    if (!chunkText.isNullOrEmpty()) {
                        fullTextBuilder.append(chunkText)
                        withContext(Dispatchers.Main) {
                            onChunkReceived(chunkText)
                        }
                    }
                }

                val finalResult = fullTextBuilder.toString().trim()
                if (finalResult.isNotBlank()) {
                    return@withContext finalResult
                }
            } catch (_: Exception) {
                // Fallback to active local streaming engine
            }
        }

        // Active Local Streaming Response (Word-by-word real-time stream)
        val fallback = generateLocalFallbackResponse(userMessage)
        val words = fallback.split(" ")
        for (i in words.indices) {
            val word = words[i] + if (i == words.lastIndex) "" else " "
            fullTextBuilder.append(word)
            withContext(Dispatchers.Main) {
                onChunkReceived(word)
            }
            delay(25) // Active typing effect
        }

        return@withContext fallback
    }

    fun computeContextualQuickReplies(response: String): List<String> {
        val lower = response.lowercase()
        return when {
            lower.contains("high risk") || lower.contains("alert") || lower.contains("emergency") ->
                listOf("📞 Call Emergency Care", "📞 Call Dr. Richter", "⚠️ Family is with me")
            lower.contains("pain") || lower.contains("ice") || lower.contains("elevate") ->
                listOf("💊 Took Pain Meds", "🧊 Ice Pack Applied", "🩺 Pain level 3/10")
            lower.contains("walk") || lower.contains("exercise") ->
                listOf("🚶 Completed 10-min Walk", "🦵 Knee Exercise Done", "❓ Feeling Slight Stiffness")
            lower.contains("med") || lower.contains("dose") ->
                listOf("💊 Took Dose with Food", "⏰ Set Dose Reminder", "❓ No Side Effects")
            lower.contains("wound") || lower.contains("dressing") || lower.contains("incision") ->
                listOf("✅ Dressing is Clean & Dry", "🩺 Slight Redness Seen", "❓ Ask Dressing Tip")
            else ->
                listOf("😊 Feeling good", "😐 Mild stiffness", "🤒 Feeling discomfort", "📋 View Schedule")
        }
    }

    private fun generateLocalFallbackResponse(message: String): String {
        val lower = message.lowercase()

        // Check if query matches any item in the Medicine Dataset
        val matchedMeds = MedicineRepository.search(message)
        val matchesSpecificMedName = matchedMeds.any { lower.contains(it.name.lowercase()) }

        if (matchedMeds.isNotEmpty() && matchesSpecificMedName) {
            val med = matchedMeds.first()
            return "Based on your clinical medicine dataset, ${med.name} (${med.strength} ${med.dosageForm}) is a ${med.category} manufactured by ${med.manufacturer}, indicated for ${med.indication} [${med.classification}].\n\nWould you like me to log ${med.name} into your Day 12 recovery schedule?"
        }

        return when {
            // RED FLAGS
            lower.contains("fever") || lower.contains("101") || lower.contains("102") || lower.contains("103") ||
            lower.contains("chest pain") || lower.contains("shortness of breath") || lower.contains("breath") ||
            lower.contains("bleeding") || lower.contains("pus") || lower.contains("discharge") || lower.contains("calf pain") -> {
                "[ALERT: HIGH RISK] Ramesh, as your doctor, I am concerned about these symptoms ($message). I am escalating this immediately to Dr. Richter and your care team.\n\nPlease rest elevated, do not walk, and seek emergency medical evaluation if breathing or pain worsens. Are you with family right now?"
            }

            // HIGH PAIN (7-10)
            lower.contains("pain 7") || lower.contains("pain 8") || lower.contains("pain 9") || lower.contains("pain 10") ||
            lower.contains("severe pain") || lower.contains("unbearable") || lower.contains("hurts a lot") -> {
                "I hear you, Ramesh. A pain level of 7+ on Day 12 requires attention. Please sit comfortably, apply an ice pack to your knee for 15 minutes, and elevate your leg above heart level.\n\nDid you take your prescribed anti-inflammatory or pain medication this morning?"
            }

            // MODERATE PAIN / STIFFNESS (4-6)
            lower.contains("pain 4") || lower.contains("pain 5") || lower.contains("pain 6") ||
            lower.contains("stiff") || lower.contains("swelling") || lower.contains("swollen") || lower.contains("tight") -> {
                "Hello Ramesh. Mild stiffness and swelling around Day 12 is a normal part of joint tissue remodeling. Applying ice for 15 minutes post-exercise helps reduce local inflammation.\n\nHave you been elevating your leg on a pillow while resting today?"
            }

            // WALKING / EXERCISE
            lower.contains("walk") || lower.contains("exercise") || lower.contains("steps") || lower.contains("knee") -> {
                "Excellent work, Ramesh! 🚶 Gradual weight-bearing and short 10-minute extension walks prevent scar tissue adhesion and promote blood circulation.\n\nHow did your knee feel during the walk—was there any sharp discomfort?"
            }

            // MEDICATIONS
            lower.contains("med") || lower.contains("pill") || lower.contains("dose") || lower.contains("paracetamol") || lower.contains("tablet") -> {
                val medSample = MedicineRepository.dataset.random()
                "Logged your medication, Ramesh! 💊 Consistency with anti-inflammatory doses like ${medSample.name} (${medSample.strength}) maintains steady recovery coverage.\n\nDid you take your dose after a light meal, and are you feeling any nausea?"
            }

            // WOUND / DRESSING
            lower.contains("wound") || lower.contains("incision") || lower.contains("dressing") || lower.contains("bandage") || lower.contains("stitch") -> {
                "Monitoring your surgical incision is vital on Day 12. Keep the dressing completely clean and dry, and avoid soaking in water until cleared by Dr. Richter.\n\nIs there any surrounding redness, warmth, or fluid seepage on the bandage?"
            }

            // POSITIVE / STABLE
            lower.contains("better") || lower.contains("good") || lower.contains("great") || lower.contains("fine") || lower.contains("okay") -> {
                "That is very reassuring to hear, Ramesh! 🎉 Your steady progress on Day 12 indicates healthy healing and good compliance.\n\nHave you taken your afternoon fluids and completed your gentle leg elevation today?"
            }

            // DEFAULT HUMAN DOCTOR CHECK-IN
            else -> {
                "Thank you for sharing that update, Ramesh. As your care manager, I have recorded: \"$message\" in your daily health log.\n\nOn a scale of 1 to 10, how would you rate your knee comfort right now?"
            }
        }
    }
}

// ==========================================
// MAIN APP CONTAINER WITH ROUTING
// ==========================================

@Preview(showBackground = true)
@Composable
fun RecoverLoopApp() {
    var currentRole by remember { mutableStateOf(AppRole.PATIENT) }
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Home, 1: Plan, 2: Chat, 3: Health, 4: More
    var isVoiceCheckinActive by remember { mutableStateOf(false) }

    // Global Dynamic Health Alerts State
    val healthAlerts = remember {
        mutableStateListOf(
            HealthAlertItem(
                id = "1",
                title = "High Priority Alert",
                time = "Today, 4:32 PM",
                detailText = "Reported: Severe breathlessness and chest pain",
                isHighPriority = true,
                icon = Icons.Default.Warning,
                color = BadgeRed
            ),
            HealthAlertItem(
                id = "2",
                title = "Missed Check-in",
                time = "Day 11 • 10:00 AM",
                detailText = "Daily morning voice check-in missed",
                isHighPriority = false,
                icon = Icons.Default.Notifications,
                color = BadgeOrange
            ),
            HealthAlertItem(
                id = "3",
                title = "High Temperature (102°F)",
                time = "Day 9 • 102°F",
                detailText = "Fever spike logged during evening check-in",
                isHighPriority = false,
                icon = Icons.Default.Thermostat,
                color = BadgeRed
            )
        )
    }

    var isPatientAtRisk by remember { mutableStateOf(true) }

    fun onNewHighPriorityAlert(alertText: String) {
        isPatientAtRisk = true
        healthAlerts.add(
            0,
            HealthAlertItem(
                id = System.currentTimeMillis().toString(),
                title = "AI Health Alert",
                time = "Just now",
                detailText = alertText,
                isHighPriority = true,
                icon = Icons.Default.Warning,
                color = BadgeRed
            )
        )
    }

    if (isVoiceCheckinActive) {
        AIVoiceCheckinScreen(
            onClose = { isVoiceCheckinActive = false },
            onHighPriorityAlertDetected = { alertText ->
                onNewHighPriorityAlert(alertText)
            }
        )
        return
    }

    Scaffold(
        containerColor = AppBgLight,
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        bottomBar = {
            if (currentRole != AppRole.ONBOARDING) {
                RecoveryLoopBottomNav(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it }
                )
            }
        }
    ) { innerPadding ->
        androidx.compose.material3.Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = AppBgLight
        ) {
            when {
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
                        isPatientAtRisk = isPatientAtRisk,
                        onSwitchToPatient = { currentRole = AppRole.PATIENT; selectedTab = 0 },
                        onSwitchRole = { currentRole = AppRole.ONBOARDING }
                    )
                }

                selectedTab == 0 -> {
                    PatientHomeScreen(
                        onNavigateToPlan = { selectedTab = 1 },
                        onNavigateToChat = { selectedTab = 2 },
                        onStartVoiceCheckin = { isVoiceCheckinActive = true },
                        onSwitchRole = { currentRole = AppRole.ONBOARDING }
                    )
                }

                selectedTab == 1 -> {
                    RecoveryPlanScreen()
                }

                selectedTab == 2 -> {
                    AIChatbotScreen(
                        onStartVoiceCheckin = { isVoiceCheckinActive = true },
                        onHighPriorityAlertDetected = { alertText ->
                            onNewHighPriorityAlert(alertText)
                        }
                    )
                }

                selectedTab == 3 -> {
                    AlertsAndHealthScreen(alertsList = healthAlerts)
                }
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
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PrimaryBlue,
                selectedTextColor = PrimaryBlue,
                indicatorColor = CardLightBlue
            )
        )
        NavigationBarItem(
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) },
            icon = { Icon(Icons.Default.DateRange, contentDescription = "Plan") },
            label = { Text("Plan", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PrimaryBlue,
                selectedTextColor = PrimaryBlue,
                indicatorColor = CardLightBlue
            )
        )
        NavigationBarItem(
            selected = selectedTab == 2,
            onClick = { onTabSelected(2) },
            icon = { Icon(Icons.Default.Chat, contentDescription = "AI Assistant") },
            label = { Text("AI Assistant", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PrimaryBlue,
                selectedTextColor = PrimaryBlue,
                indicatorColor = CardLightBlue
            )
        )
        NavigationBarItem(
            selected = selectedTab == 3,
            onClick = { onTabSelected(3) },
            icon = { Icon(Icons.Default.Notifications, contentDescription = "Alerts") },
            label = { Text("Alerts", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PrimaryBlue,
                selectedTextColor = PrimaryBlue,
                indicatorColor = CardLightBlue
            )
        )
        NavigationBarItem(
            selected = selectedTab == 4,
            onClick = { onTabSelected(4) },
            icon = { Icon(Icons.Default.LocalHospital, contentDescription = "Staff") },
            label = { Text("Care Staff", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PrimaryBlue,
                selectedTextColor = PrimaryBlue,
                indicatorColor = CardLightBlue
            )
        )
    }
}

// ==========================================
// SCREEN 1: ROLE SELECTION ONBOARDING
// ==========================================

@Composable
fun RoleSelectionScreen(onRoleSelected: (AppRole) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBgLight)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        RecoveryLoopFullLogo()

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "Select your role to view experience:",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = TextMain,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        RoleCard(
            title = "Post-Op Patient",
            subtitle = "Ramesh Kumar • Day 12 Post-Surgery\nAI Assistant & Daily Voice Check-in",
            icon = Icons.Default.Person,
            cardBg = CardLightBlue,
            accentColor = PrimaryBlue,
            onClick = { onRoleSelected(AppRole.PATIENT) }
        )

        Spacer(modifier = Modifier.height(14.dp))

        RoleCard(
            title = "Hospital Care Manager / Staff",
            subtitle = "Monitoring 24 Post-Discharge Patients\nAt-Risk Triaging & Escalations",
            icon = Icons.Default.LocalHospital,
            cardBg = CardLightGreen,
            accentColor = BadgeGreen,
            onClick = { onRoleSelected(AppRole.HOSPITAL_STAFF) }
        )

        Spacer(modifier = Modifier.height(14.dp))

        RoleCard(
            title = "Attending Clinician",
            subtitle = "Dr. Richter • Surgical Care Team\nView High Priority Health Alerts",
            icon = Icons.Default.Favorite,
            cardBg = CardLightPurple,
            accentColor = Color(0xFF8B5CF6),
            onClick = { onRoleSelected(AppRole.CLINICIAN) }
        )
    }
}

@Composable
fun RoleCard(
    title: String,
    subtitle: String,
    cardBg: Color,
    accentColor: Color,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(accentColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMain
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = TextMuted,
                    lineHeight = 16.sp
                )
            }

            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Select",
                tint = accentColor
            )
        }
    }
}

// ==========================================
// SCREEN 2: PATIENT HOME SCREEN
// ==========================================

@Composable
fun PatientHomeScreen(
    onNavigateToPlan: () -> Unit,
    onNavigateToChat: () -> Unit,
    onStartVoiceCheckin: () -> Unit,
    onSwitchRole: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBgLight)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Patient Header Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(PrimaryBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "RK",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Ramesh Kumar",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMain
                    )
                    Text(
                        text = "Day 12 Post-Surgery • Recovery Plan Active",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            }

            IconButton(onClick = onSwitchRole) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = TextMain
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // AI Voice Check-In Call Out Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = PrimaryBlue)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhoneInTalk,
                            contentDescription = "Voice Call",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "AI Voice Check-in Active",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Your RecoveryLoop AI Manager is online for your Day 12 health check-in. Tap below to speak or chat actively.",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = onStartVoiceCheckin,
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BadgeGreen)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Mic",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Start Voice Check-in", fontSize = 12.sp)
                    }

                    Button(
                        onClick = onNavigateToChat,
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f))
                    ) {
                        Text(text = "Text Chat", fontSize = 12.sp, color = Color.White)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Recovery Progress Gauge Card
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
                    Text(
                        text = "Recovery Milestone",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMain
                    )

                    PillTag(
                        text = "68% Complete",
                        backgroundColor = BadgeGreenBg,
                        textColor = BadgeGreen
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                LinearProgressIndicator(
                    progress = { 0.68f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp)),
                    color = PrimaryBlue,
                    trackColor = CardLightBlue,
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Day 12 of 18 Recovery Loop • 4 Tasks Remaining Today",
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Status Grid 2x2
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatusGridCard(
                title = "Next Task",
                value = "11:00 AM Walk",
                bgColor = CardLightGreen,
                icon = Icons.Default.DirectionsWalk,
                iconTint = BadgeGreen,
                modifier = Modifier.weight(1f)
            )

            StatusGridCard(
                title = "Medication",
                value = "3 / 4 Taken",
                bgColor = CardLightBlue,
                icon = Icons.Default.Medication,
                iconTint = PrimaryBlue,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatusGridCard(
                title = "Symptom Status",
                value = "Stable",
                bgColor = CardLightPurple,
                icon = Icons.Default.Favorite,
                iconTint = Color(0xFF8B5CF6),
                modifier = Modifier.weight(1f)
            )

            StatusGridCard(
                title = "Care Doctor",
                value = "Dr. Richter",
                bgColor = CardLightYellow,
                icon = Icons.Default.LocalHospital,
                iconTint = BadgeOrange,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Daily Checklist Button Bar
        Button(
            onClick = onNavigateToPlan,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
        ) {
            Text(text = "View Today's Recovery Schedule >", fontSize = 14.sp)
        }
    }
}

@Composable
fun StatusGridCard(
    title: String,
    value: String,
    bgColor: Color,
    iconTint: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(text = title, fontSize = 11.sp, color = TextMuted)
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextMain
            )
        }
    }
}

// ==========================================
// SCREEN 3: RECOVERY PLAN & TIMELINE SCREEN
// ==========================================

@Composable
fun RecoveryPlanScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBgLight)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Today's Recovery Plan",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextMain
        )

        Text(
            text = "Day 12 • Post-Discharge Knee Surgery Protocol",
            fontSize = 12.sp,
            color = TextMuted
        )

        Spacer(modifier = Modifier.height(16.dp))

        TimelineTaskItem(
            time = "08:00 AM",
            title = "Morning Medication",
            subtitle = "Take Paracetamol 500mg & Antibiotic after breakfast",
            isCompleted = true,
            icon = Icons.Default.Medication,
            color = BadgeGreen
        )

        TimelineTaskItem(
            time = "09:00 AM",
            title = "AI Voice Check-in",
            subtitle = "Log daily pain rating & swelling update with AI Care Manager",
            isCompleted = true,
            icon = Icons.Default.PhoneInTalk,
            color = PrimaryBlue
        )

        TimelineTaskItem(
            time = "11:00 AM",
            title = "Guided Knee Extension Walk",
            subtitle = "10-minute slow walk with support brace",
            isCompleted = false,
            icon = Icons.Default.DirectionsWalk,
            color = TealAccent
        )

        TimelineTaskItem(
            time = "01:30 PM",
            title = "High Protein Lunch & Hydration",
            subtitle = "Protein-rich meal to assist wound healing + 500ml water",
            isCompleted = false,
            icon = Icons.Default.Restaurant,
            color = BadgeOrange
        )

        TimelineTaskItem(
            time = "05:00 PM",
            title = "Surgical Wound Inspection",
            subtitle = "Check dressing for discharge or redness",
            isCompleted = false,
            icon = Icons.Default.Favorite,
            color = Color(0xFF8B5CF6)
        )

        TimelineTaskItem(
            time = "08:00 PM",
            title = "Evening Dose & Rest",
            subtitle = "Take evening dose and log night status in AI chat",
            isCompleted = false,
            icon = Icons.Default.Medication,
            color = PrimaryBlue
        )

        MedicineFormularySection()
    }
}

@Composable
fun TimelineTaskItem(
    time: String,
    title: String,
    subtitle: String,
    isCompleted: Boolean,
    icon: ImageVector,
    color: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) CardLightGreen else Color.White
        )
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (isCompleted) BadgeGreen else color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isCompleted) Icons.Default.CheckCircle else icon,
                    contentDescription = title,
                    tint = if (isCompleted) Color.White else color,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMain
                    )
                    Text(
                        text = time,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextMuted
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = TextMuted,
                    lineHeight = 15.sp
                )
            }
        }
    }
}

@Composable
fun MedicineFormularySection() {
    var searchQuery by remember { mutableStateOf("") }
    var selectedIndicationFilter by remember { mutableStateOf("All") }

    val filteredList = remember(searchQuery, selectedIndicationFilter) {
        val matches = MedicineRepository.search(searchQuery)
        if (selectedIndicationFilter == "All") matches
        else matches.filter { it.indication.contains(selectedIndicationFilter, ignoreCase = true) || it.category.contains(selectedIndicationFilter, ignoreCase = true) }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(CardLightBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Medication,
                            contentDescription = "Formulary",
                            tint = PrimaryBlue,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = "Medicine Dataset Directory",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMain
                        )
                        Text(
                            text = "Search post-op medicines & indications",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                }

                PillTag(
                    text = "${MedicineRepository.dataset.size} Meds",
                    backgroundColor = BadgeGreenBg,
                    textColor = BadgeGreen
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search medicine, strength, category...", fontSize = 12.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = TextMuted
                    )
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = Color.LightGray
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Indication Filter Pills
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("All", "Wound", "Fever", "Pain", "Infection").forEach { filter ->
                    FilterTabPill(
                        label = filter,
                        isSelected = selectedIndicationFilter == filter,
                        onClick = { selectedIndicationFilter = filter }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            filteredList.take(6).forEach { med ->
                MedicineDatasetRowItem(med = med)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun MedicineDatasetRowItem(med: MedicineItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardLightBlue)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = med.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMain
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${med.strength} • ${med.dosageForm}",
                        fontSize = 11.sp,
                        color = PrimaryBlue,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "Category: ${med.category} | Indication: ${med.indication}",
                    fontSize = 11.sp,
                    color = TextMuted
                )
                Text(
                    text = "Mfr: ${med.manufacturer}",
                    fontSize = 10.sp,
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            PillTag(
                text = med.classification,
                backgroundColor = if (med.classification == "Prescription") BadgeRedBg else BadgeGreenBg,
                textColor = if (med.classification == "Prescription") BadgeRed else BadgeGreen
            )
        }
    }
}

// ==========================================
// SCREEN 4: AI CHATBOT SCREEN (ACTIVE REAL-TIME STREAMING AI)
// ==========================================

@Composable
fun AIChatbotScreen(
    onStartVoiceCheckin: () -> Unit,
    onHighPriorityAlertDetected: (String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var showApiKeyDialog by remember { mutableStateOf(false) }
    var apiKeyInput by remember { mutableStateOf(RecoveryLoopAIEngine.userApiKey) }

    val messages = remember {
        mutableStateListOf(
            ChatMessage(
                id = "1",
                sender = ChatSender.AI_BOT,
                text = "Good morning, Ramesh! 👋\nI am your RecoveryLoop AI Care Assistant.\n\nHow are you feeling on Day 12 post-surgery?",
                time = "9:00 AM",
                quickReplies = listOf("😊 Feeling better", "😐 Feeling the same", "🤒 Feeling worse", "💊 Log Morning Meds")
            )
        )
    }

    var inputText by remember { mutableStateOf("") }
    var isAIGenerating by remember { mutableStateOf(false) }

    // Auto-scroll as streaming content arrives
    LaunchedEffect(messages.lastOrNull()?.text) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }

    fun sendUserMessage(userText: String) {
        if (userText.isBlank() || isAIGenerating) return

        messages.add(
            ChatMessage(
                id = System.currentTimeMillis().toString(),
                sender = ChatSender.USER,
                text = userText,
                time = "Just now"
            )
        )

        val aiMsgId = (System.currentTimeMillis() + 1).toString()
        messages.add(
            ChatMessage(
                id = aiMsgId,
                sender = ChatSender.AI_BOT,
                text = "⚡ ",
                time = "Just now",
                isLoading = true
            )
        )

        isAIGenerating = true

        coroutineScope.launch {
            val fullResponse = RecoveryLoopAIEngine.generateStreamResponse(
                userMessage = userText,
                historyMessages = messages.filter { !it.isLoading }
            ) { chunk ->
                val idx = messages.indexOfFirst { it.id == aiMsgId }
                if (idx != -1) {
                    val prev = messages[idx]
                    val updatedText = if (prev.text == "⚡ ") chunk else prev.text + chunk
                    messages[idx] = prev.copy(
                        text = updatedText,
                        isLoading = false
                    )
                }
            }

            val contextualReplies = RecoveryLoopAIEngine.computeContextualQuickReplies(fullResponse)
            val idx = messages.indexOfFirst { it.id == aiMsgId }
            if (idx != -1) {
                messages[idx] = messages[idx].copy(
                    text = fullResponse,
                    isLoading = false,
                    quickReplies = contextualReplies
                )
            }

            isAIGenerating = false

            if (fullResponse.contains("[ALERT: HIGH RISK]") || fullResponse.contains("alerting your care team")) {
                onHighPriorityAlertDetected(userText)
            }
        }
    }

    if (showApiKeyDialog) {
        AlertDialog(
            onDismissRequest = { showApiKeyDialog = false },
            title = { Text("Gemini AI Configuration") },
            text = {
                Column {
                    Text(
                        "Enter your Google Gemini API Key for active single-prompt streaming. (Leave blank to use active offline recovery engine)",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = apiKeyInput,
                        onValueChange = { apiKeyInput = it },
                        placeholder = { Text("AIzaSy...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    RecoveryLoopAIEngine.userApiKey = apiKeyInput.trim()
                    showApiKeyDialog = false
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showApiKeyDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE5DDD5))
    ) {
        // Chat Header
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
                        text = "RecoveryLoop AI Care",
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
                    text = if (isAIGenerating) "⚡ AI Care Assistant streaming..." else "Active AI Care Manager • Online",
                    fontSize = 11.sp,
                    color = if (isAIGenerating) BadgeOrange else BadgeGreen,
                    fontWeight = FontWeight.Medium
                )
            }

            IconButton(onClick = { showApiKeyDialog = true }) {
                Icon(
                    imageVector = Icons.Default.Key,
                    contentDescription = "API Key",
                    tint = if (RecoveryLoopAIEngine.userApiKey.isNotBlank()) BadgeGreen else TextMuted
                )
            }

            IconButton(onClick = onStartVoiceCheckin) {
                Icon(
                    imageVector = Icons.Default.PhoneInTalk,
                    contentDescription = "Voice Call",
                    tint = PrimaryBlue
                )
            }
        }

        // Messages List
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(12.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.Bottom
        ) {
            messages.forEach { msg ->
                ChatBubbleItem(
                    message = msg,
                    onQuickReplyClicked = { replyText ->
                        sendUserMessage(replyText)
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        // Chat Input Bar
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
                placeholder = { Text("Ask AI Care Assistant...", fontSize = 13.sp) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = Color.LightGray
                )
            )

            Spacer(modifier = Modifier.width(6.dp))

            IconButton(
                onClick = {
                    val textToSend = inputText
                    inputText = ""
                    sendUserMessage(textToSend)
                },
                enabled = !isAIGenerating && inputText.isNotBlank()
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(if (inputText.isNotBlank() && !isAIGenerating) PrimaryBlue else Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
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
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Mic",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
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
                if (message.isLoading) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(14.dp),
                            strokeWidth = 2.dp,
                            color = PrimaryBlue
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = message.text,
                            fontSize = 12.sp,
                            color = PrimaryBlue,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else if (message.isAudio) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play",
                            tint = PrimaryBlue
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Voice Message (${message.audioDuration})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
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

        // Active Quick Reply Action Pills
        if (!isUser && !message.quickReplies.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                message.quickReplies.forEach { option ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                when {
                                    option.contains("better") || option.contains("Done") || option.contains("Meds") -> CardLightGreen
                                    option.contains("same") || option.contains("Walk") || option.contains("Schedule") -> CardLightBlue
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
                                option.contains("better") || option.contains("Done") || option.contains("Meds") -> BadgeGreen
                                option.contains("same") || option.contains("Walk") || option.contains("Schedule") -> PrimaryBlue
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
// ACTIVE SOUND WAVE VISUALIZER
// ==========================================

@Composable
fun ActiveSoundWaveVisualizer(
    isSpeaking: Boolean,
    isListening: Boolean
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave")
    val h1 by infiniteTransition.animateFloat(
        initialValue = 0.2f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(350, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "h1"
    )
    val h2 by infiniteTransition.animateFloat(
        initialValue = 0.8f, targetValue = 0.2f,
        animationSpec = infiniteRepeatable(tween(450, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "h2"
    )
    val h3 by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 0.9f,
        animationSpec = infiniteRepeatable(tween(400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "h3"
    )

    val active = isSpeaking || isListening
    val waveColor = if (isSpeaking) PrimaryBlue else BadgeOrange

    Row(
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.height(28.dp)
    ) {
        Box(
            modifier = Modifier
                .width(5.dp)
                .height(if (active) (22.dp * h1) else 6.dp)
                .clip(CircleShape)
                .background(if (active) waveColor else Color.Gray)
        )
        Box(
            modifier = Modifier
                .width(5.dp)
                .height(if (active) (28.dp * h2) else 12.dp)
                .clip(CircleShape)
                .background(if (active) waveColor else Color.Gray)
        )
        Box(
            modifier = Modifier
                .width(5.dp)
                .height(if (active) (20.dp * h3) else 6.dp)
                .clip(CircleShape)
                .background(if (active) waveColor else Color.Gray)
        )
    }
}

// ==========================================
// SCREEN 5: AI VOICE CHECK-IN SCREEN (ACTIVE SPEECH & TTS)
// ==========================================

@Composable
fun AIVoiceCheckinScreen(
    onClose: () -> Unit,
    onHighPriorityAlertDetected: (String) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var selectedLanguage by remember { mutableStateOf("English") }
    var showLanguageDialog by remember { mutableStateOf(false) }

    var isListening by remember { mutableStateOf(false) }
    var isTTSSpeaking by remember { mutableStateOf(false) }
    var autoListenMode by remember { mutableStateOf(true) }

    var spokenUserText by remember { mutableStateOf("") }
    var aiVoiceResponseText by remember { mutableStateOf("Hello Ramesh, I'm your RecoveryLoop care manager.\nHow are you feeling today?") }

    // Android TextToSpeech Engine with UtteranceProgressListener for active dialog loop
    var ttsEngine by remember { mutableStateOf<TextToSpeech?>(null) }

    var speechRecognizer by remember { mutableStateOf<SpeechRecognizer?>(null) }

    fun startListening() {
        if (isTTSSpeaking) {
            ttsEngine?.stop()
            isTTSSpeaking = false
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE,
                when (selectedLanguage) {
                    "Hindi" -> "hi-IN"
                    "Spanish" -> "es-ES"
                    else -> "en-US"
                }
            )
        }

        isListening = true
        spokenUserText = "Listening actively for your voice..."
        speechRecognizer?.startListening(intent)
    }

    DisposableEffect(context) {
        val tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // Setup active utterance listener
            }
        }

        tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                isTTSSpeaking = true
            }

            override fun onDone(utteranceId: String?) {
                isTTSSpeaking = false
                if (autoListenMode) {
                    coroutineScope.launch {
                        delay(600)
                        startListening()
                    }
                }
            }

            override fun onError(utteranceId: String?) {
                isTTSSpeaking = false
            }
        })

        ttsEngine = tts

        onDispose {
            tts.stop()
            tts.shutdown()
        }
    }

    fun speakText(text: String) {
        ttsEngine?.let { tts ->
            tts.language = when (selectedLanguage) {
                "Hindi" -> Locale("hi", "IN")
                "Spanish" -> Locale("es", "ES")
                else -> Locale.US
            }
            isTTSSpeaking = true
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "AIVoiceRef")
        }
    }

    fun processVoiceInput(recognizedText: String) {
        spokenUserText = recognizedText
        isListening = false

        coroutineScope.launch {
            val aiResponse = RecoveryLoopAIEngine.generateStreamResponse(
                userMessage = recognizedText,
                selectedLanguage = selectedLanguage
            ) {}

            aiVoiceResponseText = aiResponse
            speakText(aiResponse)

            if (aiResponse.contains("[ALERT: HIGH RISK]") || aiResponse.contains("alerting your care team")) {
                onHighPriorityAlertDetected(recognizedText)
            }
        }
    }

    DisposableEffect(context) {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            val recognizer = SpeechRecognizer.createSpeechRecognizer(context)
            recognizer.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {}
                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() {
                    isListening = false
                }

                override fun onError(error: Int) {
                    isListening = false
                }

                override fun onResults(results: Bundle?) {
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (!matches.isNullOrEmpty()) {
                        processVoiceInput(matches[0])
                    }
                }

                override fun onPartialResults(partialResults: Bundle?) {}
                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
            speechRecognizer = recognizer
        }

        onDispose {
            speechRecognizer?.destroy()
        }
    }

    // Pulse Animation for Mic
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val micPulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isListening) 1.25f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text("Select Voice Language") },
            text = {
                Column {
                    listOf("English", "Hindi", "Spanish", "Marathi", "Tamil", "Telugu").forEach { lang ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedLanguage = lang
                                    showLanguageDialog = false
                                }
                                .padding(vertical = 8.dp)
                        ) {
                            RadioButton(
                                selected = selectedLanguage == lang,
                                onClick = {
                                    selectedLanguage = lang
                                    showLanguageDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = lang, fontSize = 14.sp)
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showLanguageDialog = false }) { Text("Close") }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkVoiceBg)
            .padding(24.dp)
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            RecoveryLoopLogoGraphic(modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "RecoveryLoop Active Voice Check-in",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Care Manager Avatar & Active Response Box
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(PrimaryBlue),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "AI Care Manager",
                    tint = Color.White,
                    modifier = Modifier.size(65.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            ActiveSoundWaveVisualizer(
                isSpeaking = isTTSSpeaking,
                isListening = isListening
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = aiVoiceResponseText,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                lineHeight = 21.sp,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (spokenUserText.isNotBlank()) {
                Text(
                    text = "You said: \"$spokenUserText\"",
                    fontSize = 12.sp,
                    color = BadgeGreen,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Voice Microphone Listening Button
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .scale(micPulseScale)
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(if (isListening) BadgeOrange else BadgeGreen)
                    .clickable {
                        if (isListening) {
                            speechRecognizer?.stopListening()
                            isListening = false
                        } else {
                            startListening()
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Listening",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isListening) "Listening actively..." else "Tap Mic to Speak ($selectedLanguage)",
                fontSize = 12.sp,
                color = if (isListening) BadgeOrange else BadgeGreen,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons
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
                        Icon(
                            imageVector = Icons.Default.CallEnd,
                            contentDescription = "End Call",
                            tint = Color.White
                        )
                    }
                }

                TextButton(onClick = {
                    if (isTTSSpeaking) {
                        ttsEngine?.stop()
                        isTTSSpeaking = false
                    } else {
                        speakText(aiVoiceResponseText)
                    }
                }) {
                    Icon(
                        imageVector = if (isTTSSpeaking) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                        contentDescription = "TTS",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isTTSSpeaking) "Mute AI" else "Read Aloud",
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }

                TextButton(onClick = { showLanguageDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = "Language",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = selectedLanguage, color = Color.White, fontSize = 12.sp)
                }
            }
        }
    }
}

// ==========================================
// SCREEN 6: ALERTS & HEALTH SCREEN
// ==========================================

@Composable
fun AlertsAndHealthScreen(
    alertsList: List<HealthAlertItem>
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

        val topHighPriority = alertsList.firstOrNull { it.isHighPriority }

        if (topHighPriority != null) {
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
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = "Alert",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                Text(
                                    text = topHighPriority.title,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BadgeRed
                                )
                                Text(
                                    text = "Needs Immediate Attention",
                                    fontSize = 12.sp,
                                    color = TextMain,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Text(text = topHighPriority.time, fontSize = 11.sp, color = TextMuted)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Patient: Ramesh Kumar",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMain
                    )
                    Text(text = "ID: RMH-4587", fontSize = 11.sp, color = TextMuted)

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = topHighPriority.detailText,
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
                        Text(
                            text = "High Priority Escalation",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
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
                        Text(
                            text = "Escalate to Dr. Richter >",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        Text(
            text = "Recent Alerts Log (${alertsList.size})",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextMain
        )

        Spacer(modifier = Modifier.height(12.dp))

        alertsList.forEach { alert ->
            AlertLogRow(
                title = alert.title,
                time = "${alert.time} • ${alert.detailText}",
                icon = alert.icon,
                iconColor = alert.color
            )
        }

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
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconColor,
                    modifier = Modifier.size(18.dp)
                )
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
    isPatientAtRisk: Boolean,
    onSwitchToPatient: () -> Unit,
    onSwitchRole: () -> Unit
) {
    var selectedFilter by remember { mutableIntStateOf(0) } // 0: All, 1: At Risk, 2: Stable
    var searchQuery by remember { mutableStateOf("") }

    val patients = remember(isPatientAtRisk) {
        listOf(
            PatientRecord(
                "1",
                "Ramesh Kumar",
                "RMH-4587",
                12,
                if (isPatientAtRisk) "At Risk" else "Stable",
                if (isPatientAtRisk) "Reported high risk symptoms to AI Care Manager" else "All vitals normal",
                PrimaryBlue
            ),
            PatientRecord(
                "2",
                "Savitri Devi",
                "SD-2210",
                8,
                "Stable",
                "All vitals normal",
                Color(0xFFE11D48)
            ),
            PatientRecord(
                "3",
                "Mohit Sharma",
                "MS-3321",
                5,
                "Stable",
                "Good progress",
                Color(0xFFD97706)
            ),
            PatientRecord(
                "4",
                "Ayesha Khan",
                "AK-7789",
                3,
                "Stable",
                "No issues reported",
                Color(0xFF059669)
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBgLight)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Staff Header
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

            IconButton(onClick = onSwitchRole) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Role",
                    tint = TextMain
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Filter Pills
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterTabPill(
                label = "All Patients (24)",
                isSelected = selectedFilter == 0,
                onClick = { selectedFilter = 0 })
            FilterTabPill(
                label = "At Risk (${if (isPatientAtRisk) 5 else 4})",
                isSelected = selectedFilter == 1,
                onClick = { selectedFilter = 1 })
            FilterTabPill(
                label = "Stable (${if (isPatientAtRisk) 19 else 20})",
                isSelected = selectedFilter == 2,
                onClick = { selectedFilter = 2 })
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search patient by name or ID...", fontSize = 12.sp) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = TextMuted
                )
            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = "Filter",
                    tint = TextMuted
                )
            },
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

        // Patients List
        patients.forEach { patient ->
            PatientMonitoringCard(
                patient = patient,
                onClick = { if (patient.name == "Ramesh Kumar") onSwitchToPatient() }
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
    patient: PatientRecord,
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
                    .background(patient.avatarBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Avatar",
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = patient.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMain
                    )

                    PillTag(
                        text = patient.status,
                        backgroundColor = if (patient.status == "At Risk") BadgeRedBg else BadgeGreenBg,
                        textColor = if (patient.status == "At Risk") BadgeRed else BadgeGreen
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(text = patient.patientCode, fontSize = 11.sp, color = TextMuted)
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = patient.subtitle,
                    fontSize = 11.sp,
                    color = if (patient.status == "At Risk") BadgeRed else TextMuted,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// ==========================================
// REUSABLE GRAPHICS & PILLS
// ==========================================

@Composable
fun RecoveryLoopLogoGraphic(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val strokeWidth = (minOf(width, height) * 0.14f).coerceAtLeast(6f)

        val centerY = height / 2f
        val leftCenterX = width * 0.32f
        val rightCenterX = width * 0.68f
        val loopRadius = minOf(width, height) * 0.28f

        // Left Green Loop
        drawArc(
            brush = androidx.compose.ui.graphics.Brush.linearGradient(
                colors = listOf(BadgeGreen, Color(0xFF34D399))
            ),
            startAngle = 45f,
            sweepAngle = 270f,
            useCenter = false,
            topLeft = Offset(leftCenterX - loopRadius, centerY - loopRadius),
            size = Size(loopRadius * 2f, loopRadius * 2f),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        // Right Blue Loop
        drawArc(
            brush = androidx.compose.ui.graphics.Brush.linearGradient(
                colors = listOf(TealAccent, PrimaryBlue)
            ),
            startAngle = -135f,
            sweepAngle = 270f,
            useCenter = false,
            topLeft = Offset(rightCenterX - loopRadius, centerY - loopRadius),
            size = Size(loopRadius * 2f, loopRadius * 2f),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        // House Roof Symbol inside Right Loop
        val housePath = androidx.compose.ui.graphics.Path().apply {
            val houseCenterX = rightCenterX
            val houseCenterY = centerY
            val hSize = loopRadius * 0.6f

            moveTo(houseCenterX, houseCenterY - hSize * 0.6f)
            lineTo(houseCenterX + hSize * 0.6f, houseCenterY)
            lineTo(houseCenterX + hSize * 0.45f, houseCenterY)
            lineTo(houseCenterX + hSize * 0.45f, houseCenterY + hSize * 0.5f)
            lineTo(houseCenterX - hSize * 0.45f, houseCenterY + hSize * 0.5f)
            lineTo(houseCenterX - hSize * 0.45f, houseCenterY)
            lineTo(houseCenterX - hSize * 0.6f, houseCenterY)
            close()
        }
        drawPath(path = housePath, color = PrimaryBlue)

        // Medical Cross (+) inside House
        val crossWidth = loopRadius * 0.12f
        val crossLength = loopRadius * 0.35f
        drawRect(
            color = Color.White,
            topLeft = Offset(rightCenterX - crossWidth / 2f, centerY - crossLength / 2f + loopRadius * 0.05f),
            size = Size(crossWidth, crossLength)
        )
        drawRect(
            color = Color.White,
            topLeft = Offset(rightCenterX - crossLength / 2f, centerY - crossWidth / 2f + loopRadius * 0.05f),
            size = Size(crossLength, crossWidth)
        )
    }
}

@Composable
fun RecoveryLoopFullLogo(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        RecoveryLoopLogoGraphic(modifier = Modifier.size(120.dp, 65.dp))

        Spacer(modifier = Modifier.height(12.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Recovery",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryBlue
            )
            Text(
                text = "Loop",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = BadgeGreen
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "― YOUR JOURNEY TO A HEALTHIER TOMORROW ―",
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryBlue,
            letterSpacing = 1.sp
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
