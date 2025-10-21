package org.example.appbbmges.ui.settings.registationex

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.appbbmges.data.Repository
import org.example.appbbmges.RoleEntity
import org.example.appbbmges.ui.usuarios.AppColors
import org.jetbrains.compose.resources.painterResource
import appbbmges.composeapp.generated.resources.Res
import appbbmges.composeapp.generated.resources.logoSystem

// --- ENUMS Y ESTADOS ---

enum class RoleFormStep {
    INFO,
    PERMISSIONS,
    CONFIRMATION
}

sealed class RoleFormState {
    object Idle : RoleFormState()
    object Loading : RoleFormState()
    data class Error(val message: String) : RoleFormState()
    object Success : RoleFormState()
}

data class RoleValidationResult(
    val isValid: Boolean,
    val nameError: String? = null,
    val permissionError: String? = null
)

data class RoleWithPermissions(
    val role: RoleEntity,
    val permissions: List<String>
)

// --- VALIDATOR ---
class RoleValidator {
    companion object {
        fun validateName(
            name: String,
            existingRoles: List<RoleEntity>,
            excludeId: Long? = null
        ): RoleValidationResult {
            val nameError = when {
                name.isEmpty() -> "El nombre del rol es obligatorio"
                name.length < 3 -> "Debe tener al menos 3 caracteres"
                name.length > 50 -> "Demasiado largo (máximo 50 caracteres)"
                !name.matches(Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")) -> "Solo letras y espacios"
                existingRoles.any { it.name.equals(name, ignoreCase = true) && it.id != excludeId } -> "Ya existe un rol con ese nombre"
                else -> null
            }

            return RoleValidationResult(
                isValid = nameError == null,
                nameError = nameError
            )
        }

        fun validatePermissions(
            selectedPermissions: Set<String>
        ): RoleValidationResult {
            val permissionError = if (selectedPermissions.isEmpty())
                "Debes seleccionar al menos un permiso" else null

            return RoleValidationResult(
                isValid = permissionError == null,
                permissionError = permissionError
            )
        }

        fun validateComplete(
            name: String,
            existingRoles: List<RoleEntity>,
            selectedPermissions: Set<String>,
            excludeId: Long? = null
        ): RoleValidationResult {
            val nameValidation = validateName(name, existingRoles, excludeId)
            val permissionValidation = validatePermissions(selectedPermissions)

            return RoleValidationResult(
                isValid = nameValidation.isValid && permissionValidation.isValid,
                nameError = nameValidation.nameError,
                permissionError = permissionValidation.permissionError
            )
        }
    }
}

// --- PERMISOS AGRUPADOS POR MÓDULO (ACTUALIZADO PARA PRODUCCIÓN) ---
val permissionsByModule = mapOf(
    "Usuarios" to listOf("USUARIOS_VER", "USUARIOS_CREAR", "USUARIOS_EDITAR", "USUARIOS_ELIMINAR"),
    "Roles" to listOf("ROLES_CREAR", "ROLES_EDITAR", "ROLES_ELIMINAR"),
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

// --- FUNCIÓN PARA OBTENER COLOR POR MÓDULO ---
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

// --- FUNCIÓN AUXILIAR PARA FORMATEAR NOMBRES DE PERMISOS ---
fun formatPermissionName(permission: String): String {
    return permission
        .replace("_", " ")
        .split(" ")
        .joinToString(" ") { word ->
            word.lowercase().replaceFirstChar { it.uppercase() }
        }
}

// --- PANTALLA PRINCIPAL CON LISTA ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNewRoleScreen(
    onDismiss: () -> Unit,
    repository: Repository,
    modifier: Modifier = Modifier
) {
    var showForm by remember { mutableStateOf(false) }
    var editingRole by remember { mutableStateOf<RoleEntity?>(null) }
    var showDeleteDialog by remember { mutableStateOf<RoleEntity?>(null) }
    var rolesWithPermissions by remember { mutableStateOf<List<RoleWithPermissions>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        try {
            val roles = repository.getAllRoles()
            rolesWithPermissions = roles.map { role ->
                val permissions = repository.getRolePermissions(role.id)
                RoleWithPermissions(role, permissions)
            }
        } catch (e: Exception) {
            println("Error cargando roles: ${e.message}")
        } finally {
            isLoading = false
        }
    }

    fun reloadRoles() {
        isLoading = true
        try {
            val roles = repository.getAllRoles()
            rolesWithPermissions = roles.map { role ->
                val permissions = repository.getRolePermissions(role.id)
                RoleWithPermissions(role, permissions)
            }
        } catch (e: Exception) {
            println("Error recargando roles: ${e.message}")
        } finally {
            isLoading = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppColors.Background)
    ) {
        if (showForm || editingRole != null) {
            RoleFormDialog(
                onDismiss = {
                    showForm = false
                    editingRole = null
                    reloadRoles()
                },
                repository = repository,
                existingRoles = rolesWithPermissions.map { it.role },
                editingRole = editingRole
            )
        }

        showDeleteDialog?.let { role ->
            AlertDialog(
                onDismissRequest = { showDeleteDialog = null },
                title = { Text("Eliminar Rol") },
                text = { Text("¿Estás seguro de que deseas eliminar el rol '${role.name}'? Esto también eliminará todos los permisos asociados.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            try {
                                repository.deleteRolePermissions(role.id)
                                repository.deleteRole(role.id)
                                reloadRoles()
                                showDeleteDialog = null
                            } catch (e: Exception) {
                                println("Error al eliminar rol: ${e.message}")
                                showDeleteDialog = null
                            }
                        }
                    ) {
                        Text("Eliminar", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = null }) {
                        Text("Cancelar")
                    }
                }
            )
        }

