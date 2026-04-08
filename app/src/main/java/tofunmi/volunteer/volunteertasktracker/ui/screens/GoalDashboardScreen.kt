//package tofunmi.volunteer.volunteertasktracker.ui.screens
//
//import android.os.Build
//import androidx.annotation.RequiresApi
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Add
//import androidx.compose.material.icons.filled.Api
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.FloatingActionButton
//import androidx.compose.material3.Icon
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.material3.TopAppBar
//import androidx.compose.material3.TopAppBarDefaults
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import tofunmi.volunteer.volunteertasktracker.models.DashboardItem.Goal
//import tofunmi.volunteer.volunteertasktracker.models.UserProfile
//import tofunmi.volunteer.volunteertasktracker.models.UserRole
//import tofunmi.volunteer.volunteertasktracker.models.VolunteerGroup
//import tofunmi.volunteer.volunteertasktracker.ui.components.TaskAssignDialog
//import tofunmi.volunteer.volunteertasktracker.ui.components.DashboardItemCard
//import tofunmi.volunteer.volunteertasktracker.ui.components.GoalCreateDialog
//import tofunmi.volunteer.volunteertasktracker.ui.components.TaskCreateDialog
//
//@RequiresApi(Build.VERSION_CODES.O)
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun GoalDashboardScreen(
//    orgId: String,
//    goals: List<Goal>,
//    currentUser: UserProfile,
//    availableGroups: List<VolunteerGroup>,
//    onGoalCompleted: (goal: Goal) -> Unit,
//    onGoalDeleted: (goal: Goal) -> Unit,
//    onGoalCreated: (orgId: String, title: String, desc: String, assignee: UserProfile?, daysToExpiry: Long) -> Unit,
//    onGoalPicked: (taskId: String) -> Unit,
//    onGoalUnassigned: (taskId: String) -> Unit,
//    onGoalAssigned: (taskId: String, user: UserProfile) -> Unit,
//) {
//    var showCreateDialog by remember { mutableStateOf(false) }
//    var taskToAssign by remember { mutableStateOf<Goal?>(null) }
//
//    Scaffold(
//        topBar = {
//            Column {
//                TopAppBar(
//                    title = { Text("Tasks") },
//                    colors = TopAppBarDefaults.topAppBarColors(
//                        containerColor = MaterialTheme.colorScheme.primaryContainer,
//                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
//                    )
//                )
//            }
//        },
//        floatingActionButton = {
//            if (currentUser.role == UserRole.ORGANIZATION) {
//                FloatingActionButton(onClick = { showCreateDialog = true }) {
//                    Icon(Icons.Filled.Api, contentDescription = "Create Goal")
//                }
//            }
//        }
//    ) { paddingValues ->
//        // Main Screen Content
//        LazyColumn(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//                .padding(horizontal = 16.dp),
//            verticalArrangement = Arrangement.spacedBy(12.dp)
//        ) {
//            item { Spacer(modifier = Modifier.height(8.dp)) }
//
//            items(goals) { goal ->
//                DashboardItemCard(
//                    data = goal,
//                    currentUserRole = currentUser.role,
//                    onDeleted = { onGoalDeleted(goal) },
//                    onCompleted = { onGoalCompleted(goal) },
//                    onUnassigned = { onGoalUnassigned(goal.id) },
//                    onAssigned = {
//                        taskToAssign = goal
//                    },
//                    onPicked = { onGoalPicked(goal.id) }
//                )
//            }
//            item { Spacer(modifier = Modifier.height(88.dp)) } // FAB clearance
//        }
//
//        if (showCreateDialog) {
//            GoalCreateDialog(
//                onDismissRequested = { showCreateDialog = false },
//                onGoalCreated = { title, desc, assignedGroup, days ->
//                    onGoalCreated(title, desc, assignedGroup, days)
//                    showCreateDialog = false
//                },
//                currentUser = currentUser,
//                availableGroups = availableGroups
//            )
//        }
//    }
//}