package org.example.appbbmges.ui.usuarios.registation.studentsform

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
import org.example.appbbmges.ui.usuarios.AppColors
import org.jetbrains.compose.resources.painterResource

@Composable
fun AddAlumnoScreen(
    onDismiss: () -> Unit,
    repository: Repository
) {
    val state = rememberStudentFormState()

    val currentUserDetails = remember { repository.getCurrentUserWithDetails() }
    val isAdmin = currentUserDetails?.roleName?.lowercase() in listOf("admin", "administrador")

    val allFranchises = remember { repository.getAllFranchises() }
    val allRoles = remember { repository.getAllRoles() }

    var selectedFranchise by remember {
        mutableStateOf(if (isAdmin) "" else currentUserDetails?.franchiseName ?: "")
    }
    var selectedFranchiseId by remember {
        mutableStateOf(if (isAdmin) 0L else currentUserDetails?.franchiseId ?: 0L)
    }

    var selectedRole by remember { mutableStateOf("") }
    var selectedRoleId by remember { mutableStateOf<Long?>(null) }

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
                        text = "Registro de Alumno",
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
                        if (isAdmin) {
                            CustomDropdownField(
                                value = selectedFranchise,
                                onValueChange = { name ->
                                    selectedFranchise = name
                                    selectedFranchiseId = allFranchises.find { it.name == name }?.id ?: 0L
                                },
                                label = "Unidad",
                                options = allFranchises.map { it.name },
                                placeholder = "Selecciona la unidad",
                                modifier = Modifier.weight(1f)
                            )
                        } else {
                            CustomOutlinedTextField(
                                value = selectedFranchise,
                                onValueChange = {},
                                label = "Unidad",
                                readOnly = true,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        CustomDropdownField(
                            value = selectedRole,
                            onValueChange = { roleName ->
                                selectedRole = roleName
                                selectedRoleId = allRoles.find { it.name == roleName }?.id
                            },
                            label = "Rol",
                            options = allRoles.map { it.name },
                            placeholder = "Selecciona un rol",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    FormProgressIndicator(
                        currentStep = state.currentStep,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    when (state.currentStep) {
                        StudentFormStep.PERSONAL_INFO -> {
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

                        StudentFormStep.ADDRESS_INFO -> AddressInfoStep(
                            data = state.data,
                            errors = state.errors,
                            onDataChange = { state.updateData(it) }
                        )

                        StudentFormStep.ADDITIONAL_INFO -> AdditionalInfoStep(
                            data = state.data,
                            onDataChange = { state.updateData(it) }
                        )

                        StudentFormStep.CONFIRMATION -> ConfirmationStep(
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
                            if (state.currentStep == StudentFormStep.PERSONAL_INFO) {
                                val curp = state.data.curp.trim()

                                if (curp.isEmpty()) {
                                    state.errors = state.errors.copy(general = "El CURP es obligatorio para continuar.")
                                    return@FormNavigationButtons
                                }

                                val existingStudent = repository.getStudentByCurp(curp)
                                if (existingStudent != null) {
                                    state.errors = state.errors.copy(
                                        general = "Ya existe un alumno con el CURP: $curp"
                                    )
                                    return@FormNavigationButtons
                                }
                            }

                            state.nextStep()
                        },
                        onSubmit = {
                            if (state.validateCurrentStep()) {
                                val curp = state.data.curp.trim()
                                if (curp.isNotEmpty()) {
                                    val existingStudent = repository.getStudentByCurp(curp)
                                    if (existingStudent != null) {
                                        state.errors = state.errors.copy(
                                            general = "El CURP $curp ya está registrado."
                                        )
                                        return@FormNavigationButtons
                                    }
                                }

                                if (selectedRoleId == null) {
                                    state.errors = state.errors.copy(
                                        general = "Debes seleccionar un rol antes de registrar."
                                    )
                                    return@FormNavigationButtons
                                }

                                val username = state.data.username
                                val existingUser = repository.getUserByUsername(username)
                                if (existingUser != null) {
                                    state.errors = state.errors.copy(
                                        general = "El nombre de usuario ($username) ya existe. Cambia los datos personales."
                                    )
                                    return@FormNavigationButtons
                                }

                                try {
                                    repository.insertStudentWithUser(
                                        franchiseId = selectedFranchiseId,
                                        firstName = state.data.firstName,
                                        lastNamePaternal = state.data.lastNamePaternal.takeIf { it.isNotEmpty() },
                                        lastNameMaternal = state.data.lastNameMaternal.takeIf { it.isNotEmpty() },
                                        gender = state.data.gender.takeIf { it.isNotEmpty() },
                                        birthDate = state.data.birthDate.takeIf { it.isNotEmpty() },
                                        nationality = state.data.nationality.takeIf { it.isNotEmpty() },
                                        curp = curp,
                                        phone = state.data.phone.takeIf { it.isNotEmpty() },
                                        email = state.data.email.takeIf { it.isNotEmpty() },
                                        addressStreet = state.data.addressStreet.takeIf { it.isNotEmpty() },
                                        addressZip = state.data.addressZip.takeIf { it.isNotEmpty() },
                                        parentFatherFirstName = state.data.parentFatherName.takeIf { it.isNotEmpty() },
                                        parentFatherLastNamePaternal = null,
                                        parentFatherLastNameMaternal = null,
                                        parentMotherFirstName = state.data.parentMotherName.takeIf { it.isNotEmpty() },
                                        parentMotherLastNamePaternal = null,
                                        parentMotherLastNameMaternal = null,
                                        bloodType = state.data.bloodType.takeIf { it.isNotEmpty() },
                                        chronicDisease = state.data.chronicDisease.takeIf { it.isNotEmpty() },
                                        username = state.data.username,
                                        password = state.data.password,
                                        roleId = selectedRoleId ?: 0L,
                                        active = if (state.data.active) 1L else 0L
                                    )
                                    onDismiss()
                                } catch (e: Exception) {
                                    state.errors = state.errors.copy(
                                        general = "Error al guardar los datos: ${e.message}"
                                    )
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
