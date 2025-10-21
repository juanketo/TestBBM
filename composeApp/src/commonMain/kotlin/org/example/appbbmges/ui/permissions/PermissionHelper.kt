package org.example.appbbmges.ui.permissions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.example.appbbmges.data.Repository

class PermissionHelper(
    private val userId: Long,
    private val franchiseId: Long,
    private val repository: Repository
) {
    private var permissions: Set<String> = emptySet()
    private val _isLoaded = MutableStateFlow(false)
    val isLoaded: StateFlow<Boolean> = _isLoaded.asStateFlow()

    private val _permissionsFlow = MutableStateFlow<Set<String>>(emptySet())
    val permissionsFlow: StateFlow<Set<String>> = _permissionsFlow.asStateFlow()

    fun loadPermissions() {
        try {
            permissions = repository.getUserPermissionsByFranchise(userId, franchiseId).toSet()
            _permissionsFlow.value = permissions
            _isLoaded.value = true
        } catch (e: Exception) {
            println("Error cargando permisos: ${e.message}")
            e.printStackTrace()
            permissions = emptySet()
            _permissionsFlow.value = emptySet()
            _isLoaded.value = false
        }
    }

    fun reloadPermissions() {
        _isLoaded.value = false
        loadPermissions()
    }

    fun can(permission: String): Boolean {
        if (_isLoaded.value.not()) {
            return false
        }
        return permissions.contains(permission)
    }

    fun canAny(vararg perms: String): Boolean {
        if (_isLoaded.value.not()) return false
        return perms.any { can(it) }
    }

    fun canAll(vararg perms: String): Boolean {
        if (_isLoaded.value.not()) return false
        return perms.all { can(it) }
    }

    fun canViewSection(module: String): Boolean {
        if (_isLoaded.value.not()) return false
        val perms = permissionsByModule[module] ?: return false
        return perms.any { permissions.contains(it) }
    }

    fun getAccessibleModules(): List<String> {
        if (_isLoaded.value.not()) return emptyList()
        return permissionsByModule.filter { (_, perms) ->
            perms.any { permissions.contains(it) }
        }.keys.toList()
    }

    fun getAllPermissions(): List<String> {
        return permissions.toList()
    }

    fun isAdmin(): Boolean {
        if (_isLoaded.value.not()) return false
        return can("CONFIG_AVANZADA_VER") || can("RESPALDO_DATOS")
    }

    fun getModuleCapabilities(module: String): ModuleCapabilities {
        if (_isLoaded.value.not()) {
            return ModuleCapabilities(canView = false, canCreate = false, canEdit = false, canDelete = false)
        }

        val modulePerms = permissionsByModule[module] ?: return ModuleCapabilities()

        return ModuleCapabilities(
            canView = modulePerms.any { it.contains("VER") && permissions.contains(it) },
            canCreate = modulePerms.any { it.contains("CREAR") && permissions.contains(it) },
            canEdit = modulePerms.any { it.contains("EDITAR") && permissions.contains(it) },
            canDelete = modulePerms.any { it.contains("ELIMINAR") && permissions.contains(it) }
        )
    }

    fun getPermissionsByModule(): Map<String, List<String>> {
        if (_isLoaded.value.not()) return emptyMap()

        return permissionsByModule.mapNotNull { (module, perms) ->
            val userPerms = perms.filter { permissions.contains(it) }
            if (userPerms.isNotEmpty()) module to userPerms else null
        }.toMap()
    }

    fun canAccessDashboard(): Boolean {
        return can("DASHBOARD_VER")
    }

    fun clearPermissions() {
        permissions = emptySet()
        _permissionsFlow.value = emptySet()
        _isLoaded.value = false
    }

    fun getPermissionsSummary(): String {
        return buildString {
            appendLine("=== Resumen de Permisos ===")
            appendLine("Usuario ID: $userId")
            appendLine("Franquicia ID: $franchiseId")
            appendLine("Permisos cargados: ${_isLoaded.value}")
            appendLine("Total de permisos: ${permissions.size}")
            appendLine("\nMódulos accesibles:")
            getAccessibleModules().forEach {
                appendLine("  - $it")
            }
            appendLine("\nPermisos detallados:")
            permissions.sorted().forEach {
                appendLine("  ✓ $it")
            }
        }
    }
}

data class ModuleCapabilities(
    val canView: Boolean = false,
    val canCreate: Boolean = false,
    val canEdit: Boolean = false,
    val canDelete: Boolean = false
) {
    fun hasAnyPermission(): Boolean = canView || canCreate || canEdit || canDelete
    fun hasFullAccess(): Boolean = canView && canCreate && canEdit && canDelete
}

