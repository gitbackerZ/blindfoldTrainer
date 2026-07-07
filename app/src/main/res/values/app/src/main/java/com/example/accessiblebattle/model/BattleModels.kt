package com.example.accessiblebattle.model

data class PokemonSlot(
    val name: String,
    val heldItem: String,
    val hpPercent: Int,
    val status: String?,
    val statChanges: String,
    val moves: List<MoveInfo>,
    val canMegaEvolve: Boolean,
    val isMegaEvolved: Boolean = false
)

data class MoveInfo(
    val name: String,
    val type: String,
    val basePower: Int,
    val pp: Int,
    val targetType: TargetType
)

enum class TargetType { SINGLE, MULTI, SELF, FIELD }
enum class Target { OPP_LEFT, OPP_RIGHT, PARTNER }

/** Which side of the field an action came from — needed because the left and
 *  right PokemonColumn/TurnOrderColumn instances share one callback each. */
enum class Side { LEFT, RIGHT }

data class BattleUiState(
    val leftMon: PokemonSlot,
    val rightMon: PokemonSlot,
    val leftTurnOrder: Int,
    val rightTurnOrder: Int,
    val benchCount: Int,
    val bench: List<PokemonSlot>,
    val showingSwitchOverlay: Boolean = false,
    val switchRequestedBy: Side? = null
)
