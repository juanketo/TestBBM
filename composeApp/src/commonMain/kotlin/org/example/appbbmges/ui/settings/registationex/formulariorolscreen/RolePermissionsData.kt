package org.example.appbbmges.ui.settings.registationex.formulariorolscreen

import androidx.compose.ui.graphics.Color

val permissionsByModule = mapOf(
    "Usuarios" to listOf("USUARIOS_VER", "USUARIOS_CREAR", "USUARIOS_EDITAR", "USUARIOS_ELIMINAR"),
    "Roles" to listOf("ROLES_CREAR", "ROLES_EDITAR", "ROLES_ELIMINAR"),
    "Franquicias" to listOf("FRANQUICIAS_CREAR", "FRANQUICIAS_EDITAR", "FRANQUICIAS_ELIMINAR", "FRANQUICIAS_VER"),
    "Salones" to listOf("SALONES_CREAR", "SALONES_EDITAR", "SALONES_ELIMINAR", "SALONES_VER"),
    "Niveles" to listOf("NIVELES_CREAR", "NIVELES_EDITAR", "NIVELES_ELIMINAR", "NIVELES_VER"),
    "Disciplinas" to listOf("DISCIPLINAS_CREAR", "DISCIPLINAS_EDITAR", "DISCIPLINAS_ELIMINAR", "DISCIPLINAS_VER"),
    "Alumnos" to listOf("ALUMNOS_CREAR", "ALUMNOS_EDITAR", "ALUMNOS_ELIMINAR", "ALUMNOS_VER"),
    "Profesores" to listOf("PROFESORES_CREAR", "PROFESORES_EDITAR", "PROFESORES_ELIMINAR", "PROFESORES_VER"),
    "Administrativos" to listOf("ADMINISTRATIVOS_CREAR", "ADMINISTRATIVOS_EDITAR", "ADMINISTRATIVOS_ELIMINAR", "ADMINISTRATIVOS_VER"),
    "Horarios" to listOf("HORARIOS_CREAR", "HORARIOS_EDITAR", "HORARIOS_ELIMINAR", "HORARIOS_VER"),
    "Asistencias" to listOf("ASISTENCIAS_REGISTRAR", "ASISTENCIAS_VER"),
    "Pagos" to listOf("PAGOS_VER", "PAGOS_REGISTRAR"),
    "Promociones" to listOf("PROMOCIONES_CREAR", "PROMOCIONES_EDITAR", "PROMOCIONES_ELIMINAR", "PROMOCIONES_VER"),
    "Cuotas" to listOf("CUOTAS_CREAR", "CUOTAS_EDITAR", "CUOTAS_ELIMINAR", "CUOTAS_VER"),
    "Productos" to listOf("PRODUCTOS_CREAR", "PRODUCTOS_EDITAR", "PRODUCTOS_ELIMINAR", "PRODUCTOS_VER"),
    "Membresías" to listOf("MEMBRESIAS_CREAR", "MEMBRESIAS_EDITAR", "MEMBRESIAS_ELIMINAR", "MEMBRESIAS_VER"),
    "Eventos" to listOf("EVENTOS_CREAR", "EVENTOS_EDITAR", "EVENTOS_ELIMINAR", "EVENTOS_VER"),
    "Reportes Maestros" to listOf("REPORTES_MAESTROS_VER", "REPORTES_MAESTROS_CREAR"),
    "Clase Muestra" to listOf("CLASE_MUESTRA_CREAR", "CLASE_MUESTRA_VER"),
    "Perfil" to listOf("PERFIL_VER", "PERFIL_EDITAR"),
    "Configuración" to listOf("CONFIG_AVANZADA_VER", "RESPALDO_DATOS"),
    "Dashboard" to listOf("DASHBOARD_VER")
)

fun getModuleColor(module: String): Color = when (module) {
    "Usuarios" -> Color(0xFF1976D2)
    "Roles" -> Color(0xFF7B1FA2)
    "Franquicias" -> Color(0xFFD32F2F)
    "Salones" -> Color(0xFF388E3C)
    "Niveles" -> Color(0xFFFF6F00)
    "Disciplinas" -> Color(0xFF0288D1)
    "Alumnos" -> Color(0xFF00897B)
    "Profesores" -> Color(0xFF5E35B1)
    "Administrativos" -> Color(0xFFC62828)
    "Horarios" -> Color(0xFF2E7D32)
    "Asistencias" -> Color(0xFF00ACC1)
    "Pagos" -> Color(0xFF6A1B9A)
    "Promociones" -> Color(0xFFF57C00)
    "Cuotas" -> Color(0xFF0277BD)
    "Productos" -> Color(0xFF558B2F)
    "Membresías" -> Color(0xFF4527A0)
    "Eventos" -> Color(0xFFE64A19)
    "Reportes Maestros" -> Color(0xFF1565C0)
    "Clase Muestra" -> Color(0xFF00897B)
    "Perfil" -> Color(0xFF5D4037)
    "Configuración" -> Color(0xFFD84315)
    "Dashboard" -> Color(0xFF283593)
    else -> Color(0xFF1976D2)
}