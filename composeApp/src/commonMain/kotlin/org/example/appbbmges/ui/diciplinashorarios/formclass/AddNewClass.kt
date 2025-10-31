package org.example.appbbmges.ui.diciplinashorarios.formclass

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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

enum class NormalClassStep {
    INFO,
    CONFIRMATION
}

sealed class NormalClassState {
    object Idle : NormalClassState()
    object Loading : NormalClassState()
    object Success : NormalClassState()
    data class Error(val message: String) : NormalClassState()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNewClassScreen(
    onDismiss: () -> Unit,
    repository: Repository,
    franchiseId: Long,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()

    // Paso actual
    var currentStep by remember { mutableStateOf(NormalClassStep.INFO) }
    var state by remember { mutableStateOf<NormalClassState>(NormalClassState.Idle) }

    // Campos del formulario
    var selectedClassroom by remember { mutableStateOf<ClassroomEntity?>(null) }
    var selectedTeacher by remember { mutableStateOf<TeacherEntity?>(null) }
    var selectedDiscipline by remember { mutableStateOf<DisciplineSelectAll?>(null) }
    var selectedDayOfWeek by remember { mutableStateOf("Lunes") }
    var startTime by remember { mutableStateOf("") }
    var endTime by remember { mutableStateOf("") }

    // Listas de datos
    val classrooms = remember { mutableStateOf<List<ClassroomEntity>>(emptyList()) }
    val teachers = remember { mutableStateOf<List<TeacherEntity>>(emptyList()) }
    val disciplines = remember { mutableStateOf<List<DisciplineSelectAll>>(emptyList()) }

    // Cargar datos iniciales
    LaunchedEffect(Unit) {
        classrooms.value = repository.getClassroomsByFranchiseId(franchiseId)
        teachers.value = repository.getAllTeachers()
        disciplines.value = repository.getAllDisciplines()
    }

    // Días disponibles
    val daysOfWeek = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado")

    // Estados dropdown
    var expandedClassroom by remember { mutableStateOf(false) }
    var expandedTeacher by remember { mutableStateOf(false) }
    var expandedDiscipline by remember { mutableStateOf(false) }
    var expandedDay by remember { mutableStateOf(false) }

    // Validación
    fun validateForm(): Boolean {
        return selectedClassroom != null &&
                selectedTeacher != null &&
                selectedDiscipline != null &&
                startTime.isNotBlank() &&
                endTime.isNotBlank()
    }

    fun proceedToNext() {
        when (currentStep) {
            NormalClassStep.INFO -> {
                if (validateForm()) currentStep = NormalClassStep.CONFIRMATION
            }
            NormalClassStep.CONFIRMATION -> {
                state = NormalClassState.Loading
                coroutineScope.launch {
                    try {
                        // Simulación de guardado
                        delay(800)

                        repository.insertSchedule(
                            franchiseId = franchiseId,
                            classroomId = selectedClassroom!!.id,
                            teacherId = selectedTeacher!!.id,
                            disciplineId = selectedDiscipline!!.id,
                            dayOfWeek = daysOfWeek.indexOf(selectedDayOfWeek) + 1L,
                            startTime = startTime,
                            endTime = endTime
                        )

                        state = NormalClassState.Success
                        onDismiss()
                    } catch (e: Exception) {
                        state = NormalClassState.Error("Error al guardar: ${e.message}")
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
                .heightIn(max = 700.dp)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(4.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                BackgroundLogo()

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    val progress by animateFloatAsState(
                        targetValue = when (currentStep) {
                            NormalClassStep.INFO -> 0.5f
                            NormalClassStep.CONFIRMATION -> 1f
                        },
                        animationSpec = tween(300),
                        label = "progress"
                    )

                    Text(
                        text = "Registrar Nueva Clase",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth(),
                        color = AppColors.Primary
                    )

                    Spacer(Modifier.height(12.dp))

                    when (currentStep) {
                        NormalClassStep.INFO -> {
                            DropdownField(
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

                            DropdownField(
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

                            DropdownField(
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

                            DropdownField(
                                label = "Día de la semana",
                                value = selectedDayOfWeek,
                                expanded = expandedDay,
                                onExpandedChange = { expandedDay = it },
                                items = daysOfWeek,
                                onItemSelected = { selectedDayOfWeek = it }
                            )

                            Spacer(Modifier.height(12.dp))

                            OutlinedTextField(
                                value = startTime,
                                onValueChange = { startTime = it },
                                label = { Text("Hora inicio (HH:mm)") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                keyboardActions = KeyboardActions(
                                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                                )
                            )

                            Spacer(Modifier.height(8.dp))

                            OutlinedTextField(
                                value = endTime,
                                onValueChange = { endTime = it },
                                label = { Text("Hora fin (HH:mm)") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
                            )
                        }

                        NormalClassStep.CONFIRMATION -> {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        "Confirmar Datos",
                                        fontWeight = FontWeight.Bold
                                    )
                                    HorizontalDivider(
                                        modifier = Modifier,
                                        thickness = DividerDefaults.Thickness,
                                        color = AppColors.Primary.copy(alpha = 0.3f)
                                    )
                                    ConfirmationRow("Salón:", selectedClassroom?.name ?: "")
                                    ConfirmationRow("Profesor:", selectedTeacher?.first_name ?: "")
                                    ConfirmationRow("Disciplina:", selectedDiscipline?.name ?: "")
                                    ConfirmationRow("Día:", selectedDayOfWeek)
                                    ConfirmationRow("Inicio:", startTime)
                                    ConfirmationRow("Fin:", endTime)
                                }
                            }

                            when (state) {
                                is NormalClassState.Loading -> {
                                    Spacer(Modifier.height(16.dp))
                                    CircularProgressIndicator(
                                        modifier = Modifier.align(Alignment.CenterHorizontally),
                                        color = AppColors.Primary
                                    )
                                }
                                is NormalClassState.Error -> {
                                    Text(
                                        text = (state as NormalClassState.Error).message,
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

                        if (currentStep == NormalClassStep.CONFIRMATION) {
                            Button(
                                onClick = { currentStep = NormalClassStep.INFO },
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
                                    NormalClassStep.INFO -> "Siguiente"
                                    NormalClassStep.CONFIRMATION -> "Guardar"
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
fun DropdownField(
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

@Composable
private fun ConfirmationRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontWeight = FontWeight.Bold)
        Text(value)
    }
}