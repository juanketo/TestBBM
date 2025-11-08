package org.example.appbbmges.ui.login

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import appbbmges.composeapp.generated.resources.Mascotas_3
import appbbmges.composeapp.generated.resources.Res
import appbbmges.composeapp.generated.resources.logoSystem
import kotlinx.coroutines.delay
import org.example.appbbmges.data.Repository
import org.example.appbbmges.navigation.Screen
import org.example.appbbmges.navigation.SimpleNavController
import org.example.appbbmges.ui.sessions.SessionManager
import org.jetbrains.compose.resources.painterResource

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object LoadingPermissions : LoginState()
    data class Error(val message: String) : LoginState()
    data class Success(val userId: Long, val franchiseId: Long) : LoginState()
}

data class ValidationResult(
    val isValid: Boolean,
    val usernameError: String? = null,
    val passwordError: String? = null
)

class LoginValidator {
    companion object {
        fun validateCredentials(username: String, password: String): ValidationResult {
            val usernameError = when {
                username.isEmpty() -> "El usuario es obligatorio"
                username.length !in 4..16 -> "El usuario debe tener entre 4 y 16 caracteres"
                username.matches(Regex("^[a-zA-Z0-9_]+$")).not() -> "El usuario solo puede contener letras, números y guiones bajos"
                else -> null
            }

            val passwordError = when {
                password.isEmpty() -> "La contraseña es obligatoria"
                password.length !in 8..50 -> "La contraseña debe tener entre 8 y 50 caracteres"
                password.any { it.isUpperCase() }.not() -> "La contraseña debe contener al menos una mayúscula"
                password.any { it.isLowerCase() }.not() -> "La contraseña debe contener al menos una minúscula"
                password.any { it.isDigit() }.not() -> "La contraseña debe contener al menos un número"
                else -> null
            }

            return ValidationResult(
                isValid = usernameError == null && passwordError == null,
                usernameError = usernameError,
                passwordError = passwordError
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: SimpleNavController,
    userRepository: Repository,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var loginState by remember { mutableStateOf<LoginState>(LoginState.Idle) }
    var validationResult by remember { mutableStateOf(ValidationResult(true)) }

    suspend fun performLogin() {
        loginState = LoginState.Loading

        delay(500)

        try {
            val user = userRepository.getUserByUsername(username)

            if (user != null && user.password == password) {
                loginState = LoginState.LoadingPermissions

                SessionManager.initSession(
                    userId = user.id,
                    franchiseId = user.franchise_id,
                    repository = userRepository
                )

                if (SessionManager.isSessionActive()) {
                    loginState = LoginState.Success(user.id, user.franchise_id)

                    navController.navigateTo(Screen.Dashboard(user.id, user.franchise_id))
                } else {
                    loginState = LoginState.Error("Error al cargar permisos del usuario")
                    SessionManager.clearSession()
                }
            } else {
                loginState = LoginState.Error("Usuario o contraseña incorrectos")
            }
        } catch (e: Exception) {
            loginState = LoginState.Error("Error de conexión: ${e.message}")
            e.printStackTrace()
        }
    }

    fun validateAndLogin() {
        val validation = LoginValidator.validateCredentials(username, password)
        validationResult = validation

        if (validation.isValid) {
            loginState = LoginState.Loading
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Fondo con blobs animados
        AnimatedBlobBackground()

        // Contenido original
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Transparent
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .fillMaxHeight(0.75f)
                        .widthIn(max = 1000.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Row(modifier = Modifier.fillMaxSize()) {
                        LeftPanel()

                        RightPanel(
                            username = username,
                            password = password,
                            passwordVisible = passwordVisible,
                            loginState = loginState,
                            validationResult = validationResult,
                            onUsernameChange = {
                                username = it
                                if (validationResult.usernameError != null) {
                                    validationResult = validationResult.copy(usernameError = null)
                                }
                            },
                            onPasswordChange = {
                                password = it
                                if (validationResult.passwordError != null) {
                                    validationResult = validationResult.copy(passwordError = null)
                                }
                            },
                            onPasswordVisibilityToggle = { passwordVisible = passwordVisible.not() },
                            onLoginClick = { validateAndLogin() },
                            onForgotPasswordClick = { /* TODO: Implementar recuperación de contraseña */ },
                            focusManager = focusManager
                        )
                    }
                }
            }
        }
    }

    LaunchedEffect(loginState) {
        if (loginState is LoginState.Loading) {
            performLogin()
        }
    }
}

@Composable
fun AnimatedBlobBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "blob_transition")

