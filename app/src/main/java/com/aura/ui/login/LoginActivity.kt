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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * The login activity for the app.
 */
@AndroidEntryPoint
class LoginActivity : AppCompatActivity()
{

  private lateinit var loginViewModel: LoginActivityViewModel

  /**
   * The binding for the login layout.
   */
  private lateinit var binding: ActivityLoginBinding

  override fun onCreate(savedInstanceState: Bundle?)
  {
    super.onCreate(savedInstanceState)
    setUpViewModel()


    binding = ActivityLoginBinding.inflate(layoutInflater)
    setContentView(binding.root)

    val login = binding.login
    val loading = binding.loading

    login.setOnClickListener {
      loading.visibility = View.VISIBLE

      val intent = Intent(this@LoginActivity, HomeActivity::class.java)
      startActivity(intent)

      finish()
    }

    lifecycleScope.launch {
      repeatOnLifecycle(Lifecycle.State.STARTED) {
        loginViewModel.isLoginValid.collect { isValid ->
          binding.login.isEnabled = isValid
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
