package com.ksheera.sagaraaa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.ksheera.sagaraaa.ui.*
import com.ksheera.sagaraaa.viewmodel.AppTheme
import com.ksheera.sagaraaa.viewmodel.AuthViewModel
import com.ksheera.sagaraaa.viewmodel.FarmViewModel
import com.ksheera.sagaraaa.viewmodel.ThemeViewModel

data class NavItem(val label: String, val icon: ImageVector)

class MainActivity : ComponentActivity() {
    private val farmViewModel: FarmViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NotificationHelper.scheduleDailyReminder(this)
        setContent {
            KsheeraSagaraRoot(farmViewModel, authViewModel, themeViewModel)
        }
    }
}

@Composable
fun KsheeraSagaraRoot(
    farmViewModel: FarmViewModel,
    authViewModel: AuthViewModel,
    themeViewModel: ThemeViewModel
) {
    val isDark = themeViewModel.isDarkMode
    val theme = themeViewModel.currentTheme

    val colorScheme = when {
        isDark -> darkColorScheme(
            primary = Color(0xFF4CAF50),
            secondary = Color(0xFF2E7D32),
            background = Color(0xFF121212),
            surface = Color(0xFF1E1E1E)
        )
        theme == AppTheme.BLUE -> lightColorScheme(
            primary = Color(0xFF1565C0),
            secondary = Color(0xFF0D47A1)
        )
        theme == AppTheme.ORANGE -> lightColorScheme(
            primary = Color(0xFFE65100),
            secondary = Color(0xFFBF360C)
        )
        else -> lightColorScheme(
            primary = Color(0xFF2E7D32),
            secondary = Color(0xFF1B5E20)
        )
    }

    MaterialTheme(colorScheme = colorScheme) {
        KsheeraSagaraApp(farmViewModel, authViewModel, themeViewModel)
    }
}

@Composable
fun KsheeraSagaraApp(
    farmViewModel: FarmViewModel,
    authViewModel: AuthViewModel,
    themeViewModel: ThemeViewModel
) {
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
    val username by authViewModel.username.collectAsState()
    val onboardingDone = themeViewModel.onboardingDone
    var showSplash by remember { mutableStateOf(true) }
    var currentAuthScreen by remember { mutableStateOf("login") }

    when {
        showSplash -> SplashScreen(onSplashDone = { showSplash = false })
        !onboardingDone -> OnboardingScreen(onFinished = { themeViewModel.setOnboardingDone() })
        !isLoggedIn -> when (currentAuthScreen) {
            "login" -> LoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = {},
                onNavigateToRegister = { currentAuthScreen = "register" }
            )
            "register" -> RegisterScreen(
                authViewModel = authViewModel,
                onRegisterSuccess = {},
                onNavigateToLogin = { currentAuthScreen = "login" }
            )
        }
        else -> MainAppScreen(farmViewModel, authViewModel, themeViewModel, username)
    }
}

@Composable
fun MainAppScreen(
    farmViewModel: FarmViewModel,
    authViewModel: AuthViewModel,
    themeViewModel: ThemeViewModel,
    username: String
) {
    var selectedIndex by remember { mutableStateOf(0) }
    var showProfileDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }

    // This forces recomposition when language changes
    val lang = AppStrings.currentLanguage
    val isDark = themeViewModel.isDarkMode

    val navItems = listOf(
        NavItem(AppStrings.get("dashboard"), Icons.Default.Home),
        NavItem(AppStrings.get("income"), Icons.Default.Add),
        NavItem(AppStrings.get("expense"), Icons.Default.ShoppingCart),
        NavItem(AppStrings.get("cows"), Icons.Default.Pets),
        NavItem(AppStrings.get("summary"), Icons.Default.BarChart)
    )

    if (showProfileDialog) {
        ProfileDialog(
            username = username,
            isDarkMode = isDark,
            onLogout = { authViewModel.logout(); showProfileDialog = false },
            onLanguage = { showProfileDialog = false; showLanguageDialog = true },
            onTheme = { showProfileDialog = false; showThemeDialog = true },
            onToggleDark = { themeViewModel.toggleDarkMode() },
            onDismiss = { showProfileDialog = false }
        )
    }
    if (showLanguageDialog) {
        LanguageDialog(onDismiss = { showLanguageDialog = false })
    }
    if (showThemeDialog) {
        ThemeDialog(
            currentTheme = themeViewModel.currentTheme,
            onThemeSelected = { themeViewModel.setTheme(it) },
            onDismiss = { showThemeDialog = false }
        )
    }

    Scaffold(
        topBar = {
            // ✅ Welcome username ONLY in top bar — not in individual screens
            AppTopBar(
                username = username,
                onProfileClick = { showProfileDialog = true }
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color(0xFF1B5E20)) {
                navItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = { selectedIndex = index },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label, fontSize = 9.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = Color.White,
                            unselectedIconColor = Color(0xFFA5D6A7),
                            unselectedTextColor = Color(0xFFA5D6A7),
                            indicatorColor = Color(0xFF2E7D32)
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (selectedIndex) {
                0 -> DashboardScreen(viewModel = farmViewModel, username = username)
                1 -> IncomeScreen(viewModel = farmViewModel)
                2 -> ExpenseScreen(viewModel = farmViewModel)
                3 -> CowsTabScreen(viewModel = farmViewModel)
                4 -> ReportTabScreen(viewModel = farmViewModel, username = username)
            }
        }
    }
}

