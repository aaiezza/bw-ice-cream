package io.github.aaiezza.bwic

class GameEngine(private val maxScoopsToWin: MaxScoopsToWin) {

    fun applyAction(state: GameState, action: PlayerAction): GameState {
        return when (action) {
            is PlayerAction.RevealTopCard -> handleReveal(state)
            is PlayerAction.GuessFlavor -> handleGuess(state, action.flavor)
        }.recordSnapshot()
    }

    private fun handleReveal(state: GameState): GameState {
        require(state.turn.phase == TurnPhase.RevealCard) { "Must be in RevealCard phase" }
        require(state.deck.isNotEmpty()) { "Deck is empty" }

        return state.copy(
            turn = state.turn.copy(phase = TurnPhase.GuessFlavor)
        )
    }

    private fun handleGuess(state: GameState, guessedFlavor: IceCreamFlavor): GameState {
        require(state.turn.phase == TurnPhase.GuessFlavor)

        val card = state.deck.first()
        val remainingDeck = state.deck.drop(1)

        val correct = card.actualScoop.flavor == guessedFlavor
        val currentPlayer = state.players[state.turn.currentPlayerIndex]
        val newFlavorGroups = state.flavorGroups.toMutableMap()

        val updatedPlayers = state.players.toMutableList()

        if (correct) {
            val claimedScoops = (newFlavorGroups[guessedFlavor] ?: emptyList()).map { it.actualScoop } +
                    listOf(card.actualScoop)

            updatedPlayers[state.turn.currentPlayerIndex] = currentPlayer.copy(
                cone = currentPlayer.cone.addScoops(claimedScoops)
            )
            newFlavorGroups[guessedFlavor] = emptyList()
        } else {
            newFlavorGroups.getOrPut(card.actualScoop.flavor) { emptyList() }
                .let { list -> newFlavorGroups[card.actualScoop.flavor] = list + card }
        }

        val updatedState = state.copy(
            players = updatedPlayers,
            deck = remainingDeck,
            flavorGroups = newFlavorGroups,
            turn = nextTurn(state),
            phase = if (isVictory(updatedPlayers[state.turn.currentPlayerIndex])) GamePhase.Completed else GamePhase.InProgress
        )

        return updatedState
    }

    private fun isVictory(player: PlayerState): Boolean {
        return player.cone.scoops
            .groupingBy { it.flavor }
            .eachCount()
            .any { it.value >= maxScoopsToWin.value }
    }

    private fun nextTurn(state: GameState): TurnState {
        return TurnState(
            number = TurnNumber(state.turn.number.value + 1),
            currentPlayerIndex = (state.turn.currentPlayerIndex + 1) % state.players.size,
            phase = TurnPhase.RevealCard
        )
    }

    private fun GameState.recordSnapshot(): GameState {
        return this.copy(history = this.history + GameSnapshot(this))
    }
}
