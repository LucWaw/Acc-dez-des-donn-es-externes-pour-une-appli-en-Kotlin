package com.aura.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.aura.databinding.ActivityLoginBinding
import com.aura.ui.home.HomeActivity
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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
            loginViewModel.pushConnexionData(
                binding.identifier.text.toString(), binding.password.text.toString()
            )
            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    loginViewModel.uiState.collect {
                        if (it.login.isGranted) {
                            binding.loading.visibility = View.VISIBLE

                            val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                            startActivity(intent)

                            finish()
                        } else if (it.isViewLoading) {
                            binding.loading.visibility = View.VISIBLE
                        } else if (it.errorMessage?.isNotBlank() == true){
                            binding.loading.visibility = View.GONE
                            Snackbar.make(binding.root, it.errorMessage, Snackbar.LENGTH_LONG)
                                .show()
                        }
                    }
                }
            }
        }

// Ajouter des listeners pour les modifications de texte
        binding.identifier.doOnTextChanged { text, _, _, _ ->
            loginViewModel.validateLogin(text.toString(), binding.password.text.toString())
        }

        binding.password.doOnTextChanged { text, _, _, _ ->
            loginViewModel.validateLogin(binding.identifier.text.toString(), text.toString())
        }


    }


    private fun setUpViewModel() {
        loginViewModel = ViewModelProvider(this)[LoginActivityViewModel::class.java]
    }


}
