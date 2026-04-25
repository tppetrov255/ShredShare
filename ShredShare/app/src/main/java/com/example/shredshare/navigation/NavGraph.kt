package com.example.shredshare.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.shredshare.ui.theme.screens.*
import com.example.shredshare.viewmodel.AdminViewModel
import com.example.shredshare.viewmodel.OwnerViewModel
import androidx.compose.ui.platform.LocalContext
import com.example.shredshare.data.local.SessionManager
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import android.widget.Toast
import com.example.shredshare.viewmodel.CartViewModel
@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val cartViewModel: CartViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        // LOGIN
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToOwnerDashboard = { userId ->
                    navController.navigate(Screen.OwnerDashboard.createRoute(userId)) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToAdminDashboard = {
                    navController.navigate(Screen.AdminDashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onSignUpClick = {
                    navController.navigate(Screen.ChooseRole.route)
                },
                onForgotPasswordClick = {
                    navController.navigate(Screen.ForgotPassword.route)
                }
            )
        }

        composable(Screen.ForgotPassword.route) {
            ForgotPasswordRoute(
                onBackClick = {
                    navController.popBackStack()
                },
                onNavigateToReset = { email ->
                    navController.navigate(Screen.ResetPassword.createRoute(email))
                }
            )
        }

        composable(
            route = Screen.ResetPassword.route,
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""

            ResetPasswordRoute(
                email = email,
                onBackClick = {
                    navController.popBackStack()
                },
                onPasswordResetSuccess = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        // CHOOSE ROLE
        composable(Screen.ChooseRole.route) {
            ChooseRoleScreen(
                onCustomerClick = {
                    navController.navigate(Screen.Register.createRoute("Customer"))
                },
                onOwnerClick = {
                    navController.navigate(Screen.Register.createRoute("Owner"))
                }
            )
        }

        // REGISTER
        composable(
            route = Screen.Register.route,
            arguments = listOf(navArgument("role") { type = NavType.StringType })
        ) { backStackEntry ->
            val role = backStackEntry.arguments?.getString("role") ?: "Customer"

            RegisterScreen(
                role = role,
                onRegisterSuccess = { userId, registeredRole ->
                    sessionManager.saveLogin(userId, registeredRole)

                    if (registeredRole.equals("Owner", ignoreCase = true)) {
                        navController.navigate(Screen.CreateWardrobe.createRoute(userId)) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                },
                onBackToLoginClick = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Login.route) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
        }

        // HOME
        composable(Screen.Home.route) {
            HomeScreen(
                onViewWardrobesClick = { resort ->
                    val resortId = resort?.resortId ?: 0
                    navController.navigate(Screen.WardrobeList.createRoute(resortId))
                }
            )
        }

        // OWNER DASHBOARD
        composable(
            route = Screen.OwnerDashboard.route,
            arguments = listOf(navArgument("userId") { type = NavType.IntType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0

            OwnerDashboardRoute(
                userId = userId,
                onCreateWardrobeClick = {
                    navController.navigate(Screen.CreateWardrobe.createRoute(userId))
                },
                onManageWardrobeClick = { wardrobeId ->
                    navController.navigate(Screen.ManageEquipment.createRoute(wardrobeId))
                },
                onManageOrdersClick = {
                    navController.navigate(Screen.OwnerBookings.createRoute(userId))
                },
                onLogoutClick = {
                    sessionManager.clearSession()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        // CREATE WARDROBE
        composable(
            route = Screen.CreateWardrobe.route,
            arguments = listOf(navArgument("ownerId") { type = NavType.IntType })
        ) { backStackEntry ->
            val ownerId = backStackEntry.arguments?.getInt("ownerId") ?: 0
            CreateWardrobeScreen(
                ownerId = ownerId,
                navController = navController,
                onPickLocationClick = {
                    navController.navigate(Screen.PickLocation.route)
                },
                onCreateSuccess = {
                    navController.navigate(Screen.OwnerDashboard.createRoute(ownerId)) {
                        popUpTo(Screen.CreateWardrobe.route) { inclusive = true }
                    }
                }
            )
        }

        // WARDROBE LIST
        composable(
            route = Screen.WardrobeList.route,
            arguments = listOf(navArgument("resortId") { type = NavType.IntType })
        ) { backStackEntry ->
            val resortId = backStackEntry.arguments?.getInt("resortId") ?: 0

            WardrobeListRoute(
                resortId = resortId,
                onViewEquipmentClick = { wardrobeId, latitude, longitude, wardrobeName ->
                    navController.currentBackStackEntry?.savedStateHandle?.set("wardrobe_latitude", latitude)
                    navController.currentBackStackEntry?.savedStateHandle?.set("wardrobe_longitude", longitude)
                    navController.currentBackStackEntry?.savedStateHandle?.set("wardrobe_name", wardrobeName)

                    navController.navigate(Screen.WardrobeEquipmentList.createRoute(wardrobeId))
                },
                onBackClick = { navController.popBackStack() },
                onProfileClick = {
                    navController.navigate(Screen.Profile.route)
                },
                onCartClick = {
                    navController.navigate(Screen.Cart.route)
                },
                onBookingsClick = {
                    navController.navigate(Screen.MyBookings.route)
                },
                onLogoutClick = {
                    sessionManager.clearSession()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        // PICK LOCATION
        composable(Screen.PickLocation.route) {
            PickLocationScreen(
                onLocationPicked = { latLng ->
                    navController.previousBackStackEntry?.savedStateHandle?.set("picked_lat", latLng.latitude)
                    navController.previousBackStackEntry?.savedStateHandle?.set("picked_lng", latLng.longitude)
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.AdminDashboard.route) {
            AdminDashboardRoute(
                onProfileClick = {
                    navController.navigate(Screen.Profile.route)
                },
                onLogoutClick = {
                    sessionManager.clearSession()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onViewDetailsClick = { id ->
                    navController.navigate(Screen.AdminDetails.createRoute(id))
                }
            )
        }

        composable(
            route = Screen.AddEquipment.route,
            arguments = listOf(navArgument("wardrobeId") { type = NavType.IntType })
        ) { backStackEntry ->
            val wardrobeId = backStackEntry.arguments?.getInt("wardrobeId") ?: 0

            AddEquipmentRoute(
                wardrobeId = wardrobeId,
                onBackClick = {
                    navController.popBackStack()
                },
                onAddSuccess = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.ManageEquipment.route,
            arguments = listOf(navArgument("wardrobeId") { type = NavType.IntType })
        ) { backStackEntry ->
            val wardrobeId = backStackEntry.arguments?.getInt("wardrobeId") ?: 0

            ManageEquipmentRoute(
                wardrobeId = wardrobeId,
                onAddItemClick = {
                    navController.navigate(Screen.AddEquipment.createRoute(wardrobeId))
                },
                onEditItemClick = { currentWardrobeId, equipmentId ->
                    navController.navigate(Screen.EditEquipment.createRoute(currentWardrobeId, equipmentId))
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.EditEquipment.route,
            arguments = listOf(
                navArgument("wardrobeId") { type = NavType.IntType },
                navArgument("equipmentId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val wardrobeId = backStackEntry.arguments?.getInt("wardrobeId") ?: 0
            val equipmentId = backStackEntry.arguments?.getInt("equipmentId") ?: 0

            AddEquipmentRoute(
                wardrobeId = wardrobeId,
                equipmentId = equipmentId,
                onBackClick = {
                    navController.popBackStack()
                },
                onAddSuccess = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.WardrobeEquipmentList.route,
            arguments = listOf(navArgument("wardrobeId") { type = NavType.IntType })
        ) { backStackEntry ->
            val wardrobeId = backStackEntry.arguments?.getInt("wardrobeId") ?: 0

            val latitude = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get<Double?>("wardrobe_latitude")

            val longitude = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get<Double?>("wardrobe_longitude")

            val wardrobeName = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get<String>("wardrobe_name") ?: "Локация на гардероба"

            WardrobeEquipmentListRoute(
                wardrobeId = wardrobeId,
                latitude = latitude,
                longitude = longitude,
                wardrobeName = wardrobeName,
                onBackClick = { navController.popBackStack() },
                onProfileClick = {
                    navController.navigate(Screen.Profile.route)
                },
                onCartClick = {
                    navController.navigate(Screen.Cart.route)
                },
                onBookingsClick = {
                    navController.navigate(Screen.MyBookings.route)
                },
                onLogoutClick = {
                    sessionManager.clearSession()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onItemClick = { equipmentId ->
                    navController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set("details_wardrobe_name", wardrobeName)

                    navController.navigate(
                        Screen.EquipmentDetails.createRoute(wardrobeId, equipmentId)
                    )
                }
            )
        }

        composable(
            route = Screen.EquipmentDetails.route,
            arguments = listOf(
                navArgument("wardrobeId") { type = NavType.IntType },
                navArgument("equipmentId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val wardrobeId = backStackEntry.arguments?.getInt("wardrobeId") ?: 0
            val equipmentId = backStackEntry.arguments?.getInt("equipmentId") ?: 0

            val wardrobeName = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get<String>("details_wardrobe_name")
                ?: "Неизвестен гардероб"

            EquipmentDetailsRoute(
                wardrobeId = wardrobeId,
                equipmentId = equipmentId,
                wardrobeName = wardrobeName,
                onBackClick = { navController.popBackStack() },
                onBookClick = { equipment ->
                    cartViewModel.addEquipmentToCart(equipment)
                    Toast.makeText(context, "Артикулът е добавен в количката", Toast.LENGTH_SHORT).show()
                },
                onProfileClick = {
                    navController.navigate(Screen.Profile.route)
                },
                onCartClick = {
                    navController.navigate(Screen.Cart.route)
                },
                onBookingsClick = {
                    navController.navigate(Screen.MyBookings.route)
                },
                onLogoutClick = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Screen.Profile.route) {
            val userId = sessionManager.getUserId()

            if (userId != -1) {
                ProfileRoute(
                    userId = userId,
                    onBackClick = { navController.popBackStack() },
                    onCartClick = {
                        navController.navigate(Screen.Cart.route)
                    },
                    onBookingsClick = {
                        navController.navigate(Screen.MyBookings.route)
                    },
                    onLogoutClick = {
                        sessionManager.clearSession()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Няма активна сесия. Моля, влезте отново.")
                }
            }
        }

        composable(Screen.Cart.route) {
            CartRoute(
                viewModel = cartViewModel,
                onBackClick = { navController.popBackStack() },
                onProfileClick = {
                    navController.navigate(Screen.Profile.route)
                },
                onBookingsClick = {
                    navController.navigate(Screen.MyBookings.route)
                },
                onLogoutClick = {
                    sessionManager.clearSession()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Screen.MyBookings.route) {
            val userId = sessionManager.getUserId()

            if (userId != -1) {
                BookingsRoute(
                    userId = userId,
                    onBackClick = { navController.popBackStack() },
                    onProfileClick = {
                        navController.navigate(Screen.Profile.route)
                    },
                    onCartClick = {
                        navController.navigate(Screen.Cart.route)
                    },
                    onLogoutClick = {
                        sessionManager.clearSession()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Няма активна сесия.")
                }
            }
        }

        composable(
            route = Screen.OwnerBookings.route,
            arguments = listOf(navArgument("ownerId") { type = NavType.IntType })
        ) { backStackEntry ->
            val ownerId = backStackEntry.arguments?.getInt("ownerId") ?: 0

            OwnerBookingsRoute(
                ownerId = ownerId,
                onBackClick = { navController.popBackStack() },
                onLogoutClick = {
                    sessionManager.clearSession()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(
            route = Screen.AdminDetails.route,
            arguments = listOf(navArgument("wardrobeId") { type = NavType.IntType })
        ) { backStackEntry ->
            val wardrobeId = backStackEntry.arguments?.getInt("wardrobeId") ?: 0

            AdminDetailsRoute(
                wardrobeId = wardrobeId,
                onBackClick = { navController.popBackStack() },
                onProfileClick = {
                    navController.navigate(Screen.Profile.route)
                },
                onLogoutClick = {
                    sessionManager.clearSession()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

    }
}

@Composable
fun OwnerDashboardRoute(
    userId: Int,
    viewModel: OwnerViewModel = viewModel(),
    onCreateWardrobeClick: () -> Unit = {},
    onManageWardrobeClick: (Int) -> Unit = {},
    onManageOrdersClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(userId) {
        viewModel.loadOwnerWardrobes(userId)
    }

    OwnerDashboardScreen(
        wardrobes = uiState.wardrobes,
        isLoading = uiState.isLoading,
        error = uiState.error,
        onCreateWardrobeClick = onCreateWardrobeClick,
        onManageWardrobeClick = onManageWardrobeClick,
        onManageOrdersClick = onManageOrdersClick,
        onLogoutClick = onLogoutClick
    )
}

@Composable
fun AdminDashboardRoute(
    viewModel: AdminViewModel = viewModel(),
    onLogoutClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onViewDetailsClick: (Int) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadDashboard()
    }

    AdminDashboardScreen(
        pendingRequests = uiState.pendingRequests,
        totalPending = uiState.totalPending,
        totalApproved = uiState.totalApproved,
        totalOwners = uiState.totalOwners,
        totalUsers = uiState.totalUsers,
        isLoading = uiState.isLoading,
        onApproveClick = { id -> viewModel.approveWardrobe(id) },
        onRejectClick = { id -> viewModel.rejectWardrobe(id) },
        onViewDetailsClick = onViewDetailsClick,
        onLogoutClick = onLogoutClick,
        onProfileClick = onProfileClick
    )
}

