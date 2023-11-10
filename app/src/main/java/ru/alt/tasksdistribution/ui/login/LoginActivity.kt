package ru.alt.tasksdistribution.ui.login

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import ru.alt.tasksdistribution.MainActivity
import ru.alt.tasksdistribution.R
import ru.alt.tasksdistribution.databinding.ActivityLoginBinding
import ru.alt.tasksdistribution.requests.Http
import ru.alt.tasksdistribution.ui.tasks.TasksViewModel

class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding

    private lateinit var mainActivityIntent: Intent
    private var isLoggedRecently: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("LoginActivity", "OnCreate()")
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!checkPermissions(context = this)) {
            requestPermissions(this)
        }

        val username = binding.username
        val password = binding.password
        val login = binding.login
        val loading = binding.loading

        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())[LoginViewModel::class.java]

        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            login.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        })

        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
            val loginResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                updateUiWithUser(loginResult.success)
            }
            setResult(Activity.RESULT_OK)

            with (getSharedPreferences("userData", Context.MODE_PRIVATE).edit()) {
                putBoolean("isLoggedRecently", true)
                putString("id", loginResult.success?.userId.toString())
                putString("login", loginResult.success?.email)
                putString("displayName", loginResult.success?.displayName)
                apply()
            }

            val tasksViewModel: TasksViewModel = ViewModelProvider(this)[TasksViewModel::class.java]
            // tasksViewModel.setUserId(loginResult.success?.userId.toString())

            //val taskList = Http.execute("GET", "/tasks", "token=${loginResult.success?.userId}").get()
            //tasksViewModel.setTaskList(taskList)

            //Complete and destroy login activity once successful
            finish()

        })

        username.afterTextChanged {
            loginViewModel.loginDataChanged(
                username.text.toString(),
                password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        loginViewModel.login(
                            context,
                            username.text.toString(),
                            password.text.toString()
                        )
                }
                false
            }

            login.setOnClickListener {
                loading.visibility = View.VISIBLE
                loginViewModel.login(context, username.text.toString(), password.text.toString())
            }
        }
    }

    override fun onStart() {
        Log.d("LoginActivity", "OnStart()")
        super.onStart()

        // get data from shared preferences
        val sharedPref = this.getSharedPreferences("userData", Context.MODE_PRIVATE)
        isLoggedRecently = sharedPref.getBoolean("isLoggedRecently", false)

        if (isLoggedRecently) {
            Log.d("LoginActivity", "Write sharedPrefs")
            mainActivityIntent = Intent(this@LoginActivity, MainActivity::class.java)
                .putExtra("id", sharedPref.getString("id", null))
                .putExtra("displayName", sharedPref.getString("displayName", "unknown"))
                .putExtra("login", sharedPref.getString("login", "unknown"))
            startActivity(mainActivityIntent)
        }

    }

    override fun onDestroy() {
        Log.d("LoginActivity", "OnDestroy()")
        val sharedPref = this.getSharedPreferences("userData", Context.MODE_PRIVATE)

        mainActivityIntent = Intent(this@LoginActivity, MainActivity::class.java)
            .putExtra("id", sharedPref.getString("id", null))
            .putExtra("displayName", sharedPref.getString("displayName", "unknown"))
            .putExtra("login", sharedPref.getString("login", "unknown"))
        startActivity(mainActivityIntent)

        super.onDestroy()
    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName
        Toast.makeText(
            applicationContext,
            "$welcome $displayName",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }
}


fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })

}
    private fun checkPermissions(context: Context): Boolean {
        val requestedPermissions = arrayOf(
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        )

        for (permission in requestedPermissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PermissionChecker.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    private fun requestPermissions(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ),
            200
        )
    }
