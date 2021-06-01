package com.nemesiss.dev.crossingcontainermovement

import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.nemesiss.dev.crossingcontainermovement.databinding.ActivityMainBinding
import com.nemesiss.dev.crossingcontainermovement.model.Coord
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private lateinit var gameBoard: GameBoard
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        gameBoard = GameBoard(binding.gameBoardView)
        binding.gameBoardView.setDecorView(window.decorView as ViewGroup)
        setup()

//        val fakeMap = arrayOf(
//            arrayOf(2, 4, 2, 4),
//            arrayOf(4, 2, 4, 2),
//            arrayOf(2, 4, 2, 4),
//            arrayOf(4, 2, 4, 2)
//        )
//
//        fakeSetup(fakeMap)

        binding.reset.setOnClickListener { gameBoard.reset() }
    }

    private fun setup() {
        gameBoard.setup()
    }

    private fun fakeSetup(map: Array<Array<Int>>) {
        val actions = arrayListOf<Pair<GameBoard.Element, Coord>>()
        for (i in map.indices) {
            for (j in map[i].indices) {
                actions += GameBoard.Element(map[i][j]) to Coord(i, j)
            }
        }
        gameBoard.set(actions)
    }
}