        if (!showForm && editingRole == null && showDeleteDialog == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Roles y Permisos",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.TextColor
                        )
                        Text(
                            text = "${rolesWithPermissions.size} roles registrados",
                            fontSize = 14.sp,
                            color = AppColors.TextColor.copy(alpha = 0.7f)
                        )
                    }

                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFff8abe)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Volver",
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = AppColors.Primary)
                    }
                } else if (rolesWithPermissions.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(48.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.SupervisorAccount,
                                contentDescription = "Sin roles",
                                modifier = Modifier.size(64.dp),
                                tint = AppColors.Primary.copy(alpha = 0.6f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No hay roles registrados",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.TextColor,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Presiona el botón + para agregar el primer rol",
                                style = MaterialTheme.typography.bodyMedium,
                                color = AppColors.TextColor.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                } else {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(AppColors.Primary.copy(alpha = 0.1f))
                                        .padding(horizontal = 20.dp, vertical = 18.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Roles Activos",
                                        fontWeight = FontWeight.Bold,
                                        color = AppColors.Primary,
                                        fontSize = 14.sp,
                                        modifier = Modifier.weight(1.3f)
                                    )
                                    Text(
                                        text = "Descripción",
                                        fontWeight = FontWeight.Bold,
                                        color = AppColors.Primary,
                                        fontSize = 14.sp,
                                        modifier = Modifier
                                            .weight(1.5f)
                                            .padding(horizontal = 12.dp)
                                    )
                                    Text(
                                        text = "Permisos en el Sistema",
                                        fontWeight = FontWeight.Bold,
                                        color = AppColors.Primary,
                                        fontSize = 14.sp,
                                        modifier = Modifier
                                            .weight(3.2f)
                                            .padding(horizontal = 12.dp)
                                    )
                                    Text(
                                        text = "Acciones",
                                        fontWeight = FontWeight.Bold,
                                        color = AppColors.Primary,
                                        fontSize = 14.sp,
                                        modifier = Modifier.weight(0.8f),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }

                            items(rolesWithPermissions) { roleWithPerms ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier.weight(1.3f),
                                        contentAlignment = Alignment.CenterStart
                                    ) {
                                        Text(
                                            text = roleWithPerms.role.name,
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = AppColors.TextColor,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .weight(1.5f)
                                            .padding(horizontal = 12.dp),
                                        contentAlignment = Alignment.CenterStart
                                    ) {
                                        Text(
                                            text = roleWithPerms.role.description ?: "Sin descripción",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = AppColors.TextColor.copy(alpha = 0.7f)
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .weight(3.2f)
                                            .padding(horizontal = 12.dp),
                                        contentAlignment = Alignment.CenterStart
                                    ) {
                                        if (roleWithPerms.permissions.isEmpty()) {
                                            Text(
                                                text = "Sin permisos asignados",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = AppColors.TextColor.copy(alpha = 0.5f),
                                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                            )
                                        } else {
                                            val permissionsByModuleForRole = permissionsByModule.mapNotNull { (module, perms) ->
                                                val modulePermissions = roleWithPerms.permissions.filter { it in perms }
                                                if (modulePermissions.isNotEmpty()) module to modulePermissions else null
                                            }.toMap()

                                            var expandedModule by remember { mutableStateOf<String?>(null) }

                                            FlowRow(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                                maxItemsInEachRow = 3
                                            ) {
                                                permissionsByModuleForRole.forEach { (module, perms) ->
                                                    val isExpanded = expandedModule == module

                                                    Card(
                                                        onClick = {
                                                            expandedModule = if (isExpanded) null else module
                                                        },
                                                        colors = CardDefaults.cardColors(
                                                            containerColor = getModuleColor(module).copy(
                                                                alpha = if (isExpanded) 0.12f else 0.08f
                                                            )
                                                        ),
                                                        shape = RoundedCornerShape(8.dp),
                                                        border = BorderStroke(
                                                            if (isExpanded) 2.dp else 1.dp,
                                                            getModuleColor(module).copy(
                                                                alpha = if (isExpanded) 0.5f else 0.3f
                                                            )
                                                        ),
                                                        modifier = Modifier.width(if (isExpanded) 280.dp else 180.dp)
                                                    ) {
                                                        Column(
                                                            modifier = Modifier.padding(10.dp),
                                                            verticalArrangement = Arrangement.spacedBy(6.dp)
                                                        ) {
                                                            Row(
                                                                verticalAlignment = Alignment.CenterVertically,
                                                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                                modifier = Modifier.fillMaxWidth()
                                                            ) {
                                                                Box(
                                                                    modifier = Modifier
                                                                        .size(6.dp)
                                                                        .background(
                                                                            getModuleColor(module),
                                                                            shape = CircleShape
                                                                        )
                                                                )
                                                                Text(
                                                                    text = module.uppercase(),
                                                                    style = MaterialTheme.typography.labelMedium,
                                                                    fontWeight = FontWeight.Bold,
                                                                    color = getModuleColor(module),
                                                                    fontSize = 11.sp,
                                                                    letterSpacing = 0.5.sp,
                                                                    modifier = Modifier.weight(1f)
                                                                )
                                                                Icon(
                                                                    imageVector = if (isExpanded)
                                                                        Icons.Outlined.KeyboardArrowUp
                                                                    else
                                                                        Icons.Outlined.KeyboardArrowDown,
                                                                    contentDescription = if (isExpanded) "Contraer" else "Expandir",
                                                                    tint = getModuleColor(module),
                                                                    modifier = Modifier.size(16.dp)
                                                                )
                                                            }

                                                            if (isExpanded) {
                                                                HorizontalDivider(
                                                                    color = getModuleColor(module).copy(alpha = 0.3f),
                                                                    thickness = 1.dp,
                                                                    modifier = Modifier.padding(vertical = 4.dp)
                                                                )

                                                                Column(
                                                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                                                ) {
                                                                    perms.forEach { perm ->
                                                                        Row(
                                                                            verticalAlignment = Alignment.CenterVertically,
                                                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                                        ) {
                                                                            Text(
                                                                                text = "•",
                                                                                color = getModuleColor(module).copy(alpha = 0.7f),
                                                                                fontSize = 10.sp,
                                                                                fontWeight = FontWeight.Bold
                                                                            )
                                                                            Text(
                                                                                text = formatPermissionName(perm),
                                                                                style = MaterialTheme.typography.bodySmall,
                                                                                color = AppColors.TextColor.copy(alpha = 0.85f),
                                                                                fontSize = 11.sp
                                                                            )
                                                                        }
                                                                    }
                                                                }
                                                            } else {
                                                                Text(
                                                                    text = "${perms.size} permiso${if (perms.size > 1) "s" else ""}",
                                                                    style = MaterialTheme.typography.bodySmall,
                                                                    color = AppColors.TextColor.copy(alpha = 0.6f),
                                                                    fontSize = 10.sp
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    Row(
                                        modifier = Modifier.weight(0.8f),
                                        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        IconButton(
                                            onClick = { editingRole = roleWithPerms.role },
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.Edit,
                                                contentDescription = "Editar",
                                                tint = AppColors.Primary,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        IconButton(
                                            onClick = { showDeleteDialog = roleWithPerms.role },
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.Delete,
                                                contentDescription = "Eliminar",
                                                tint = Color(0xFFE57373),
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }
                                if (roleWithPerms != rolesWithPermissions.last()) {
                                    HorizontalDivider(
                                        modifier = Modifier.padding(horizontal = 16.dp),
                                        thickness = 1.dp,
                                        color = Color(0xFFF0F0F0)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            FloatingActionButton(
                onClick = { showForm = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp),
                containerColor = AppColors.Primary,
                contentColor = Color.White
            ) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = "Agregar rol"
                )
            }
        }
    }
}

// --- FORMULARIO DE ROLES ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RoleFormDialog(
    onDismiss: () -> Unit,
    repository: Repository,
    existingRoles: List<RoleEntity>,
    editingRole: RoleEntity? = null
) {
    val focusManager = LocalFocusManager.current
    val isEditing = editingRole != null

    var currentStep by remember { mutableStateOf(RoleFormStep.INFO) }
    var formState by remember { mutableStateOf<RoleFormState>(RoleFormState.Idle) }

    var roleName by remember { mutableStateOf(editingRole?.name ?: "") }
    var roleDescription by remember { mutableStateOf(editingRole?.description ?: "") }
    var selectedPermissions by remember { mutableStateOf<Set<String>>(emptySet()) }
    var validationResult by remember { mutableStateOf(RoleValidationResult(true)) }

    LaunchedEffect(editingRole) {
        editingRole?.let { role ->
            try {
                val permissions = repository.getRolePermissions(role.id)
                selectedPermissions = permissions.toSet()
            } catch (e: Exception) {
                println("Error cargando permisos: ${e.message}")
            }
        }
    }

    fun validateCurrentStep(): Boolean {
        val result = when (currentStep) {
            RoleFormStep.INFO -> RoleValidator.validateName(roleName, existingRoles, editingRole?.id)
            RoleFormStep.PERMISSIONS -> RoleValidator.validatePermissions(selectedPermissions)
            RoleFormStep.CONFIRMATION -> RoleValidator.validateComplete(
                roleName,
                existingRoles,
                selectedPermissions,
                editingRole?.id
            )
        }
        validationResult = result
        return result.isValid
    }

    fun proceedToNext() {
        when (currentStep) {
            RoleFormStep.INFO -> {
                if (validateCurrentStep()) {
                    currentStep = RoleFormStep.PERMISSIONS
                    validationResult = validationResult.copy(permissionError = null)
                }
            }

            RoleFormStep.PERMISSIONS -> {
                if (validateCurrentStep()) {
                    currentStep = RoleFormStep.CONFIRMATION
                }
            }

            RoleFormStep.CONFIRMATION -> {
                if (validateCurrentStep()) {
                    formState = RoleFormState.Loading
                    try {
                        if (isEditing) {
                            editingRole.let { role ->
                                repository.updateRole(role.id, roleName.trim(), roleDescription.ifBlank { null })
                                repository.deleteRolePermissions(role.id)
                                selectedPermissions.forEach { permission ->
                                    repository.insertRolePermission(role.id, permission)
                                }
                            }
                        } else {
                            repository.insertRole(roleName.trim(), roleDescription.ifBlank { null })
                            val createdRole = repository.getAllRoles().find {
                                it.name.equals(roleName.trim(), ignoreCase = true)
                            }
                            createdRole?.let { role ->
                                selectedPermissions.forEach { permission ->
                                    repository.insertRolePermission(role.id, permission)
                                }
                            }
                        }
                        formState = RoleFormState.Success
                        onDismiss()
                    } catch (e: Exception) {
                        formState = RoleFormState.Error("Error al ${if (isEditing) "actualizar" else "guardar"} rol: ${e.message}")
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .widthIn(min = 400.dp, max = 500.dp)
                .heightIn(max = 700.dp)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(4.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                BackgroundLogo()

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    FormHeader(currentStep, isEditing)

                    Spacer(Modifier.height(12.dp))

                    when (currentStep) {
                        RoleFormStep.INFO -> {
                            RoleInfoContent(
                                roleName = roleName,
                                roleDescription = roleDescription,
                                validationResult = validationResult,
                                formState = formState,
                                onNameChange = {
                                    roleName = it
                                    if (validationResult.nameError != null)
                                        validationResult = validationResult.copy(nameError = null)
                                },
                                onDescriptionChange = { roleDescription = it },
                                focusManager = focusManager
                            )
                        }

                        RoleFormStep.PERMISSIONS -> {
                            RolePermissionsContent(
                                selectedPermissions = selectedPermissions,
                                validationResult = validationResult,
                                onPermissionToggle = { permission, checked ->
                                    selectedPermissions = if (checked)
                                        selectedPermissions + permission
                                    else
                                        selectedPermissions - permission

                                    if (checked && validationResult.permissionError != null) {
                                        validationResult = validationResult.copy(permissionError = null)
                                    }
                                }
                            )
                        }

                        RoleFormStep.CONFIRMATION -> {
                            RoleConfirmationContent(
                                roleName = roleName,
                                roleDescription = roleDescription,
                                selectedPermissions = selectedPermissions,
                                formState = formState,
                                isEditing = isEditing
                            )
                        }
                    }

                    Spacer(Modifier.weight(1f))

                    NavigationButtons(
                        currentStep = currentStep,
                        formState = formState,
                        onDismiss = onDismiss,
                        onPrevious = {
                            currentStep = when (currentStep) {
                                RoleFormStep.PERMISSIONS -> RoleFormStep.INFO
                                RoleFormStep.CONFIRMATION -> RoleFormStep.PERMISSIONS
                                else -> RoleFormStep.INFO
                            }
                            validationResult = RoleValidationResult(true)
                        },
                        onNext = { proceedToNext() },
                        isEditing = isEditing
                    )
                }
            }
        }
    }
}

@Composable
private fun BackgroundLogo() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(Res.drawable.logoSystem),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .padding(40.dp)
                .alpha(0.08f),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
private fun FormHeader(currentStep: RoleFormStep, isEditing: Boolean) {
    val animatedProgress by animateFloatAsState(
        targetValue = when (currentStep) {
            RoleFormStep.INFO -> 0.33f
            RoleFormStep.PERMISSIONS -> 0.66f
            RoleFormStep.CONFIRMATION -> 1f
        },
        animationSpec = tween(300),
        label = "progress"
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = if (isEditing) "Editar Rol" else "Nuevo Rol",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier.fillMaxWidth(),
            color = AppColors.Primary
        )

        Text(
            text = when (currentStep) {
                RoleFormStep.INFO -> "Paso 1: Información del Rol"
                RoleFormStep.PERMISSIONS -> "Paso 2: Permisos"
                RoleFormStep.CONFIRMATION -> "Paso 3: Confirmación"
            },
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RoleInfoContent(
    roleName: String,
    roleDescription: String,
    validationResult: RoleValidationResult,
    formState: RoleFormState,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    focusManager: androidx.compose.ui.focus.FocusManager
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = roleName,
            onValueChange = onNameChange,
            label = { Text("Nombre del Rol") },
            placeholder = { Text("Ej: Administrador, Instructor, Recepcionista") },
            isError = validationResult.nameError != null,
            supportingText = {
                validationResult.nameError?.let {
                    Text(it, color = MaterialTheme.colorScheme.error)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = formState !is RoleFormState.Loading,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = {
                focusManager.moveFocus(FocusDirection.Down)
            }),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AppColors.Primary,
                focusedLabelColor = AppColors.Primary
            )
        )

        OutlinedTextField(
            value = roleDescription,
            onValueChange = onDescriptionChange,
            label = { Text("Descripción (opcional)") },
            placeholder = { Text("Ej: Responsable de la gestión administrativa") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = false,
            maxLines = 3,
            enabled = formState !is RoleFormState.Loading,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
            }),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AppColors.Primary,
                focusedLabelColor = AppColors.Primary
            )
        )
    }
}

@Composable
private fun RolePermissionsContent(
    selectedPermissions: Set<String>,
    validationResult: RoleValidationResult,
    onPermissionToggle: (String, Boolean) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (validationResult.permissionError != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    validationResult.permissionError,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        permissionsByModule.forEach { (module, perms) ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = getModuleColor(module).copy(alpha = 0.05f)
                ),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, getModuleColor(module).copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(getModuleColor(module), shape = CircleShape)
                        )
                        Text(
                            module,
                            fontWeight = FontWeight.Bold,
                            color = getModuleColor(module),
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    perms.forEach { perm ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Checkbox(
                                checked = selectedPermissions.contains(perm),
                                onCheckedChange = { checked ->
                                    onPermissionToggle(perm, checked)
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = getModuleColor(module)
                                )
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = formatPermissionName(perm),
                                color = AppColors.TextColor
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RoleConfirmationContent(
    roleName: String,
    roleDescription: String,
    selectedPermissions: Set<String>,
    formState: RoleFormState,
    isEditing: Boolean
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFF5F5F5)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = if (isEditing) "Confirmar Edición de Rol" else "Confirmar Registro de Rol",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextColor
                )

                HorizontalDivider(
                    color = AppColors.Primary.copy(alpha = 0.3f),
                    thickness = 1.dp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Nombre del rol:",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = AppColors.TextColor,
                        modifier = Modifier.width(100.dp)
                    )
                    Text(
                        text = roleName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppColors.Primary,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (roleDescription.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "Descripción:",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = AppColors.TextColor,
                            modifier = Modifier.width(100.dp)
                        )
                        Text(
                            text = roleDescription,
                            style = MaterialTheme.typography.bodyMedium,
                            color = AppColors.TextColor
                        )
                    }
                }

                HorizontalDivider(
                    color = AppColors.Primary.copy(alpha = 0.3f),
                    thickness = 1.dp
                )

                Text(
                    text = "Permisos seleccionados (${selectedPermissions.size}):",
                    fontWeight = FontWeight.Bold,
                    color = AppColors.Primary
                )

                val permissionsByModuleForConfirm = permissionsByModule.mapNotNull { (module, perms) ->
                    val modulePermissions = selectedPermissions.filter { it in perms }
                    if (modulePermissions.isNotEmpty()) module to modulePermissions else null
                }.toMap()

                permissionsByModuleForConfirm.forEach { (module, perms) ->
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "▸ $module",
                            fontWeight = FontWeight.SemiBold,
                            color = getModuleColor(module),
                            fontSize = 13.sp
                        )
                        perms.sorted().forEach { perm ->
                            Text(
                                text = "  • ${formatPermissionName(perm)}",
                                color = AppColors.TextColor.copy(alpha = 0.8f),
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        if (formState is RoleFormState.Error) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = formState.message,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}

@Composable
private fun NavigationButtons(
    currentStep: RoleFormStep,
    formState: RoleFormState,
    onDismiss: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    isEditing: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onDismiss,
            modifier = Modifier
                .width(100.dp)
                .height(40.dp),
            enabled = formState !is RoleFormState.Loading,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFff8abe),
                disabledContainerColor = Color(0xFFff8abe).copy(alpha = 0.6f)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "Cancelar",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }

        if (currentStep != RoleFormStep.INFO) {
            Button(
                onClick = onPrevious,
                modifier = Modifier
                    .width(100.dp)
                    .height(40.dp),
                enabled = formState !is RoleFormState.Loading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFff8abe),
                    disabledContainerColor = Color(0xFFff8abe).copy(alpha = 0.6f)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Anterior",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onNext,
            enabled = formState !is RoleFormState.Loading,
            modifier = Modifier
                .width(110.dp)
                .height(40.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AppColors.Primary,
                disabledContainerColor = AppColors.Primary.copy(alpha = 0.6f)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            if (formState is RoleFormState.Loading) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "...",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            } else {
                Text(
                    text = when {
                        currentStep == RoleFormStep.CONFIRMATION && isEditing -> "Actualizar"
                        currentStep == RoleFormStep.CONFIRMATION -> "Guardar"
                        else -> "Siguiente"
                    },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }
        }
    }
}