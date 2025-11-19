package org.example.appbbmges.ui.usuarios.registation.administrativeform

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.example.appbbmges.ui.usuarios.registation.studentsform.CustomCountryCodeDropdown
import org.example.appbbmges.ui.usuarios.registation.studentsform.CustomDateSelectorField
import org.example.appbbmges.ui.usuarios.registation.studentsform.CustomDropdownField
import org.example.appbbmges.ui.usuarios.registation.studentsform.CustomOutlinedTextField

@Composable
fun PersonalInfoStep(
    data: AdministrativeFormData,
    errors: AdministrativeFormErrors,
    onDataChange: (AdministrativeFormData) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CustomOutlinedTextField(
                value = data.firstName,
                onValueChange = { newValue ->
                    onDataChange(data.copy(firstName = TextFormatters.cleanAndFormatNameInput(newValue)))
                },
                label = "Nombre(s)",
                placeholder = "Nombre(s)",
                modifier = Modifier.weight(1f),
                isError = errors.firstName != null,
                errorMessage = errors.firstName
            )
            CustomOutlinedTextField(
                value = data.lastNamePaternal,
                onValueChange = { newValue ->
                    onDataChange(data.copy(lastNamePaternal = TextFormatters.cleanAndFormatNameInput(newValue)))
                },
                label = "Apellido Paterno",
                placeholder = "Apellido Paterno",
                modifier = Modifier.weight(1f),
                isError = errors.lastNamePaternal != null,
                errorMessage = errors.lastNamePaternal
            )
            CustomOutlinedTextField(
                value = data.lastNameMaternal,
                onValueChange = { newValue ->
                    onDataChange(data.copy(lastNameMaternal = TextFormatters.cleanAndFormatNameInput(newValue)))
                },
                label = "Apellido Materno",
                placeholder = "Apellido Materno",
                modifier = Modifier.weight(1f),
                isError = errors.lastNameMaternal != null,
                errorMessage = errors.lastNameMaternal
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CustomDropdownField(
                value = data.gender,
                onValueChange = { onDataChange(data.copy(gender = it)) },
                label = "Género",
                options = AdministrativeFormConstants.genderOptions,
                placeholder = "Seleccione su género",
                modifier = Modifier.weight(1f)
            )
            CustomDateSelectorField(
                value = data.birthDate,
                onDateSelected = { onDataChange(data.copy(birthDate = it)) },
                label = "Fecha de Nacimiento",
                modifier = Modifier.weight(1.5f),
                isError = errors.birthDate != null,
                errorMessage = errors.birthDate
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CustomDropdownField(
                value = data.nationality,
                onValueChange = { onDataChange(data.copy(nationality = it)) },
                label = "Nacionalidad",
                options = AdministrativeFormConstants.nationalityOptions,
                placeholder = "Seleccione nacionalidad",
                modifier = Modifier.weight(1f)
            )
            CustomOutlinedTextField(
                value = data.email,
                onValueChange = { onDataChange(data.copy(email = it)) },
                label = "Email",
                placeholder = "correo@ejemplo.com",
                modifier = Modifier.weight(1f),
                isError = errors.email != null,
                errorMessage = errors.email,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CustomCountryCodeDropdown(
                selectedCode = data.countryCode,
                onCodeSelected = { onDataChange(data.copy(countryCode = it)) },
                options = AdministrativeFormConstants.countryCodeOptions,
                modifier = Modifier.width(120.dp)
            )
            CustomOutlinedTextField(
                value = data.phone,
                onValueChange = {
                    val filtered = it.filter { char -> char.isDigit() }.take(10)
                    onDataChange(data.copy(phone = filtered))
                },
                label = "Teléfono",
                placeholder = "1234567890",
                modifier = Modifier.weight(1f),
                isError = errors.phone != null,
                errorMessage = errors.phone,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                maxLength = 10
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CustomOutlinedTextField(
                value = data.username,
                onValueChange = {},
                label = "Usuario",
                placeholder = "Se genera automáticamente",
                modifier = Modifier.weight(1f),
                readOnly = true
            )
            CustomOutlinedTextField(
                value = data.password,
                onValueChange = {},
                label = "Contraseña",
                placeholder = "Se genera automáticamente",
                modifier = Modifier.weight(1f),
                readOnly = true
            )
        }

        if (data.username.isNotEmpty() && data.password.isNotEmpty()) {
            Text(
                text = "Usuario y contraseña se han generado automáticamente.",
                color = Color(0xFFFF8C00),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp)
            )
        }
    }
}

@Composable
fun ProfessionalInfoStep(
    data: AdministrativeFormData,
    errors: AdministrativeFormErrors,
    onDataChange: (AdministrativeFormData) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CustomOutlinedTextField(
                value = data.taxId,
                onValueChange = {
                    val filtered = it.uppercase().filter { char -> char.isLetterOrDigit() }.take(13)
                    onDataChange(data.copy(taxId = filtered))
                },
                label = "RFC",
                placeholder = "ABCD123456XYZ (opcional)",
                modifier = Modifier.weight(1f),
                isError = errors.taxId != null,
                errorMessage = errors.taxId,
                maxLength = 13
            )
            CustomOutlinedTextField(
                value = data.nss,
                onValueChange = {
                    val filtered = it.filter { char -> char.isDigit() }.take(11)
                    onDataChange(data.copy(nss = filtered))
                },
                label = "NSS",
                placeholder = "12345678901 (opcional)",
                modifier = Modifier.weight(1f),
                isError = errors.nss != null,
                errorMessage = errors.nss,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                maxLength = 11
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CustomOutlinedTextField(
                value = data.salary,
                onValueChange = {
                    val filtered = it.filter { char -> char.isDigit() || char == '.' }
                    onDataChange(data.copy(salary = filtered))
                },
                label = "Salario Mensual",
                placeholder = "15000.00",
                modifier = Modifier.weight(1f),
                isError = errors.salary != null,
                errorMessage = errors.salary,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
            CustomDateSelectorField(
                value = data.startDate,
                onDateSelected = { onDataChange(data.copy(startDate = it)) },
                label = "Fecha de Inicio",
                modifier = Modifier.weight(1f),
                isError = errors.startDate != null,
                errorMessage = errors.startDate
            )
        }
    }
}

@Composable
fun AddressInfoStep(
    data: AdministrativeFormData,
    errors: AdministrativeFormErrors,
    onDataChange: (AdministrativeFormData) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CustomOutlinedTextField(
            value = data.addressStreet,
            onValueChange = { onDataChange(data.copy(addressStreet = it)) },
            label = "Calle y número",
            placeholder = "Ej. Av. Siempre Viva 123",
            modifier = Modifier.weight(1f),
            isError = errors.addressStreet != null,
            errorMessage = errors.addressStreet
        )
        CustomOutlinedTextField(
            value = data.addressZip,
            onValueChange = {
                val filtered = it.filter { char -> char.isDigit() }.take(5)
                onDataChange(data.copy(addressZip = filtered))
            },
            label = "Código Postal",
            placeholder = "Ej. 12345",
            modifier = Modifier.weight(1f),
            isError = errors.addressZip != null,
            errorMessage = errors.addressZip,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            maxLength = 5
        )
    }
}

