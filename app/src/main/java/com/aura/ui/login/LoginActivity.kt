package com.aura.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.aura.R
import com.aura.databinding.ActivityLoginBinding
import com.aura.ui.home.HomeActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "userData")


/**
 * The login activity for the app.
 */
@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginActivityViewModel

    /**
     * The binding for the login layout.
     */
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpViewModel()


        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                loginViewModel.uiState.collect { isValid ->
                    binding.login.isEnabled = isValid.isCheckReady
                }
            }
        }

        binding.login.setOnClickListener {

            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    loginViewModel.pushConnexionData(
                        binding.identifier.text.toString(), binding.password.text.toString()
                    ).collect {
                        updateUiAfterLoginTry()
                    }
                }
            }


        }

        binding.identifier.doOnTextChanged { text, _, _, _ ->
            loginViewModel.validateLogin(text.toString(), binding.password.text.toString())
        }

        binding.password.doOnTextChanged { text, _, _, _ ->
            loginViewModel.validateLogin(binding.identifier.text.toString(), text.toString())
        }


    }

    /**
     * Update UI based on BusinessState.
     * Should be launch after a push.
     * Update Loading Error and Sucess State depending of the user connexion.
     */
    private fun updateUiAfterLoginTry() {
        val state = loginViewModel.uiBusinessState.value

        when {
            state.isViewLoading -> {
                binding.loading.visibility = View.VISIBLE
                binding.login.isEnabled = false
            }
            state.errorMessage?.isNotBlank() == true -> {
                binding.loading.visibility = View.GONE
                binding.login.isEnabled = true
                Toast.makeText(this, state.errorMessage, Toast.LENGTH_LONG).show()
            }
            state.login.isGranted -> {
                binding.loading.visibility = View.GONE
                binding.login.isEnabled = true

                val userNameCounter = stringPreferencesKey("Username")
                lifecycleScope.launch {
                    dataStore.edit { userData ->
                        userData[userNameCounter] = binding.identifier.text.toString()
                    }
                }

                val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }
            else -> {
                binding.loading.visibility = View.GONE
                binding.login.isEnabled = true
                Toast.makeText(this, getString(R.string.invalid_credentials), Toast.LENGTH_LONG).show()
            }
        }
    }




    private fun setUpViewModel() {
        loginViewModel = ViewModelProvider(this)[LoginActivityViewModel::class.java]
    }


}
