package org.example.appbbmges

import androidx.compose.runtime.*
import androidx.compose.foundation.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import appbbmges.composeapp.generated.resources.Res
import appbbmges.composeapp.generated.resources.logoSystem
import org.example.appbbmges.data.DatabaseDriverFactory
import org.example.appbbmges.data.Repository
import org.example.appbbmges.navigation.Screen
import org.example.appbbmges.navigation.SimpleNavController
import org.example.appbbmges.ui.avatars.UserAvatar
import org.example.appbbmges.ui.login.LoginScreen
import org.example.appbbmges.ui.dashboard.DashboardScreen
import org.example.appbbmges.ui.diciplinashorarios.DisciplinasHorariosScreen
import org.example.appbbmges.ui.eventospromociones.EventosPromocionesScreen.EventosPromocionesScreen
import org.example.appbbmges.ui.franquicias.FranquiciasScreen
import org.example.appbbmges.ui.productos.ProductosScreen
import org.example.appbbmges.ui.settings.SettingsScreen
import org.example.appbbmges.ui.usuarios.UsuariosScreen
import org.example.appbbmges.ui.sessions.SessionManager
import org.jetbrains.compose.resources.painterResource

@Composable
fun App(databaseDriverFactory: DatabaseDriverFactory) {
    val appData = remember { AppData(databaseDriverFactory) }
    val repository = appData.userRepository

    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            val navController = remember { SimpleNavController(Screen.Login) }
            val currentScreen by navController.currentScreen.collectAsState()

            when (currentScreen) {
                Screen.Login -> LoginScreen(navController = navController, userRepository = repository)
                else -> {
                    if (SessionManager.isSessionActive()) {
                        Row(modifier = Modifier.fillMaxSize()) {
                            Box(
                                modifier = Modifier
                                    .width(200.dp)
                                    .fillMaxHeight()
                                    .background(Color.White)
                                    .border(
                                        width = 1.dp,
                                        color = Color(0xFFE0E0E0),
                                        shape = RoundedCornerShape(0.dp)
                                    )
                            ) {
                                SidebarWithLogo(
                                    navController = navController,
                                    currentRoute = when (currentScreen) {
                                        is Screen.Dashboard -> "dashboard"
                                        is Screen.Franquicias -> "franquicias"
                                        is Screen.Usuarios -> "usuarios"
                                        is Screen.DisciplinasHorarios -> "disciplinas_horarios"
                                        is Screen.Productos -> "productos"
                                        is Screen.EventosPromociones -> "eventos_promociones"
                                        is Screen.Settings -> "settings"
                                        else -> "dashboard"
                                    }
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                CustomHeaderRight(
                                    navController = navController,
                                    userRepository = repository
                                )

                                HorizontalDivider(
                                    modifier = Modifier.fillMaxWidth(),
                                    thickness = 1.dp,
                                    color = Color(0xFFE0E0E0)
                                )

                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color(0xFFF8F8F8))
                                ) {
                                    when (currentScreen) {
                                        is Screen.Dashboard -> DashboardScreen(navController = navController, repository = repository)
                                        is Screen.Franquicias -> FranquiciasScreen(navController = navController, repository = repository)
                                        is Screen.Usuarios -> UsuariosScreen(navController = navController, repository = repository)
                                        is Screen.DisciplinasHorarios -> DisciplinasHorariosScreen(navController = navController, repository = repository)
                                        is Screen.Productos -> ProductosScreen(navController = navController)
                                        is Screen.EventosPromociones -> EventosPromocionesScreen(navController = navController)
                                        is Screen.Settings -> SettingsScreen(navController = navController, repository = repository)
                                        else -> {}
                                    }
                                }
                            }
                        }
                    } else {
                        LaunchedEffect(Unit) {
                            navController.navigateTo(Screen.Login)
                        }
                    }
                }
            }
        }
    }
}

class AppData(databaseDriverFactory: DatabaseDriverFactory) {
    private val database = AppDatabaseBaby(databaseDriverFactory.createDriver())
    val userRepository = Repository(database)

    init {
        userRepository.initializeData()
    }
}

fun clearUserSession() {
    SessionManager.clearSession()
}

data class UserHeaderInfo(
    val fullName: String,
    val initials: String,
    val rolePrefix: String,
    val gender: String? = null,
    val avatarId: String = "avatar_01"
)

