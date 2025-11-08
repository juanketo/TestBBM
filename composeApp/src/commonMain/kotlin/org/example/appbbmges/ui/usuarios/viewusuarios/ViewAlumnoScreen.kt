package org.example.appbbmges.ui.usuarios.viewusuarios

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.example.appbbmges.data.Repository
import org.example.appbbmges.StudentEntity
import org.example.appbbmges.StudentAuthorizedAdultEntity
import org.example.appbbmges.PaymentEntity
import kotlinx.datetime.*
import org.example.appbbmges.navigation.SimpleNavController
import org.example.appbbmges.ui.usuarios.viewusuarios.viewpagos.ViewPagosListScreen
import org.example.appbbmges.ui.avatars.UserAvatar
import org.example.appbbmges.ui.avatars.AvatarSelectorDialog

object AppColors {
    val Primary = Color(0xFF00B4D8)
    val Background = Color(0xFFF8F9FA)
    val OnPrimary = Color.White
    val TextColor = Color(0xFF333333)
    val BackgroundOverlay = Color(0xFF1C1C1C).copy(alpha = 0.7f)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewAlumnoScreen(
    studentId: Long,
    repository: Repository,
    navController: SimpleNavController,
    onDismiss: () -> Unit = { navController.navigateBack() }
) {
    var selectedSection by remember { mutableStateOf<String?>("personal") }
    var showPaymentForm by remember { mutableStateOf(false) }

    // Estados de datos
    var student by remember { mutableStateOf<StudentEntity?>(null) }
    val adults by produceState<List<StudentAuthorizedAdultEntity>>(emptyList()) {
        value = repository.getStudentAuthorizedAdultsByStudentId(studentId)
    }
    val payments by produceState<List<PaymentEntity>>(emptyList(), studentId) {
        value = repository.getPaymentsByStudentId(studentId)
    }

    // Cargar estudiante
    LaunchedEffect(studentId) {
        student = repository.getStudentById(studentId)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            showPaymentForm -> {
                NewPaymentScreen(
                    studentId = studentId,
                    onDismiss = {
                        showPaymentForm = false
                    },
                    repository = repository
                )
            }

            selectedSection == "personal" -> {
                ViewAlumnoMainScreen(
                    student = student,
                    adults = adults,
                    payments = payments,
                    onDismiss = onDismiss,
                    onSectionClick = { section -> selectedSection = section },
                    onNewPaymentClick = { showPaymentForm = true },
                    selectedSection = selectedSection ?: "personal",
                    onStudentUpdated = { student = repository.getStudentById(studentId) },
                    repository = repository,
                    studentId = studentId
                )
            }

            selectedSection != null -> {
                when (selectedSection) {
                    "financiera" -> {
                        ViewPagosListScreen(
                            studentId = studentId,
                            repository = repository,
                            navController = navController
                        )
                    }
                    "calendario" -> {
                        StudentCalendarDetailScreen(
                            studentId = studentId,
                            onDismiss = { selectedSection = "personal" },
                            repository = repository
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewAlumnoMainScreen(
    student: StudentEntity?,
    adults: List<StudentAuthorizedAdultEntity>,
    payments: List<PaymentEntity>,
    onDismiss: () -> Unit,
    onSectionClick: (String) -> Unit,
    onNewPaymentClick: () -> Unit,
    selectedSection: String,
    onStudentUpdated: () -> Unit,
    repository: Repository,
    studentId: Long
) {
    var isEditing by remember { mutableStateOf(false) }
    var showSuccessMessage by remember { mutableStateOf(false) }
    var showAvatarSelector by remember { mutableStateOf(false) }

    // Estados editables
    var firstName by remember { mutableStateOf("") }
    var lastNamePaternal by remember { mutableStateOf("") }
    var lastNameMaternal by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var addressStreet by remember { mutableStateOf("") }
    var addressZip by remember { mutableStateOf("") }
    var avatarId by remember { mutableStateOf("avatar_01") }

    // Cargar datos iniciales
    LaunchedEffect(student) {
        student?.let { s ->
            firstName = s.first_name
            lastNamePaternal = s.last_name_paternal ?: ""
            lastNameMaternal = s.last_name_maternal ?: ""
            phone = s.phone ?: ""
            email = s.email ?: ""
            addressStreet = s.address_street ?: ""
            addressZip = s.address_zip ?: ""
            avatarId = s.avatar_id ?: "avatar_01"
        }
    }

    if (showAvatarSelector) {
        AvatarSelectorDialog(
            currentAvatarId = avatarId,
            onAvatarSelected = { newAvatarId ->
                avatarId = newAvatarId
                try {
                    repository.updateStudentAvatar(studentId, newAvatarId)
                    onStudentUpdated()
                } catch (e: Exception) {
                    println("Error al actualizar avatar: ${e.message}")
                }
            },
            onDismiss = { showAvatarSelector = false }
        )
    }

    if (showSuccessMessage) {
        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(2000)
            showSuccessMessage = false
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar",
                            tint = AppColors.Primary
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Perfil de Alumno",
                        style = MaterialTheme.typography.headlineSmall,
                        color = AppColors.Primary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row {
                    if (isEditing) {
                        OutlinedButton(
                            onClick = {
                                isEditing = false
                                student?.let { s ->
                                    firstName = s.first_name
                                    lastNamePaternal = s.last_name_paternal ?: ""
                                    lastNameMaternal = s.last_name_maternal ?: ""
                                    phone = s.phone ?: ""
                                    email = s.email ?: ""
                                    addressStreet = s.address_street ?: ""
                                    addressZip = s.address_zip ?: ""
                                }
                            },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFF6B7280)
                            ),
                            border = BorderStroke(1.dp, Color(0xFFD1D5DB))
                        ) {
                            Text("Cancelar")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                student?.let { s ->
                                    try {
                                        repository.updateStudent(
                                            id = s.id,
                                            franchiseId = s.franchise_id,
                                            firstName = firstName,
                                            lastNamePaternal = lastNamePaternal.ifBlank { null },
                                            lastNameMaternal = lastNameMaternal.ifBlank { null },
                                            gender = s.gender,
                                            birthDate = s.birth_date,
                                            nationality = s.nationality,
                                            curp = s.curp,
                                            phone = phone.ifBlank { null },
                                            email = email.ifBlank { null },
                                            addressStreet = addressStreet.ifBlank { null },
                                            addressZip = addressZip.ifBlank { null },
                                            parentFatherFirstName = s.parent_father_first_name,
                                            parentFatherLastNamePaternal = s.parent_father_last_name_paternal,
                                            parentFatherLastNameMaternal = s.parent_father_last_name_maternal,
                                            parentMotherFirstName = s.parent_mother_first_name,
                                            parentMotherLastNamePaternal = s.parent_mother_last_name_paternal,
                                            parentMotherLastNameMaternal = s.parent_mother_last_name_maternal,
                                            bloodType = s.blood_type,
                                            chronicDisease = s.chronic_disease,
                                            active = s.active
                                        )
                                        onStudentUpdated()
                                        isEditing = false
                                        showSuccessMessage = true
                                    } catch (e: Exception) {
                                        println("Error al guardar: ${e.message}")
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF6366F1)
                            )
                        ) {
                            Text("Guardar", color = Color.White)
                        }
                    } else {
                        Button(
                            onClick = { isEditing = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF6366F1)
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Editar", color = Color.White)
                        }
                    }
                }
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            student?.let { student ->
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Panel izquierdo
                    Column(
                        modifier = Modifier
                            .width(280.dp)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        StudentProfileCard(
                            student = student,
                            avatarId = avatarId,
                            onAvatarClick = { showAvatarSelector = true }
                        )

                        StudentNavigationCard(
                            onSectionClick = onSectionClick,
                            selectedSection = selectedSection
                        )
                    }

                    // Panel derecho
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        StudentPersonalSection(
                            student = student,
                            adults = adults,
                            isEditing = isEditing,
                            firstName = firstName,
                            onFirstNameChange = { firstName = it },
                            lastNamePaternal = lastNamePaternal,
                            onLastNamePaternalChange = { lastNamePaternal = it },
                            lastNameMaternal = lastNameMaternal,
                            onLastNameMaternalChange = { lastNameMaternal = it },
                            phone = phone,
                            onPhoneChange = { phone = it },
                            email = email,
                            onEmailChange = { email = it },
                            addressStreet = addressStreet,
                            onAddressStreetChange = { addressStreet = it },
                            addressZip = addressZip,
                            onAddressZipChange = { addressZip = it }
                        )
                    }
                }
            } ?: run {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF6366F1))
                }
            }

            // Snackbar de éxito
            if (showSuccessMessage) {
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    containerColor = Color(0xFF10B981),
                    contentColor = Color.White
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Text("Cambios guardados exitosamente")
                    }
                }
            }
        }
    }
}

