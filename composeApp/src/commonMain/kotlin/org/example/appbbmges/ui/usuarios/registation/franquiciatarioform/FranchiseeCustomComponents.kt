package org.example.appbbmges.ui.usuarios.registation.franquiciatarioform

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
    currentStep: FranchiseeFormStep,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        LinearProgressIndicator(
            progress = {
                when (currentStep) {
                    FranchiseeFormStep.PERSONAL_INFO -> 0.25f
                    FranchiseeFormStep.ADDRESS_INFO -> 0.50f
                    FranchiseeFormStep.ADDITIONAL_INFO -> 0.75f
                    FranchiseeFormStep.CONFIRMATION -> 1.0f
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            color = AppColors.Primary
        )
        Text(
            text = when (currentStep) {
                FranchiseeFormStep.PERSONAL_INFO -> "Datos Personales (Paso 1 de 4)"
                FranchiseeFormStep.ADDRESS_INFO -> "Dirección (Paso 2 de 4)"
                FranchiseeFormStep.ADDITIONAL_INFO -> "Información Adicional (Paso 3 de 4)"
                FranchiseeFormStep.CONFIRMATION -> "Confirmación (Paso 4 de 4)"
            },
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}

@Composable
fun FormNavigationButtons(
    currentStep: FranchiseeFormStep,
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
        if (currentStep != FranchiseeFormStep.PERSONAL_INFO) {
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
            onClick = if (currentStep == FranchiseeFormStep.CONFIRMATION) onSubmit else onNext,
            modifier = Modifier.width(if (currentStep == FranchiseeFormStep.CONFIRMATION) 130.dp else 110.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(if (currentStep == FranchiseeFormStep.CONFIRMATION) "Registrar" else "Siguiente")
        }
    }
}