@Composable
fun CustomHeaderRight(
    modifier: Modifier = Modifier,
    navController: SimpleNavController? = null,
    userRepository: Repository? = null
) {
    var showDropdownMenu by remember { mutableStateOf(false) }

    val userId = SessionManager.userId

    val userHeaderInfo = remember(userId) {
        if (userId != null && userRepository != null) {
            try {
                val user = userRepository.getUserById(userId) ?: return@remember UserHeaderInfo(
                    fullName = "Usuario",
                    initials = "U",
                    rolePrefix = "en",
                    avatarId = "avatar_01"
                )

                val studentId = user.student_id
                val teacherId = user.teacher_id
                val administrativeId = user.administrative_id
                val franchiseeId = user.franchisee_id

                when {
                    studentId != null -> {
                        val student = userRepository.getStudentById(studentId)
                        if (student != null) {
                            val fullName = "${student.first_name} ${student.last_name_paternal ?: ""} ${student.last_name_maternal ?: ""}".trim()
                            val initials = getInitials(student.first_name, student.last_name_paternal, student.last_name_maternal)
                            val rolePrefix = when (student.gender?.lowercase()) {
                                "masculino" -> "Alumno en"
                                "femenino" -> "Alumna en"
                                else -> "Alumno/a en"
                            }
                            UserHeaderInfo(
                                fullName = fullName,
                                initials = initials,
                                rolePrefix = rolePrefix,
                                gender = student.gender,
                                avatarId = student.avatar_id ?: "avatar_01"
                            )
                        } else {
                            UserHeaderInfo("Usuario", "U", "en", avatarId = "avatar_01")
                        }
                    }

                    teacherId != null -> {
                        val teacher = userRepository.getTeacherById(teacherId)
                        if (teacher != null) {
                            val fullName = "${teacher.first_name} ${teacher.last_name_paternal ?: ""} ${teacher.last_name_maternal ?: ""}".trim()
                            val initials = getInitials(teacher.first_name, teacher.last_name_paternal, teacher.last_name_maternal)
                            val rolePrefix = when (teacher.gender?.lowercase()) {
                                "masculino" -> "Profesor en"
                                "femenino" -> "Profesora en"
                                else -> "Profesor/a en"
                            }
                            UserHeaderInfo(
                                fullName = fullName,
                                initials = initials,
                                rolePrefix = rolePrefix,
                                gender = teacher.gender,
                                avatarId = teacher.avatar_id ?: "avatar_01"
                            )
                        } else {
                            UserHeaderInfo("Usuario", "U", "en", avatarId = "avatar_01")
                        }
                    }

                    franchiseeId != null -> {
                        val franchisee = userRepository.getFranchiseeById(franchiseeId)
                        if (franchisee != null) {
                            val fullName = "${franchisee.first_name} ${franchisee.last_name_paternal ?: ""} ${franchisee.last_name_maternal ?: ""}".trim()
                            val initials = getInitials(franchisee.first_name, franchisee.last_name_paternal, franchisee.last_name_maternal)
                            val rolePrefix = when (franchisee.gender?.lowercase()) {
                                "masculino" -> "Franquiciatario en"
                                "femenino" -> "Franquiciataria en"
                                else -> "Franquiciatario/a en"
                            }
                            UserHeaderInfo(
                                fullName = fullName,
                                initials = initials,
                                rolePrefix = rolePrefix,
                                gender = franchisee.gender,
                                avatarId = franchisee.avatar_id ?: "avatar_01"
                            )
                        } else {
                            UserHeaderInfo("Usuario", "U", "en", avatarId = "avatar_01")
                        }
                    }

                    administrativeId != null -> {
                        val admin = userRepository.getAdministrativeById(administrativeId)
                        if (admin != null) {
                            val fullName = "${admin.first_name} ${admin.last_name_paternal ?: ""} ${admin.last_name_maternal ?: ""}".trim()
                            val initials = getInitials(admin.first_name, admin.last_name_paternal, admin.last_name_maternal)
                            val rolePrefix = when (admin.gender?.lowercase()) {
                                "masculino" -> "Administrativo en"
                                "femenino" -> "Administrativa en"
                                else -> "Administrativo/a en"
                            }
                            UserHeaderInfo(
                                fullName = fullName,
                                initials = initials,
                                rolePrefix = rolePrefix,
                                gender = admin.gender,
                                avatarId = admin.avatar_id ?: "avatar_01"
                            )
                        } else {
                            UserHeaderInfo("Usuario", "U", "en", avatarId = "avatar_01")
                        }
                    }

                    else -> {
                        val isSuperAdmin = userRepository.isSuperAdmin(userId)
                        if (isSuperAdmin) {
                            UserHeaderInfo("Administrador Principal", "AP", "Super Admin en", avatarId = "avatar_01")
                        } else {
                            UserHeaderInfo("Usuario", "U", "en", avatarId = "avatar_01")
                        }
                    }
                }
            } catch (e: Exception) {
                println("✗ Error obteniendo información del usuario: ${e.message}")
                UserHeaderInfo("Usuario", "U", "en", avatarId = "avatar_01")
            }
        } else {
            UserHeaderInfo("Usuario", "U", "en", avatarId = "avatar_01")
        }
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp),
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${userHeaderInfo.rolePrefix} Baby Ballet Marbet®",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = Color(0xFF333333)
            )

            Spacer(modifier = Modifier.weight(1f))

            Box {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { showDropdownMenu = true }
                ) {
                    Text(
                        text = userHeaderInfo.fullName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF555555)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    UserAvatar(
                        avatarId = userHeaderInfo.avatarId,
                        size = 32
                    )
                }

                DropdownMenu(
                    expanded = showDropdownMenu,
                    onDismissRequest = { showDropdownMenu = false },
                    modifier = Modifier.width(200.dp)
                ) {
                    DropdownMenuItem(
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Settings,
                                    contentDescription = "Configuración",
                                    tint = Color(0xFF555555),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Configuración",
                                    color = Color(0xFF555555),
                                    fontSize = 14.sp
                                )
                            }
                        },
                        onClick = {
                            showDropdownMenu = false
                            navController?.navigateTo(Screen.Settings())
                        }
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        thickness = 1.dp,
                        color = Color(0xFFE0E0E0)
                    )

                    DropdownMenuItem(
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Outlined.Logout,
                                    contentDescription = "Cerrar Sesión",
                                    tint = Color(0xFFD32F2F),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Cerrar Sesión",
                                    color = Color(0xFFD32F2F),
                                    fontSize = 14.sp
                                )
                            }
                        },
                        onClick = {
                            showDropdownMenu = false
                            clearUserSession()
                            navController?.navigateTo(Screen.Login)
                        }
                    )
                }
            }
        }
    }
}

