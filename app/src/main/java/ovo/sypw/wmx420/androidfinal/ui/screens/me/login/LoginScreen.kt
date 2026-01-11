package ovo.sypw.wmx420.androidfinal.ui.screens.me.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import org.koin.compose.koinInject
import ovo.sypw.androidendproject.ui.screens.login.LoginUiState
import ovo.sypw.androidendproject.ui.screens.login.LoginViewModel
import ovo.sypw.wmx420.androidfinal.R

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

    // Email/Password states
    var isRegisterMode by remember { mutableStateOf(false) }
    var isForgotPasswordMode by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("") } // Only used for registration
    var passwordVisible by remember { mutableStateOf(false) }

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
                            isRegisterMode -> "注册"
                            else -> "登录"
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (isForgotPasswordMode) {
                            isForgotPasswordMode = false
                        } else {
                            onBack()
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
//            verticalArrangement = Arrangement.Center
        ) {

            // Logo or Welcome Text
            Text(
                text = "何味道?",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // 忘记密码模式
            if (isForgotPasswordMode) {
                Text(
                    text = "输入您的邮箱地址，我们将发送密码重置链接",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("电子邮箱") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Done
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (uiState is LoginUiState.Loading) {
                    CircularProgressIndicator()
                } else {
                    Button(
                        onClick = { viewModel.sendPasswordResetEmail(email) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("发送重置邮件")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    TextButton(onClick = { isForgotPasswordMode = false }) {
                        Text("返回登录")
                    }
                }
            } else {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
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
                    onValueChange = { password = it },
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

                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, contentDescription = null)
                        }
                    },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (uiState is LoginUiState.Loading) {
                    CircularProgressIndicator()
                } else {
                    Button(
                        onClick = {
                            if (isRegisterMode) {
                                viewModel.signUpWithEmail(email, password)
                            } else {
                                viewModel.signInWithEmail(email, password)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (isRegisterMode) "注册" else "登录")
                    }

                    // 仅在登录模式下显示忘记密码
                    if (!isRegisterMode) {
                        TextButton(onClick = { isForgotPasswordMode = true }) {
                            Text("忘记密码？")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Toggle Mode
                    TextButton(onClick = { isRegisterMode = !isRegisterMode }) {
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
                        onClick = {
                            launchGoogleSignIn(context, googleSignInLauncher)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Google 登录")
                    }
                }
            }
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
