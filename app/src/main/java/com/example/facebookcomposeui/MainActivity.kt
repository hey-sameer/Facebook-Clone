package com.example.facebookcomposeui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.facebookcomposeui.ui.theme.FacebookComposeUITheme
import com.example.facebookcomposeui.util.CONSTANTS
import com.example.facebookcomposeui.util.CONSTANTS.HOME_SCREEN_ROUTE
import com.example.facebookcomposeui.util.CONSTANTS.SIGN_IN_SCREEN_ROUTE
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

            FacebookComposeUITheme {
                TransparentSystemBar()
                NavHost(navController = navController, startDestination = CONSTANTS.HOME_SCREEN_ROUTE) {
                    composable(route = CONSTANTS.HOME_SCREEN_ROUTE) {
                        HomeScreen {
                            navController.navigate(route = CONSTANTS.SIGN_IN_SCREEN_ROUTE) {
                                popUpTo(route = CONSTANTS.HOME_SCREEN_ROUTE) {
                                    inclusive = true
                                }
                            }
                        }
                    }
                    composable(route = SIGN_IN_SCREEN_ROUTE) {
                        SignInScreen {
                            navController.navigate(route = HOME_SCREEN_ROUTE){
                                popUpTo(route = SIGN_IN_SCREEN_ROUTE){
                                    inclusive = true
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun TransparentSystemBar() {
        val systemUiController = rememberSystemUiController()
        val useDarkIcons = MaterialTheme.colors.isLight
        SideEffect {
            systemUiController.setSystemBarsColor(
                color = Color.Transparent,
                darkIcons = useDarkIcons
            )
        }
    }


}


