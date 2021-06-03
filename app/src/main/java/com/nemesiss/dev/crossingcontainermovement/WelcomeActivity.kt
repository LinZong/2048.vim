package com.nemesiss.dev.crossingcontainermovement

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.nemesiss.dev.crossingcontainermovement.databinding.ActivityWelcomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WelcomeActivity : AppCompatActivity() {

    private val binding by lazy { ActivityWelcomeBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    /**
     * Start a new game.
     * @see [MainActivity]
     */
    fun startGame(view: View) {
        startActivity(Intent(this, MainActivity::class.java))
    }
    fun exitGame(view: View) {
        finish()
    }
    fun resumeGame(view: View) {
        // TODO add MMKV
    }
}