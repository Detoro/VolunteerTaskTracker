package tofunmi.volunteer.volunteertasktracker

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import tofunmi.volunteer.volunteertasktracker.ui.screens.LoginScreen
import tofunmi.volunteer.volunteertasktracker.ui.screens.OrganizationDashboardScreen
import tofunmi.volunteer.volunteertasktracker.ui.screens.SignUpScreen
import tofunmi.volunteer.volunteertasktracker.ui.screens.TaskCompleteScreen
import tofunmi.volunteer.volunteertasktracker.ui.screens.TaskDashboardScreen
//import tofunmi.volunteer.volunteertasktracker.ui.screens.GoalDashboardScreen
import tofunmi.volunteer.volunteertasktracker.ui.theme.VolunteerTaskTrackerTheme

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object OrganizationDashboard : Screen("org_dashboard")
    object TaskDashboard : Screen("task_dashboard/{orgId}") {
        fun createRoute(orgId: String) = "task_dashboard/$orgId"
    }
    object GoalDashboard : Screen("goal_dashboard/{orgId}") {
        fun createRoute(orgId: String) = "goal_dashboard/$orgId"
    }
    object TaskComplete : Screen("task_complete/{orgId}") {
        fun createRoute(orgId: String) = "task_complete/$orgId"
    }
}

class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<AppViewModel>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VolunteerTaskTrackerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(viewModel)
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(viewModel: AppViewModel) {
    val navController = rememberNavController()

    val organizations by viewModel.organizations.collectAsState()
    val allTasks by viewModel.tasks.collectAsState()
    val allGoals by viewModel.goals.collectAsState()
    val loginError by viewModel.loginError.collectAsState()
    val currentUser = viewModel.currentUser.collectAsState()

    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToSignUp = { navController.navigate(Screen.SignUp.route) },
                onLoginSubmit = {credentials ->
                    viewModel.loginUser(credentials, onSuccess = {
                        navController.navigate(Screen.OrganizationDashboard.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    })
                },
                loginError = loginError
            )
        }
        composable(Screen.SignUp.route) {
            SignUpScreen(
                onNavigateBack = { navController.popBackStack() },
                onSignUpSuccess = {newUser ->
                    viewModel.registerNewUser(newUser, onSuccess = {
                        navController.navigate(Screen.OrganizationDashboard.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    })
                }
            )
        }
        composable(Screen.OrganizationDashboard.route) {
            OrganizationDashboardScreen(
                organizations = organizations,
                currentUser = currentUser.value,
                onOrgSelected = { orgId ->
                    navController.navigate(Screen.TaskDashboard.createRoute(orgId))
                }
            )
        }
        composable(Screen.TaskDashboard.route) { backStackEntry ->
            val orgId = backStackEntry.arguments?.getString("orgId") ?: return@composable
            val availableSubscribers by viewModel.userProfiles.collectAsState()

            LaunchedEffect(orgId) {
                viewModel.fetchUserProfilesFromFlask(orgId)
            }

            val orgTasks = allTasks.filter { it.orgId == orgId }
            Log.d("Tasks", orgTasks.toString())

            TaskDashboardScreen(
                orgId = orgId,
                tasks = orgTasks,
                currentUser = currentUser.value,
                availableSubscribers = availableSubscribers,
                onTaskCompleted = { task ->
                    viewModel.changeTaskCompletedStatus(task)
                    navController.navigate(Screen.TaskComplete.createRoute(orgId))
                },
                onTaskCreated = { id, title, desc, assignee, days ->
                    viewModel.createTask(id, title, desc, assignee, days)
                },
                onTaskPicked = { taskId ->
                    viewModel.pickTask(taskId, currentUser.value)
                },
                onTaskUnassigned = { taskId ->
                    viewModel.assignTask(taskId)
                },
                onTaskAssigned = { taskId, user ->
                    viewModel.assignTask(taskId, user)
                },
                onTaskDeleted = {task ->
                    viewModel.deleteTask(task)
                },
                onRemoveTaskLog = { task ->
                    viewModel.changeTaskCompletedStatus(task)
                }
            )
        }
        composable(Screen.TaskComplete.route) { backStackEntry ->
            val orgId = backStackEntry.arguments?.getString("orgId") ?: return@composable
            TaskCompleteScreen(
                orgId = orgId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}