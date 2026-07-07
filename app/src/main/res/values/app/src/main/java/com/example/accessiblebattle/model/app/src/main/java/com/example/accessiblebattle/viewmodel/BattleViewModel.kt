package com.example.accessiblebattle.viewmodel

import androidx.lifecycle.ViewModel
import com.example.accessiblebattle.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class BattleViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(sampleState())
    val uiState: StateFlow<BattleUiState> = _uiState

    fun onSwitchRequested(side: Side) {
        _uiState.update { it.copy(showingSwitchOverlay = true, switchRequestedBy = side) }
    }

    fun onSwitchCancelled() {
        _uiState.update { it.copy(showingSwitchOverlay = false, switchRequestedBy = null) }
    }

    fun onSwitchSelected(chosen: PokemonSlot) {
        _uiState.update { state ->
            val side = state.switchRequestedBy ?: return@update state
            val outgoing = if (side == Side.LEFT) state.leftMon else state.rightMon
            val newBench = state.bench.map { if (it == chosen) outgoing else it }
            when (side) {
                Side.LEFT -> state.copy(leftMon = chosen, bench = newBench, showingSwitchOverlay = false, switchRequestedBy = null)
                Side.RIGHT -> state.copy(rightMon = chosen, bench = newBench, showingSwitchOverlay = false, switchRequestedBy = null)
            }
        }
    }

    fun onMegaToggle(side: Side, on: Boolean) {
        _uiState.update { state ->
            when (side) {
                Side.LEFT -> state.copy(leftMon = state.leftMon.copy(isMegaEvolved = on))
                Side.RIGHT -> state.copy(rightMon = state.rightMon.copy(isMegaEvolved = on))
            }
        }
    }

    fun onMoveChosen(side: Side, move: MoveInfo, target: Target?) {
        // TODO: hand (side, move, target) off to your battle engine.
        // This ViewModel only owns the accessible-UI state, not turn resolution.
    }

    private fun sampleState(): BattleUiState {
        val sampleMoves = listOf(
            MoveInfo("Thunderbolt", "Electric", 90, 15, TargetType.SINGLE),
            MoveInfo("Protect", "Normal", 0, 10, TargetType.SELF),
            MoveInfo("Earthquake", "Ground", 100, 10, TargetType.MULTI),
            MoveInfo("Volt Switch", "Electric", 70, 20, TargetType.SINGLE)
        )
        val left = PokemonSlot("Pikachu", "Light Ball", 100, null, "No stat changes", sampleMoves, false)
        val right = PokemonSlot("Charizard", "Charizardite Y", 78, "burned", "+1 SpAtk", sampleMoves, true)
        val bench = listOf(
            PokemonSlot("Garchomp", "Rocky Helmet", 100, null, "No stat changes", sampleMoves, false),
            PokemonSlot("Ferrothorn", "Leftovers", 100, null, "No stat changes", sampleMoves, false)
        )
        return BattleUiState(left, right, 1, 2, bench.size, bench)
    }
}
