package org.example.appbbmges.ui.settings.registationex.formulariorolscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.SupervisorAccount
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.appbbmges.RoleEntity
import org.example.appbbmges.ui.usuarios.AppColors

@Composable
fun RoleListScreen(
    rolesWithPermissions: List<RoleWithPermissions>,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onAddRole: () -> Unit,
    onEditRole: (RoleEntity) -> Unit,
    onDeleteRole: (RoleEntity) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
            RoleListHeader(
                roleCount = rolesWithPermissions.size,
                onDismiss = onDismiss
            )

            Spacer(modifier = Modifier.height(24.dp))

            when {
                isLoading -> LoadingIndicator()
                rolesWithPermissions.isEmpty() -> EmptyRoleState()
                else -> RoleListContent(
                    rolesWithPermissions = rolesWithPermissions,
                    onEditRole = onEditRole,
                    onDeleteRole = onDeleteRole
                )
            }
        }

        FloatingActionButton(
            onClick = onAddRole,
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp),
            containerColor = AppColors.Primary,
            contentColor = Color.White
        ) {
            Icon(imageVector = Icons.Outlined.Add, contentDescription = "Agregar rol")
        }
    }
}

@Composable
private fun RoleListHeader(roleCount: Int, onDismiss: () -> Unit) {
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
                text = "$roleCount roles registrados",
                fontSize = 14.sp,
                color = AppColors.TextColor.copy(alpha = 0.7f)
            )
        }

        Button(
            onClick = onDismiss,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFff8abe)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(text = "Volver", color = Color.White, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun LoadingIndicator() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = AppColors.Primary)
    }
}

@Composable
private fun EmptyRoleState() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(48.dp),
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
    rolesWithPermissions: List<RoleWithPermissions>,
    onEditRole: (RoleEntity) -> Unit,
    onDeleteRole: (RoleEntity) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        androidx.compose.foundation.lazy.LazyColumn(modifier = Modifier.fillMaxWidth()) {
            item { RoleListTableHeader() }

            items(rolesWithPermissions.size) { index ->
                val roleWithPerms = rolesWithPermissions[index]
                RoleListItem(
                    roleWithPermissions = roleWithPerms,
                    onEdit = { onEditRole(roleWithPerms.role) },
                    onDelete = { onDeleteRole(roleWithPerms.role) }
                )
                if (index < rolesWithPermissions.size - 1) {
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
private fun RoleListTableHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppColors.Primary.copy(alpha = 0.1f))
            .padding(horizontal = 20.dp, vertical = 18.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Roles Activos", fontWeight = FontWeight.Bold, color = AppColors.Primary,
            fontSize = 14.sp, modifier = Modifier.weight(1.3f))
        Text("Descripción", fontWeight = FontWeight.Bold, color = AppColors.Primary,
            fontSize = 14.sp, modifier = Modifier.weight(1.5f).padding(horizontal = 12.dp))
        Text("Permisos en el Sistema", fontWeight = FontWeight.Bold, color = AppColors.Primary,
            fontSize = 14.sp, modifier = Modifier.weight(3.2f).padding(horizontal = 12.dp))
        Text("Acciones", fontWeight = FontWeight.Bold, color = AppColors.Primary,
            fontSize = 14.sp, modifier = Modifier.weight(0.8f), textAlign = TextAlign.Center)
    }
}

@Composable
private fun RoleListItem(
    roleWithPermissions: RoleWithPermissions,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = roleWithPermissions.role.name,
            style = MaterialTheme.typography.bodyLarge,
            color = AppColors.TextColor,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1.3f)
        )

        Text(
            text = roleWithPermissions.role.description ?: "Sin descripción",
            style = MaterialTheme.typography.bodyMedium,
            color = AppColors.TextColor.copy(alpha = 0.7f),
            modifier = Modifier.weight(1.5f).padding(horizontal = 12.dp)
        )

        Box(modifier = Modifier.weight(3.2f).padding(horizontal = 12.dp)) {
            RolePermissionsBadges(permissions = roleWithPermissions.permissions)
        }

        RoleActionButtons(onEdit = onEdit, onDelete = onDelete, modifier = Modifier.weight(0.8f))
    }
}