    // Animación 1 - Movimiento vertical amplio
    val offsetY1 by infiniteTransition.animateFloat(
        initialValue = -30f,
        targetValue = 80f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset_y1"
    )

    // Animación 2 - Movimiento horizontal amplio
    val offsetX2 by infiniteTransition.animateFloat(
        initialValue = -40f,
        targetValue = 60f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset_x2"
    )

    // Animación 3 - Movimiento diagonal
    val offsetY3 by infiniteTransition.animateFloat(
        initialValue = -60f,
        targetValue = 40f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset_y3"
    )

    val offsetX3 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 50f,
        animationSpec = infiniteRepeatable(
            animation = tween(9000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset_x3"
    )

    // Animación 4 - Escala pulsante
    val scale4 by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(7000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale_4"
    )

    // Animación 5 - Movimiento circular
    val offsetX5 by infiniteTransition.animateFloat(
        initialValue = -30f,
        targetValue = 30f,
        animationSpec = infiniteRepeatable(
            animation = tween(11000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset_x5"
    )

    val offsetY5 by infiniteTransition.animateFloat(
        initialValue = 20f,
        targetValue = -50f,
        animationSpec = infiniteRepeatable(
            animation = tween(9500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset_y5"
    )

    // Animación 6 - Escala lenta
    val scale6 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(13000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale_6"
    )

    // Animación 7 - Movimiento vertical rápido
    val offsetY7 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 70f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset_y7"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF9c27b0), // Morado profundo
                        Color(0xFFbf0cb9), // Morado medio
                        Color(0xFFff8abe)  // Rosa
                    )
                )
            )
    ) {
        // Blob 1 - Grande superior derecha
        Box(
            modifier = Modifier
                .size(550.dp)
                .offset(x = 320.dp, y = (-150).dp + offsetY1.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFffc1e3).copy(alpha = 0.45f),
                            Color(0xFFff8abe).copy(alpha = 0.28f),
                            Color(0xFFff8abe).copy(alpha = 0.12f),
                            Color.Transparent
                        ),
                        radius = 850f
                    ),
                    shape = CircleShape
                )
        )

        // Blob 2 - Mediano izquierda con movimiento horizontal
        Box(
            modifier = Modifier
                .size(420.dp)
                .offset(x = (-120).dp + offsetX2.dp, y = 180.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFbf0cb9).copy(alpha = 0.4f),
                            Color(0xFF9c27b0).copy(alpha = 0.25f),
                            Color(0xFF9c27b0).copy(alpha = 0.12f),
                            Color.Transparent
                        ),
                        radius = 750f
                    ),
                    shape = CircleShape
                )
        )

        // Blob 3 - Grande inferior con movimiento diagonal
        Box(
            modifier = Modifier
                .size(600.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 180.dp + offsetX3.dp, y = 220.dp + offsetY3.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFff8abe).copy(alpha = 0.35f),
                            Color(0xFFbf0cb9).copy(alpha = 0.2f),
                            Color(0xFFbf0cb9).copy(alpha = 0.1f),
                            Color.Transparent
                        ),
                        radius = 950f
                    ),
                    shape = CircleShape
                )
        )

        // Blob 4 - Centro con escala pulsante
        Box(
            modifier = Modifier
                .size(380.dp * scale4)
                .align(Alignment.Center)
                .offset(x = (-100).dp, y = 130.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFffc1e3).copy(alpha = 0.3f),
                            Color(0xFFff8abe).copy(alpha = 0.15f),
                            Color.Transparent
                        ),
                        radius = 650f
                    ),
                    shape = CircleShape
                )
        )

        // Blob 5 - Superior izquierda con movimiento circular
        Box(
            modifier = Modifier
                .size(350.dp)
                .offset(x = (-80).dp + offsetX5.dp, y = 30.dp + offsetY5.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFbf0cb9).copy(alpha = 0.25f),
                            Color(0xFF9c27b0).copy(alpha = 0.12f),
                            Color.Transparent
                        ),
                        radius = 550f
                    ),
                    shape = CircleShape
                )
        )

        // Blob 6 - Centro derecha con escala lenta
        Box(
            modifier = Modifier
                .size(320.dp * scale6)
                .align(Alignment.CenterEnd)
                .offset(x = 100.dp, y = (-50).dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFff8abe).copy(alpha = 0.25f),
                            Color(0xFFffc1e3).copy(alpha = 0.12f),
                            Color.Transparent
                        ),
                        radius = 500f
                    ),
                    shape = CircleShape
                )
        )

        // Blob 7 - Inferior izquierda con movimiento vertical rápido
        Box(
            modifier = Modifier
                .size(400.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-50).dp, y = 150.dp + offsetY7.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFbf0cb9).copy(alpha = 0.3f),
                            Color(0xFFff8abe).copy(alpha = 0.15f),
                            Color.Transparent
                        ),
                        radius = 600f
                    ),
                    shape = CircleShape
                )
        )

        // Blob 8 - Pequeño arriba centro
        Box(
            modifier = Modifier
                .size(280.dp)
                .align(Alignment.TopCenter)
                .offset(x = 50.dp, y = (-80).dp + (offsetY1 * 0.6f).dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFffc1e3).copy(alpha = 0.2f),
                            Color.Transparent
                        ),
                        radius = 400f
                    ),
                    shape = CircleShape
                )
        )

        // Blob 9 - Mediano centro izquierda
        Box(
            modifier = Modifier
                .size(340.dp)
                .align(Alignment.CenterStart)
                .offset(x = (-60).dp + (offsetX2 * 0.5f).dp, y = 80.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF9c27b0).copy(alpha = 0.22f),
                            Color(0xFFbf0cb9).copy(alpha = 0.1f),
                            Color.Transparent
                        ),
                        radius = 520f
                    ),
                    shape = CircleShape
                )
        )

        // Blob 10 - Pequeño inferior derecha
        Box(
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 50.dp + (offsetX3 * 0.7f).dp, y = 80.dp + (offsetY7 * 0.4f).dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFff8abe).copy(alpha = 0.2f),
                            Color.Transparent
                        ),
                        radius = 450f
                    ),
                    shape = CircleShape
                )
        )
    }
}

