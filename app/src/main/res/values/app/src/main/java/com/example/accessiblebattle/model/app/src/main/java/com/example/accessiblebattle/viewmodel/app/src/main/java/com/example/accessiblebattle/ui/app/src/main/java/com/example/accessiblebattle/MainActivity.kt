package com.example.accessiblebattle

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.accessiblebattle.ui.BattleScreen
import com.example.accessiblebattle.viewmodel.BattleViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: BattleViewModel = viewModel()
            val state by viewModel.uiState.collectAsState()
            BattleScreen(
                state = state,
                onSwitchTap = viewModel::onSwitchRequested,
                onMegaToggle = viewModel::onMegaToggle,
                onMoveChosen = viewModel::onMoveChosen,
                onSwitchSelected = viewModel::onSwitchSelected,
                onSwitchBack = viewModel::onSwitchCancelled
            )
        }
    }
}
