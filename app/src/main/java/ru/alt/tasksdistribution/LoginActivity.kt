package ru.alt.tasksdistribution

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.transition.Visibility
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import com.android.volley.RequestQueue.RequestEvent
import ru.alt.tasksdistribution.databinding.ActivityLoginBinding
import ru.alt.tasksdistribution.helpers.PermissionsManager
import ru.alt.tasksdistribution.requests.Http
import java.util.UUID

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private lateinit var mainActivityIntent: Intent
    private val tag = this::class.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(tag, "OnCreate()")
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val requiredPermissions = arrayOf<String>(
            Manifest.permission.INTERNET
        )

        val permissionManager = PermissionsManager

        if (!permissionManager.checkPermissions(context = this, requiredPermissions)) {
            permissionManager.requestPermissions(activity = this, requiredPermissions)
        }

        val etUsername = binding.username
        val etPassword = binding.password
        val btnLogin = binding.login
        val pbLoading = binding.loading

        btnLogin.setOnClickListener {
            pbLoading.visibility = View.VISIBLE

            // request
            with (Http(this)) {
                sendCredentials(etUsername.text.toString(), etPassword.text.toString()).also {
                    it.addRequestEventListener { request, event ->
                        if (event == RequestEvent.REQUEST_FINISHED) {
                            Log.d(tag, "Extracted uuid = $uuid")
                            request.tag = "DONE"

                            if (this.uuid == "empty") {
                                Toast.makeText(this@LoginActivity, "Incorrect login or password", Toast.LENGTH_LONG).show()
                            }
                            else {
                                // save data
                                val sharedPrefs = this@LoginActivity.getSharedPreferences("userData", Context.MODE_PRIVATE)
                                with (sharedPrefs.edit()) {
                                    putBoolean("isLogged", true)
                                    putString("uuid", uuid)
                                    putString("username", etUsername.text.toString())
                                    putString("displayName", "User-${uuid.subSequence(0, 5)}")
                                    apply()
                                }

                                mainActivityIntent = Intent(this@LoginActivity, MainActivity::class.java)
                                    .putExtra("username", etUsername.text.toString())
                                    .putExtra("uuid", uuid)
                                    .putExtra("displayName", "User-${uuid.subSequence(0, 5)}")

                                pbLoading.visibility = View.GONE

                                // call main activity
                                this@LoginActivity.startActivity(mainActivityIntent)


                                Log.d(tag, "Clear queue")
                            }
                            it.cancelAll("DONE")
                        }
                    }
                }
            }
        }
    }

    override fun onStart() {
        Log.d(tag, "OnStart()")
        super.onStart()

        // get data from shared preferences
        val sharedPref = this.getSharedPreferences("userData", Context.MODE_PRIVATE)
        val isLoggedRecently = sharedPref.getBoolean("isLogged", false)

        if (isLoggedRecently) {
            Log.d(tag, "Found recently logging")
            mainActivityIntent = Intent(this@LoginActivity, MainActivity::class.java)
                .putExtra("uuid", sharedPref.getString("uuid", null))
                .putExtra("displayName", sharedPref.getString("displayName", "unknown"))
                .putExtra("username", sharedPref.getString("username", "unknown"))
            startActivity(mainActivityIntent)
        }

    }
}
