package org.example.appbbmges.ui.usuarios.registation.administrativeform

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.example.appbbmges.ui.usuarios.AppColors

@Composable
fun FormProgressIndicator(
    currentStep: AdministrativeFormStep,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        LinearProgressIndicator(
            progress = {
                when (currentStep) {
                    AdministrativeFormStep.PERSONAL_INFO -> 0.20f
                    AdministrativeFormStep.PROFESSIONAL_INFO -> 0.40f
                    AdministrativeFormStep.ADDRESS_INFO -> 0.60f
                    AdministrativeFormStep.ADDITIONAL_INFO -> 0.80f
                    AdministrativeFormStep.CONFIRMATION -> 1.0f
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            color = AppColors.Primary
        )
        Text(
            text = when (currentStep) {
                AdministrativeFormStep.PERSONAL_INFO -> "Datos Personales (Paso 1 de 5)"
                AdministrativeFormStep.PROFESSIONAL_INFO -> "Información Profesional (Paso 2 de 5)"
                AdministrativeFormStep.ADDRESS_INFO -> "Dirección (Paso 3 de 5)"
                AdministrativeFormStep.ADDITIONAL_INFO -> "Información Adicional (Paso 4 de 5)"
                AdministrativeFormStep.CONFIRMATION -> "Confirmación (Paso 5 de 5)"
            },
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}

@Composable
fun FormNavigationButtons(
    currentStep: AdministrativeFormStep,
    onCancel: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(
            onClick = onCancel,
            modifier = Modifier.width(100.dp),
            colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF1E88E5))
        ) {
            Text("Cancelar")
        }
        Spacer(modifier = Modifier.weight(1f))
        if (currentStep != AdministrativeFormStep.PERSONAL_INFO) {
            OutlinedButton(
                onClick = onPrevious,
                modifier = Modifier.width(110.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = AppColors.Primary)
            ) {
                Text("Anterior")
            }
            Spacer(modifier = Modifier.width(8.dp))
        }
        Button(
            onClick = if (currentStep == AdministrativeFormStep.CONFIRMATION) onSubmit else onNext,
            modifier = Modifier.width(if (currentStep == AdministrativeFormStep.CONFIRMATION) 130.dp else 110.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(if (currentStep == AdministrativeFormStep.CONFIRMATION) "Registrar" else "Siguiente")
        }
    }
}