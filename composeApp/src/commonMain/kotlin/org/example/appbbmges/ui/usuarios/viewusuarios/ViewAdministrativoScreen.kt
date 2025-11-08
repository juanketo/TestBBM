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
import org.example.appbbmges.navigation.SimpleNavController
import org.example.appbbmges.AdministrativeEntity
import org.example.appbbmges.ui.avatars.UserAvatar
import org.example.appbbmges.ui.avatars.AvatarSelectorDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewAdministrativoScreen(
    administrativeId: Long,
    repository: Repository,
    navController: SimpleNavController,
    onDismiss: () -> Unit = { navController.navigateBack() }
) {
    var administrative by remember { mutableStateOf<AdministrativeEntity?>(null) }
    var showAvatarSelector by remember { mutableStateOf(false) }
    var isEditing by remember { mutableStateOf(false) }

    // Estados editables
    var firstName by remember { mutableStateOf("") }
    var lastNamePaternal by remember { mutableStateOf("") }
    var lastNameMaternal by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var addressStreet by remember { mutableStateOf("") }
    var addressZip by remember { mutableStateOf("") }
    var emergencyContactName by remember { mutableStateOf("") }
    var emergencyContactPhone by remember { mutableStateOf("") }
    var position by remember { mutableStateOf("") }
    var salary by remember { mutableStateOf("") }
    var avatarId by remember { mutableStateOf("avatar_01") }

    var selectedSection by remember { mutableStateOf("personal") }
    var showSuccessMessage by remember { mutableStateOf(false) }

    // Cargar datos iniciales
    LaunchedEffect(administrativeId) {
        administrative = repository.getAdministrativeById(administrativeId)
        administrative?.let { admin ->
            firstName = admin.first_name
            lastNamePaternal = admin.last_name_paternal ?: ""
            lastNameMaternal = admin.last_name_maternal ?: ""
            phone = admin.phone ?: ""
            email = admin.email ?: ""
            addressStreet = admin.address_street ?: ""
            addressZip = admin.address_zip ?: ""
            emergencyContactName = admin.emergency_contact_name ?: ""
            emergencyContactPhone = admin.emergency_contact_phone ?: ""
            position = admin.position
            salary = admin.salary.toString()
            avatarId = admin.avatar_id ?: "avatar_01"
        }
    }

    if (showAvatarSelector) {
        AvatarSelectorDialog(
            currentAvatarId = avatarId,
            onAvatarSelected = { newAvatarId ->
                avatarId = newAvatarId
                // Guardar inmediatamente el avatar
                try {
                    repository.updateAdministrativeAvatar(administrativeId, newAvatarId)
                    administrative = repository.getAdministrativeById(administrativeId)
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil Administrativo") },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar",
                            tint = Color(0xFF374151)
                        )
                    }
                },
                actions = {
                    if (isEditing) {
                        OutlinedButton(
                            onClick = {
                                isEditing = false
                                // Recargar datos originales
                                administrative?.let { admin ->
                                    firstName = admin.first_name
                                    lastNamePaternal = admin.last_name_paternal ?: ""
                                    lastNameMaternal = admin.last_name_maternal ?: ""
                                    phone = admin.phone ?: ""
                                    email = admin.email ?: ""
                                    addressStreet = admin.address_street ?: ""
                                    addressZip = admin.address_zip ?: ""
                                    emergencyContactName = admin.emergency_contact_name ?: ""
                                    emergencyContactPhone = admin.emergency_contact_phone ?: ""
                                    position = admin.position
                                    salary = admin.salary.toString()
                                }
                            },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFF6B7280)
                            ),
                            border = BorderStroke(1.dp, Color(0xFFD1D5DB)),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text("Cancelar")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                administrative?.let { admin ->
                                    try {
                                        repository.updateAdministrative(
                                            id = admin.id,
                                            franchiseId = admin.franchise_id,
                                            firstName = firstName,
                                            lastNamePaternal = lastNamePaternal.ifBlank { null },
                                            lastNameMaternal = lastNameMaternal.ifBlank { null },
                                            gender = admin.gender,
                                            birthDate = admin.birth_date,
                                            nationality = admin.nationality,
                                            taxId = admin.tax_id,
                                            nss = admin.nss,
                                            phone = phone.ifBlank { null },
                                            email = email.ifBlank { null },
                                            addressStreet = addressStreet.ifBlank { null },
                                            addressZip = addressZip.ifBlank { null },
                                            emergencyContactName = emergencyContactName.ifBlank { null },
                                            emergencyContactPhone = emergencyContactPhone.ifBlank { null },
                                            position = position,
                                            salary = salary.toDoubleOrNull() ?: 0.0,
                                            startDate = admin.start_date,
                                            active = admin.active
                                        )
                                        administrative = repository.getAdministrativeById(administrativeId)
                                        isEditing = false
                                        showSuccessMessage = true
                                    } catch (e: Exception) {
                                        println("Error al guardar: ${e.message}")
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF6366F1)
                            ),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text("Guardar", color = Color.White)
                        }
                    } else {
                        Button(
                            onClick = { isEditing = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF6366F1)
                            ),
                            modifier = Modifier.height(36.dp)
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
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color(0xFF1F2937)
                )
            )
        },
        containerColor = Color(0xFFF9FAFB)
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            administrative?.let { admin ->
                Row(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Panel izquierdo - Perfil y navegación
                    Column(
                        modifier = Modifier.width(280.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Tarjeta de perfil
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
                                    onEditClick = { showAvatarSelector = true }
                                )

                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "$firstName $lastNamePaternal",
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        color = Color(0xFF1F2937)
                                    )
                                    Text(
                                        text = position,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color(0xFF6B7280),
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = "Última actualización: ${admin.start_date}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF9CA3AF),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }

                        // Menú de navegación
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                listOf(
                                    "personal" to "Información Personal",
                                    "laboral" to "Datos Laborales",
                                    "permisos" to "Permisos y Accesos"
                                ).forEach { (id, title) ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                if (selectedSection == id) Color(0xFF6366F1).copy(alpha = 0.1f)
                                                else Color.Transparent
                                            )
                                            .padding(vertical = 12.dp, horizontal = 12.dp)
                                            .clickable { selectedSection = id },
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Icon(
                                            imageVector = when {
                                                title.contains("Personal") -> Icons.Default.Person
                                                title.contains("Laboral") -> Icons.Default.Work
                                                else -> Icons.Default.Security
                                            },
                                            contentDescription = null,
                                            tint = if (selectedSection == id) Color(0xFF6366F1) else Color(0xFF9CA3AF),
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Text(
                                            text = title,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = if (selectedSection == id) Color(0xFF6366F1) else Color(0xFF374151),
                                            fontWeight = if (selectedSection == id) FontWeight.Medium else FontWeight.Normal
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Panel derecho - Contenido
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        when (selectedSection) {
                            "personal" -> {
                                // Información Personal
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
                                            text = "INFORMACIÓN PERSONAL",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFF374151)
                                        )

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                                        ) {
                                            Column(
                                                modifier = Modifier.weight(1f),
                                                verticalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Text(
                                                    text = "Nombre",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    fontWeight = FontWeight.Medium,
                                                    color = Color(0xFF374151)
                                                )
                                                OutlinedTextField(
                                                    value = firstName,
                                                    onValueChange = { firstName = it },
                                                    enabled = isEditing,
                                                    modifier = Modifier.fillMaxWidth(),
                                                    colors = OutlinedTextFieldDefaults.colors(
                                                        disabledTextColor = Color(0xFF374151),
                                                        disabledBorderColor = Color(0xFFD1D5DB)
                                                    ),
                                                    shape = RoundedCornerShape(8.dp)
                                                )
                                            }
                                            Column(
                                                modifier = Modifier.weight(1f),
                                                verticalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Text(
                                                    text = "Apellido Paterno",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    fontWeight = FontWeight.Medium,
                                                    color = Color(0xFF374151)
                                                )
                                                OutlinedTextField(
                                                    value = lastNamePaternal,
                                                    onValueChange = { lastNamePaternal = it },
                                                    enabled = isEditing,
                                                    modifier = Modifier.fillMaxWidth(),
                                                    colors = OutlinedTextFieldDefaults.colors(
                                                        disabledTextColor = Color(0xFF374151),
                                                        disabledBorderColor = Color(0xFFD1D5DB)
                                                    ),
                                                    shape = RoundedCornerShape(8.dp)
                                                )
                                            }
                                        }

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                                        ) {
                                            Column(
                                                modifier = Modifier.weight(1f),
                                                verticalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Text(
                                                    text = "Apellido Materno",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    fontWeight = FontWeight.Medium,
                                                    color = Color(0xFF374151)
                                                )
                                                OutlinedTextField(
                                                    value = lastNameMaternal,
                                                    onValueChange = { lastNameMaternal = it },
                                                    enabled = isEditing,
                                                    modifier = Modifier.fillMaxWidth(),
                                                    colors = OutlinedTextFieldDefaults.colors(
                                                        disabledTextColor = Color(0xFF374151),
                                                        disabledBorderColor = Color(0xFFD1D5DB)
                                                    ),
                                                    shape = RoundedCornerShape(8.dp)
                                                )
                                            }
                                            Column(
                                                modifier = Modifier.weight(1f),
                                                verticalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Text(
                                                    text = "Teléfono",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    fontWeight = FontWeight.Medium,
                                                    color = Color(0xFF374151)
                                                )
                                                OutlinedTextField(
                                                    value = phone,
                                                    onValueChange = { phone = it },
                                                    enabled = isEditing,
                                                    modifier = Modifier.fillMaxWidth(),
                                                    colors = OutlinedTextFieldDefaults.colors(
                                                        disabledTextColor = Color(0xFF374151),
                                                        disabledBorderColor = Color(0xFFD1D5DB)
                                                    ),
                                                    shape = RoundedCornerShape(8.dp)
                                                )
                                            }
                                        }

                                        Column(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Text(
                                                text = "Email",
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = FontWeight.Medium,
                                                color = Color(0xFF374151)
                                            )
                                            OutlinedTextField(
                                                value = email,
                                                onValueChange = { email = it },
                                                enabled = isEditing,
                                                modifier = Modifier.fillMaxWidth(),
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    disabledTextColor = Color(0xFF374151),
                                                    disabledBorderColor = Color(0xFFD1D5DB)
                                                ),
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                        }

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                                        ) {
                                            Column(
                                                modifier = Modifier.weight(1f),
                                                verticalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Text(
                                                    text = "Calle",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    fontWeight = FontWeight.Medium,
                                                    color = Color(0xFF374151)
                                                )
                                                OutlinedTextField(
                                                    value = addressStreet,
                                                    onValueChange = { addressStreet = it },
                                                    enabled = isEditing,
                                                    modifier = Modifier.fillMaxWidth(),
                                                    colors = OutlinedTextFieldDefaults.colors(
                                                        disabledTextColor = Color(0xFF374151),
                                                        disabledBorderColor = Color(0xFFD1D5DB)
                                                    ),
                                                    shape = RoundedCornerShape(8.dp)
                                                )
                                            }
                                            Column(
                                                modifier = Modifier.weight(1f),
                                                verticalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Text(
                                                    text = "Código Postal",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    fontWeight = FontWeight.Medium,
                                                    color = Color(0xFF374151)
                                                )
                                                OutlinedTextField(
                                                    value = addressZip,
                                                    onValueChange = { addressZip = it },
                                                    enabled = isEditing,
                                                    modifier = Modifier.fillMaxWidth(),
                                                    colors = OutlinedTextFieldDefaults.colors(
                                                        disabledTextColor = Color(0xFF374151),
                                                        disabledBorderColor = Color(0xFFD1D5DB)
                                                    ),
                                                    shape = RoundedCornerShape(8.dp)
                                                )
                                            }
                                        }
                                    }
                                }

                                // Contacto de Emergencia
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
                                            text = "CONTACTO DE EMERGENCIA",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFF374151)
                                        )

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                                        ) {
                                            Column(
                                                modifier = Modifier.weight(1f),
                                                verticalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Text(
                                                    text = "Nombre",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    fontWeight = FontWeight.Medium,
                                                    color = Color(0xFF374151)
                                                )
                                                OutlinedTextField(
                                                    value = emergencyContactName,
                                                    onValueChange = { emergencyContactName = it },
                                                    enabled = isEditing,
                                                    modifier = Modifier.fillMaxWidth(),
                                                    colors = OutlinedTextFieldDefaults.colors(
                                                        disabledTextColor = Color(0xFF374151),
                                                        disabledBorderColor = Color(0xFFD1D5DB)
                                                    ),
                                                    shape = RoundedCornerShape(8.dp)
                                                )
                                            }
                                            Column(
                                                modifier = Modifier.weight(1f),
                                                verticalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Text(
                                                    text = "Teléfono",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    fontWeight = FontWeight.Medium,
                                                    color = Color(0xFF374151)
                                                )
                                                OutlinedTextField(
                                                    value = emergencyContactPhone,
                                                    onValueChange = { emergencyContactPhone = it },
                                                    enabled = isEditing,
                                                    modifier = Modifier.fillMaxWidth(),
                                                    colors = OutlinedTextFieldDefaults.colors(
                                                        disabledTextColor = Color(0xFF374151),
                                                        disabledBorderColor = Color(0xFFD1D5DB)
                                                    ),
                                                    shape = RoundedCornerShape(8.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            "laboral" -> {
                                // Datos Laborales
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
                                            text = "INFORMACIÓN LABORAL",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFF374151)
                                        )

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                                        ) {
                                            Column(
                                                modifier = Modifier.weight(1f),
                                                verticalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Text(
                                                    text = "Puesto",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    fontWeight = FontWeight.Medium,
                                                    color = Color(0xFF374151)
                                                )
                                                OutlinedTextField(
                                                    value = position,
                                                    onValueChange = { position = it },
                                                    enabled = isEditing,
                                                    modifier = Modifier.fillMaxWidth(),
                                                    colors = OutlinedTextFieldDefaults.colors(
                                                        disabledTextColor = Color(0xFF374151),
                                                        disabledBorderColor = Color(0xFFD1D5DB)
                                                    ),
                                                    shape = RoundedCornerShape(8.dp)
                                                )
                                            }
                                            Column(
                                                modifier = Modifier.weight(1f),
                                                verticalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Text(
                                                    text = "Salario",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    fontWeight = FontWeight.Medium,
                                                    color = Color(0xFF374151)
                                                )
                                                OutlinedTextField(
                                                    value = salary,
                                                    onValueChange = { salary = it },
                                                    enabled = isEditing,
                                                    modifier = Modifier.fillMaxWidth(),
                                                    colors = OutlinedTextFieldDefaults.colors(
                                                        disabledTextColor = Color(0xFF374151),
                                                        disabledBorderColor = Color(0xFFD1D5DB)
                                                    ),
                                                    shape = RoundedCornerShape(8.dp)
                                                )
                                            }
                                        }

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                                        ) {
                                            Column(
                                                modifier = Modifier.weight(1f),
                                                verticalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Text(
                                                    text = "Fecha de Inicio",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    fontWeight = FontWeight.Medium,
                                                    color = Color(0xFF374151)
                                                )
                                                OutlinedTextField(
                                                    value = admin.start_date,
                                                    onValueChange = {},
                                                    enabled = false,
                                                    modifier = Modifier.fillMaxWidth(),
                                                    colors = OutlinedTextFieldDefaults.colors(
                                                        disabledTextColor = Color(0xFF374151),
                                                        disabledBorderColor = Color(0xFFD1D5DB)
                                                    ),
                                                    shape = RoundedCornerShape(8.dp)
                                                )
                                            }
                                            Column(
                                                modifier = Modifier.weight(1f),
                                                verticalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Text(
                                                    text = "Estado",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    fontWeight = FontWeight.Medium,
                                                    color = Color(0xFF374151)
                                                )
                                                OutlinedTextField(
                                                    value = if (admin.active == 1L) "Activo" else "Inactivo",
                                                    onValueChange = {},
                                                    enabled = false,
                                                    modifier = Modifier.fillMaxWidth(),
                                                    colors = OutlinedTextFieldDefaults.colors(
                                                        disabledTextColor = Color(0xFF374151),
                                                        disabledBorderColor = Color(0xFFD1D5DB)
                                                    ),
                                                    shape = RoundedCornerShape(8.dp)
                                                )
                                            }
                                        }

                                        Column(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Text(
                                                text = "Franquicia Asignada",
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = FontWeight.Medium,
                                                color = Color(0xFF374151)
                                            )
                                            OutlinedTextField(
                                                value = repository.getFranchiseById(admin.franchise_id)?.name ?: "N/A",
                                                onValueChange = {},
                                                enabled = false,
                                                modifier = Modifier.fillMaxWidth(),
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    disabledTextColor = Color(0xFF374151),
                                                    disabledBorderColor = Color(0xFFD1D5DB)
                                                ),
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            "permisos" -> {
                                // Permisos y Accesos
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
                                            text = "PERMISOS Y ACCESOS",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFF374151)
                                        )
                                        Text(
                                            text = "Funcionalidad en desarrollo",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color(0xFF6B7280)
                                        )
                                    }
                                }
                            }
                        }
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