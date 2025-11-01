package org.example.appbbmges.ui.diciplinashorarios

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.appbbmges.navigation.SimpleNavController
import kotlinx.datetime.*
import org.example.appbbmges.data.Repository
import org.example.appbbmges.ui.diciplinashorarios.formclass.AddNewTrialClassScreen
import org.example.appbbmges.ui.diciplinashorarios.formclass.AddNewClassScreen

// --- Data Definitions ---

data class CalendarDay(
    val dayName: String,
    val dayNumber: Int,
    val month: Int,
    val year: Int,
    val date: LocalDate
)

data class TimeSlot(
    val hour: Int,
    val minute: Int,
    val displayTime: String
)

data class ClassSchedule(
    val className: String,
    val teacher: String,
    val schedule: String,
    val location: String,
    val classType: String,
    val roomNumber: Int,
    val day: CalendarDay,
    val timeSlot: TimeSlot,
    val discipline: String
)

data class Discipline(
    val name: String,
    val color: Color,
    val isEnabled: Boolean = true
)

sealed class DisciplinasHorariosScreenState {
    object Calendar : DisciplinasHorariosScreenState()
    object AddClassMuestra : DisciplinasHorariosScreenState()
    object AddNewClass : DisciplinasHorariosScreenState()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisciplinasHorariosScreen(navController: SimpleNavController, repository: Repository) {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    var currentWeek by remember { mutableStateOf(getCurrentWeekForDate(today)) }

    val currentUser = repository.getCurrentUser()
    val franchiseId = currentUser?.franchise_id ?: 1L

    val availableDisciplines = remember {
        listOf(
            Discipline("Danza Aérea", Color(0xFF4CAF50)),
            Discipline("Ballet", Color(0xFFBA68C8)),
            Discipline("K-Pop", Color(0xFFFF7043)),
            Discipline("Mexidanza", Color(0xFF26C6DA))
        )
    }

    val scheduledClasses = remember(currentWeek, franchiseId) {
        mutableStateOf(loadClassesFromRepository(repository, franchiseId, currentWeek))
    }

    var currentScreenState by remember { mutableStateOf<DisciplinasHorariosScreenState>(DisciplinasHorariosScreenState.Calendar) }

    val onDismissClassMuestra: () -> Unit = {
        currentScreenState = DisciplinasHorariosScreenState.Calendar
        scheduledClasses.value = loadClassesFromRepository(repository, franchiseId, currentWeek)
    }

