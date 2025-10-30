package org.example.appbbmges.ui.settings.registationex.formulariorolscreen

fun formatPermissionName(permission: String): String {
    return permission
        .replace("_", " ")
        .split(" ")
        .joinToString(" ") { word ->
            word.lowercase().replaceFirstChar { it.uppercase() }
        }
}