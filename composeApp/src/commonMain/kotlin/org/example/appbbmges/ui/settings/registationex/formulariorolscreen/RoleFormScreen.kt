package org.example.appbbmges.ui.settings.registationex.formulariorolscreen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import appbbmges.composeapp.generated.resources.Res
import appbbmges.composeapp.generated.resources.logoSystem
import org.example.appbbmges.FranchiseEntity
import org.example.appbbmges.ui.usuarios.AppColors
import org.jetbrains.compose.resources.painterResource

/**
 * Pantalla del formulario multi-paso para crear/editar roles
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleFormScreen(
    viewModel: RoleViewModel,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isEditing = viewModel.editingRole != null

    Box(
        modifier = modifier
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
                // Logo de fondo
                BackgroundLogo()

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Header con progreso
                    FormHeader(
                        currentStep = viewModel.currentStep,
                        isEditing = isEditing
                    )

                    Spacer(Modifier.height(12.dp))

                    // Contenido según el paso actual
                    when (viewModel.currentStep) {
                        RoleFormStep.INFO -> {
                            RoleInfoStep(viewModel = viewModel)
                        }
                        RoleFormStep.PERMISSIONS -> {
                            RolePermissionsStep(viewModel = viewModel)
                        }
                        RoleFormStep.FRANCHISES -> {
                            RoleFranchisesStep(viewModel = viewModel)
                        }
                        RoleFormStep.CONFIRMATION -> {
                            RoleConfirmationStep(viewModel = viewModel, isEditing = isEditing)
                        }
                    }

                    Spacer(Modifier.weight(1f))

                    // Botones de navegación
                    NavigationButtons(
                        currentStep = viewModel.currentStep,
                        formState = viewModel.formState,
                        isEditing = isEditing,
                        onDismiss = onDismiss,
                        onPrevious = { viewModel.goToPreviousStep() },
                        onNext = { viewModel.proceedToNext() }
                    )
                }
            }
        }
    }
}

// ==================== COMPONENTES GENERALES ====================

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
            RoleFormStep.INFO -> 0.25f
            RoleFormStep.PERMISSIONS -> 0.50f
            RoleFormStep.FRANCHISES -> 0.75f
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
                RoleFormStep.FRANCHISES -> "Paso 3: Franquicias"
                RoleFormStep.CONFIRMATION -> "Paso 4: Confirmación"
            },
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun NavigationButtons(
    currentStep: RoleFormStep,
    formState: RoleFormState,
    isEditing: Boolean,
    onDismiss: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Botón Cancelar
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

        // Botón Anterior (solo si no es el primer paso)
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

        // Botón Siguiente/Guardar
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

// ==================== PASO 1: INFORMACIÓN ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RoleInfoStep(viewModel: RoleViewModel) {
    val focusManager = LocalFocusManager.current

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = viewModel.formData.name,
            onValueChange = { viewModel.updateRoleName(it) },
            label = { Text("Nombre del Rol") },
            placeholder = { Text("Ej: Administrador, Instructor, Recepcionista") },
            isError = viewModel.validationResult.nameError != null,
            supportingText = {
                viewModel.validationResult.nameError?.let {
                    Text(it, color = MaterialTheme.colorScheme.error)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = viewModel.formState !is RoleFormState.Loading,
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
            value = viewModel.formData.description,
            onValueChange = { viewModel.updateRoleDescription(it) },
            label = { Text("Descripción (opcional)") },
            placeholder = { Text("Ej: Responsable de la gestión administrativa") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = false,
            maxLines = 3,
            enabled = viewModel.formState !is RoleFormState.Loading,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
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

// ==================== PASO 2: PERMISOS ====================

@Composable
private fun RolePermissionsStep(viewModel: RoleViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Mostrar error si existe
        if (viewModel.validationResult.permissionError != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    viewModel.validationResult.permissionError!!,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        // Lista de permisos por módulo
        RolePermissions.permissionsByModule.forEach { (module, permissions) ->
            PermissionModuleCard(
                module = module,
                permissions = permissions,
                selectedPermissions = viewModel.formData.selectedPermissions,
                onPermissionToggle = { permission, checked ->
                    viewModel.togglePermission(permission, checked)
                }
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
    val moduleColor = RoleColors.getColorForModule(module)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = moduleColor.copy(alpha = 0.05f)
        ),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, moduleColor.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header del módulo
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(moduleColor, shape = CircleShape)
                )
                Text(
                    module,
                    fontWeight = FontWeight.Bold,
                    color = moduleColor,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Lista de permisos con checkboxes
            permissions.forEach { permission ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = selectedPermissions.contains(permission),
                        onCheckedChange = { checked ->
                            onPermissionToggle(permission, checked)
                        },
                        colors = CheckboxDefaults.colors(
                            checkedColor = moduleColor
                        )
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = formatPermissionName(permission),
                        color = AppColors.TextColor
                    )
                }
            }
        }
    }
}

// ==================== PASO 3: FRANQUICIAS ====================

@Composable
private fun RoleFranchisesStep(viewModel: RoleViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Mostrar error si existe
        if (viewModel.validationResult.franchiseError != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    viewModel.validationResult.franchiseError!!,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        // Switch para asignar a todas las franquicias
        AssignToAllFranchisesCard(
            assignToAll = viewModel.formData.assignToAllFranchises,
            onToggle = { viewModel.toggleAssignToAllFranchises(it) }
        )

        // Lista de franquicias (solo si no están todas seleccionadas)
        if (!viewModel.formData.assignToAllFranchises) {
            FranchiseSelectionList(
                allFranchises = viewModel.allFranchises,
                selectedFranchises = viewModel.formData.selectedFranchises,
                onFranchiseToggle = { franchiseId, checked ->
                    viewModel.toggleFranchise(franchiseId, checked)
                }
            )
        } else {
            AllFranchisesSelectedCard(totalFranchises = viewModel.allFranchises.size)
        }
    }
}

@Composable
private fun AssignToAllFranchisesCard(
    assignToAll: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = AppColors.Primary.copy(alpha = 0.08f)
        ),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(2.dp, AppColors.Primary.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Asignar a todas las franquicias",
                    fontWeight = FontWeight.Bold,
                    color = AppColors.Primary,
                    fontSize = 14.sp
                )
                Text(
                    text = "El rol estará disponible en todas las unidades",
                    color = AppColors.TextColor.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
            }
            Switch(
                checked = assignToAll,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = AppColors.Primary,
                    checkedTrackColor = AppColors.Primary.copy(alpha = 0.5f)
                )
            )
        }
    }
}

@Composable
private fun FranchiseSelectionList(
    allFranchises: List<FranchiseEntity>,
    selectedFranchises: Set<Long>,
    onFranchiseToggle: (Long, Boolean) -> Unit
) {
    if (allFranchises.isEmpty()) {
        NoFranchisesWarning()
        return
    }

    Text(
        text = "Selecciona las franquicias donde estará disponible este rol:",
        fontWeight = FontWeight.Medium,
        color = AppColors.TextColor,
        fontSize = 13.sp
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            allFranchises.forEach { franchise ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = selectedFranchises.contains(franchise.id),
                        onCheckedChange = { checked ->
                            onFranchiseToggle(franchise.id, checked)
                        },
                        colors = CheckboxDefaults.colors(
                            checkedColor = AppColors.Primary
                        )
                    )
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text(
                            text = franchise.name,
                            color = AppColors.TextColor,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                        franchise.zone?.let { zone ->
                            if (zone.isNotEmpty()) {
                                Text(
                                    text = "Zona: $zone",
                                    color = AppColors.TextColor.copy(alpha = 0.6f),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
                if (franchise != allFranchises.last()) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = Color(0xFFF0F0F0)
                    )
                }
            }
        }
    }

    Text(
        text = "${selectedFranchises.size} de ${allFranchises.size} franquicias seleccionadas",
        color = AppColors.TextColor.copy(alpha = 0.7f),
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium
    )
}

@Composable
private fun NoFranchisesWarning() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFF3E0)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = null,
                tint = Color(0xFFFF6F00)
            )
            Text(
                text = "No hay franquicias registradas. Debes crear franquicias primero.",
                color = Color(0xFFE65100),
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun AllFranchisesSelectedCard(totalFranchises: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE8F5E9)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.CheckCircle,
                contentDescription = null,
                tint = Color(0xFF4CAF50)
            )
            Text(
                text = "Este rol estará disponible en las $totalFranchises franquicias registradas",
                color = Color(0xFF2E7D32),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// ==================== PASO 4: CONFIRMACIÓN ====================

@Composable
private fun RoleConfirmationStep(
    viewModel: RoleViewModel,
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

                // Información básica
                ConfirmationRow(
                    label = "Nombre del rol:",
                    value = viewModel.formData.name,
                    highlight = true
                )

                if (viewModel.formData.description.isNotEmpty()) {
                    ConfirmationRow(
                        label = "Descripción:",
                        value = viewModel.formData.description
                    )
                }

                HorizontalDivider(
                    color = AppColors.Primary.copy(alpha = 0.3f),
                    thickness = 1.dp
                )

                // Permisos
                PermissionsSummary(
                    selectedPermissions = viewModel.formData.selectedPermissions
                )

                HorizontalDivider(
                    color = AppColors.Primary.copy(alpha = 0.3f),
                    thickness = 1.dp
                )

                // Franquicias
                FranchisesSummary(
                    assignToAllFranchises = viewModel.formData.assignToAllFranchises,
                    selectedFranchises = viewModel.formData.selectedFranchises,
                    allFranchises = viewModel.allFranchises
                )
            }
        }

        // Mostrar error si existe
        if (viewModel.formState is RoleFormState.Error) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = (viewModel.formState as RoleFormState.Error).message,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}

@Composable
private fun ConfirmationRow(
    label: String,
    value: String,
    highlight: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = if (highlight) Alignment.CenterVertically else Alignment.Top
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = AppColors.TextColor,
            modifier = Modifier.width(120.dp)
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
    Text(
        text = "Permisos seleccionados (${selectedPermissions.size}):",
        fontWeight = FontWeight.Bold,
        color = AppColors.Primary
    )

    val permissionsByModule = RolePermissions.getModulesForPermissions(selectedPermissions.toList())

    permissionsByModule.forEach { (module, permissions) ->
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "▸ $module",
                fontWeight = FontWeight.SemiBold,
                color = RoleColors.getColorForModule(module),
                fontSize = 13.sp
            )
            permissions.sorted().forEach { permission ->
                Text(
                    text = "  • ${formatPermissionName(permission)}",
                    color = AppColors.TextColor.copy(alpha = 0.8f),
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun FranchisesSummary(
    assignToAllFranchises: Boolean,
    selectedFranchises: Set<Long>,
    allFranchises: List<FranchiseEntity>
) {
    Text(
        text = "Franquicias asignadas:",
        fontWeight = FontWeight.Bold,
        color = AppColors.Primary
    )

    if (assignToAllFranchises) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFE8F5E9)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Todas las franquicias (${allFranchises.size})",
                    color = Color(0xFF2E7D32),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    } else {
        val selectedFranchisesList = allFranchises.filter { it.id in selectedFranchises }
        selectedFranchisesList.forEach { franchise ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Store,
                    contentDescription = null,
                    tint = AppColors.Primary.copy(alpha = 0.7f),
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = franchise.name,
                    color = AppColors.TextColor.copy(alpha = 0.8f),
                    fontSize = 12.sp
                )
            }
        }
    }
}