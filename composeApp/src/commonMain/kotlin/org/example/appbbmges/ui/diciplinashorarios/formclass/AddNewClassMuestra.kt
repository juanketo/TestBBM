package org.example.appbbmges.ui.diciplinashorarios.formclass

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.example.appbbmges.data.Repository
import org.example.appbbmges.ui.usuarios.AppColors
import org.example.appbbmges.ClassroomEntity
import org.example.appbbmges.DisciplineSelectAll
import org.example.appbbmges.TeacherEntity
import appbbmges.composeapp.generated.resources.Res
import appbbmges.composeapp.generated.resources.logoSystem
import org.jetbrains.compose.resources.painterResource

enum class TrialClassStep {
    INFO,
    CONFIRMATION
}

sealed class TrialClassState {
    object Idle : TrialClassState()
    object Loading : TrialClassState()
    object Success : TrialClassState()
    data class Error(val message: String) : TrialClassState()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNewTrialClassScreen(
    onDismiss: () -> Unit,
    repository: Repository,
    franchiseId: Long,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()

    var currentStep by remember { mutableStateOf(TrialClassStep.INFO) }
    var state by remember { mutableStateOf<TrialClassState>(TrialClassState.Idle) }

    // Datos del alumno
    var studentFirstName by remember { mutableStateOf("") }
    var studentLastNamePaternal by remember { mutableStateOf("") }
    var studentLastNameMaternal by remember { mutableStateOf("") }
    var ageYears by remember { mutableStateOf("") }
    var ageMonths by remember { mutableStateOf("") }

    // Datos del adulto
    var adultFirstName by remember { mutableStateOf("") }
    var adultLastNamePaternal by remember { mutableStateOf("") }
    var adultLastNameMaternal by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    // Datos de clase
    var selectedDiscipline by remember { mutableStateOf<DisciplineSelectAll?>(null) }
    var selectedTeacher by remember { mutableStateOf<TeacherEntity?>(null) }
    var selectedClassroom by remember { mutableStateOf<ClassroomEntity?>(null) }
    var requestDate by remember { mutableStateOf("") }
    var scheduledDate by remember { mutableStateOf("") }
    var scheduledTime by remember { mutableStateOf("") }

    // Dropdowns
    var expandedDiscipline by remember { mutableStateOf(false) }
    var expandedTeacher by remember { mutableStateOf(false) }
    var expandedClassroom by remember { mutableStateOf(false) }

    val disciplines = remember { mutableStateOf<List<DisciplineSelectAll>>(emptyList()) }
    val teachers = remember { mutableStateOf<List<TeacherEntity>>(emptyList()) }
    val classrooms = remember { mutableStateOf<List<ClassroomEntity>>(emptyList()) }

    // Cargar datos
    LaunchedEffect(Unit) {
        disciplines.value = repository.getAllDisciplines()
        teachers.value = repository.getAllTeachers()
        classrooms.value = repository.getClassroomsByFranchiseId(franchiseId)
    }

    fun validateForm(): Boolean {
        return studentFirstName.isNotBlank() &&
                ageYears.toIntOrNull() != null &&
                selectedDiscipline != null &&
                adultFirstName.isNotBlank() &&
                phone.isNotBlank()
    }

    fun proceedToNext() {
        when (currentStep) {
            TrialClassStep.INFO -> {
                if (validateForm()) {
                    currentStep = TrialClassStep.CONFIRMATION
                }
            }

            TrialClassStep.CONFIRMATION -> {
                state = TrialClassState.Loading
                coroutineScope.launch {
                    try {
                        delay(1000)

                        repository.insertTrialClass(
                            franchiseId = franchiseId,
                            studentId = null,
                            adultFirstName = adultFirstName,
                            adultLastNamePaternal = adultLastNamePaternal.ifBlank { null },
                            adultLastNameMaternal = adultLastNameMaternal.ifBlank { null },
                            phone = phone,
                            email = email.ifBlank { null },
                            studentFirstName = studentFirstName,
                            studentLastNamePaternal = studentLastNamePaternal.ifBlank { null },
                            studentLastNameMaternal = studentLastNameMaternal.ifBlank { null },
                            ageYears = ageYears.toLong(),
                            ageMonths = ageMonths.toLongOrNull() ?: 0L,
                            disciplineId = selectedDiscipline!!.id,
                            requestDate = requestDate.ifBlank { "2025-01-01" },
                            scheduledDate = scheduledDate.ifBlank { null },
                            scheduledTime = scheduledTime.ifBlank { null },
                            classroomId = selectedClassroom?.id,
                            teacherId = selectedTeacher?.id,
                            attendance = null,
                            cancellationReason = null,
                            howDiscovered = null
                        )

                        state = TrialClassState.Success
                        onDismiss()
                    } catch (e: Exception) {
                        state = TrialClassState.Error("Error al guardar: ${e.message}")
                    }
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppColors.Background),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .widthIn(min = 400.dp, max = 500.dp)
                .heightIn(max = 720.dp)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(4.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                TrialClassBackgroundLogo()

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    val progress by animateFloatAsState(
                        targetValue = when (currentStep) {
                            TrialClassStep.INFO -> 0.5f
                            TrialClassStep.CONFIRMATION -> 1f
                        },
                        animationSpec = tween(300),
                        label = "progress"
                    )

                    Text(
                        text = "Registrar Clase Muestra",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth(),
                        color = AppColors.Primary
                    )

                    Spacer(Modifier.height(16.dp))

                    when (currentStep) {
                        TrialClassStep.INFO -> {
                            Text("Datos del Alumno", fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(8.dp))

                            OutlinedTextField(
                                value = studentFirstName,
                                onValueChange = { studentFirstName = it },
                                label = { Text("Nombre del alumno") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            Spacer(Modifier.height(8.dp))
                            OutlinedTextField(
                                value = studentLastNamePaternal,
                                onValueChange = { studentLastNamePaternal = it },
                                label = { Text("Apellido paterno") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            Spacer(Modifier.height(8.dp))
                            OutlinedTextField(
                                value = studentLastNameMaternal,
                                onValueChange = { studentLastNameMaternal = it },
                                label = { Text("Apellido materno") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            Spacer(Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = ageYears,
                                    onValueChange = { value ->
                                        if (value.all { char -> char.isDigit() } || value.isEmpty()) {
                                            ageYears = value
                                        }
                                    },
                                    label = { Text("Años") },
                                    modifier = Modifier.weight(1f),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                )
                                OutlinedTextField(
                                    value = ageMonths,
                                    onValueChange = { value ->
                                        if (value.all { char -> char.isDigit() } || value.isEmpty()) {
                                            ageMonths = value
                                        }
                                    },
                                    label = { Text("Meses") },
                                    modifier = Modifier.weight(1f),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                )
                            }

                            Spacer(Modifier.height(16.dp))
                            Text("Datos del Adulto Responsable", fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(8.dp))

                            OutlinedTextField(
                                value = adultFirstName,
                                onValueChange = { adultFirstName = it },
                                label = { Text("Nombre del adulto") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            Spacer(Modifier.height(8.dp))
                            OutlinedTextField(
                                value = adultLastNamePaternal,
                                onValueChange = { adultLastNamePaternal = it },
                                label = { Text("Apellido paterno") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            Spacer(Modifier.height(8.dp))
                            OutlinedTextField(
                                value = phone,
                                onValueChange = { phone = it },
                                label = { Text("Teléfono") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                            )

                            Spacer(Modifier.height(8.dp))
                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = { Text("Correo (opcional)") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                            )

                            Spacer(Modifier.height(16.dp))
                            Text("Datos de la Clase", fontWeight = FontWeight.Bold)

                            Spacer(Modifier.height(8.dp))
                            TrialDropdownField(
                                label = "Disciplina",
                                value = selectedDiscipline?.name ?: "Seleccione disciplina",
                                expanded = expandedDiscipline,
                                onExpandedChange = { expandedDiscipline = it },
                                items = disciplines.value.map { it.name },
                                onItemSelected = { name ->
                                    selectedDiscipline = disciplines.value.first { it.name == name }
                                }
                            )

                            Spacer(Modifier.height(8.dp))
                            TrialDropdownField(
                                label = "Profesor",
                                value = selectedTeacher?.first_name ?: "Seleccione profesor",
                                expanded = expandedTeacher,
                                onExpandedChange = { expandedTeacher = it },
                                items = teachers.value.map { it.first_name },
                                onItemSelected = { name ->
                                    selectedTeacher = teachers.value.first { it.first_name == name }
                                }
                            )

                            Spacer(Modifier.height(8.dp))
                            TrialDropdownField(
                                label = "Salón",
                                value = selectedClassroom?.name ?: "Seleccione salón",
                                expanded = expandedClassroom,
                                onExpandedChange = { expandedClassroom = it },
                                items = classrooms.value.map { it.name },
                                onItemSelected = { name ->
                                    selectedClassroom = classrooms.value.first { it.name == name }
                                }
                            )

                            Spacer(Modifier.height(8.dp))
                            OutlinedTextField(
                                value = scheduledDate,
                                onValueChange = { scheduledDate = it },
                                label = { Text("Fecha programada (YYYY-MM-DD)") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            Spacer(Modifier.height(8.dp))
                            OutlinedTextField(
                                value = scheduledTime,
                                onValueChange = { scheduledTime = it },
                                label = { Text("Hora programada (HH:mm)") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        }

                        TrialClassStep.CONFIRMATION -> {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text("Confirmar Datos", fontWeight = FontWeight.Bold)
                                    HorizontalDivider(color = AppColors.Primary.copy(alpha = 0.3f))
                                    TrialConfirmationRow("Alumno:", studentFirstName)
                                    TrialConfirmationRow("Edad:", "$ageYears años $ageMonths meses")
                                    TrialConfirmationRow("Adulto:", adultFirstName)
                                    TrialConfirmationRow("Teléfono:", phone)
                                    TrialConfirmationRow("Disciplina:", selectedDiscipline?.name ?: "")
                                    TrialConfirmationRow("Profesor:", selectedTeacher?.first_name ?: "")
                                    TrialConfirmationRow("Salón:", selectedClassroom?.name ?: "")
                                    TrialConfirmationRow("Fecha:", scheduledDate)
                                    TrialConfirmationRow("Hora:", scheduledTime)
                                }
                            }

                            when (state) {
                                is TrialClassState.Loading -> {
                                    Spacer(Modifier.height(16.dp))
                                    CircularProgressIndicator(
                                        modifier = Modifier.align(Alignment.CenterHorizontally),
                                        color = AppColors.Primary
                                    )
                                }
                                is TrialClassState.Error -> {
                                    Text(
                                        text = (state as TrialClassState.Error).message,
                                        color = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.padding(top = 8.dp)
                                    )
                                }
                                else -> {}
                            }
                        }
                    }

                    Spacer(Modifier.height(20.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFff8abe))
                        ) { Text("Cancelar") }

                        if (currentStep == TrialClassStep.CONFIRMATION) {
                            Button(
                                onClick = { currentStep = TrialClassStep.INFO },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFff8abe))
                            ) { Text("Atrás") }
                        }

                        Button(
                            onClick = { proceedToNext() },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary)
                        ) {
                            Text(
                                when (currentStep) {
                                    TrialClassStep.INFO -> "Siguiente"
                                    TrialClassStep.CONFIRMATION -> "Guardar"
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TrialDropdownField(
    label: String,
    value: String,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    items: List<String>,
    onItemSelected: (String) -> Unit
) {
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = onExpandedChange) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { onExpandedChange(false) }) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item) },
                    onClick = {
                        onItemSelected(item)
                        onExpandedChange(false)
                    }
                )
            }
        }
    }
}

@Composable
private fun TrialClassBackgroundLogo() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource(Res.drawable.logoSystem),
            contentDescription = null,
            modifier = Modifier.fillMaxSize().padding(40.dp).alpha(0.08f),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
private fun TrialConfirmationRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontWeight = FontWeight.Bold)
        Text(value)
    }
}