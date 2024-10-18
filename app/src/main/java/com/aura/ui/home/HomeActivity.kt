package com.aura.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.aura.R
import com.aura.databinding.ActivityHomeBinding
import com.aura.ui.login.LoginActivity
import com.aura.ui.login.dataStore
import com.aura.ui.transfer.TransferActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * The home activity for the app.
 */
@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    private lateinit var homeViewModel: HomeActivityViewModel

    /**
     * The binding for the home layout.
     */
    private lateinit var binding: ActivityHomeBinding

    /**
     * A callback for the result of starting the TransferActivity.
     */
    private val startTransferActivityForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            //TODO
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpViewModel()

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val balance = binding.balance
        val transfer = binding.transfer

        val userNameCounter = stringPreferencesKey("Username")

        val exampleCounterFlow: Flow<String> = this.dataStore.data
            .map { preferences ->
                // No type safety.
                preferences[userNameCounter] ?: ""
            }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                exampleCounterFlow.collect{
                    homeViewModel.pushConnexionData(it)
                    updateUiWithBalance()
                }
            }
        }


        transfer.setOnClickListener {
            startTransferActivityForResult.launch(
                Intent(
                    this@HomeActivity,
                    TransferActivity::class.java
                )
            )
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Terminer l'application
                finish()
            }
        }

        // Ajouter le callback à l'onBackPressedDispatcher
        onBackPressedDispatcher.addCallback(this, callback)
    }

    private suspend fun updateUiWithBalance() {
        homeViewModel.uiBusinessState.collect {
            if (it.isViewLoading){
                binding.loading.visibility = View.VISIBLE
            }
        }
    }

    private fun setUpViewModel() {
        homeViewModel = ViewModelProvider(this)[HomeActivityViewModel::class.java]

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.disconnect -> {
                startActivity(Intent(this@HomeActivity, LoginActivity::class.java))
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

}