    val onDismissNewClass: () -> Unit = {
        currentScreenState = DisciplinasHorariosScreenState.Calendar
        scheduledClasses.value = loadClassesFromRepository(repository, franchiseId, currentWeek)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (currentScreenState) {
            is DisciplinasHorariosScreenState.Calendar -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(16.dp)
                ) {
                    // Header
                    Text(
                        text = "Horarios y Disciplinas",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Disciplinas disponibles
                    DisciplinesRow(
                        disciplines = availableDisciplines,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Action Buttons
                    ActionButtonsRow(
                        onNewClassClick = { currentScreenState = DisciplinasHorariosScreenState.AddNewClass },
                        onClassMuestraClick = { currentScreenState = DisciplinasHorariosScreenState.AddClassMuestra },
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // NUEVO: Vista tipo Trello/Kanban
                    TrelloStyleScheduleView(
                        weekDays = currentWeek,
                        scheduledClasses = scheduledClasses.value
                    )
                }
            }
            is DisciplinasHorariosScreenState.AddClassMuestra -> {
                AddNewTrialClassScreen(
                    onDismiss = onDismissClassMuestra,
                    repository = repository,
                    franchiseId = franchiseId,
                    modifier = Modifier.fillMaxSize()
                )
            }
            is DisciplinasHorariosScreenState.AddNewClass -> {
                AddNewClassScreen(
                    onDismiss = onDismissNewClass,
                    repository = repository,
                    franchiseId = franchiseId,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

// NUEVO: Vista principal estilo Trello
@Composable
fun TrelloStyleScheduleView(
    weekDays: List<CalendarDay>,
    scheduledClasses: List<ClassSchedule>
) {
    // Solo días laborales (Lunes a Sábado)
    val workDays = weekDays.take(6)

    LazyRow(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(workDays) { day ->
            DayColumn(
                day = day,
                classes = scheduledClasses.filter { it.day.date == day.date }
            )
        }
    }
}

// NUEVO: Columna de un día (como una lista de Trello)
@Composable
fun DayColumn(
    day: CalendarDay,
    classes: List<ClassSchedule>
) {
    Card(
        modifier = Modifier
            .width(280.dp)
            .fillMaxHeight(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2C2C2C) // Fondo oscuro como en tu mockup
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            // Header del día
            Text(
                text = day.dayName.lowercase(),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                ),
                color = Color.White,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Lista de clases (tarjetas)
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(classes) { classSchedule ->
                    TrelloClassCard(classSchedule = classSchedule)
                }
            }
        }
    }
}

// NUEVO: Tarjeta de clase estilo Trello
@Composable
fun TrelloClassCard(
    classSchedule: ClassSchedule
) {
    val cardColor = when (classSchedule.discipline) {
        "Danza Aérea" -> Color(0xFF29B6F6) // Azul claro
        "Ballet" -> Color(0xFFBA68C8) // Morado
        "K-Pop" -> Color(0xFFFF7043) // Naranja
        "Mexidanza" -> Color(0xFF26C6DA) // Cyan
        else -> Color(0xFF757575)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Barra lateral con número de salón
            Box(
                modifier = Modifier
                    .width(60.dp)
                    .height(120.dp)
                    .background(
                        color = cardColor,
                        shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = classSchedule.roomNumber.toString(),
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 40.sp
                    ),
                    color = Color.White
                )
            }

            // Contenido de la tarjeta
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Título de la disciplina
                Text(
                    text = classSchedule.discipline.uppercase(),
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    ),
                    color = Color.Black
                )

                // Maestra
                Row {
                    Text(
                        text = "Maestra: ",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.Black
                    )
                    Text(
                        text = classSchedule.teacher,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Black
                    )
                }

                // Horario
                Row {
                    Text(
                        text = "Horario: ",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.Black
                    )
                }
                Text(
                    text = classSchedule.schedule,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Black
                )

                // Sucursal
                Row {
                    Text(
                        text = "Sucursal: ",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.Black
                    )
                }
                Text(
                    text = classSchedule.location,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun DisciplinesRow(
    disciplines: List<Discipline>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        disciplines.forEach { discipline ->
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(
                    containerColor = discipline.color
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = discipline.name,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun ActionButtonsRow(
    onNewClassClick: () -> Unit,
    onClassMuestraClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
    ) {
        Button(
            onClick = onNewClassClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFE91E63)
            ),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.height(40.dp)
        ) {
            Text(
                text = "NUEVA CLASE",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
        }

        Button(
            onClick = onClassMuestraClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFE91E63)
            ),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.height(40.dp)
        ) {
            Text(
                text = "CLASE MUESTRA",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
        }
    }
}

// --- Helper Functions ---

fun getCurrentWeekForDate(referenceDate: LocalDate): List<CalendarDay> {
    val currentDayOfWeek = referenceDate.dayOfWeek.ordinal
    val startOfWeek = referenceDate.minus(currentDayOfWeek, DateTimeUnit.DAY)

    return (0..6).map { dayOffset ->
        val date = startOfWeek.plus(dayOffset, DateTimeUnit.DAY)
        CalendarDay(
            dayName = getDayNameInSpanish(date.dayOfWeek),
            dayNumber = date.dayOfMonth,
            month = date.monthNumber,
            year = date.year,
            date = date
        )
    }
}

fun getDayNameInSpanish(dayOfWeek: DayOfWeek): String {
    return when (dayOfWeek) {
        DayOfWeek.MONDAY -> "Lunes"
        DayOfWeek.TUESDAY -> "Martes"
        DayOfWeek.WEDNESDAY -> "Miércoles"
        DayOfWeek.THURSDAY -> "Jueves"
        DayOfWeek.FRIDAY -> "Viernes"
        DayOfWeek.SATURDAY -> "Sábado"
        DayOfWeek.SUNDAY -> "Domingo"
        else -> ""
    }
}

fun getMonthName(monthNumber: Int): String {
    return when (monthNumber) {
        1 -> "Enero"
        2 -> "Febrero"
        3 -> "Marzo"
        4 -> "Abril"
        5 -> "Mayo"
        6 -> "Junio"
        7 -> "Julio"
        8 -> "Agosto"
        9 -> "Septiembre"
        10 -> "Octubre"
        11 -> "Noviembre"
        12 -> "Diciembre"
        else -> ""
    }
}

fun loadClassesFromRepository(
    repository: Repository,
    franchiseId: Long,
    weekDays: List<CalendarDay>
): List<ClassSchedule> {
    val schedules = repository.getSchedulesByFranchiseId(franchiseId)

    return schedules.mapNotNull { schedule ->
        val classroom = repository.getClassroomById(schedule.classroom_id)
        val teacher = repository.getTeacherById(schedule.teacher_id)
        val discipline = repository.getDisciplineById(schedule.discipline_id)

        if (classroom != null && teacher != null && discipline != null) {
            val dayOfWeek = weekDays.getOrNull((schedule.day_of_week - 1).toInt())

            if (dayOfWeek != null) {
                ClassSchedule(
                    className = discipline.name,
                    teacher = "${teacher.first_name} ${teacher.last_name_paternal ?: ""}".trim(),
                    schedule = "${schedule.start_time} - ${schedule.end_time}",
                    location = "Unidad Mérida",
                    classType = "Regular",
                    roomNumber = classroom.name.toIntOrNull() ?: 1,
                    day = dayOfWeek,
                    timeSlot = TimeSlot(0, 0, schedule.start_time),
                    discipline = discipline.name
                )
            } else null
        } else null
    }
}