package org.example.appbbmges.ui.settings.registationex.formulariorolscreen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.appbbmges.RoleEntity
import org.example.appbbmges.data.Repository
import org.example.appbbmges.ui.usuarios.AppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNewRoleScreen(
    onDismiss: () -> Unit,
    repository: Repository,
    modifier: Modifier = Modifier
) {
    // ViewModel
    val viewModel = remember { RoleViewModel(repository) }

    // Estados locales para diálogos
    var showForm by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf<RoleEntity?>(null) }

    // ==================== DIÁLOGO DE ELIMINACIÓN ====================

    showDeleteDialog?.let { role ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Eliminar Rol") },
            text = {
                Text(
                    "¿Estás seguro de que deseas eliminar el rol '${role.name}'? " +
                            "Esto también eliminará todos los permisos y asignaciones de franquicias asociadas."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteRole(role)
                        showDeleteDialog = null
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

    // ==================== CONTENIDO PRINCIPAL ====================

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppColors.Background)
    ) {
        // Mostrar formulario si está activo
        if (showForm || viewModel.editingRole != null) {
            RoleFormScreen(
                viewModel = viewModel,
                onDismiss = {
                    showForm = false
                    viewModel.cancelForm()
                }
            )
        }

        // Mostrar lista si no hay formulario activo
        if (!showForm && viewModel.editingRole == null && showDeleteDialog == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                // Header
                RoleListHeader(
                    totalRoles = viewModel.rolesWithPermissions.size,
                    onBack = onDismiss
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Contenido según estado
                when {
                    viewModel.isLoadingList -> {
                        LoadingState()
                    }
                    viewModel.listError != null -> {
                        ErrorState(message = viewModel.listError!!)
                    }
                    viewModel.rolesWithPermissions.isEmpty() -> {
                        EmptyState()
                    }
                    else -> {
                        RoleListContent(
                            roles = viewModel.rolesWithPermissions,
                            onEdit = { role ->
                                viewModel.startEditRole(role)
                            },
                            onDelete = { role ->
                                showDeleteDialog = role
                            }
                        )
                    }
                }
            }

            // FAB para agregar nuevo rol
            FloatingActionButton(
                onClick = {
                    viewModel.startNewRole()
                    showForm = true
                },
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

// ==================== COMPONENTES DE LA LISTA ====================

@Composable
private fun RoleListHeader(
    totalRoles: Int,
    onBack: () -> Unit
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
                text = "$totalRoles roles registrados",
                fontSize = 14.sp,
                color = AppColors.TextColor.copy(alpha = 0.7f)
            )
        }

        Button(
            onClick = onBack,
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
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = AppColors.Primary)
    }
}

@Composable
private fun ErrorState(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Outlined.ErrorOutline,
                contentDescription = "Error",
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                color = MaterialTheme.colorScheme.onErrorContainer,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun EmptyState() {
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
}

@Composable
private fun RoleListContent(
    roles: List<RoleWithPermissions>,
    onEdit: (RoleEntity) -> Unit,
    onDelete: (RoleEntity) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Header de la tabla
            item {
                RoleTableHeader()
            }

            // Filas de roles
            items(roles) { roleWithPerms ->
                RoleTableRow(
                    roleWithPermissions = roleWithPerms,
                    onEdit = { onEdit(roleWithPerms.role) },
                    onDelete = { onDelete(roleWithPerms.role) }
                )

                if (roleWithPerms != roles.last()) {
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

@Composable
private fun RoleTableHeader() {
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

@Composable
private fun RoleTableRow(
    roleWithPermissions: RoleWithPermissions,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Nombre del rol
        Box(
            modifier = Modifier.weight(1.3f),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = roleWithPermissions.role.name,
                style = MaterialTheme.typography.bodyLarge,
                color = AppColors.TextColor,
                fontWeight = FontWeight.SemiBold
            )
        }

        // Descripción
        Box(
            modifier = Modifier
                .weight(1.5f)
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = roleWithPermissions.role.description ?: "Sin descripción",
                style = MaterialTheme.typography.bodyMedium,
                color = AppColors.TextColor.copy(alpha = 0.7f)
            )
        }

        // Permisos
        Box(
            modifier = Modifier
                .weight(3.2f)
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            RolePermissionsTags(permissions = roleWithPermissions.permissions)
        }

        // Acciones
        Row(
            modifier = Modifier.weight(0.8f),
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onEdit,
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
                onClick = onDelete,
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
}

@Composable
private fun RolePermissionsTags(permissions: List<String>) {
    if (permissions.isEmpty()) {
        Text(
            text = "Sin permisos asignados",
            style = MaterialTheme.typography.bodySmall,
            color = AppColors.TextColor.copy(alpha = 0.5f),
            fontStyle = FontStyle.Italic
        )
        return
    }

    val permissionsByModule = RolePermissions.getModulesForPermissions(permissions)
    var expandedModule by remember { mutableStateOf<String?>(null) }

    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        maxItemsInEachRow = 3
    ) {
        permissionsByModule.forEach { (module, perms) ->
            PermissionModuleCard(
                module = module,
                permissions = perms,
                isExpanded = expandedModule == module,
                onToggleExpand = {
                    expandedModule = if (expandedModule == module) null else module
                }
            )
        }
    }
}

@Composable
private fun PermissionModuleCard(
    module: String,
    permissions: List<String>,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit
) {
    val moduleColor = RoleColors.getColorForModule(module)

    Card(
        onClick = onToggleExpand,
        colors = CardDefaults.cardColors(
            containerColor = moduleColor.copy(alpha = if (isExpanded) 0.12f else 0.08f)
        ),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(
            if (isExpanded) 2.dp else 1.dp,
            moduleColor.copy(alpha = if (isExpanded) 0.5f else 0.3f)
        ),
        modifier = Modifier.width(if (isExpanded) 280.dp else 180.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Header del módulo
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(moduleColor, shape = CircleShape)
                )
                Text(
                    text = module.uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = moduleColor,
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
                    tint = moduleColor,
                    modifier = Modifier.size(16.dp)
                )
            }

            // Contenido expandido
            if (isExpanded) {
                HorizontalDivider(
                    color = moduleColor.copy(alpha = 0.3f),
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    permissions.forEach { perm ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "•",
                                color = moduleColor.copy(alpha = 0.7f),
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
                    text = "${permissions.size} permiso${if (permissions.size > 1) "s" else ""}",
                    style = MaterialTheme.typography.bodySmall,
                    color = AppColors.TextColor.copy(alpha = 0.6f),
                    fontSize = 10.sp
                )
            }
        }
    }
}