package org.example.appbbmges.ui.settings.registationex.formulariorolscreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.appbbmges.ui.usuarios.AppColors

@Composable
fun RolePermissionsBadges(permissions: List<String>) {
    if (permissions.isEmpty()) {
        Text(
            text = "Sin permisos asignados",
            style = MaterialTheme.typography.bodySmall,
            color = AppColors.TextColor.copy(alpha = 0.5f),
            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
        )
    } else {
        val permissionsByModuleForRole = permissionsByModule.mapNotNull { (module, perms) ->
            val modulePermissions = permissions.filter { it in perms }
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
                PermissionModuleBadge(
                    module = module,
                    permissions = perms,
                    isExpanded = expandedModule == module,
                    onToggle = { expandedModule = if (expandedModule == module) null else module }
                )
            }
        }
    }
}

@Composable
private fun PermissionModuleBadge(
    module: String,
    permissions: List<String>,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    Card(
        onClick = onToggle,
        colors = CardDefaults.cardColors(
            containerColor = getModuleColor(module).copy(alpha = if (isExpanded) 0.12f else 0.08f)
        ),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(
            if (isExpanded) 2.dp else 1.dp,
            getModuleColor(module).copy(alpha = if (isExpanded) 0.5f else 0.3f)
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
                    modifier = Modifier.size(6.dp)
                        .background(getModuleColor(module), shape = CircleShape)
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
                    imageVector = if (isExpanded) Icons.Outlined.KeyboardArrowUp
                    else Icons.Outlined.KeyboardArrowDown,
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

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    permissions.forEach { perm ->
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
                    text = "${permissions.size} permiso${if (permissions.size > 1) "s" else ""}",
                    style = MaterialTheme.typography.bodySmall,
                    color = AppColors.TextColor.copy(alpha = 0.6f),
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
fun RoleActionButtons(onEdit: () -> Unit, onDelete: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
            Icon(
                imageVector = Icons.Outlined.Edit,
                contentDescription = "Editar",
                tint = AppColors.Primary,
                modifier = Modifier.size(20.dp)
            )
        }
        IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
            Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = "Eliminar",
                tint = Color(0xFFE57373),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}