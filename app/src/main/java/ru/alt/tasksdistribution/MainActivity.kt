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
import com.android.volley.RequestQueue.RequestEvent
import com.google.android.material.navigation.NavigationView
import ru.alt.tasksdistribution.databinding.ActivityMainBinding
import ru.alt.tasksdistribution.requests.Http
import ru.alt.tasksdistribution.ui.map.MapViewModel
import ru.alt.tasksdistribution.ui.tasks.TasksViewModel
import ru.alt.tasksdistribution.ui.tasks.data.Task
import ru.alt.tasksdistribution.ui.tasks.data.TasksAdapter
import java.util.UUID

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private val tag = this::class.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(tag, "OnCreate()")
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

        Log.d(tag, "End OnCreate()")
    }

    override fun onStart() {
        super.onStart()

        // get extras
        val extras = intent.extras!!

        // bind profile fields
        val tvUsername: TextView = binding.navView.getHeaderView(0).findViewById(R.id.tvUsername)
        val tvEmailAddress: TextView = binding.navView.getHeaderView(0).findViewById(R.id.tvEmailAddress)

        // set values to user profile
        tvUsername.text = extras.getString("displayName")!!
        Log.d(tag, "display name = ${extras.getString("displayName")!!}")

        tvEmailAddress.text = extras.getString("login")!!
        Log.d(tag, "username = ${extras.getString("login")!!}")

        val tasksViewModel: TasksViewModel = ViewModelProvider(this)[TasksViewModel::class.java]

        // set user id
        val userId = extras.getString("id")!!
        Log.d(tag, "user id = $userId")
        tasksViewModel.setUserId(userId)


        // get list of tasks
        with (Http(this)) {
            getTasks(userId).also {
                it.addRequestEventListener { _, event ->
                    if (event == RequestEvent.REQUEST_FINISHED) {
                        Log.d(tag, "Extracted response = ${this.jsonResponse}")

                        // parse response
                        val taskList = emptyList<Task>()

                        tasksViewModel.recyclerView.observe(this@MainActivity) { rvTaskList ->
                            rvTaskList.adapter = TasksAdapter(taskList, this@MainActivity)
                        }
                        ViewModelProvider(this@MainActivity)[MapViewModel::class.java].also { mvm ->
                            mvm.setTaskList(taskList)
                        }
                    }
                }
            }
        }
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