private fun getInitials(firstName: String?, lastNamePaternal: String?, lastNameMaternal: String?): String {
    val first = firstName?.firstOrNull()?.uppercase() ?: ""
    val paternal = lastNamePaternal?.firstOrNull()?.uppercase() ?: ""

    return when {
        first.isNotEmpty() && paternal.isNotEmpty() -> "$first$paternal"
        first.isNotEmpty() -> "$first"
        else -> "U"
    }
}

@Composable
fun SidebarWithLogo(
    navController: SimpleNavController,
    currentRoute: String
) {
    val permissionHelper = SessionManager.permissionHelper
    val franchiseId = SessionManager.franchiseId ?: 0L

    val accessibleModules = remember(permissionHelper) {
        permissionHelper?.getAccessibleModules() ?: emptyList()
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            color = Color.White
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(Res.drawable.logoSystem),
                    contentDescription = "Logo",
                    modifier = Modifier.size(150.dp)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 2.dp)
        ) {
            if (permissionHelper?.canAccessDashboard() == true) {
                SidebarMenuItem(
                    icon = Icons.Outlined.Dashboard,
                    title = "Dashboard",
                    isSelected = currentRoute == "dashboard",
                    onClick = { navController.navigateTo(Screen.Dashboard(franchiseId = franchiseId)) }
                )
            }

            if (permissionHelper?.canViewSection("Franquicias") == true) {
                SidebarMenuItem(
                    icon = Icons.Outlined.Business,
                    title = "Franquicias",
                    isSelected = currentRoute == "franquicias",
                    onClick = { navController.navigateTo(Screen.Franquicias()) }
                )
            }

            if (permissionHelper?.canViewSection("Usuarios") == true) {
                SidebarMenuItem(
                    icon = Icons.Outlined.People,
                    title = "Usuarios",
                    isSelected = currentRoute == "usuarios",
                    onClick = { navController.navigateTo(Screen.Usuarios()) }
                )
            }

            if (permissionHelper?.canViewSection("Disciplinas") == true ||
                permissionHelper?.canViewSection("Horarios") == true) {
                SidebarMenuItem(
                    icon = Icons.Outlined.DateRange,
                    title = "Disciplinas y Horarios",
                    isSelected = currentRoute == "disciplinas_horarios",
                    onClick = { navController.navigateTo(Screen.DisciplinasHorarios()) }
                )
            }

            if (permissionHelper?.canViewSection("Productos") == true) {
                SidebarMenuItem(
                    icon = Icons.Outlined.ShoppingBag,
                    title = "Productos",
                    isSelected = currentRoute == "productos",
                    onClick = { navController.navigateTo(Screen.Productos()) }
                )
            }

            if (permissionHelper?.canViewSection("Eventos") == true ||
                permissionHelper?.canViewSection("Promociones") == true) {
                SidebarMenuItem(
                    icon = Icons.Outlined.Event,
                    title = "Eventos y Promociones",
                    isSelected = currentRoute == "eventos_promociones",
                    onClick = { navController.navigateTo(Screen.EventosPromociones()) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SidebarMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    showArrow: Boolean = false
) {
    val backgroundColor = if (isSelected) Color(0xFFF2F2F2) else Color.Transparent
    val textColor = if (isSelected) Color(0xFF3667EA) else Color(0xFF555555)
    val borderColor = if (isSelected) Color(0xFF3667EA) else Color.Transparent

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .border(
                width = if (isSelected) 0.dp else 0.dp,
                color = borderColor,
                shape = RoundedCornerShape(8.dp)
            ),
        color = backgroundColor,
        shape = RoundedCornerShape(8.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = title,
                tint = textColor,
                modifier = Modifier.size(18.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = title,
                color = textColor,
                fontSize = 14.sp,
                modifier = Modifier.weight(1f)
            )

            if (showArrow) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Expandir",
                    tint = textColor,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}