package com.nemesiss.dev.crossingcontainermovement

import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.nemesiss.dev.crossingcontainermovement.databinding.ActivityMainBinding
import com.nemesiss.dev.crossingcontainermovement.model.Coord

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private lateinit var gameBoard: GameBoard
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        gameBoard = GameBoard(binding.gameBoardView)
        binding.gameBoardView.setDecorView(window.decorView as ViewGroup)
        gameBoard.setup()

        binding.reset.setOnClickListener { gameBoard.reset() }
    }
}