@Composable
fun StudentPersonalDetailScreen(
    student: StudentEntity?,
    adults: List<StudentAuthorizedAdultEntity>,
    onDismiss: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        DetailScreenHeader(
            title = "Información Personal",
            onDismiss = onDismiss
        )

        student?.let { student ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                StudentPersonalSection(
                    student = student,
                    adults = adults,
                    isEditing = false,
                    firstName = student.first_name,
                    onFirstNameChange = {},
                    lastNamePaternal = student.last_name_paternal ?: "",
                    onLastNamePaternalChange = {},
                    lastNameMaternal = student.last_name_maternal ?: "",
                    onLastNameMaternalChange = {},
                    phone = student.phone ?: "",
                    onPhoneChange = {},
                    email = student.email ?: "",
                    onEmailChange = {},
                    addressStreet = student.address_street ?: "",
                    onAddressStreetChange = {},
                    addressZip = student.address_zip ?: "",
                    onAddressZipChange = {}
                )
            }
        }
    }
}

@Composable
fun StudentCalendarDetailScreen(
    studentId: Long,
    onDismiss: () -> Unit,
    repository: Repository
) {
    Column(modifier = Modifier.fillMaxSize()) {
        DetailScreenHeader(
            title = "Calendario de Clases",
            onDismiss = onDismiss
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Calendario de clases",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Funcionalidad pendiente de implementar")
                }
            }
        }
    }
}

