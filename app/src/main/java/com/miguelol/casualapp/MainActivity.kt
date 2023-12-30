package com.miguelol.casualapp

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import coil.Coil
import coil.ImageLoader
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.miguelol.casualapp.presentation.navigation.CasualNavGraph
import com.miguelol.casualapp.presentation.navigation.Destinations.LOGIN_ROUTE
import com.miguelol.casualapp.presentation.navigation.Destinations.PLANS_ROUTE
import com.miguelol.casualapp.presentation.theme.CasualAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var uid = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Coil.setImageLoader(ImageLoader(this))
        //val startingDestination = if (uid != null) PLANS_ROUTE else LOGIN_ROUTE

        setContent {
            CasualAppTheme {
                CasualNavGraph()
            }
        }
    }

    private fun notification() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }
            // Get new FCM registration token
            val token = task.result
            println("Token : $token")
        }
    }
}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CasualAppTheme {
        CasualNavGraph()
    }
}