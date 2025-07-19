package io.github.aaiezza.bwic

class GameEngine {

    fun applyAction(state: GameState, action: PlayerAction): GameState {
        return when (action) {
            is PlayerAction.RevealTopCard -> handleReveal(state)
            is PlayerAction.GuessFlavor -> handleGuess(state, action.flavor)
        }.recordSnapshot()
    }

    private fun handleReveal(state: GameState): GameState {
        require(state.turn.phase == TurnPhase.RevealCard) { "Must be in RevealCard phase" }
        require(state.deck.asList().isNotEmpty()) { "Deck is empty" }

        return state.copy(
            turn = state.turn.copy(phase = TurnPhase.GuessFlavor)
        )
    }

    private fun handleGuess(state: GameState, guessedFlavor: IceCreamFlavor): GameState {
        require(state.turn.phase == TurnPhase.GuessFlavor)

        val (newDeck, card) = state.deck.draw()
        val correct = card.actualScoop.flavor == guessedFlavor
        val currentPlayer = state.players[state.turn.currentPlayerIndex]
        val newFlavorGroups = state.flavorGroups.mapValues { it.value.toMutableList() }.toMutableMap()
        val updatedPlayers = state.players.toMutableList()

        if (correct) {
            val claimed = newFlavorGroups[guessedFlavor].orEmpty().map { it.actualScoop } + card.actualScoop
            updatedPlayers[state.turn.currentPlayerIndex] = currentPlayer.copy(
                cone = currentPlayer.cone.addScoops(claimed)
            )
            newFlavorGroups[guessedFlavor] = mutableListOf()
        } else {
            val flavor = card.actualScoop.flavor
            val updatedPile = newFlavorGroups.getOrPut(flavor) { mutableListOf() }.toMutableList()
            updatedPile += card
            newFlavorGroups[flavor] = updatedPile
        }

        val updatedState = state.copy(
            players = updatedPlayers,
            deck = newDeck,
            flavorGroups = newFlavorGroups,
            turn = nextTurn(state),
            phase = if (isVictory(state, updatedPlayers[state.turn.currentPlayerIndex])) GamePhase.Completed else GamePhase.InProgress
        )

        return updatedState
    }

    private fun isVictory(gameState: GameState, player: PlayerState) =
        player.cone.scoops.size >= gameState.maxScoopsToWin.value
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