@Composable
fun NewPaymentScreen(
    studentId: Long,
    onDismiss: () -> Unit,
    repository: Repository
) {
    Column(modifier = Modifier.fillMaxSize()) {
        DetailScreenHeader(
            title = "Nuevo Pago",
            onDismiss = onDismiss
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Formulario de Nuevo Pago",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Student ID: $studentId")
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Aquí iría el formulario de pago")
                }
            }
        }
    }
}

@Composable
private fun DetailScreenHeader(
    title: String,
    onDismiss: () -> Unit,
    action: @Composable (() -> Unit)? = null
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Regresar",
                        tint = AppColors.Primary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = AppColors.Primary,
                    fontWeight = FontWeight.Bold
                )
            }

            action?.invoke()
        }
    }
}

@Composable
private fun StudentProfileCard(
    student: StudentEntity,
    avatarId: String,
    onAvatarClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Avatar con funcionalidad de edición
            UserAvatar(
                avatarId = avatarId,
                size = 80,
                showEditIcon = true,
                onEditClick = onAvatarClick
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "${student.first_name} ${student.last_name_paternal}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF1F2937)
                )
                Text(
                    text = student.email ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF6B7280),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Última actualización: ${getCurrentDate()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF9CA3AF),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun StudentNavigationCard(
    onSectionClick: (String) -> Unit,
    selectedSection: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            NavigationItem(
                text = "Información Personal",
                onClick = { onSectionClick("personal") },
                icon = Icons.Default.Person,
                isSelected = selectedSection == "personal"
            )
            NavigationItem(
                text = "Información Financiera",
                onClick = { onSectionClick("financiera") },
                icon = Icons.Default.AttachMoney,
                isSelected = selectedSection == "financiera"
            )
            NavigationItem(
                text = "Calendario de clases",
                onClick = { onSectionClick("calendario") },
                icon = Icons.Default.CalendarToday,
                isSelected = selectedSection == "calendario"
            )
        }
    }
}

@Composable
private fun NavigationItem(
    text: String,
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean = false,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isSelected) Color(0xFF6366F1).copy(alpha = 0.1f)
                else Color.Transparent
            )
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = if (isSelected) Color(0xFF6366F1) else Color(0xFF6B7280),
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isSelected) Color(0xFF6366F1) else Color(0xFF374151),
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
        )
    }
}

