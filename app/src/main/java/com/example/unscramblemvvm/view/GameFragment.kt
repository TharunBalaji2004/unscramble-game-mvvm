package com.example.unscramblemvvm.view

import android.os.Binder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.example.unscramblemvvm.R
import com.example.unscramblemvvm.databinding.GameFragmentBinding
import com.example.unscramblemvvm.model.MAX_NO_OF_WORDS
import com.example.unscramblemvvm.viewmodel.GameViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class GameFragment : Fragment() {

    // Create reference of ViewModel
    private val viewModel: GameViewModel by viewModels()

    private lateinit var binding: GameFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.game_fragment,container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.gameViewModel = viewModel
        binding.maxNoOfWords = MAX_NO_OF_WORDS
        binding.lifecycleOwner = viewLifecycleOwner

        binding.submit.setOnClickListener {
            onSubmitWord()
        }
        binding.skip.setOnClickListener {
            onSkipWord()
        }
        updateNextWordOnScreen()
    }

    private fun onSubmitWord(){
        val playerWord = binding.textInputEditText.text.toString()

        if (viewModel.isUserWordCorrect(playerWord)){
            setErrorTextField(false)
            if (viewModel.maxWordsReached()){
                showFinalScoreDialog()
            }
        } else {
            setErrorTextField(true)
        }
    }

    private fun onSkipWord(){
        if (viewModel.maxWordsReached()){
            showFinalScoreDialog()
        } else {
            setErrorTextField(false)
            updateNextWordOnScreen()
        }
    }

    private fun updateNextWordOnScreen(){
        viewModel.currentScrambledWord.observe(viewLifecycleOwner) {
            newWord -> binding.textViewUnscrambledWord.text = newWord
        }
    }

    private fun setErrorTextField(error: Boolean){
        if (error) {
            binding.textField.isErrorEnabled = true
            binding.textField.error = getString(R.string.try_again)
        } else {
            binding.textField.isErrorEnabled = false
            binding.textInputEditText.text = null
        }
    }

    private fun showFinalScoreDialog(){
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.congratulations))
            .setMessage(getString(R.string.you_scored, viewModel.score.value))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.exit)) { _, _ ->
                exitGame()
            }
            .setPositiveButton(getString(R.string.play_again)) { _, _ ->
                restartGame()
            }
            .show()
    }

    private fun exitGame(){
        activity?.finish()
    }

    private fun restartGame(){
        viewModel.reinitdata()
        setErrorTextField(false)
        updateNextWordOnScreen()
    }
}