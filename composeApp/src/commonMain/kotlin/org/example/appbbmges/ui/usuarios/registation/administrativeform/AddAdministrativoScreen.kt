package org.example.appbbmges.ui.usuarios.registation.administrativeform

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
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAdministrativoScreen(
    onDismiss: () -> Unit,
    repository: Repository
) {
    val state = rememberAdministrativeFormState()

    val permissionHelper = SessionManager.permissionHelper
    val currentUserId = SessionManager.userId ?: 0L

    // Verificar si es Super Admin o tiene permisos para administrar corporativos
    val isSuperAdmin = repository.isSuperAdmin(currentUserId)
    val canManageAdministratives = isSuperAdmin ||
            permissionHelper?.can("ADMINISTRATIVOS_CREAR") == true

    // Sede Principal (siempre fija)
    val sedeNombre = AdministrativeFormConstants.SEDE_PRINCIPAL_NAME
    val sedeId = AdministrativeFormConstants.SEDE_PRINCIPAL_ID

    // Áreas corporativas disponibles
    val corporateAreas = AdministrativeFormConstants.corporateAreaOptions

    // Obtener TODOS los roles disponibles de la BD
    val allRoles = remember { repository.getAllRoles() }

    // Estados para los dropdowns
    var selectedArea by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("") }
    var selectedRoleId by remember { mutableStateOf<Long?>(null) }

    // Generar credenciales automáticamente
    LaunchedEffect(
        state.data.firstName,
        state.data.lastNamePaternal,
        state.data.lastNameMaternal
    ) {
        if (state.data.firstName.isNotBlank() &&
            state.data.lastNamePaternal.isNotBlank() &&
            state.data.lastNameMaternal.isNotBlank()
        ) {
            state.generateCredentials()
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

                // Logo de fondo
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
                        text = "Registro de Personal Corporativo",
                        style = MaterialTheme.typography.headlineSmall,
                        color = AppColors.TextColor,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    // Mostrar restricción si no tiene permisos
                    if (!canManageAdministratives) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFFFEBEE)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "⚠️ No tienes permisos para agregar personal corporativo",
                                color = Color(0xFFC62828),
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }

                    // Fila: Sede Principal, Área Corporativa y Rol
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // 1. Sede Principal (bloqueado)
                        OutlinedTextField(
                            value = sedeNombre,
                            onValueChange = {},
                            label = { Text("Sede") },
                            readOnly = true,
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AppColors.Primary,
                                unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                                disabledBorderColor = Color.Gray.copy(alpha = 0.3f),
                                disabledTextColor = Color.Gray
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )

                        // 2. Área Corporativa
                        Box(modifier = Modifier.weight(1f)) {
                            var expandedArea by remember { mutableStateOf(false) }

                            ExposedDropdownMenuBox(
                                expanded = expandedArea,
                                onExpandedChange = { expandedArea = it }
                            ) {
                                OutlinedTextField(
                                    value = selectedArea,
                                    onValueChange = { },
                                    readOnly = true,
                                    label = { Text("Área Corporativa") },
                                    placeholder = { Text("Selecciona el área") },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedArea)
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
                                    expanded = expandedArea,
                                    onDismissRequest = { expandedArea = false }
                                ) {
                                    corporateAreas.forEach { area ->
                                        DropdownMenuItem(
                                            text = { Text(area) },
                                            onClick = {
                                                selectedArea = area
                                                expandedArea = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // 3. Rol
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
                                    if (allRoles.isEmpty()) {
                                        DropdownMenuItem(
                                            text = { Text("No hay roles disponibles", color = Color.Gray) },
                                            onClick = { }
                                        )
                                    } else {
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
                    }

                    FormProgressIndicator(
                        currentStep = state.currentStep,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Renderizar pasos del formulario
                    when (state.currentStep) {
                        AdministrativeFormStep.PERSONAL_INFO -> {
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

                        AdministrativeFormStep.PROFESSIONAL_INFO -> ProfessionalInfoStep(
                            data = state.data,
                            errors = state.errors,
                            onDataChange = { state.updateData(it) }
                        )

                        AdministrativeFormStep.ADDRESS_INFO -> AddressInfoStep(
                            data = state.data,
                            errors = state.errors,
                            onDataChange = { state.updateData(it) }
                        )

                        AdministrativeFormStep.ADDITIONAL_INFO -> AdditionalInfoStep(
                            data = state.data,
                            onDataChange = { state.updateData(it) }
                        )

                        AdministrativeFormStep.CONFIRMATION -> ConfirmationStep(
                            data = state.data,
                            errors = state.errors,
                            selectedArea = selectedArea
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Botones de navegación
                    FormNavigationButtons(
                        currentStep = state.currentStep,
                        onCancel = onDismiss,
                        onPrevious = { state.previousStep() },
                        onNext = {
                            state.nextStep()
                        },
                        onSubmit = {
                            // Validaciones antes de guardar
                            if (selectedArea.isEmpty()) {
                                state.errors = state.errors.copy(
                                    general = "Debes seleccionar un área corporativa antes de registrar."
                                )
                                return@FormNavigationButtons
                            }

                            if (selectedRoleId == null) {
                                state.errors = state.errors.copy(
                                    general = "Debes seleccionar un rol antes de registrar."
                                )
                                return@FormNavigationButtons
                            }

                            if (!canManageAdministratives) {
                                state.errors = state.errors.copy(
                                    general = "No tienes permisos para realizar esta acción."
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
                                    val salaryValue = state.data.salary.toDoubleOrNull() ?: 0.0

                                    repository.insertAdministrative(
                                        franchiseId = sedeId,
                                        firstName = state.data.firstName,
                                        lastNamePaternal = state.data.lastNamePaternal.takeIf { it.isNotEmpty() },
                                        lastNameMaternal = state.data.lastNameMaternal.takeIf { it.isNotEmpty() },
                                        gender = state.data.gender.takeIf { it.isNotEmpty() },
                                        birthDate = state.data.birthDate.takeIf { it.isNotEmpty() },
                                        nationality = state.data.nationality.takeIf { it.isNotEmpty() },
                                        taxId = state.data.taxId.takeIf { it.isNotEmpty() },
                                        nss = state.data.nss.takeIf { it.isNotEmpty() },
                                        phone = state.data.phone.takeIf { it.isNotEmpty() },
                                        email = state.data.email.takeIf { it.isNotEmpty() },
                                        addressStreet = state.data.addressStreet.takeIf { it.isNotEmpty() },
                                        addressZip = state.data.addressZip.takeIf { it.isNotEmpty() },
                                        emergencyContactName = state.data.emergencyContactName.takeIf { it.isNotEmpty() },
                                        emergencyContactPhone = state.data.emergencyContactPhone.takeIf { it.isNotEmpty() },
                                        position = selectedArea,
                                        salary = salaryValue,
                                        startDate = state.data.startDate.takeIf { it.isNotEmpty() } ?: "",
                                        active = if (state.data.active) 1L else 0L
                                    )

                                    println("✓ Personal Corporativo registrado exitosamente")
                                    println("  - Usuario: ${state.data.username}")
                                    println("  - Sede: $sedeNombre (ID: $sedeId)")
                                    println("  - Área Corporativa: $selectedArea")
                                    println("  - Rol: $selectedRole (ID: $selectedRoleId)")
                                    println("  - Salario: $$salaryValue")
                                    println("  - Avatar ID: ${state.data.avatarId}")

                                    onDismiss()
                                } catch (e: Exception) {
                                    state.errors = state.errors.copy(
                                        general = "Error al guardar los datos: ${e.message}"
                                    )
                                    println("✗ Error al registrar personal corporativo: ${e.message}")
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