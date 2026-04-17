package tofunmi.volunteer.volunteertasktracker.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Task
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import tofunmi.volunteer.volunteertasktracker.models.UserRole
import tofunmi.volunteer.volunteertasktracker.models.DashboardItem
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun DashboardItemCard(
    data: DashboardItem,
    currentUserRole: UserRole,
    onCompleted: () -> Unit,
    onDeleted: () -> Unit = {},
    onUnassigned: () -> Unit = {},
    onAssigned: () -> Unit = {},
    onPicked: () -> Unit = {},
    onRemoveLog: () -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .clickable { expanded = !expanded }
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = data.title ?: "",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = if (expanded) "Show less" else "Show more"
                )
            }

            val assigneeText = data.assignee?.name ?: "Unassigned"
            Text(
                text = "Assigned to: $assigneeText",
                style = MaterialTheme.typography.labelMedium,
                color = if (data.assignee == null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = data.description ?: "",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))

                val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

                // The Creator / Assignor
                Text(
                    text = "Created by: ${data.assigner?.name ?: "Unknown"}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )

                // Creation Date
                val dateText = data.assignedDateTime?.let { formatter.format(it) } ?: "Unknown"
                Text(
                    text = "Created on: $dateText",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // start time
                val startTime = data.startTimestamp?.let { formatter.format(it) } ?: "Not Started"
                Text(
                    text = "Started on: $startTime",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // completion time
                val completionTime = data.endTimestamp?.let { formatter.format(it) } ?: "Not finished"
                Text(
                    text = "Completed on: $completionTime",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // completion time

                // Goal it belongs to
                when (data) {
                    is DashboardItem.Task -> {
                        Text(
                            text = "Goal: ${data.goal ?: "Not part of any goal"}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    is DashboardItem.Goal -> {
                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Progress: ${data.progressPercentage}%",
                            style = MaterialTheme.typography.labelMedium
                        )

                        androidx.compose.material3.LinearProgressIndicator(
                            progress = { data.progressPercentage / 100f },
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                        )
                    }
                }

                // Expiry Date
                data.expiryDate?.let { expiry ->
                    Text(
                        text = "Expires on: ${formatter.format(expiry)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error // Makes the expiry stand out
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left side: Delete button (Strictly for Organizations)
                    if (currentUserRole == UserRole.ORGANIZATION) {
                        TextButton(
                            onClick = onDeleted,
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Delete Task",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Delete")
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }

                    // Middle assign task button also Strictly for Organizations
                    if (currentUserRole == UserRole.ORGANIZATION) {
                        TextButton(
                            onClick = onAssigned,
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.secondary
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Task,
                                contentDescription = "Assign Task",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Assign")
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }

                    Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                        if (currentUserRole == UserRole.SUBSCRIBER && !data.isCompleted  && data.assignee != null) {
                            Button(onClick = onUnassigned) {
                                Text("Unpick Task")
                            }
                        } else if (currentUserRole == UserRole.ORGANIZATION && !data.isCompleted) {
                            Button(onClick = onCompleted) {
                                Text("Mark Complete")
                            }
                        }
                    }
                    
                    Row(horizontalArrangement = Arrangement.End) {
                        if (currentUserRole == UserRole.SUBSCRIBER && data.assignee == null) {
                            Button(onClick = onPicked) {
                                Text("Pick Task")
                            }
                        } else if (currentUserRole == UserRole.SUBSCRIBER && !data.isCompleted) {
                            Button(onClick = onCompleted) {
                                Text("Mark Complete")
                            }
                        } else if (data.isCompleted) {
                            Button(onClick = onRemoveLog) {
                                Text("Not Complete")
                            }
                        }
                    }
                }
            }
        }
    }
}