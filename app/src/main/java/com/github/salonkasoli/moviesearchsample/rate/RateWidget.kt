package com.github.salonkasoli.moviesearchsample.rate

import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.savedstate.SavedStateRegistry
import com.github.salonkasoli.moviesearchsample.core.ui.LoadingWidget

class RateWidget(
    private val seekBarContainer: View,
    private val rateText: TextView,
    private val seekBar: SeekBar,
    private val saveButton: View,
    private val loadingWidget: LoadingWidget,
    savedStateRegistry: SavedStateRegistry
) {

    var saveListener: ((progress: Float) -> Unit)? = null

    init {
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                rateText.text = "Ваша оценка: ${getRate(progress)}"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
            }
        })

        saveButton.setOnClickListener {
            saveListener?.invoke(getRate(seekBar.progress))
        }

        val bundle: Bundle? = savedStateRegistry.consumeRestoredStateForKey(BUNDLE_KEY)
        if (bundle != null) {
            updateProgress(bundle.getInt(STATE_PROGRESS, 0))
        } else {
            updateProgress(50)
        }
        savedStateRegistry.registerSavedStateProvider(BUNDLE_KEY, {
            Bundle().apply {
                putInt(STATE_PROGRESS, seekBar.progress)
            }
        })

        loadingWidget.hide()
    }

    fun showLoading() {
        seekBarContainer.visibility = View.GONE
        loadingWidget.showLoading()
    }

    fun showError() {
        loadingWidget.hide()
        seekBarContainer.visibility = View.VISIBLE
        Toast.makeText(seekBar.context, "Произошла ошибка", Toast.LENGTH_SHORT).show()
    }

    private fun updateProgress(progress: Int) {
        seekBar.progress = progress
        rateText.text = "Ваша оценка: ${getRate(progress)}"
    }

    private fun getRate(progress: Int): Float {
        // 0 - 10 округляем до 0,5.
        val progress: Float = Math.round((progress / 10f) * 2) / 2f
        return Math.max(0.5f, progress)
    }

    companion object {

        private const val BUNDLE_KEY = "rate_widget"
        private const val STATE_PROGRESS = "progress"
    }
}