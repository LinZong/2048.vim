package com.nemesiss.dev.crossingcontainermovement

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.snackbar.Snackbar
import com.nemesiss.dev.crossingcontainermovement.databinding.ActivityMainBinding
import com.nemesiss.dev.crossingcontainermovement.manager.SaveDataManager
import com.nemesiss.dev.crossingcontainermovement.model.Coord
import com.nemesiss.dev.crossingcontainermovement.util.NO_OP
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private lateinit var gameBoard: GameBoard

    private val saveDataManager = SaveDataManager.INSTANCE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        gameBoard = GameBoard(binding.gameBoardView)
        binding.gameBoardView.setDecorView(window.decorView as ViewGroup)


        val action = intent.action
        if (action == "Resume") {
            val map = saveDataManager.getSavedMap()
            if (map != null) {
                gameBoard.set(map)
                return
            } else {
                Snackbar.make(binding.root, getString(R.string.savedata_broken_hint), Snackbar.LENGTH_SHORT).show()
                gameBoard.setup()
            }
        } else {
            saveDataManager.removeSavedMap()
            gameBoard.setup()
        }

//        setup()

//        val fakeMap = arrayOf(
//            arrayOf(2, 4, 2, 4),
//            arrayOf(4, 2, 4, 2),
//            arrayOf(2, 4, 2, 4),
//            arrayOf(4, 2, 4, 2)
//        )
//
//        fakeSetup(fakeMap)

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

    fun reset(view: View) {
        MaterialDialog(this).show {
            title(text = "Confirm")
            message(text = "Are you sure to reset current game board?")
            positiveButton {
                gameBoard.reset()
            }
            negativeButton {
                NO_OP()
            }
        }
    }
}