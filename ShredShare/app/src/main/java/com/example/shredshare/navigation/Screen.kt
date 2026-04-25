package com.example.shredshare.navigation

import android.net.Uri

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object ChooseRole : Screen("choose_role")
    data object Register : Screen("register/{role}") {
        fun createRoute(role: String): String = "register/$role"
    }
    data object Home : Screen("home")
    data object OwnerDashboard : Screen("owner_dashboard/{userId}") {
        fun createRoute(userId: Int): String = "owner_dashboard/$userId"
    }
    data object PickLocation : Screen("pick_location")
    data object CreateWardrobe : Screen("create_wardrobe/{ownerId}") {
        fun createRoute(ownerId: Int): String = "create_wardrobe/$ownerId"
    }
    data object WardrobeList : Screen("wardrobe_list/{resortId}") {
        fun createRoute(resortId: Int): String = "wardrobe_list/$resortId"
    }

    data object AddEquipment : Screen("add_equipment/{wardrobeId}") {
        fun createRoute(wardrobeId: Int): String = "add_equipment/$wardrobeId"
    }

    data object AdminDashboard : Screen("admin_dashboard")

    data object ManageEquipment : Screen("manage_equipment/{wardrobeId}") {
        fun createRoute(wardrobeId: Int): String = "manage_equipment/$wardrobeId"
    }

    data object EditEquipment : Screen("edit_equipment/{wardrobeId}/{equipmentId}") {
        fun createRoute(wardrobeId: Int, equipmentId: Int): String {
            return "edit_equipment/$wardrobeId/$equipmentId"
        }
    }

    data object WardrobeEquipmentList : Screen("wardrobe_equipment_list/{wardrobeId}") {
        fun createRoute(wardrobeId: Int): String = "wardrobe_equipment_list/$wardrobeId"
    }

    data object EquipmentDetails : Screen("equipment_details/{wardrobeId}/{equipmentId}") {
        fun createRoute(wardrobeId: Int, equipmentId: Int) =
            "equipment_details/$wardrobeId/$equipmentId"
    }

    data object Profile : Screen("profile")
    data object Cart : Screen("cart")
    data object MyBookings : Screen("my_bookings")

    data object OwnerBookings : Screen("owner_bookings/{ownerId}") {
        fun createRoute(ownerId: Int): String = "owner_bookings/$ownerId"
    }

    data object ForgotPassword : Screen("forgot_password")
    data object ResetPassword : Screen("reset_password/{email}") {
        fun createRoute(email: String): String = "reset_password/${Uri.encode(email)}"
    }

    data object AdminDetails : Screen("admin_details/{wardrobeId}") {
        fun createRoute(wardrobeId: Int) = "admin_details/$wardrobeId"
    }
}