@Composable
private fun StudentPersonalSection(
    student: StudentEntity,
    adults: List<StudentAuthorizedAdultEntity>,
    isEditing: Boolean,
    firstName: String,
    onFirstNameChange: (String) -> Unit,
    lastNamePaternal: String,
    onLastNamePaternalChange: (String) -> Unit,
    lastNameMaternal: String,
    onLastNameMaternalChange: (String) -> Unit,
    phone: String,
    onPhoneChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    addressStreet: String,
    onAddressStreetChange: (String) -> Unit,
    addressZip: String,
    onAddressZipChange: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Información Personal
        FormSection(
            title = "INFORMACIÓN PERSONAL",
            content = {
                FormGrid {
                    EditableFormField(
                        label = "Nombre",
                        value = firstName,
                        onValueChange = onFirstNameChange,
                        enabled = isEditing,
                        modifier = Modifier.weight(1f)
                    )
                    FormGrid {
                        EditableFormField(
                            label = "Apellido Paterno",
                            value = lastNamePaternal,
                            onValueChange = onLastNamePaternalChange,
                            enabled = isEditing,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                FormGrid {
                    EditableFormField(
                        label = "Apellido Materno",
                        value = lastNameMaternal,
                        onValueChange = onLastNameMaternalChange,
                        enabled = isEditing,
                        modifier = Modifier.weight(1f)
                    )
                    FormField(
                        label = "Estado",
                        value = if (student.active == 1L) "Activo" else "Inactivo",
                        modifier = Modifier.weight(1f)
                    )
                }

                FormGrid {
                    EditableFormField(
                        label = "Correo Electrónico",
                        value = email,
                        onValueChange = onEmailChange,
                        enabled = isEditing,
                        modifier = Modifier.weight(1f)
                    )
                    EditableFormField(
                        label = "Teléfono",
                        value = phone,
                        onValueChange = onPhoneChange,
                        enabled = isEditing,
                        modifier = Modifier.weight(1f)
                    )
                }

                FormField(
                    label = "CURP",
                    value = student.curp ?: "",
                    modifier = Modifier.fillMaxWidth()
                )
            }
        )

        // Dirección Personal
        FormSection(
            title = "DIRECCIÓN PERSONAL",
            content = {
                FormGrid {
                    EditableFormField(
                        label = "Dirección",
                        value = addressStreet,
                        onValueChange = onAddressStreetChange,
                        enabled = isEditing,
                        modifier = Modifier.weight(1f)
                    )
                    EditableFormField(
                        label = "Código Postal",
                        value = addressZip,
                        onValueChange = onAddressZipChange,
                        enabled = isEditing,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        )

        // Información de Responsables
        FormSection(
            title = "RESPONSABLES",
            content = {
                FormGrid {
                    FormField(
                        label = "Padre",
                        value = buildParentName(
                            student.parent_father_first_name,
                            student.parent_father_last_name_paternal,
                            student.parent_father_last_name_maternal
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    FormField(
                        label = "Madre",
                        value = buildParentName(
                            student.parent_mother_first_name,
                            student.parent_mother_last_name_paternal,
                            student.parent_mother_last_name_maternal
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        )

        // Información Médica
        FormSection(
            title = "INFORMACIÓN MÉDICA",
            content = {
                FormGrid {
                    FormField(
                        label = "Tipo Sanguíneo",
                        value = student.blood_type ?: "N/A",
                        modifier = Modifier.weight(1f)
                    )
                    FormField(
                        label = "Enfermedad Crónica",
                        value = student.chronic_disease ?: "Ninguna",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        )

        // Adultos Autorizados
        if (adults.isNotEmpty()) {
            FormSection(
                title = "ADULTOS AUTORIZADOS (${adults.size})",
                content = {
                    adults.chunked(2).forEach { pair ->
                        FormGrid {
                            pair.forEach { adult ->
                                FormField(
                                    label = "Adulto Autorizado",
                                    value = "${adult.first_name} ${adult.last_name_paternal ?: ""}",
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (pair.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun FormSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF374151)
            )
            content()
        }
    }
}

@Composable
private fun FormGrid(
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        content()
    }
}

@Composable
private fun FormField(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF374151)
        )
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF6366F1),
                unfocusedBorderColor = Color(0xFFD1D5DB),
                disabledBorderColor = Color(0xFFD1D5DB),
                disabledTextColor = Color(0xFF374151)
            ),
            shape = RoundedCornerShape(8.dp)
        )
    }
}

@Composable
private fun EditableFormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF374151)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF6366F1),
                unfocusedBorderColor = Color(0xFFD1D5DB),
                disabledBorderColor = Color(0xFFD1D5DB),
                disabledTextColor = Color(0xFF374151)
            ),
            shape = RoundedCornerShape(8.dp)
        )
    }
}

private fun buildParentName(
    firstName: String?,
    lastNameP: String?,
    lastNameM: String?
): String {
    return listOfNotNull(firstName, lastNameP, lastNameM)
        .joinToString(" ")
        .ifEmpty { "N/A" }
}

private fun getCurrentDate(): String {
    val now = Clock.System.now()
    val date = now.toLocalDateTime(TimeZone.currentSystemDefault()).date
    return "${date.dayOfMonth.toString().padStart(2, '0')}-${date.monthNumber.toString().padStart(2, '0')}-${date.year}"
}

private fun formatTimestamp(timestamp: Long): String {
    val instant = Instant.fromEpochMilliseconds(timestamp)
    val date = instant.toLocalDateTime(TimeZone.currentSystemDefault()).date
    return "${date.dayOfMonth.toString().padStart(2, '0')}/${date.monthNumber.toString().padStart(2, '0')}/${date.year}"
}