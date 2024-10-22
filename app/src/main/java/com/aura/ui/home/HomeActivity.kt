package com.aura.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
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
import java.text.NumberFormat
import java.util.Locale

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

        val transfer = binding.transfer

        getDataOfUser()


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
                finish()
            }
        }

        binding.retry.setOnClickListener {
            getDataOfUser()
        }

        // Ajouter le callback à l'onBackPressedDispatcher
        onBackPressedDispatcher.addCallback(this, callback)
    }

    private fun getDataOfUser() {
        val userNameCounter = stringPreferencesKey("Username")

        val idCounterFlow: Flow<String> = this.dataStore.data
            .map { preferences ->
                // No type safety.
                preferences[userNameCounter] ?: ""
            }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                idCounterFlow.collect {
                    homeViewModel.pushConnexionData(it)
                        .collect {
                            updateUiAfterAccountTry()
                        }
                }
            }
        }
    }

    /**
     * Update UI based on BusinessState.
     * Should be launch after a get.
     * Update Loading Error and Sucess State depending of the user amount get try.
     */
    private fun updateUiAfterAccountTry() {
        val state = homeViewModel.homeBusinessState.value

        when {
            state.isViewLoading -> {
                binding.loading.visibility = View.VISIBLE
                binding.retry.visibility = View.GONE
                binding.balance.visibility = View.GONE
            }
            state.accounts.isNotEmpty() -> {
                binding.loading.visibility = View.GONE
                binding.retry.visibility = View.GONE
                binding.balance.visibility = View.VISIBLE


                for (account in state.accounts){
                    if(account.main) {
                        val formattedBalance = NumberFormat.getNumberInstance(Locale.FRANCE).format(account.balance)
                        binding.balance.text = getString(R.string.balance_amount, formattedBalance)
                    }
                }
            }
            state.errorMessage?.isNotBlank() == true -> {
                binding.loading.visibility = View.GONE
                binding.retry.visibility = View.VISIBLE
                binding.balance.visibility = View.GONE
                Toast.makeText(this, state.errorMessage, Toast.LENGTH_LONG).show()
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