@Composable
fun AdditionalInfoStep(
    data: AdministrativeFormData,
    onDataChange: (AdministrativeFormData) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CustomOutlinedTextField(
                value = data.emergencyContactName,
                onValueChange = {
                    onDataChange(data.copy(emergencyContactName = TextFormatters.cleanAndFormatNameInput(it)))
                },
                label = "Nombre del Contacto de Emergencia",
                placeholder = "Opcional",
                modifier = Modifier.weight(1f)
            )
            CustomOutlinedTextField(
                value = data.emergencyContactPhone,
                onValueChange = {
                    val filtered = it.filter { char -> char.isDigit() }.take(10)
                    onDataChange(data.copy(emergencyContactPhone = filtered))
                },
                label = "Teléfono de Emergencia",
                placeholder = "1234567890",
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                maxLength = 10
            )
        }
    }
}

@Composable
fun ConfirmationStep(
    data: AdministrativeFormData,
    errors: AdministrativeFormErrors,
    selectedArea: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Confirmación de Datos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text("Nombre(s): ${data.firstName}")
            if (data.lastNamePaternal.isNotEmpty()) Text("Apellido Paterno: ${data.lastNamePaternal}")
            if (data.lastNameMaternal.isNotEmpty()) Text("Apellido Materno: ${data.lastNameMaternal}")
            if (data.gender.isNotEmpty()) Text("Género: ${data.gender}")
            if (data.birthDate.isNotEmpty()) {
                val displayDate = AdministrativeFormUtils.convertToDisplayFormat(data.birthDate)
                Text("Fecha de Nacimiento: $displayDate")
            }
            if (data.nationality.isNotEmpty()) Text("Nacionalidad: ${data.nationality}")
            if (data.taxId.isNotEmpty()) Text("RFC: ${data.taxId}")
            if (data.nss.isNotEmpty()) Text("NSS: ${data.nss}")
            if (data.phone.isNotEmpty()) Text("Teléfono: ${data.countryCode} ${data.phone}")
            if (data.email.isNotEmpty()) Text("Email: ${data.email}")
            if (data.addressStreet.isNotEmpty()) Text("Calle: ${data.addressStreet}")
            if (data.addressZip.isNotEmpty()) Text("Código Postal: ${data.addressZip}")
            if (data.emergencyContactName.isNotEmpty()) Text("Contacto de Emergencia: ${data.emergencyContactName}")
            if (data.emergencyContactPhone.isNotEmpty()) Text("Teléfono de Emergencia: ${data.emergencyContactPhone}")
            if (selectedArea.isNotEmpty()) Text("Área Corporativa: $selectedArea")
            if (data.salary.isNotEmpty()) Text("Salario: $${data.salary}")
            if (data.startDate.isNotEmpty()) {
                val displayDate = AdministrativeFormUtils.convertToDisplayFormat(data.startDate)
                Text("Fecha de Inicio: $displayDate")
            }
            Text("Estado: ${if (data.active) "Activo" else "Inactivo"}")

            if (errors.general != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errors.general,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}