package org.example.appbbmges.ui.usuarios.registation.studentsform

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.example.appbbmges.ui.usuarios.AppColors

@Composable
fun CustomDateSelectorField(
    value: String,
    onDateSelected: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    val currentYear = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).year
    val yearRange = remember { (currentYear downTo 1900).toList() }
    val months = remember {
        listOf(
            "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
        )
    }

    var selectedDay by remember { mutableStateOf<Int?>(null) }
    var selectedMonth by remember { mutableStateOf<Int?>(null) }
    var selectedYear by remember { mutableStateOf<Int?>(null) }

    // Parse initial value
    LaunchedEffect(value) {
        if (value.matches("\\d{4}-\\d{2}-\\d{2}".toRegex())) {
            val parts = value.split("-")
            selectedYear = parts[0].toIntOrNull()
            selectedMonth = parts[1].toIntOrNull()
            selectedDay = parts[2].toIntOrNull()
        }
    }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DropdownSelector(
                label = "Día",
                options = (1..31).map { it.toString().padStart(2, '0') },
                selected = selectedDay?.toString()?.padStart(2, '0'),
                onSelect = { selected ->
                    selectedDay = selected.toInt()
                    combineDateIfValid(selectedYear, selectedMonth, selectedDay, onDateSelected)
                },
                modifier = Modifier.weight(1f),
                isError = isError
            )

            DropdownSelector(
                label = "Mes",
                options = months,
                selected = selectedMonth?.let { months.getOrNull(it - 1) },
                onSelect = { selected ->
                    selectedMonth = months.indexOf(selected) + 1
                    combineDateIfValid(selectedYear, selectedMonth, selectedDay, onDateSelected)
                },
                modifier = Modifier.weight(1f),
                isError = isError
            )

            DropdownSelector(
                label = "Año",
                options = yearRange.map { it.toString() },
                selected = selectedYear?.toString(),
                onSelect = { selected ->
                    selectedYear = selected.toInt()
                    combineDateIfValid(selectedYear, selectedMonth, selectedDay, onDateSelected)
                },
                modifier = Modifier.weight(1f),
                isError = isError
            )
        }

        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownSelector(
    label: String,
    options: List<String>,
    selected: String?,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selected ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AppColors.Primary,
                unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                errorBorderColor = MaterialTheme.colorScheme.error
            ),
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true),
            shape = RoundedCornerShape(8.dp),
            isError = isError,
            singleLine = true
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.heightIn(max = 200.dp)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

private fun formatDate(year: Int, month: Int, day: Int): String {
    return buildString {
        append(year.toString().padStart(4, '0'))
        append('-')
        append(month.toString().padStart(2, '0'))
        append('-')
        append(day.toString().padStart(2, '0'))
    }
}

private fun combineDateIfValid(
    year: Int?,
    month: Int?,
    day: Int?,
    onDateSelected: (String) -> Unit
) {
    if (year != null && month != null && day != null) {
        if (isValidDate(year, month, day)) {
            onDateSelected(formatDate(year, month, day))
        }
    }
}

private fun isValidDate(year: Int, month: Int, day: Int): Boolean {
    if (month < 1 || month > 12) return false
    if (day < 1) return false

    val daysInMonth = when (month) {
        2 -> if (isLeapYear(year)) 29 else 28
        4, 6, 9, 11 -> 30
        else -> 31
    }

    return day <= daysInMonth
}

private fun isLeapYear(year: Int): Boolean {
    return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
}