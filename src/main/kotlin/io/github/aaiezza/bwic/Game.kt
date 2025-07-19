package io.github.aaiezza.bwic

class Game(
    val players: List<Player>,
    val board: Board,
    val deck: Deck
)

enum class TurnPhase {
    RevealCard,
    GuessFlavor,
    ResolveGuess,
    EndTurn
}

data class TurnState(
    val number: TurnNumber,
    val currentPlayerIndex: Int,
    val phase: TurnPhase
)

data class GameState(
    val players: List<PlayerState>,
    val flavorGroups: Map<IceCreamFlavor, List<Card>>,
    val deck: Deck,
    val turn: TurnState,
    val maxScoopsToWin: MaxScoopsToWin,
    val phase: GamePhase,
    val history: List<GameSnapshot>
)

enum class GamePhase {
    SetUp,
    InProgress,
    Completed
}

data class GameSnapshot(val state: GameState)
