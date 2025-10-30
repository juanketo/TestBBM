package org.example.appbbmges.ui.settings.registationex.formulariorolscreen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.appbbmges.ui.usuarios.AppColors

@Composable
fun FormHeader(currentStep: RoleFormStep, isEditing: Boolean) {
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
fun RoleInfoContent(
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
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AppColors.Primary,
                focusedLabelColor = AppColors.Primary
            )
        )
    }
}

@Composable
fun RolePermissionsFormContent(
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
            PermissionModuleCard(
                module = module,
                permissions = perms,
                selectedPermissions = selectedPermissions,
                onPermissionToggle = onPermissionToggle
            )
        }
    }
}

@Composable
private fun PermissionModuleCard(
    module: String,
    permissions: List<String>,
    selectedPermissions: Set<String>,
    onPermissionToggle: (String, Boolean) -> Unit
) {
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
                    modifier = Modifier.size(8.dp)
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
            permissions.forEach { perm ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = selectedPermissions.contains(perm),
                        onCheckedChange = { checked -> onPermissionToggle(perm, checked) },
                        colors = CheckboxDefaults.colors(checkedColor = getModuleColor(module))
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(text = formatPermissionName(perm), color = AppColors.TextColor)
                }
            }
        }
    }
}

@Composable
fun RoleConfirmationContent(
    roleName: String,
    roleDescription: String,
    selectedPermissions: Set<String>,
    formState: RoleFormState,
    isEditing: Boolean
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
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

                HorizontalDivider(color = AppColors.Primary.copy(alpha = 0.3f), thickness = 1.dp)

                ConfirmationRow(label = "Nombre del rol:", value = roleName, highlight = true)

                if (roleDescription.isNotEmpty()) {
                    ConfirmationRow(label = "Descripción:", value = roleDescription)
                }

                HorizontalDivider(color = AppColors.Primary.copy(alpha = 0.3f), thickness = 1.dp)

                Text(
                    text = "Permisos seleccionados (${selectedPermissions.size}):",
                    fontWeight = FontWeight.Bold,
                    color = AppColors.Primary
                )

                PermissionsSummary(selectedPermissions = selectedPermissions)
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
private fun ConfirmationRow(label: String, value: String, highlight: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = if (label == "Descripción:") Alignment.Top else Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = AppColors.TextColor,
            modifier = Modifier.width(100.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = if (highlight) AppColors.Primary else AppColors.TextColor,
            fontWeight = if (highlight) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun PermissionsSummary(selectedPermissions: Set<String>) {
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

@Composable
fun NavigationButtons(
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
            modifier = Modifier.width(100.dp).height(40.dp),
            enabled = formState !is RoleFormState.Loading,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFff8abe),
                disabledContainerColor = Color(0xFFff8abe).copy(alpha = 0.6f)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Cancelar", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color.White)
        }

        if (currentStep != RoleFormStep.INFO) {
            Button(
                onClick = onPrevious,
                modifier = Modifier.width(100.dp).height(40.dp),
                enabled = formState !is RoleFormState.Loading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFff8abe),
                    disabledContainerColor = Color(0xFFff8abe).copy(alpha = 0.6f)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Anterior", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onNext,
            enabled = formState !is RoleFormState.Loading,
            modifier = Modifier.width(110.dp).height(40.dp),
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
                    Text("...", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color.White)
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