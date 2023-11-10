package ru.alt.tasksdistribution

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import ru.alt.tasksdistribution.databinding.ActivityMainBinding
import ru.alt.tasksdistribution.requests.Http
import ru.alt.tasksdistribution.ui.tasks.TasksViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(this::class.simpleName, "OnCreate()")
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_tasks,
                R.id.nav_map,
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)



        val extras = intent.extras!!

        // bind profile fields
        val tvUsername: TextView = binding.navView.getHeaderView(0).findViewById(R.id.tvUsername)
        val tvEmailAddress: TextView = binding.navView.getHeaderView(0).findViewById(R.id.tvEmailAddress)

        // set values to user profile
        tvUsername.text = extras.getString("displayName")!!
        Log.d(this::class.simpleName, "display name = ${extras.getString("displayName")!!}")

        tvEmailAddress.text = extras.getString("login")!!
        Log.d(this::class.simpleName, "username = ${extras.getString("login")!!}")

        // set user id
        val tasksViewModel: TasksViewModel = ViewModelProvider(this)[TasksViewModel::class.java]

        val userId = extras.getString("id")!!

        Log.d("MainActivity", "user id = $userId")

        // tasksViewModel.setUserId(userId)

        // val taskList = Http.execute("GET", "/tasks", "token=$userId").get()
        // Log.d("MainActivity", "taskList = $taskList")
        tasksViewModel.recyclerView.observe(this) {
            // it.adapter = TasksAdapter(taskList, this)
        }

        Log.d(this::class.simpleName, "End OnCreate()")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}