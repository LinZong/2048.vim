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
//        gameBoard.setup()
//        gameBoard.set(GameBoard.Element(4), Coord(1,1))
//        gameBoard.set(GameBoard.Element(4), Coord(2,1))
        val data = arrayOf(
            arrayOf(-1, -1, -1, -1),
            arrayOf(-1,-1,-1,-1),
            arrayOf(-1, -1,2,2),
            arrayOf(-1,-1,-1,8)
        )
        for (i in 0 until 4) {
            for (j in 0 until 4) {
                if (data[i][j] != -1)
                    gameBoard.set(GameBoard.Element(data[i][j]), Coord(i, j))
            }
        }
    }
}