package com.example.accessiblebattle.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.semantics.*
import com.example.accessiblebattle.model.*

@Composable
fun BattleScreen(
    state: BattleUiState,
    onSwitchTap: (Side) -> Unit,
    onMegaToggle: (Side, Boolean) -> Unit,
    onMoveChosen: (Side, MoveInfo, Target?) -> Unit,
    onSwitchSelected: (PokemonSlot) -> Unit,
    onSwitchBack: () -> Unit
) {
    if (state.showingSwitchOverlay) {
        SwitchPokemonOverlay(bench = state.bench, onSelect = onSwitchSelected, onBack = onSwitchBack)
    } else {
        MoveSelectionOverlay(state, onSwitchTap, onMegaToggle, onMoveChosen)
    }
}

@Composable
fun MoveSelectionOverlay(
    state: BattleUiState,
    onSwitchTap: (Side) -> Unit,
    onMegaToggle: (Side, Boolean) -> Unit,
    onMoveChosen: (Side, MoveInfo, Target?) -> Unit
) {
    Column(
        Modifier.fillMaxSize()
            .semantics { contentDescription = "Move Selection. Select the moves of your Pokémon." }
    ) {
        Row(Modifier.weight(1f)) {
            TurnOrderColumn(state.leftTurnOrder, Side.LEFT, onSwitchTap, Modifier.weight(1f))
            PokemonColumn(state.leftMon, Side.LEFT, onMegaToggle, onMoveChosen, Modifier.weight(3f))
            PokemonColumn(state.rightMon, Side.RIGHT, onMegaToggle, onMoveChosen, Modifier.weight(3f))
            TurnOrderColumn(state.rightTurnOrder, Side.RIGHT, onSwitchTap, Modifier.weight(1f))
        }
    }
}

@Composable
fun TurnOrderColumn(turnOrder: Int, side: Side, onSwitchTap: (Side) -> Unit, modifier: Modifier) {
    Column(modifier.fillMaxHeight()) {
        Box(
            Modifier.weight(1f).fillMaxWidth()
                .clearAndSetSemantics { contentDescription = "Turn order $turnOrder" },
            contentAlignment = Alignment.Center
        ) { Text("$turnOrder") }

        Box(
            Modifier.weight(5f).fillMaxWidth()
                .clickable(onClickLabel = "Switch Pokémon") { onSwitchTap(side) }
                .clearAndSetSemantics { contentDescription = "Switch"; role = Role.Button },
            contentAlignment = Alignment.Center
        ) { Text("Switch", modifier = Modifier.rotate(-90f)) }
    }
}

@Composable
fun PokemonColumn(
    pokemon: PokemonSlot,
    side: Side,
    onMegaToggle: (Side, Boolean) -> Unit,
    onMoveChosen: (Side, MoveInfo, Target?) -> Unit,
    modifier: Modifier
) {
    var showingStatChanges by remember { mutableStateOf(false) }

    Column(modifier.fillMaxHeight()) {
        val infoText = if (showingStatChanges) pokemon.statChanges
            else "${pokemon.name}, ${pokemon.heldItem}, ${pokemon.hpPercent}% HP" +
                 (pokemon.status?.let { ", $it" } ?: "")

        Box(
            Modifier.weight(1f).fillMaxWidth()
                .clickable(onClickLabel = if (showingStatChanges) "Show info" else "Show stat changes") {
                    showingStatChanges = !showingStatChanges
                }
                .clearAndSetSemantics { contentDescription = infoText; role = Role.Button },
            contentAlignment = Alignment.Center
        ) { Text(infoText) }

        pokemon.moves.forEach { move -> MoveCell(move, side, onMoveChosen, Modifier.weight(1f)) }

        val megaLabel = if (pokemon.isMegaEvolved) "on" else "off"
        Box(
            Modifier.weight(1f).fillMaxWidth()
                .clickable(enabled = !pokemon.isMegaEvolved, onClickLabel = "Mega Evolve") {
                    onMegaToggle(side, true)
                }
                .clearAndSetSemantics {
                    contentDescription = "Mega Evolve, $megaLabel"
                    if (pokemon.isMegaEvolved) disabled() else role = Role.Button
                },
            contentAlignment = Alignment.Center
        ) { Text("Mega Evolve: $megaLabel") }
    }
}

@Composable
fun MoveCell(move: MoveInfo, side: Side, onMoveChosen: (Side, MoveInfo, Target?) -> Unit, modifier: Modifier) {
    var expanded by remember { mutableStateOf(false) }
    val readout = "${move.name}, ${move.type}, ${move.basePower} power, ${move.pp} PP"

    if (!expanded) {
        Box(
            modifier.fillMaxWidth()
                .clickable(onClickLabel = "Select move") {
                    if (move.targetType == TargetType.SINGLE) expanded = true
                    else onMoveChosen(side, move, null)
                }
                .clearAndSetSemantics { contentDescription = readout; role = Role.Button },
            contentAlignment = Alignment.Center
        ) { Text(readout) }
    } else {
        Row(modifier.fillMaxWidth()) {
            listOf("Left" to Target.OPP_LEFT, "Right" to Target.OPP_RIGHT, "Partner" to Target.PARTNER)
                .forEach { (label, target) ->
                    Box(
                        Modifier.weight(1f).fillMaxHeight()
                            .clickable(onClickLabel = "Target $label") {
                                onMoveChosen(side, move, target)
                                expanded = false
                            }
                            .clearAndSetSemantics { contentDescription = label; role = Role.Button },
                        contentAlignment = Alignment.Center
                    ) { Text(label) }
                }
        }
    }
}

@Composable
fun SwitchPokemonOverlay(bench: List<PokemonSlot>, onSelect: (PokemonSlot) -> Unit, onBack: () -> Unit) {
    Column(Modifier.fillMaxSize()) {
        Box(
            Modifier.weight(1f).fillMaxWidth()
                .clearAndSetSemantics { contentDescription = "Switch Pokémon. Select which Pokémon to switch to." },
            contentAlignment = Alignment.Center
        ) { Text("Switch Pokémon") }

        repeat(2) { index ->
            val mon = bench.getOrNull(index)
            Box(
                Modifier.weight(1f).fillMaxWidth()
                    .then(if (mon != null) Modifier.clickable(onClickLabel = "Confirm switch") { onSelect(mon) } else Modifier)
                    .clearAndSetSemantics {
                        if (mon != null) {
                            contentDescription = "${mon.name}, ${mon.heldItem}, ${mon.hpPercent}% HP" + (mon.status?.let { ", $it" } ?: "")
                            role = Role.Button
                        } else {
                            contentDescription = "Empty"
                            disabled()
                        }
                    },
                contentAlignment = Alignment.Center
            ) { Text(mon?.name ?: "") }
        }

        Box(
            Modifier.weight(1f).fillMaxWidth()
                .clickable(onClickLabel = "Go back") { onBack() }
                .clearAndSetSemantics { contentDescription = "Back"; role = Role.Button },
            contentAlignment = Alignment.Center
        ) { Text("Back") }
    }
}