@Composable
private fun RowScope.LeftPanel() {
    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFbf0cb9), // Morado
                        Color(0xFFff8abe)  // Rosa
                    )
                ),
                shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)
            )
    ) {
        // Bolitas rebotando en el fondo
        BouncingBlobsInPanel()

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(30.dp))

            Image(
                painter = painterResource(Res.drawable.logoSystem),
                contentDescription = "Logo de la aplicación",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .padding(horizontal = 24.dp)
                    .semantics {
                        contentDescription = "Logo de la aplicación"
                    },
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Bienvenido",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            Image(
                painter = painterResource(Res.drawable.Mascotas_3),
                contentDescription = "Niños jugando",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .semantics {
                        contentDescription = "Ilustración de niños jugando"
                    },
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun BouncingBlobsInPanel() {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val blobSize = 150.dp
        val blobSizePx = with(LocalDensity.current) { blobSize.toPx() }
        val containerWidthPx = with(LocalDensity.current) { maxWidth.toPx() }
        val containerHeightPx = with(LocalDensity.current) { maxHeight.toPx() }
        val radius = blobSizePx / 2f

        // Inicializar posiciones basadas en el tamaño del contenedor
        var blob1X by remember { mutableStateOf((containerWidthPx * 0.2f).coerceIn(0f, containerWidthPx - blobSizePx)) }
        var blob1Y by remember { mutableStateOf((containerHeightPx * 0.2f).coerceIn(0f, containerHeightPx - blobSizePx)) }
        var blob1VelX by remember { mutableStateOf(1.2f) }
        var blob1VelY by remember { mutableStateOf(0.9f) }

        var blob2X by remember { mutableStateOf((containerWidthPx * 0.5f).coerceIn(0f, containerWidthPx - blobSizePx)) }
        var blob2Y by remember { mutableStateOf((containerHeightPx * 0.6f).coerceIn(0f, containerHeightPx - blobSizePx)) }
        var blob2VelX by remember { mutableStateOf(-1.0f) }
        var blob2VelY by remember { mutableStateOf(0.8f) }

        var blob3X by remember { mutableStateOf((containerWidthPx * 0.7f).coerceIn(0f, containerWidthPx - blobSizePx)) }
        var blob3Y by remember { mutableStateOf((containerHeightPx * 0.3f).coerceIn(0f, containerHeightPx - blobSizePx)) }
        var blob3VelX by remember { mutableStateOf(0.9f) }
        var blob3VelY by remember { mutableStateOf(-1.1f) }

        // Actualizar posiciones en cada frame
        LaunchedEffect(containerWidthPx, containerHeightPx) {
            while (true) {
                withFrameMillis {
                    // Actualizar posiciones
                    blob1X += blob1VelX
                    blob1Y += blob1VelY
                    blob2X += blob2VelX
                    blob2Y += blob2VelY
                    blob3X += blob3VelX
                    blob3Y += blob3VelY

                    // Rebote en los bordes - Bolita 1
                    if (blob1X <= 0f) {
                        blob1X = 0f
                        blob1VelX = kotlin.math.abs(blob1VelX)
                    }
                    if (blob1X >= containerWidthPx - blobSizePx) {
                        blob1X = containerWidthPx - blobSizePx
                        blob1VelX = -kotlin.math.abs(blob1VelX)
                    }
                    if (blob1Y <= 0f) {
                        blob1Y = 0f
                        blob1VelY = kotlin.math.abs(blob1VelY)
                    }
                    if (blob1Y >= containerHeightPx - blobSizePx) {
                        blob1Y = containerHeightPx - blobSizePx
                        blob1VelY = -kotlin.math.abs(blob1VelY)
                    }

                    // Rebote en los bordes - Bolita 2
                    if (blob2X <= 0f) {
                        blob2X = 0f
                        blob2VelX = kotlin.math.abs(blob2VelX)
                    }
                    if (blob2X >= containerWidthPx - blobSizePx) {
                        blob2X = containerWidthPx - blobSizePx
                        blob2VelX = -kotlin.math.abs(blob2VelX)
                    }
                    if (blob2Y <= 0f) {
                        blob2Y = 0f
                        blob2VelY = kotlin.math.abs(blob2VelY)
                    }
                    if (blob2Y >= containerHeightPx - blobSizePx) {
                        blob2Y = containerHeightPx - blobSizePx
                        blob2VelY = -kotlin.math.abs(blob2VelY)
                    }

                    // Rebote en los bordes - Bolita 3
                    if (blob3X <= 0f) {
                        blob3X = 0f
                        blob3VelX = kotlin.math.abs(blob3VelX)
                    }
                    if (blob3X >= containerWidthPx - blobSizePx) {
                        blob3X = containerWidthPx - blobSizePx
                        blob3VelX = -kotlin.math.abs(blob3VelX)
                    }
                    if (blob3Y <= 0f) {
                        blob3Y = 0f
                        blob3VelY = kotlin.math.abs(blob3VelY)
                    }
                    if (blob3Y >= containerHeightPx - blobSizePx) {
                        blob3Y = containerHeightPx - blobSizePx
                        blob3VelY = -kotlin.math.abs(blob3VelY)
                    }

                    // Detectar colisión entre bolita 1 y 2
                    val dx12 = (blob2X + radius) - (blob1X + radius)
                    val dy12 = (blob2Y + radius) - (blob1Y + radius)
                    val distance12 = kotlin.math.sqrt(dx12 * dx12 + dy12 * dy12)

                    if (distance12 < blobSizePx * 0.95f) {
                        // Intercambiar velocidades
                        val tempVelX = blob1VelX
                        val tempVelY = blob1VelY
                        blob1VelX = blob2VelX
                        blob1VelY = blob2VelY
                        blob2VelX = tempVelX
                        blob2VelY = tempVelY

                        // Separar las bolitas para evitar que se peguen
                        val angle = kotlin.math.atan2(dy12.toDouble(), dx12.toDouble()).toFloat()
                        val overlap = (blobSizePx * 0.95f - distance12) / 2f
                        blob1X -= kotlin.math.cos(angle.toDouble()).toFloat() * overlap
                        blob1Y -= kotlin.math.sin(angle.toDouble()).toFloat() * overlap
                        blob2X += kotlin.math.cos(angle.toDouble()).toFloat() * overlap
                        blob2Y += kotlin.math.sin(angle.toDouble()).toFloat() * overlap
                    }

                    // Detectar colisión entre bolita 1 y 3
                    val dx13 = (blob3X + radius) - (blob1X + radius)
                    val dy13 = (blob3Y + radius) - (blob1Y + radius)
                    val distance13 = kotlin.math.sqrt(dx13 * dx13 + dy13 * dy13)

                    if (distance13 < blobSizePx * 0.95f) {
                        val tempVelX = blob1VelX
                        val tempVelY = blob1VelY
                        blob1VelX = blob3VelX
                        blob1VelY = blob3VelY
                        blob3VelX = tempVelX
                        blob3VelY = tempVelY

                        val angle = kotlin.math.atan2(dy13.toDouble(), dx13.toDouble()).toFloat()
                        val overlap = (blobSizePx * 0.95f - distance13) / 2f
                        blob1X -= kotlin.math.cos(angle.toDouble()).toFloat() * overlap
                        blob1Y -= kotlin.math.sin(angle.toDouble()).toFloat() * overlap
                        blob3X += kotlin.math.cos(angle.toDouble()).toFloat() * overlap
                        blob3Y += kotlin.math.sin(angle.toDouble()).toFloat() * overlap
                    }

                    // Detectar colisión entre bolita 2 y 3
                    val dx23 = (blob3X + radius) - (blob2X + radius)
                    val dy23 = (blob3Y + radius) - (blob2Y + radius)
                    val distance23 = kotlin.math.sqrt(dx23 * dx23 + dy23 * dy23)

                    if (distance23 < blobSizePx * 0.95f) {
                        val tempVelX = blob2VelX
                        val tempVelY = blob2VelY
                        blob2VelX = blob3VelX
                        blob2VelY = blob3VelY
                        blob3VelX = tempVelX
                        blob3VelY = tempVelY

                        val angle = kotlin.math.atan2(dy23.toDouble(), dx23.toDouble()).toFloat()
                        val overlap = (blobSizePx * 0.95f - distance23) / 2f
                        blob2X -= kotlin.math.cos(angle.toDouble()).toFloat() * overlap
                        blob2Y -= kotlin.math.sin(angle.toDouble()).toFloat() * overlap
                        blob3X += kotlin.math.cos(angle.toDouble()).toFloat() * overlap
                        blob3Y += kotlin.math.sin(angle.toDouble()).toFloat() * overlap
                    }
                }
            }
        }

        // Bolita 1 - Rosa claro
        Box(
            modifier = Modifier
                .size(blobSize)
                .offset(
                    x = with(LocalDensity.current) { blob1X.toDp() },
                    y = with(LocalDensity.current) { blob1Y.toDp() }
                )
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFffc1e3).copy(alpha = 0.8f),
                            Color(0xFFff8abe).copy(alpha = 0.5f),
                            Color(0xFFff8abe).copy(alpha = 0.3f),
                            Color.Transparent
                        ),
                        radius = 280f
                    ),
                    shape = CircleShape
                )
        )

        // Bolita 2 - Morado
        Box(
            modifier = Modifier
                .size(blobSize)
                .offset(
                    x = with(LocalDensity.current) { blob2X.toDp() },
                    y = with(LocalDensity.current) { blob2Y.toDp() }
                )
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF9c27b0).copy(alpha = 0.75f),
                            Color(0xFFbf0cb9).copy(alpha = 0.45f),
                            Color(0xFFbf0cb9).copy(alpha = 0.25f),
                            Color.Transparent
                        ),
                        radius = 280f
                    ),
                    shape = CircleShape
                )
        )

        // Bolita 3 - Rosa intenso
        Box(
            modifier = Modifier
                .size(blobSize)
                .offset(
                    x = with(LocalDensity.current) { blob3X.toDp() },
                    y = with(LocalDensity.current) { blob3Y.toDp() }
                )
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFff8abe).copy(alpha = 0.78f),
                            Color(0xFFffc1e3).copy(alpha = 0.48f),
                            Color(0xFFffc1e3).copy(alpha = 0.28f),
                            Color.Transparent
                        ),
                        radius = 280f
                    ),
                    shape = CircleShape
                )
        )
    }
}

