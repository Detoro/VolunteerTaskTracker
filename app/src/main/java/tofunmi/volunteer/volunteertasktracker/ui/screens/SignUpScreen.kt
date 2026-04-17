package tofunmi.volunteer.volunteertasktracker.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import tofunmi.volunteer.volunteertasktracker.models.SignUpPayload
import tofunmi.volunteer.volunteertasktracker.models.UserProfile
import tofunmi.volunteer.volunteertasktracker.models.UserRole


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    onNavigateBack: () -> Unit,
    onSignUpSuccess: (SignUpPayload) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var isOrganization by remember { mutableStateOf(false) }
    var orgName by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }
    val errorMessage = "Incorrect password. Please try again."

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Create Account",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Role Selector
        Row(verticalAlignment = Alignment.CenterVertically) {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = if (isOrganization) "Organization" else "Subscriber",
                    onValueChange = {}, // Leave empty, it's read-only
                    readOnly = true,
                    label = { Text("Account Type") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                // 4. The actual Menu
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Subscriber") },
                        onClick = {
                            isOrganization = false
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Organization") },
                        onClick = {
                            isOrganization = true
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        if (isOrganization) {
            OutlinedTextField(
                value = orgName,
                onValueChange = { orgName = it },
                label = { Text("Organization Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
            },
            label = { Text("Password") },
            singleLine = true,

            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                val description = if (passwordVisible) "Hide password" else "Show password"

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = description)
                }
            },

            isError = isError,
            supportingText = {
                if (isError) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
            },
            label = { Text("Confirm Password") },
            singleLine = true,

            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                val description = if (confirmPasswordVisible) "Hide password" else "Show password"

                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                    Icon(imageVector = image, contentDescription = description)
                }
            },

            isError = isError,
            supportingText = {
                if (isError) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                val selectedRole = if (isOrganization) UserRole.ORGANIZATION else UserRole.SUBSCRIBER
                val newUser = SignUpPayload(
                    id = "user_${System.currentTimeMillis()}",
                    name = name,
                    orgId = if (selectedRole == UserRole.ORGANIZATION) "org_${System.currentTimeMillis()}" else null,
                    orgName = if (selectedRole == UserRole.ORGANIZATION) orgName else null,
                    role = selectedRole,
                    email = email,
                    password = password
                )
                if (password == confirmPassword) {
                    isError = false
                    onSignUpSuccess(newUser)
                } else {
                    isError = true
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Sign Up")
        }

        TextButton(onClick = onNavigateBack) {
            Text("Already have an account? Login")
        }
    }
}