val permissionsByModule = mapOf(
    "Usuarios" to listOf("USUARIOS_VER", "USUARIOS_CREAR", "USUARIOS_EDITAR", "USUARIOS_ELIMINAR"),
    "Roles" to listOf("ROLES_VER", "ROLES_CREAR", "ROLES_EDITAR", "ROLES_ELIMINAR"),
    "Franquicias" to listOf("FRANQUICIAS_CREAR", "FRANQUICIAS_EDITAR", "FRANQUICIAS_ELIMINAR", "FRANQUICIAS_VER"),
    "Salones" to listOf("SALONES_CREAR", "SALONES_EDITAR", "SALONES_ELIMINAR", "SALONES_VER"),
    "Niveles" to listOf("NIVELES_CREAR", "NIVELES_EDITAR", "NIVELES_ELIMINAR", "NIVELES_VER"),
    "Disciplinas" to listOf("DISCIPLINAS_CREAR", "DISCIPLINAS_EDITAR", "DISCIPLINAS_ELIMINAR", "DISCIPLINAS_VER"),
    "Alumnos" to listOf("ALUMNOS_CREAR", "ALUMNOS_EDITAR", "ALUMNOS_ELIMINAR", "ALUMNOS_VER"),
    "Profesores" to listOf("PROFESORES_CREAR", "PROFESORES_EDITAR", "PROFESORES_ELIMINAR", "PROFESORES_VER"),
    "Administrativos" to listOf("ADMINISTRATIVOS_CREAR", "ADMINISTRATIVOS_EDITAR", "ADMINISTRATIVOS_ELIMINAR", "ADMINISTRATIVOS_VER"),
    "Horarios" to listOf("HORARIOS_CREAR", "HORARIOS_EDITAR", "HORARIOS_ELIMINAR", "HORARIOS_VER"),
    "Asistencias" to listOf("ASISTENCIAS_REGISTRAR", "ASISTENCIAS_VER"),
    "Pagos" to listOf("PAGOS_VER", "PAGOS_REGISTRAR"),
    "Promociones" to listOf("PROMOCIONES_CREAR", "PROMOCIONES_EDITAR", "PROMOCIONES_ELIMINAR", "PROMOCIONES_VER"),
    "Cuotas" to listOf("CUOTAS_CREAR", "CUOTAS_EDITAR", "CUOTAS_ELIMINAR", "CUOTAS_VER"),
    "Productos" to listOf("PRODUCTOS_CREAR", "PRODUCTOS_EDITAR", "PRODUCTOS_ELIMINAR", "PRODUCTOS_VER"),
    "Membresías" to listOf("MEMBRESIAS_CREAR", "MEMBRESIAS_EDITAR", "MEMBRESIAS_ELIMINAR", "MEMBRESIAS_VER"),
    "Eventos" to listOf("EVENTOS_CREAR", "EVENTOS_EDITAR", "EVENTOS_ELIMINAR", "EVENTOS_VER"),
    "Reportes Maestros" to listOf("REPORTES_MAESTROS_VER", "REPORTES_MAESTROS_CREAR"),
    "Clase Muestra" to listOf("CLASE_MUESTRA_CREAR", "CLASE_MUESTRA_VER"),
    "Perfil" to listOf("PERFIL_VER", "PERFIL_EDITAR"),
    "Configuración" to listOf("CONFIG_AVANZADA_VER", "RESPALDO_DATOS"),
    "Dashboard" to listOf("DASHBOARD_VER")
)

fun getModuleColor(module: String): Color {
    return when (module) {
        "Usuarios" -> Color(0xFF1976D2)
        "Roles" -> Color(0xFF7B1FA2)
        "Franquicias" -> Color(0xFFD32F2F)
        "Salones" -> Color(0xFF388E3C)
        "Niveles" -> Color(0xFFFF6F00)
        "Disciplinas" -> Color(0xFF0288D1)
        "Alumnos" -> Color(0xFF00897B)
        "Profesores" -> Color(0xFF5E35B1)
        "Administrativos" -> Color(0xFFC62828)
        "Horarios" -> Color(0xFF2E7D32)
        "Asistencias" -> Color(0xFF00ACC1)
        "Pagos" -> Color(0xFF6A1B9A)
        "Promociones" -> Color(0xFFF57C00)
        "Cuotas" -> Color(0xFF0277BD)
        "Productos" -> Color(0xFF558B2F)
        "Membresías" -> Color(0xFF4527A0)
        "Eventos" -> Color(0xFFE64A19)
        "Reportes Maestros" -> Color(0xFF1565C0)
        "Clase Muestra" -> Color(0xFF00897B)
        "Perfil" -> Color(0xFF5D4037)
        "Configuración" -> Color(0xFFD84315)
        "Dashboard" -> Color(0xFF283593)
        else -> Color(0xFF1976D2)
    }
}

fun formatPermissionName(permission: String): String {
    return permission
        .replace("_", " ")
        .split(" ")
        .joinToString(" ") { word ->
            word.lowercase().replaceFirstChar { it.uppercase() }
        }
}

@Composable
fun rememberPermissionHelper(
    userId: Long,
    franchiseId: Long,
    repository: Repository
): PermissionHelper {
    val helper = remember(userId, franchiseId) {
        PermissionHelper(userId, franchiseId, repository)
    }

    LaunchedEffect(helper) {
        if (helper.isLoaded.value.not()) {
            helper.loadPermissions()
        }
    }

    return helper
}

@Composable
fun ProtectedContent(
    permissionHelper: PermissionHelper,
    requiredPermission: String,
    fallback: @Composable () -> Unit = {},
    content: @Composable () -> Unit
) {
    val isLoaded by permissionHelper.isLoaded.collectAsState()

    when {
        isLoaded.not() -> {}
        permissionHelper.can(requiredPermission) -> content()
        else -> fallback()
    }
}

@Composable
fun ProtectedModule(
    permissionHelper: PermissionHelper,
    module: String,
    fallback: @Composable () -> Unit = {},
    content: @Composable () -> Unit
) {
    val isLoaded by permissionHelper.isLoaded.collectAsState()

    when {
        isLoaded.not() -> {}
        permissionHelper.canViewSection(module) -> content()
        else -> fallback()
    }
}