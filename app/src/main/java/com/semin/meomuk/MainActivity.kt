package com.semin.meomuk

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.semin.meomuk.databinding.ActivityMainBinding
import com.semin.meomuk.ui.MenuSuggestFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.util.concurrent.TimeoutException

data class Food (
    val name: String = "",
    val desc: String = "",
    val imageUrl: String = "",
    val recipeContent: String = "",
    val historyContent: String = "",
    var id: String = ""
)

data class Comment (
    val userName: String = "",
    val text: String = "",
    val rating: Float = 0f,
    val timestamp: Long = System.currentTimeMillis()
)

class MainActivity : AppCompatActivity() {
    private val db = Firebase.firestore
    private val foods: MutableList<Food> = mutableListOf()
    private lateinit var binding: ActivityMainBinding
    private val scope = CoroutineScope(Dispatchers.Main)
    private var dataReady = false
    private val tag = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.customActionBar.actionBarTitle.text = destination.label.toString()
        }

        // Keep the splash screen visible until the data is loaded
        splashScreen.setKeepOnScreenCondition { !dataReady }

        loadDataFromFirestore()
    }

    private fun loadDataFromFirestore() {
        scope.launch {
            try {
                withTimeout(3000) {
                    val result = withContext(Dispatchers.IO) {
                        db.collection("foods").get().await()
                    }
                    val documents = result.documents
                    if (documents.isEmpty()) {
                        Log.e(tag, "Data is empty")
                    }
                    else {
                        documents.forEach { item ->
                            val food = item.toObject<Food>()
                            if (food != null) {
                                food.id = item.id
                                foods.add(food)
                                Glide.with(this@MainActivity).load(food.imageUrl).diskCacheStrategy(DiskCacheStrategy.ALL).preload()
                            }
                        }
                    }
                    dataReady = true
                }
            } catch (e: TimeoutException) {
                Log.e(tag, "TimeoutException: ${e.message}")
            } catch (e: Exception) {
                Log.e(tag, e.toString())
            }
        }
    }

    fun getFoods(): List<Food> {
        return foods
    }

    fun addFood(food: Food) {
        foods.add(food)
    }
}