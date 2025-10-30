package org.example.appbbmges.ui.settings.registationex.formulariorolscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import org.example.appbbmges.data.Repository
import org.example.appbbmges.RoleEntity
import org.example.appbbmges.ui.usuarios.AppColors

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

    fun loadRoles() {
        isLoading = true
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

    LaunchedEffect(Unit) { loadRoles() }

    Box(modifier = modifier.fillMaxSize().background(AppColors.Background)) {
        // Form Dialog
        if (showForm || editingRole != null) {
            RoleFormDialog(
                onDismiss = {
                    showForm = false
                    editingRole = null
                    loadRoles()
                },
                repository = repository,
                existingRoles = rolesWithPermissions.map { it.role },
                editingRole = editingRole
            )
        }

        showDeleteDialog?.let { role ->
            DeleteRoleDialog(
                role = role,
                onConfirm = {
                    try {
                        repository.deleteRolePermissions(role.id)
                        repository.deleteRole(role.id)
                        loadRoles()
                        showDeleteDialog = null
                    } catch (e: Exception) {
                        println("Error al eliminar rol: ${e.message}")
                        showDeleteDialog = null
                    }
                },
                onDismiss = { showDeleteDialog = null }
            )
        }

        if (!showForm && editingRole == null && showDeleteDialog == null) {
            RoleListScreen(
                rolesWithPermissions = rolesWithPermissions,
                isLoading = isLoading,
                onDismiss = onDismiss,
                onAddRole = { showForm = true },
                onEditRole = { editingRole = it },
                onDeleteRole = { showDeleteDialog = it }
            )
        }
    }
}

@Composable
private fun DeleteRoleDialog(
    role: RoleEntity,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Eliminar Rol") },
        text = {
            Text("¿Estás seguro de que deseas eliminar el rol '${role.name}'? " +
                    "Esto también eliminará todos los permisos asociados.")
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Eliminar", color = Color.Red, fontWeight = FontWeight.Medium)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}