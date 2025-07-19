package io.github.aaiezza.bwic

object GameInitializer {
    fun startGame(game: Game): GameState {
        var deck = game.deck
        val flavorGroups = mutableMapOf<IceCreamFlavor, MutableList<Card>>()

        repeat(game.board.initialPrePopulatedCards) {
            val (newDeck, card) = deck.draw()
            deck = newDeck
            flavorGroups.computeIfAbsent(card.actualScoop.flavor) { mutableListOf() }.add(card)
        }

        val playerStates = game.players.map { PlayerState(it, Cone(emptyList())) }

        val initialState = GameState(
            players = playerStates,
            flavorGroups = flavorGroups.mapValues { it.value.toList() },
            deck = deck,
            turn = TurnState(
                number = TurnNumber(1),
                currentPlayerIndex = 0,
                phase = TurnPhase.RevealCard
            ),
            maxScoopsToWin = MaxScoopsToWin(game.board.requiredScoopsToWin),
            phase = GamePhase.InProgress,
            history = emptyList()
        )

        return initialState.copy(history = listOf(GameSnapshot(initialState)))
    }
}
