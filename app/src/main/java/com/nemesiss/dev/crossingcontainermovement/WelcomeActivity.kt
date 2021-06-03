package com.nemesiss.dev.crossingcontainermovement

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.nemesiss.dev.crossingcontainermovement.databinding.ActivityWelcomeBinding
import com.nemesiss.dev.crossingcontainermovement.manager.SaveDataManager
import com.tencent.mmkv.MMKV
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WelcomeActivity : AppCompatActivity() {

    private val binding by lazy { ActivityWelcomeBinding.inflate(layoutInflater) }

    private val mmkv = MMKV.defaultMMKV()!!

    private val saveDataManager = SaveDataManager.INSTANCE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        if (saveDataManager.savedMapExists()) {
            binding.resume.visibility = View.VISIBLE
        } else {
            binding.resume.visibility = View.GONE
        }
    }

    /**
     * Start a new game.
     * @see [MainActivity]
     */
    fun startGame(view: View) {
        mmkv.remove(GameConfig.SaveKey)
        startActivity(Intent(this, MainActivity::class.java))
    }

    fun exitGame(view: View) {
        finish()
    }

    fun resumeGame(view: View) {
        val resumeIntent = Intent(this, MainActivity::class.java)
        resumeIntent.action = "Resume"
        startActivity(resumeIntent)
    }
}