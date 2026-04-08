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
import tofunmi.volunteer.volunteertasktracker.models.VolunteerGroup // Make sure this is imported!

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalCreateDialog(
    onDismissRequested: () -> Unit,
    onGoalCreated: (title: String, description: String, assignedGroup: VolunteerGroup?, daysToExpiry: Long) -> Unit,
    currentUser: UserProfile,
    availableGroups: List<VolunteerGroup>
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var expiryDays by remember { mutableStateOf("7") }

    var expanded by remember { mutableStateOf(false) }
    // FIX 3: State variable holds a VolunteerGroup now
    var selectedGroup by remember { mutableStateOf<VolunteerGroup?>(null) }

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
                // FIX 4: Updated all UI Text to say "Goal"
                Text(
                    text = "Create New Goal",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Goal Summary") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Goal Description") },
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    maxLines = 4
                )

                OutlinedTextField(
                    value = expiryDays,
                    onValueChange = { if (it.all { char -> char.isDigit() }) expiryDays = it },
                    label = { Text("Days until expiry") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // 5. The Group Selection Dropdown
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedGroup?.name ?: "Unassigned (Available to all)",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Assign to Group") }, // Updated label
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Unassigned (Available to all)") },
                            onClick = {
                                selectedGroup = null
                                expanded = false
                            }
                        )
                        // FIX 6: Iterate through the groups
                        availableGroups.forEach { group ->
                            DropdownMenuItem(
                                text = { Text(group.name) },
                                onClick = {
                                    selectedGroup = group
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Text(
                    text = "Assignor: ${currentUser.name}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

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
                            // Passes the selectedGroup back to the screen
                            onGoalCreated(title, description, selectedGroup, days)
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