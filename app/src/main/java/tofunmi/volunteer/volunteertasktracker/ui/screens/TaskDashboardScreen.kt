package tofunmi.volunteer.volunteertasktracker.ui.screens

import android.os.Build
import java.util.Date
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tofunmi.volunteer.volunteertasktracker.models.DashboardItem.Task
import tofunmi.volunteer.volunteertasktracker.models.UserProfile
import tofunmi.volunteer.volunteertasktracker.models.UserRole
import tofunmi.volunteer.volunteertasktracker.ui.components.TaskAssignDialog
import tofunmi.volunteer.volunteertasktracker.ui.components.DashboardItemCard
import tofunmi.volunteer.volunteertasktracker.ui.components.TaskCreateDialog

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDashboardScreen(
    orgId: String,
    tasks: List<Task>,
    currentUser: UserProfile,
    availableSubscribers: List<UserProfile>,
    onTaskCompleted: (task: Task) -> Unit,
    onTaskDeleted: (task: Task) -> Unit,
    onTaskCreated: (orgId: String, title: String, desc: String, assignee: UserProfile?, daysToExpiry: Long) -> Unit,
    onTaskPicked: (taskId: String) -> Unit,
    onTaskUnassigned: (taskId: String) -> Unit,
    onTaskAssigned: (taskId: String, user: UserProfile) -> Unit,
    onRemoveTaskLog: (task: Task) -> Unit = {},
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = if (currentUser.role == UserRole.ORGANIZATION) {
        listOf("Assigned to Me", "All Active Tasks", "Completed Tasks")
    } else {
        listOf("My Tasks", "Available Tasks", "Completed Tasks")
    }
    var showCreateDialog by remember { mutableStateOf(false) }
    var taskToAssign by remember { mutableStateOf<Task?>(null) }

    val displayedTasks = when (selectedTabIndex) {
        0 -> tasks.filter { it.assignee?.id == currentUser.id && !it.isCompleted }
        1 -> {
            if (currentUser.role == UserRole.ORGANIZATION) {
                tasks.filter { !it.isCompleted }
            } else {
                tasks.filter { it.assignee == null && !it.isCompleted }
            }
        }
        2 -> {
            if (currentUser.role == UserRole.ORGANIZATION) {
                tasks.filter { it.isCompleted }
            } else {
                tasks.filter { it.isCompleted && it.assignee?.id == currentUser.id }
            }
        }
        else -> emptyList()
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Tasks") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
                TabRow(selectedTabIndex = selectedTabIndex) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = { Text(title) }
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (currentUser.role == UserRole.ORGANIZATION) {
                FloatingActionButton(onClick = { showCreateDialog = true }) {
                    Icon(Icons.Filled.Add, contentDescription = "Create Task")
                }
            }
        }
    ) { paddingValues ->
        // Main Screen Content
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            if (displayedTasks.isEmpty()) {
                item {
                    Text(
                        text = "No tasks found in this window.",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            items(displayedTasks) { task ->
                DashboardItemCard(
                    data = task,
                    currentUserRole = currentUser.role,
                    onDeleted = { onTaskDeleted(task) },
                    onCompleted = {
                        onTaskCompleted(task)
                        task.endTimestamp = Date() },
                    onUnassigned = { onTaskUnassigned(task.id ?: "") },
                    onAssigned = {
                        taskToAssign = task
                        task.startTimestamp = Date()
                    },
                    onPicked = { onTaskPicked(task.id ?: "") },
                    onRemoveLog = { onRemoveTaskLog(task) }
                )
            }
            item { Spacer(modifier = Modifier.height(88.dp)) } // FAB clearance
        }

        if (showCreateDialog) {
            TaskCreateDialog(
                onDismissRequested = { showCreateDialog = false },
                onTaskCreated = { title, desc, assignee, days ->
                    onTaskCreated(orgId, title, desc, assignee, days)
                    showCreateDialog = false
                },
                currentUser = currentUser,
                availableAssignees = availableSubscribers
            )
        }

        taskToAssign?.let { activeTask ->
            TaskAssignDialog(
                taskId = activeTask.id ?: "",
                onDismissRequest = { taskToAssign = null },

                onTaskAssigned = { id, assignee ->
                    if (assignee != null) {
                        onTaskAssigned(id, assignee)
                    }
                    taskToAssign = null
                },
                currentUser = currentUser,
                availableAssignees = availableSubscribers
            )
        }
    }
}