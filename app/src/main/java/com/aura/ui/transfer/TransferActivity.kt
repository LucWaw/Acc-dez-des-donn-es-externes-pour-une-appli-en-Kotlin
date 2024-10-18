package com.aura.ui.transfer

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.aura.databinding.ActivityTransferBinding
import dagger.hilt.android.AndroidEntryPoint
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
        // Ajouter des listeners pour les modifications de texte
        binding.recipient.doOnTextChanged { text, _, _, _ ->
            transferViewModel.validateLogin(text.toString(), binding.amount.text.toString())
        }

        binding.amount.doOnTextChanged { text, _, _, _ ->
            transferViewModel.validateLogin(binding.recipient.text.toString(), text.toString())
        }
    }



    private fun setUpViewModel() {
        transferViewModel = ViewModelProvider(this)[TransferActivityViewModel::class.java]
    }

}
