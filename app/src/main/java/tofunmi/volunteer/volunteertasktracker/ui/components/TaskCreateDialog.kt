package tofunmi.volunteer.volunteertasktracker.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import tofunmi.volunteer.volunteertasktracker.models.UserProfile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskCreateDialog(
    onDismissRequested: () -> Unit,
    onTaskCreated: (title: String, description: String, assignee: UserProfile?, daysToExpiry: Long) -> Unit,
    currentUser: UserProfile,
    availableAssignees: List<UserProfile>
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var expiryDays by remember { mutableStateOf("7") } // Default to 7 days

    var assigneeDropdownExpanded by remember { mutableStateOf(false) }
    var goalDropdownExpanded by remember { mutableStateOf(false) }
    var selectedAssignee by remember { mutableStateOf<UserProfile?>(null) }
    var selectedGoal by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismissRequested) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.large,
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Create New Task",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )

                // 1. Task Summary
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task Summary") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // 2. Task Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Task Description") },
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    maxLines = 4
                )

                // 3. Expiry Date (Using a simple days-from-now input for clean UX)
                OutlinedTextField(
                    value = expiryDays,
                    onValueChange = { if (it.all { char -> char.isDigit() }) expiryDays = it },
                    label = { Text("Days until expiry") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // 4. Assignee Dropdown (Only show if the current user is Organization)
                ExposedDropdownMenuBox(
                    expanded = assigneeDropdownExpanded,
                    onExpandedChange = { assigneeDropdownExpanded = !assigneeDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedAssignee?.name ?: "Unassigned (Available to all)",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Assignee") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = assigneeDropdownExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = assigneeDropdownExpanded,
                        onDismissRequest = { assigneeDropdownExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Unassigned (Available to all)") },
                            onClick = {
                                selectedAssignee = null
                                assigneeDropdownExpanded = false
                            }
                        )
                        availableAssignees.forEach { user ->
                            DropdownMenuItem(
                                text = { Text(user.name) },
                                onClick = {
                                    selectedAssignee = user
                                    assigneeDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // 5. Assignor Info (Read-only display)
                Text(
                    text = "Assignor: ${currentUser.name}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 6. Action Buttons (Discard on left, Create on right)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(
                        onClick = onDismissRequested,
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Discard")
                    }

                    Button(
                        onClick = {
                            val days = expiryDays.toLongOrNull() ?: 7L
                            onTaskCreated(title, description, selectedAssignee, days)
                            onDismissRequested()
                        },
                        enabled = title.isNotBlank() && description.isNotBlank()
                    ) {
                        Text("Create")
                    }
                }
            }
        }
    }
}