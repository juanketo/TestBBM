package org.example.appbbmges.ui.usuarios.registation.franquiciatarioform

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import appbbmges.composeapp.generated.resources.Res
import appbbmges.composeapp.generated.resources.logoSystem
import org.example.appbbmges.data.Repository
import org.example.appbbmges.ui.sessions.SessionManager
import org.example.appbbmges.ui.usuarios.AppColors
import org.example.appbbmges.ui.usuarios.registation.studentsform.CustomOutlinedTextField
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFranquiciatarioScreen(
    onDismiss: () -> Unit,
    repository: Repository
) {
    val state = rememberFranchiseeFormState()

    val permissionHelper = SessionManager.permissionHelper
    val currentUserId = SessionManager.userId ?: 0L
    val currentUserFranchiseId = SessionManager.franchiseId ?: 0L

    val canManageAllFranchises = permissionHelper?.can("FRANQUICIAS_VER") == true ||
            repository.isSuperAdmin(currentUserId)

    val availableFranchises = remember(canManageAllFranchises, currentUserFranchiseId) {
        if (canManageAllFranchises) {
            repository.getAllFranchises()
        } else {
            // Solo puede ver su propia franquicia
            listOfNotNull(repository.getFranchiseById(currentUserFranchiseId))
        }
    }

    val allRoles = remember { repository.getAllRoles() }

    var selectedFranchise by remember {
        mutableStateOf(
            if (canManageAllFranchises) {
                ""
            } else {
                availableFranchises.firstOrNull()?.name ?: ""
            }
        )
    }

    var selectedFranchiseId by remember {
        mutableStateOf(
            if (canManageAllFranchises) {
                0L
            } else {
                currentUserFranchiseId
            }
        )
    }

    var selectedRole by remember { mutableStateOf("") }
    var selectedRoleId by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(
        state.data.firstName,
        state.data.lastNamePaternal,
        state.data.lastNameMaternal,
        selectedFranchise
    ) {
        if (state.data.firstName.isNotBlank() &&
            state.data.lastNamePaternal.isNotBlank() &&
            state.data.lastNameMaternal.isNotBlank() &&
            selectedFranchise.isNotBlank()
        ) {
            state.generateCredentials(selectedFranchise)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.95f),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(4.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(Res.drawable.logoSystem),
                        contentDescription = "Logo",
                        modifier = Modifier.fillMaxSize(0.7f),
                        alpha = 0.1f,
                        contentScale = ContentScale.Fit
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "Registro de Franquiciatario",
                        style = MaterialTheme.typography.headlineSmall,
                        color = AppColors.TextColor,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            if (canManageAllFranchises) {
                                var expandedFranchise by remember { mutableStateOf(false) }

                                ExposedDropdownMenuBox(
                                    expanded = expandedFranchise,
                                    onExpandedChange = { expandedFranchise = it }
                                ) {
                                    OutlinedTextField(
                                        value = selectedFranchise,
                                        onValueChange = { },
                                        readOnly = true,
                                        label = { Text("Unidad") },
                                        placeholder = { Text("Selecciona la unidad") },
                                        trailingIcon = {
                                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedFranchise)
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = AppColors.Primary,
                                            unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
                                        ),
                                        shape = RoundedCornerShape(8.dp)
                                    )

                                    ExposedDropdownMenu(
                                        expanded = expandedFranchise,
                                        onDismissRequest = { expandedFranchise = false }
                                    ) {
                                        availableFranchises.forEach { franchise ->
                                            DropdownMenuItem(
                                                text = { Text(franchise.name) },
                                                onClick = {
                                                    selectedFranchise = franchise.name
                                                    selectedFranchiseId = franchise.id
                                                    expandedFranchise = false
                                                }
                                            )
                                        }
                                    }
                                }
                            } else {
                                CustomOutlinedTextField(
                                    value = selectedFranchise,
                                    onValueChange = {},
                                    label = "Unidad",
                                    readOnly = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        Box(modifier = Modifier.weight(1f)) {
                            var expandedRole by remember { mutableStateOf(false) }

                            ExposedDropdownMenuBox(
                                expanded = expandedRole,
                                onExpandedChange = { expandedRole = it }
                            ) {
                                OutlinedTextField(
                                    value = selectedRole,
                                    onValueChange = { },
                                    readOnly = true,
                                    label = { Text("Rol") },
                                    placeholder = { Text("Selecciona un rol") },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRole)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = AppColors.Primary,
                                        unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                )

                                ExposedDropdownMenu(
                                    expanded = expandedRole,
                                    onDismissRequest = { expandedRole = false }
                                ) {
                                    allRoles.forEach { role ->
                                        DropdownMenuItem(
                                            text = { Text(role.name) },
                                            onClick = {
                                                selectedRole = role.name
                                                selectedRoleId = role.id
                                                expandedRole = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    FormProgressIndicator(
                        currentStep = state.currentStep,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    when (state.currentStep) {
                        FranchiseeFormStep.PERSONAL_INFO -> {
                            if (state.errors.general != null) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.errorContainer
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = state.errors.general!!,
                                        color = MaterialTheme.colorScheme.onErrorContainer,
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }
                            }
                            PersonalInfoStep(
                                data = state.data,
                                errors = state.errors,
                                onDataChange = { state.updateData(it) }
                            )
                        }

                        FranchiseeFormStep.ADDRESS_INFO -> AddressInfoStep(
                            data = state.data,
                            errors = state.errors,
                            onDataChange = { state.updateData(it) }
                        )

                        FranchiseeFormStep.ADDITIONAL_INFO -> AdditionalInfoStep(
                            data = state.data,
                            onDataChange = { state.updateData(it) }
                        )

                        FranchiseeFormStep.CONFIRMATION -> ConfirmationStep(
                            data = state.data,
                            errors = state.errors
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    FormNavigationButtons(
                        currentStep = state.currentStep,
                        onCancel = onDismiss,
                        onPrevious = { state.previousStep() },
                        onNext = {
                            state.nextStep()
                        },
                        onSubmit = {

                            if (selectedFranchiseId == 0L) {
                                state.errors = state.errors.copy(
                                    general = "Debes seleccionar una unidad antes de registrar."
                                )
                                return@FormNavigationButtons
                            }

                            if (selectedRoleId == null) {
                                state.errors = state.errors.copy(
                                    general = "Debes seleccionar un rol antes de registrar."
                                )
                                return@FormNavigationButtons
                            }

                            if (state.validateCurrentStep()) {
                                val username = state.data.username
                                val existingUser = repository.getUserByUsername(username)
                                if (existingUser != null) {
                                    state.errors = state.errors.copy(
                                        general = "El nombre de usuario ($username) ya existe. Cambia los datos personales."
                                    )
                                    return@FormNavigationButtons
                                }

                                try {
                                    repository.insertFranchiseeWithUser(
                                        franchiseId = selectedFranchiseId,
                                        firstName = state.data.firstName,
                                        lastNamePaternal = state.data.lastNamePaternal.takeIf { it.isNotEmpty() },
                                        lastNameMaternal = state.data.lastNameMaternal.takeIf { it.isNotEmpty() },
                                        gender = state.data.gender.takeIf { it.isNotEmpty() },
                                        birthDate = state.data.birthDate.takeIf { it.isNotEmpty() },
                                        nationality = state.data.nationality.takeIf { it.isNotEmpty() },
                                        taxId = state.data.taxId.takeIf { it.isNotEmpty() },
                                        phone = state.data.phone.takeIf { it.isNotEmpty() },
                                        email = state.data.email.takeIf { it.isNotEmpty() },
                                        addressStreet = state.data.addressStreet.takeIf { it.isNotEmpty() },
                                        addressZip = state.data.addressZip.takeIf { it.isNotEmpty() },
                                        emergencyContactName = state.data.emergencyContactName.takeIf { it.isNotEmpty() },
                                        emergencyContactPhone = state.data.emergencyContactPhone.takeIf { it.isNotEmpty() },
                                        startDate = state.data.startDate.takeIf { it.isNotEmpty() },
                                        username = state.data.username,
                                        password = state.data.password,
                                        roleId = selectedRoleId ?: 0L,
                                        active = if (state.data.active) 1L else 0L
                                    )

                                    println("✓ Franquiciatario registrado exitosamente")
                                    println("  - Usuario: ${state.data.username}")
                                    println("  - Franquicia: $selectedFranchise (ID: $selectedFranchiseId)")
                                    println("  - Rol ID: $selectedRoleId")

                                    onDismiss()
                                } catch (e: Exception) {
                                    state.errors = state.errors.copy(
                                        general = "Error al guardar los datos: ${e.message}"
                                    )
                                    println("✗ Error al registrar franquiciatario: ${e.message}")
                                }
                            } else {
                                state.errors = state.errors.copy(
                                    general = "Revisa el formulario. Hay campos obligatorios sin completar."
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    )
                }
            }
        }
    }
}