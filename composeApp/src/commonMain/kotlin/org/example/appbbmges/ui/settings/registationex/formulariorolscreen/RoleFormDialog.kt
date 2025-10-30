package org.example.appbbmges.ui.settings.registationex.formulariorolscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import org.example.appbbmges.data.Repository
import org.example.appbbmges.RoleEntity
import org.example.appbbmges.ui.usuarios.AppColors
import org.jetbrains.compose.resources.painterResource
import appbbmges.composeapp.generated.resources.Res
import appbbmges.composeapp.generated.resources.logoSystem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleFormDialog(
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
                roleName, existingRoles, selectedPermissions, editingRole?.id
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
                            val createdRole = repository.getAllRoles()
                                .find { it.name.equals(roleName.trim(), ignoreCase = true) }
                            createdRole?.let { role ->
                                selectedPermissions.forEach { permission ->
                                    repository.insertRolePermission(role.id, permission)
                                }
                            }
                        }
                        formState = RoleFormState.Success
                        onDismiss()
                    } catch (e: Exception) {
                        formState = RoleFormState.Error(
                            "Error al ${if (isEditing) "actualizar" else "guardar"} rol: ${e.message}"
                        )
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(AppColors.Background),
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
                    modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState())
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
                            RolePermissionsFormContent(
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
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource(Res.drawable.logoSystem),
            contentDescription = null,
            modifier = Modifier.fillMaxSize().padding(40.dp).alpha(0.08f),
            contentScale = ContentScale.Fit
        )
    }
}