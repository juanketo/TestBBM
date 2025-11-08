package org.example.appbbmges.ui.avatars

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import appbbmges.composeapp.generated.resources.AvatarSistemas1
import appbbmges.composeapp.generated.resources.AvatarSistemas10
import appbbmges.composeapp.generated.resources.AvatarSistemas11
import appbbmges.composeapp.generated.resources.AvatarSistemas2
import appbbmges.composeapp.generated.resources.AvatarSistemas3
import appbbmges.composeapp.generated.resources.AvatarSistemas4
import appbbmges.composeapp.generated.resources.AvatarSistemas5
import appbbmges.composeapp.generated.resources.AvatarSistemas6
import appbbmges.composeapp.generated.resources.AvatarSistemas7
import appbbmges.composeapp.generated.resources.AvatarSistemas8
import appbbmges.composeapp.generated.resources.AvatarSistemas9
import org.jetbrains.compose.resources.painterResource

data class AvatarOption(
    val id: String,
    val name: String
)

object AvatarOptions {
    val avatars = listOf(
        AvatarOption("avatar_01", "Gissel"),
        AvatarOption("avatar_02", "Bailarín Azul"),
        AvatarOption("avatar_03", "Bailarina Coletas"),
        AvatarOption("avatar_04", "Bailarina Flor"),
        AvatarOption("avatar_05", "Bailarina Centro"),
        AvatarOption("avatar_06", "Bailarina Naranja"),
        AvatarOption("avatar_07", "Bailarina Corona"),
        AvatarOption("avatar_08", "Bailarina Morada"),
        AvatarOption("avatar_09", "Bailarín Gorro"),
        AvatarOption("avatar_10", "Bailarín Rosa"),
        AvatarOption("avatar_11", "Bailarín Verde")
    )

    fun getAvatarById(id: String): AvatarOption {
        return avatars.find { it.id == id } ?: avatars.first()
    }
}

@Composable
fun UserAvatar(
    avatarId: String,
    size: Int = 32,
    modifier: Modifier = Modifier,
    showEditIcon: Boolean = false,
    onEditClick: (() -> Unit)? = null
) {
    Box(modifier = modifier) {
        val resourceId = when (avatarId) {
            "avatar_01" -> appbbmges.composeapp.generated.resources.Res.drawable.AvatarSistemas1
            "avatar_02" -> appbbmges.composeapp.generated.resources.Res.drawable.AvatarSistemas2
            "avatar_03" -> appbbmges.composeapp.generated.resources.Res.drawable.AvatarSistemas3
            "avatar_04" -> appbbmges.composeapp.generated.resources.Res.drawable.AvatarSistemas4
            "avatar_05" -> appbbmges.composeapp.generated.resources.Res.drawable.AvatarSistemas5
            "avatar_06" -> appbbmges.composeapp.generated.resources.Res.drawable.AvatarSistemas6
            "avatar_07" -> appbbmges.composeapp.generated.resources.Res.drawable.AvatarSistemas7
            "avatar_08" -> appbbmges.composeapp.generated.resources.Res.drawable.AvatarSistemas8
            "avatar_09" -> appbbmges.composeapp.generated.resources.Res.drawable.AvatarSistemas9
            "avatar_10" -> appbbmges.composeapp.generated.resources.Res.drawable.AvatarSistemas10
            "avatar_11" -> appbbmges.composeapp.generated.resources.Res.drawable.AvatarSistemas11
            else -> appbbmges.composeapp.generated.resources.Res.drawable.AvatarSistemas11
        }

        Image(
            painter = painterResource(resourceId),
            contentDescription = "Avatar",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(size.dp)
                .clip(CircleShape)
                .then(
                    if (onEditClick != null) {
                        Modifier.clickable(onClick = onEditClick)
                    } else Modifier
                )
        )

        if (showEditIcon && onEditClick != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size((size * 0.3).dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(1.dp, Color(0xFFE0E0E0), CircleShape)
                    .padding(2.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Editar avatar",
                    modifier = Modifier.size((size * 0.2).dp),
                    tint = Color(0xFF666666)
                )
            }
        }
    }
}

@Composable
fun AvatarSelectorDialog(
    currentAvatarId: String,
    onAvatarSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "Elige tu avatar",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color(0xFF333333),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "Selecciona tu personaje favorito de Baby Ballet Marbet",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF666666),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.height(400.dp)
                ) {
                    items(AvatarOptions.avatars) { avatar ->
                        AvatarOptionItem(
                            avatar = avatar,
                            isSelected = avatar.id == currentAvatarId,
                            onClick = {
                                onAvatarSelected(avatar.id)
                                onDismiss()
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar", color = Color(0xFF666666))
                    }
                }
            }
        }
    }
}

@Composable
fun AvatarOptionItem(
    avatar: AvatarOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .border(
                    width = if (isSelected) 3.dp else 1.dp,
                    color = if (isSelected) Color(0xFFFF69B4) else Color(0xFFE0E0E0),
                    shape = CircleShape
                )
        ) {
            val resourceId = when (avatar.id) {
                "avatar_01" -> appbbmges.composeapp.generated.resources.Res.drawable.AvatarSistemas1
                "avatar_02" -> appbbmges.composeapp.generated.resources.Res.drawable.AvatarSistemas2
                "avatar_03" -> appbbmges.composeapp.generated.resources.Res.drawable.AvatarSistemas3
                "avatar_04" -> appbbmges.composeapp.generated.resources.Res.drawable.AvatarSistemas4
                "avatar_05" -> appbbmges.composeapp.generated.resources.Res.drawable.AvatarSistemas5
                "avatar_06" -> appbbmges.composeapp.generated.resources.Res.drawable.AvatarSistemas6
                "avatar_07" -> appbbmges.composeapp.generated.resources.Res.drawable.AvatarSistemas7
                "avatar_08" -> appbbmges.composeapp.generated.resources.Res.drawable.AvatarSistemas8
                "avatar_09" -> appbbmges.composeapp.generated.resources.Res.drawable.AvatarSistemas9
                "avatar_10" -> appbbmges.composeapp.generated.resources.Res.drawable.AvatarSistemas10
                "avatar_11" -> appbbmges.composeapp.generated.resources.Res.drawable.AvatarSistemas11
                else -> appbbmges.composeapp.generated.resources.Res.drawable.AvatarSistemas11
            }

            Image(
                painter = painterResource(resourceId),
                contentDescription = avatar.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0x33FF69B4), CircleShape)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = avatar.name,
            style = MaterialTheme.typography.bodySmall,
            color = if (isSelected) Color(0xFFFF69B4) else Color(0xFF666666)
        )
    }
}