@Composable
private fun RowScope.RightPanel(
    username: String,
    password: String,
    passwordVisible: Boolean,
    loginState: LoginState,
    validationResult: ValidationResult,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onPasswordVisibilityToggle: () -> Unit,
    onLoginClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    focusManager: androidx.compose.ui.focus.FocusManager
) {
    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Iniciar Sesión",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            OutlinedTextField(
                value = username,
                onValueChange = onUsernameChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .semantics {
                        contentDescription = "Campo de nombre de usuario"
                    },
                label = { Text("Nombre de usuario") },
                placeholder = { Text("Ingresa tu usuario") },
                singleLine = true,
                enabled = loginState !is LoginState.Loading && loginState !is LoginState.LoadingPermissions,
                isError = validationResult.usernameError != null,
                supportingText = {
                    validationResult.usernameError?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Icono de usuario"
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFbf0cb9),
                    focusedLabelColor = Color(0xFFbf0cb9)
                )
            )

            OutlinedTextField(
                value = password,
                onValueChange = onPasswordChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .semantics {
                        contentDescription = "Campo de contraseña"
                    },
                label = { Text("Contraseña") },
                placeholder = { Text("Ingresa tu contraseña") },
                singleLine = true,
                enabled = loginState !is LoginState.Loading && loginState !is LoginState.LoadingPermissions,
                isError = validationResult.passwordError != null,
                supportingText = {
                    validationResult.passwordError?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                visualTransformation = if (passwordVisible)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Icono de contraseña"
                    )
                },
                trailingIcon = {
                    val icon = if (passwordVisible)
                        Icons.Default.Visibility
                    else
                        Icons.Default.VisibilityOff

                    IconButton(
                        onClick = onPasswordVisibilityToggle,
                        enabled = loginState !is LoginState.Loading && loginState !is LoginState.LoadingPermissions
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = if (passwordVisible)
                                "Ocultar contraseña"
                            else
                                "Mostrar contraseña"
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        onLoginClick()
                    }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFbf0cb9),
                    focusedLabelColor = Color(0xFFbf0cb9)
                )
            )

            if (loginState is LoginState.Error) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = loginState.message,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            Text(
                text = "¿Olvidaste tu contraseña?",
                color = Color(0xFFbf0cb9),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(bottom = 24.dp)
                    .clickable(enabled = loginState !is LoginState.Loading && loginState !is LoginState.LoadingPermissions) {
                        onForgotPasswordClick()
                    }
                    .semantics {
                        contentDescription = "Enlace para recuperar contraseña"
                    }
            )

            Button(
                onClick = onLoginClick,
                enabled = loginState !is LoginState.Loading && loginState !is LoginState.LoadingPermissions,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFff8abe),
                    disabledContainerColor = Color(0xFFff8abe).copy(alpha = 0.6f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                when (loginState) {
                    is LoginState.Loading -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "Iniciando sesión...",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    is LoginState.LoadingPermissions -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "Cargando permisos...",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    else -> {
                        Text(
                            "Iniciar Sesión",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                )
                Text(
                    text = "  o  ",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    style = MaterialTheme.typography.bodySmall
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "¿Necesitas ayuda? Contacta al administrador",
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
        }
    }
}