package tofunmi.volunteer.volunteertasktracker

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tofunmi.volunteer.volunteertasktracker.models.LoginCredentials
import tofunmi.volunteer.volunteertasktracker.models.Organization
import tofunmi.volunteer.volunteertasktracker.models.DashboardItem.Task
import tofunmi.volunteer.volunteertasktracker.models.DashboardItem.Goal
import tofunmi.volunteer.volunteertasktracker.models.UserProfile
import tofunmi.volunteer.volunteertasktracker.models.UserRole
import java.util.Calendar
import java.util.Date

class AppViewModel : ViewModel() {
    val currentUser = UserProfile("user_1", "Deto_kin", UserRole.ORGANIZATION)

    private val _organizations = MutableStateFlow<List<Organization>>(emptyList())
    private val _userProfiles = MutableStateFlow<List<UserProfile>>(emptyList())
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())

    private val _goals = MutableStateFlow<List<Goal>>(emptyList())

    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()
    val goals: StateFlow<List<Goal>> = _goals.asStateFlow()
    val organizations: StateFlow<List<Organization>> = _organizations.asStateFlow()
    val userProfile: StateFlow<List<UserProfile>> = _userProfiles.asStateFlow()

    init {
        fetchOrgsFromFlask()
        fetchAllTasksFromFlask()
    }

    fun fetchAllTasksFromFlask() {
        viewModelScope.launch {
            try {
                _tasks.value = RetrofitClient.apiInterface.getAllTasks()
            } catch (e: Exception) {
                Log.e("NetworkError", "Could not fetch tasks: ${e.message}")
            }
        }
    }

    fun fetchUserProfilesFromFlask(orgId: String) {
        viewModelScope.launch {
            try {
                _userProfiles.value = RetrofitClient.apiInterface.getUserProfiles(orgId)
            } catch (e: Exception) {
                Log.e("NetworkError", "Could not fetch users: ${e.message}")
            }
        }
    }

    fun fetchOrgsFromFlask() {
        viewModelScope.launch {
            try {
                _organizations.value = RetrofitClient.apiInterface.getOrgs()
            } catch (e: Exception) {
                Log.e("NetworkError", "Could not fetch orgs: ${e.message}")
            }
        }
    }

    fun createTask(
        orgId: String, title: String, description: String,
        assignee: UserProfile?, daysUntilExpiry: Long
    ) {
        val calendar = Calendar.getInstance()
        calendar.time = Date()
        calendar.add(Calendar.DAY_OF_YEAR, daysUntilExpiry.toInt())

        val newTask = Task(
            id = "t_${System.currentTimeMillis()}",
            orgId = orgId,
            title = title,
            description = description,
            isCompleted = false,
            assigner = currentUser,
            assignee = assignee,
            assignedDateTime = Date(),
            expiryDate = calendar.time
        )

        viewModelScope.launch {
            try {
                RetrofitClient.apiInterface.setTask(newTask)
                fetchAllTasksFromFlask()
            } catch (e: Exception) {
                Log.e("NetworkError", "Could not create task: ${e.message}")
            }
        }
    }

    fun pickTask(taskId: String, user: UserProfile) {
        viewModelScope.launch {
            try {
                val updateData = mapOf("assignee" to user)
                RetrofitClient.apiInterface.updateTaskAssignee(taskId, updateData)

                fetchAllTasksFromFlask()
            } catch (e: Exception) {
                Log.e("NetworkError", "Could not pick task: ${e.message}")
            }
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            try {
                RetrofitClient.apiInterface.deleteTask(task)

                fetchAllTasksFromFlask()
            } catch (e: Exception) {
                Log.e("NetworkError", "Could not delete task: ${e.message}")
            }
        }
    }

    fun deleteGoal(goal: Goal) {
//        viewModelScope.launch {
//            try {
//                RetrofitClient.apiInterface.deleteTask(task)
//
//                fetchAllTasksFromFlask()
//            } catch (e: Exception) {
//                Log.e("NetworkError", "Could not delete task: ${e.message}")
//            }
//        }
    }

    fun assignTask(taskId: String, user: UserProfile? = null) {
        viewModelScope.launch {
            try {
                val updateData = mapOf("assignee" to user)
                RetrofitClient.apiInterface.updateTaskAssignee(taskId, updateData)

                fetchAllTasksFromFlask()
            } catch (e: Exception) {
                Log.e("NetworkError", "Could not assign task: ${e.message}")
            }
        }
    }

    fun logCompletedTask(task: Task) {
        viewModelScope.launch {
            try {
                RetrofitClient.apiInterface.logCompletedTask(task)
                fetchAllTasksFromFlask()
            } catch (e: Exception) {
                Log.e("NetworkError", "Could not log task: ${e.message}")
            }
        }
    }

    fun logCompletedGoal(goal: Goal) {
//        viewModelScope.launch {
//            try {
//                RetrofitClient.apiInterface.logCompletedTask(task)
//                fetchAllTasksFromFlask()
//            } catch (e: Exception) {
//                Log.e("NetworkError", "Could not log task: ${e.message}")
//            }
//        }
    }

    fun removeLoggedCompletedTask(task: Task) {
        viewModelScope.launch {
            try {
                RetrofitClient.apiInterface.removeLoggedCompletedTask(task)
                fetchAllTasksFromFlask()
            } catch (e: Exception) {
                Log.e("NetworkError", "Could not remove log task: ${e.message}")
            }
        }
    }

    fun registerNewUser(newUser: UserProfile) {
        viewModelScope.launch {
            try {
                RetrofitClient.apiInterface.registerUser(newUser)
            } catch (e: Exception) {
                Log.e("NetworkError", "Could not register user: ${e.message}")
            }
        }
    }

    fun loginUser(credentials: LoginCredentials) {
        viewModelScope.launch {
            try {
                RetrofitClient.apiInterface.loginUser(credentials)
            } catch (e: Exception) {
                Log.e("NetworkError", "Could not login user: ${e.message}")
            }
        }
    }
}