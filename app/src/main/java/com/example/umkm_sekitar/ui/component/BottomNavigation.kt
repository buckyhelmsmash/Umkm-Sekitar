package com.example.umkm_sekitar.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController

data class NavigationItem(
    val title: String,
    val icon: ImageVector,
    val route: String
)

sealed class Screen(val route: String) {
    object Home : Screen("home_screen")
    object Cart : Screen("cart_screen")
    object Orders : Screen("orders_screen")
    object Profile : Screen("profile_screen")
}

@Composable
fun BottomNavigationBar(
    navController: NavController
) {
    val selectedNavigationIndex = rememberSaveable {
        mutableIntStateOf(0)
    }

    val navigationItems = listOf(
        NavigationItem(
            title = "Beranda",
            icon = Icons.Default.Home,
            route = Screen.Home.route
        ),
        NavigationItem(
            title = "Keranjang",
            icon = Icons.Default.ShoppingCart,
            route = Screen.Cart.route
        ),
        NavigationItem(
            title = "Pesanan",
            icon = Icons.Default.Menu,
            route = Screen.Orders.route
        ),
        NavigationItem(
            title = "Profile",
            icon = Icons.Default.Person,
            route = Screen.Profile.route
        )
    )

    Surface (
        color = MaterialTheme.colorScheme.primary,
    ){
        NavigationBar(
            containerColor = Color.White
        ) {
            navigationItems.forEachIndexed { index, item ->
                NavigationBarItem(
                    selected = selectedNavigationIndex.intValue == index,
                    onClick = {
                        selectedNavigationIndex.intValue = index
                        navController.navigate(item.route)
                    },
                    icon = {
                        Icon(imageVector = item.icon, contentDescription = item.title)   },
                    label = {
                        Text(
                            item.title,
                            color = if (index == selectedNavigationIndex.intValue)
                                Color.Black
                            else Color.Gray
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.surface,
                        indicatorColor = MaterialTheme.colorScheme.primary
                    )

                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun BottomNavigationBarPreview() {
    BottomNavigationBar(navController = NavController(context = LocalContext.current))
}