@Composable
fun CowsTabScreen(viewModel: FarmViewModel) {
    val lang = AppStrings.currentLanguage
    var selectedTab by remember { mutableStateOf(0) }
    Column {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color(0xFF2E7D32),
            contentColor = Color.White
        ) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 },
                text = { Text(AppStrings.get("cow_mgmt"), fontSize = 11.sp) })
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 },
                text = { Text(AppStrings.get("cow_analysis"), fontSize = 11.sp) })
            Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 },
                text = { Text("💉 ${AppStrings.get("health")}", fontSize = 11.sp) })
        }
        when (selectedTab) {
            0 -> CowScreen(viewModel = viewModel)
            1 -> CowAnalysisScreen(viewModel = viewModel)
            2 -> VaccinationScreen(viewModel = viewModel)
        }
    }
}

@Composable
fun ReportTabScreen(viewModel: FarmViewModel, username: String) {
    val lang = AppStrings.currentLanguage
    var selectedTab by remember { mutableStateOf(0) }
    Column {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color(0xFF2E7D32),
            contentColor = Color.White
        ) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 },
                text = { Text(AppStrings.get("monthly_summary"), fontSize = 10.sp) })
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 },
                text = { Text("📅 ${AppStrings.get("calendar")}", fontSize = 10.sp) })
            Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 },
                text = { Text("📚 ${AppStrings.get("learn")}", fontSize = 10.sp) })
        }
        when (selectedTab) {
            0 -> SummaryScreen(viewModel = viewModel, username = username)
            1 -> MilkCalendarScreen(viewModel = viewModel)
            2 -> FinancialLiteracyScreen(viewModel = viewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(username: String, onProfileClick: () -> Unit) {
    val lang = AppStrings.currentLanguage
    TopAppBar(
        title = {
            Text(
                AppStrings.get("app_name"),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            // ✅ NO welcome here — only app name in TopBar
        },
        actions = {
            IconButton(onClick = onProfileClick) {
                Icon(
                    Icons.Default.AccountCircle,
                    contentDescription = "Profile",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1B5E20))
    )
}
@Composable
fun ProfileDialog(
    username: String,
    isDarkMode: Boolean,
    onLogout: () -> Unit,
    onLanguage: () -> Unit,
    onTheme: () -> Unit,
    onToggleDark: () -> Unit,
    onDismiss: () -> Unit
) {
    val lang = AppStrings.currentLanguage
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AccountCircle, null,
                    tint = Color(0xFF1B5E20), modifier = Modifier.size(32.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(AppStrings.get("profile"), fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column {
                Text("👤 $username", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Text("🐄 KsheeraSagara ${AppStrings.get("farmer")}",
                    fontSize = 13.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(if (isDarkMode) "🌙 Dark Mode" else "☀️ Light Mode", fontSize = 14.sp)
                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = { onToggleDark() },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF1B5E20))
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(onClick = onLanguage, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.Language, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("🌐 ${AppStrings.get("language")}")
                }
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedButton(onClick = onTheme, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.Palette, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("🎨 Color Theme")
                }
            }
        },
        confirmButton = {
            Button(onClick = onLogout,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828))) {
                Icon(Icons.Default.Logout, null, tint = Color.White)
                Spacer(modifier = Modifier.width(4.dp))
                Text(AppStrings.get("logout"), color = Color.White)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Close") }
        }
    )
}

@Composable
fun LanguageDialog(onDismiss: () -> Unit) {
    val languages = listOf(
        "🇬🇧 English" to AppStrings.Language.ENGLISH,
        "🇮🇳 ಕನ್ನಡ (Kannada)" to AppStrings.Language.KANNADA,
        "🇮🇳 हिंदी (Hindi)" to AppStrings.Language.HINDI,
        "🇮🇳 मराठी (Marathi)" to AppStrings.Language.MARATHI
    )
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    "🌐 ${AppStrings.get("change_language")}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF1B5E20)
                )
                Text(
                    AppStrings.get("app_update"),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(12.dp))
                languages.forEach { (name, lang) ->
                    val isSelected = AppStrings.currentLanguage == lang
                    Button(
                        onClick = {
                            AppStrings.currentLanguage = lang
                            onDismiss()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) Color(0xFF1B5E20)
                            else Color(0xFFE8F5E9)
                        )
                    ) {
                        Text(
                            name,
                            color = if (isSelected) Color.White else Color(0xFF1B5E20),
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(AppStrings.get("close"), color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun ThemeDialog(
    currentTheme: AppTheme,
    onThemeSelected: (AppTheme) -> Unit,
    onDismiss: () -> Unit
) {
    val themes = listOf(
        AppTheme.GREEN to Color(0xFF2E7D32),
        AppTheme.BLUE to Color(0xFF1565C0),
        AppTheme.ORANGE to Color(0xFFE65100)
    )
    Dialog(onDismissRequest = onDismiss) {
        Card(shape = MaterialTheme.shapes.large) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("🎨 Color Theme", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(12.dp))
                themes.forEach { (theme, color) ->
                    Button(
                        onClick = { onThemeSelected(theme); onDismiss() },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = color)
                    ) {
                        Text(theme.displayName, color = Color.White,
                            fontWeight = if (theme == currentTheme) FontWeight.Bold
                            else FontWeight.Normal)
                        if (theme == currentTheme) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("✓", color = Color.White)
                        }
                    }
                }
                TextButton(onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)) { Text("Close") }
            }
        }
    }
}