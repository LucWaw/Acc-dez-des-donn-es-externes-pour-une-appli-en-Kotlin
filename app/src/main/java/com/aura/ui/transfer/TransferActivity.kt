package com.aura.ui.transfer

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.aura.R
import com.aura.databinding.ActivityTransferBinding
import com.aura.ui.home.HomeActivity
import com.aura.ui.login.dataStore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * The transfer activity for the app.
 */
@AndroidEntryPoint
class TransferActivity : AppCompatActivity() {
    private lateinit var transferViewModel: TransferActivityViewModel

    /**
     * The binding for the transfer layout.
     */
    private lateinit var binding: ActivityTransferBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpViewModel()

        binding = ActivityTransferBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val recipient = binding.recipient
        val amount = binding.amount
        val transfer = binding.transfer
        val loading = binding.loading

        transfer.setOnClickListener {
            loading.visibility = View.VISIBLE

            setResult(Activity.RESULT_OK)
            finish()
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                transferViewModel.uiState.collect { isValid ->
                    binding.transfer.isEnabled = isValid.isTransferReady
                }
            }
        }
        val userNameCounter = stringPreferencesKey("Username")

        val idCounterFlow: Flow<String> = this.dataStore.data
            .map { preferences ->
                // No type safety.
                preferences[userNameCounter] ?: ""
            }

        binding.transfer.setOnClickListener {

            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    idCounterFlow.collect {
                        transferViewModel.pushTransferData(it, binding.recipient.text.toString(), binding.amount.text.toString().toDouble())
                            .collect {
                                updateUiAfterTransferTry()
                            }
                    }
                }
            }


        }

        // Ajouter des listeners pour les modifications de texte
        binding.recipient.doOnTextChanged { text, _, _, _ ->
            transferViewModel.validateLogin(text.toString(), binding.amount.text.toString())
        }

        binding.amount.doOnTextChanged { text, _, _, _ ->
            transferViewModel.validateLogin(binding.recipient.text.toString(), text.toString())
        }
    }

    /**
     * Update UI based on BusinessState.
     * Should be launch after a push.
     * Update Loading Error and Sucess State depending of the transfer result.
     */
    private fun updateUiAfterTransferTry() {
        val state = transferViewModel.uiBusinessState.value

        when {
            state.isViewLoading -> {
                binding.loading.visibility = View.VISIBLE
                binding.transfer.isEnabled = false
            }
            state.errorMessage?.isNotBlank() == true -> {
                binding.loading.visibility = View.GONE
                binding.transfer.isEnabled = true
                Toast.makeText(this, state.errorMessage, Toast.LENGTH_LONG).show()
            }
            state.transfer.result -> {
                binding.loading.visibility = View.GONE
                binding.transfer.isEnabled = true



                val intent = Intent(this@TransferActivity, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }
            else -> {
                binding.loading.visibility = View.GONE
                binding.transfer.isEnabled = true
                Toast.makeText(this, getString(R.string.invalid_transfer), Toast.LENGTH_LONG).show()
            }
        }
    }


    private fun setUpViewModel() {
        transferViewModel = ViewModelProvider(this)[TransferActivityViewModel::class.java]
    }

}
