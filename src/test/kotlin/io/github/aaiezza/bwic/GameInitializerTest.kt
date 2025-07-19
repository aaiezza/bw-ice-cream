package io.github.aaiezza.bwic

import assertk.assertThat
import assertk.assertions.*
import io.github.aaiezza.bwic.builder.GameBuilder
import org.junit.jupiter.api.Test

class GameInitializerTest {

    @Test
    fun `should initialize game state correctly`() {
        val game = GameBuilder
            .start()
            .withNumberOfPlayers(2)
            .withDefaultBoardSetUp()
            .withDefaultDeck()
            .build()

        val state = GameInitializer.startGame(game)

        assertThat(state.players).hasSize(2)
        assertThat(state.players.map { it.cone.scoops }).each { it.isEmpty() }

        assertThat(state.deck.cards.size).isLessThan(100) // 5 drawn off
        assertThat(state.flavorGroups.values.flatten()).hasSize(game.board.initialPrePopulatedCards)

        assertThat(state.turn.number.value).isEqualTo(1)
        assertThat(state.turn.currentPlayerIndex).isEqualTo(0)
        assertThat(state.turn.phase).isEqualTo(TurnPhase.RevealCard)

        assertThat(state.phase).isEqualTo(GamePhase.InProgress)
        assertThat(state.history).hasSize(1)
    }
}
