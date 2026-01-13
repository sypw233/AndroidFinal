package ovo.sypw.wmx420.androidfinal.ui.screens.me.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.delay
import org.koin.compose.koinInject
import ovo.sypw.androidendproject.ui.screens.login.LoginUiState
import ovo.sypw.androidendproject.ui.screens.login.LoginViewModel
import ovo.sypw.wmx420.androidfinal.R

/**
 * 登录方式枚举
 */
private enum class LoginMethod {
    EMAIL,
    PHONE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onBack: () -> Unit,
    viewModel: LoginViewModel = koinInject()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    // 登录方式选择
    var selectedLoginMethod by remember { mutableStateOf(LoginMethod.EMAIL) }

    // Email/Password states
    var isRegisterMode by remember { mutableStateOf(false) }
    var isForgotPasswordMode by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Phone login states
    var phoneNumber by remember { mutableStateOf("") }
    var verificationCode by remember { mutableStateOf("") }
    var countdownSeconds by remember { mutableIntStateOf(0) }

    // 倒计时效果
    LaunchedEffect(countdownSeconds) {
        if (countdownSeconds > 0) {
            delay(1000)
            countdownSeconds--
        }
    }

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                account?.let {
                    viewModel.signInWithGoogle(it)
                }
            } catch (e: ApiException) {
                Log.w("LoginScreen", "Google sign in failed", e)
                Toast.makeText(context, "Google 登录失败: ${e.statusCode}", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    LaunchedEffect(uiState) {
        Log.d("LoginScreen", "LaunchedEffect: uiState 变化, uiState=$uiState")
        when (uiState) {
            is LoginUiState.Success -> {
                Toast.makeText(context, "登录成功", Toast.LENGTH_SHORT).show()
                onLoginSuccess()
                viewModel.resetState()
            }

            is LoginUiState.VerificationRequired -> {
                Toast.makeText(context, "验证邮件已发送，请查收邮箱并验证后登录", Toast.LENGTH_LONG)
                    .show()
                viewModel.resetState()
            }

            is LoginUiState.PasswordResetEmailSent -> {
                Toast.makeText(context, "密码重置邮件已发送，请查收邮箱", Toast.LENGTH_LONG)
                    .show()
                isForgotPasswordMode = false
                viewModel.resetState()
            }

            is LoginUiState.PhoneCodeSent -> {
                Toast.makeText(context, "验证码已发送", Toast.LENGTH_SHORT).show()
                countdownSeconds = 60  // 开始 60 秒倒计时
            }

            is LoginUiState.PhoneVerifying -> {
                Log.d("LoginScreen", "LaunchedEffect: 正在验证手机验证码")
            }

            is LoginUiState.Error -> {
                Log.d(
                    "LoginScreen",
                    "LaunchedEffect: 处理 Error 状态, message=${(uiState as LoginUiState.Error).message}"
                )
                snackbarHostState.showSnackbar((uiState as LoginUiState.Error).message)
            }

            is LoginUiState.Loading -> {
                Log.d("LoginScreen", "LaunchedEffect: 状态为 Loading")
            }

            is LoginUiState.Idle -> {
                Log.d("LoginScreen", "LaunchedEffect: 状态为 Idle")
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        when {
                            isForgotPasswordMode -> "忘记密码"
                            uiState is LoginUiState.PhoneCodeSent -> "输入验证码"
                            isRegisterMode -> "注册"
                            else -> "登录"
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        when {
                            uiState is LoginUiState.PhoneCodeSent -> {
                                // 返回手机号输入界面
                                viewModel.backToPhoneInput()
                                countdownSeconds = 0
                            }
                            isForgotPasswordMode -> {
                                isForgotPasswordMode = false
                            }
                            else -> {
                                onBack()
                            }
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            // Logo or Welcome Text
            Text(
                text = "欢迎登录",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // 当处于验证码输入状态时，显示验证码输入界面
            if (uiState is LoginUiState.PhoneCodeSent) {
                PhoneVerificationContent(
                    phoneNumber = phoneNumber,
                    verificationCode = verificationCode,
                    onVerificationCodeChange = { verificationCode = it },
                    countdownSeconds = countdownSeconds,
                    isLoading = uiState is LoginUiState.PhoneVerifying,
                    onVerify = { viewModel.verifyPhoneCode(verificationCode) },
                    onResend = {
                        (context as? Activity)?.let { activity ->
                            viewModel.resendPhoneCode(activity)
                        }
                    }
                )
            } else if (isForgotPasswordMode) {
                // 忘记密码模式
                ForgotPasswordContent(
                    email = email,
                    onEmailChange = { email = it },
                    isLoading = uiState is LoginUiState.Loading,
                    onSubmit = { viewModel.sendPasswordResetEmail(email) },
                    onBack = { isForgotPasswordMode = false }
                )
            } else {
                // 登录方式选择 Tab
                TabRow(
                    selectedTabIndex = selectedLoginMethod.ordinal,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Tab(
                        selected = selectedLoginMethod == LoginMethod.EMAIL,
                        onClick = { selectedLoginMethod = LoginMethod.EMAIL },
                        text = { Text("邮箱登录") }
                    )
                    Tab(
                        selected = selectedLoginMethod == LoginMethod.PHONE,
                        onClick = { selectedLoginMethod = LoginMethod.PHONE },
                        text = { Text("手机号登录") }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                when (selectedLoginMethod) {
                    LoginMethod.EMAIL -> {
                        EmailLoginContent(
                            email = email,
                            password = password,
                            passwordVisible = passwordVisible,
                            isRegisterMode = isRegisterMode,
                            isLoading = uiState is LoginUiState.Loading,
                            onEmailChange = { email = it },
                            onPasswordChange = { password = it },
                            onPasswordVisibilityChange = { passwordVisible = it },
                            onSubmit = {
                                if (isRegisterMode) {
                                    viewModel.signUpWithEmail(email, password)
                                } else {
                                    viewModel.signInWithEmail(email, password)
                                }
                            },
                            onForgotPassword = { isForgotPasswordMode = true },
                            onToggleMode = { isRegisterMode = !isRegisterMode },
                            onGoogleSignIn = { launchGoogleSignIn(context, googleSignInLauncher) }
                        )
                    }

                    LoginMethod.PHONE -> {
                        PhoneLoginContent(
                            phoneNumber = phoneNumber,
                            isLoading = uiState is LoginUiState.Loading,
                            onPhoneNumberChange = { phoneNumber = it },
                            onSubmit = {
                                (context as? Activity)?.let { activity ->
                                    viewModel.startPhoneVerification(phoneNumber, activity)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * 邮箱登录内容
 */
@Composable
private fun EmailLoginContent(
    email: String,
    password: String,
    passwordVisible: Boolean,
    isRegisterMode: Boolean,
    isLoading: Boolean,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onPasswordVisibilityChange: (Boolean) -> Unit,
    onSubmit: () -> Unit,
    onForgotPassword: () -> Unit,
    onToggleMode: () -> Unit,
    onGoogleSignIn: () -> Unit
) {
    OutlinedTextField(
        value = email,
        onValueChange = onEmailChange,
        label = { Text("电子邮箱") },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        ),
        singleLine = true
    )

    Spacer(modifier = Modifier.height(16.dp))

    OutlinedTextField(
        value = password,
        onValueChange = onPasswordChange,
        label = { Text("密码") },
        modifier = Modifier.fillMaxWidth(),
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        ),
        trailingIcon = {
            val image = if (passwordVisible)
                Icons.Filled.Visibility
            else
                Icons.Filled.VisibilityOff

            IconButton(onClick = { onPasswordVisibilityChange(!passwordVisible) }) {
                Icon(imageVector = image, contentDescription = null)
            }
        },
        singleLine = true
    )

    Spacer(modifier = Modifier.height(24.dp))

    if (isLoading) {
        CircularProgressIndicator()
    } else {
        Button(
            onClick = onSubmit,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isRegisterMode) "注册" else "登录")
        }

        // 仅在登录模式下显示忘记密码
        if (!isRegisterMode) {
            TextButton(onClick = onForgotPassword) {
                Text("忘记密码？")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Toggle Mode
        TextButton(onClick = onToggleMode) {
            Text(if (isRegisterMode) "已有账号？去登录" else "没有账号？去注册")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Spacer(
                modifier = Modifier
                    .weight(1f)
                    .height(1.dp)
                    .padding(end = 8.dp)
            )
            Text("其他登录方式", style = MaterialTheme.typography.bodySmall)
            Spacer(
                modifier = Modifier
                    .weight(1f)
                    .height(1.dp)
                    .padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Google Sign In Button
        OutlinedButton(
            onClick = onGoogleSignIn,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Google 登录")
        }
    }
}

/**
 * 手机号登录内容
 */
@Composable
private fun PhoneLoginContent(
    phoneNumber: String,
    isLoading: Boolean,
    onPhoneNumberChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    Text(
        text = "输入您的手机号，我们将发送验证码",
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(bottom = 16.dp)
    )

    OutlinedTextField(
        value = phoneNumber,
        onValueChange = onPhoneNumberChange,
        label = { Text("手机号") },
        placeholder = { Text("请输入手机号") },
        prefix = { Text("+86 ") },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Phone,
            imeAction = ImeAction.Done
        ),
        singleLine = true
    )

    Spacer(modifier = Modifier.height(24.dp))

    if (isLoading) {
        CircularProgressIndicator()
    } else {
        Button(
            onClick = onSubmit,
            modifier = Modifier.fillMaxWidth(),
            enabled = phoneNumber.isNotBlank()
        ) {
            Text("发送验证码")
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = "点击发送验证码即表示您同意我们的服务条款",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}

/**
 * 验证码输入内容
 */
@Composable
private fun PhoneVerificationContent(
    phoneNumber: String,
    verificationCode: String,
    onVerificationCodeChange: (String) -> Unit,
    countdownSeconds: Int,
    isLoading: Boolean,
    onVerify: () -> Unit,
    onResend: () -> Unit
) {
    Text(
        text = "验证码已发送至 +86 $phoneNumber",
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(bottom = 24.dp)
    )

    OutlinedTextField(
        value = verificationCode,
        onValueChange = { if (it.length <= 6) onVerificationCodeChange(it) },
        label = { Text("验证码") },
        placeholder = { Text("请输入 6 位验证码") },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        singleLine = true
    )

    Spacer(modifier = Modifier.height(24.dp))

    if (isLoading) {
        CircularProgressIndicator()
    } else {
        Button(
            onClick = onVerify,
            modifier = Modifier.fillMaxWidth(),
            enabled = verificationCode.length == 6
        ) {
            Text("验证并登录")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "没有收到验证码？",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.width(4.dp))
            if (countdownSeconds > 0) {
                Text(
                    text = "${countdownSeconds}s 后重发",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                TextButton(onClick = onResend) {
                    Text("重新发送")
                }
            }
        }
    }
}

/**
 * 忘记密码内容
 */
@Composable
private fun ForgotPasswordContent(
    email: String,
    onEmailChange: (String) -> Unit,
    isLoading: Boolean,
    onSubmit: () -> Unit,
    onBack: () -> Unit
) {
    Text(
        text = "输入您的邮箱地址，我们将发送密码重置链接",
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(bottom = 24.dp)
    )

    OutlinedTextField(
        value = email,
        onValueChange = onEmailChange,
        label = { Text("电子邮箱") },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Done
        ),
        singleLine = true
    )

    Spacer(modifier = Modifier.height(24.dp))

    if (isLoading) {
        CircularProgressIndicator()
    } else {
        Button(
            onClick = onSubmit,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("发送重置邮件")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onBack) {
            Text("返回登录")
        }
    }
}

private fun launchGoogleSignIn(
    context: Context,
    launcher: ActivityResultLauncher<Intent>
) {
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()

    val googleSignInClient = GoogleSignIn.getClient(context, gso)
    launcher.launch(googleSignInClient.signInIntent)
}

