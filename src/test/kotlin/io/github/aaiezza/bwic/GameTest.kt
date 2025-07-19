package io.github.aaiezza.bwic

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isEqualToWithGivenProperties
import assertk.assertions.isTrue
import io.github.aaiezza.bwic.builder.GameBuilder
import org.junit.jupiter.api.Test

class GameTest {
    @Test
    fun `bots play deterministic game to completion`() {
        val game = GameBuilder
            .start()
            .withNumberOfPlayers(2)
            .withBoardSetUp { withNumberOfClaimedCardsToWin(6) }
            .withDeck {
                withDefaultNumberOfHintFlavorsPerCard()
                withNumberOfCards(100)
                withSeed(42L) // deterministic randomness
            }
            .build()

        val engine = GameEngine()
        val initialState = GameInitializer.startGame(game)

        val turnLimit = 1000 // safety to avoid infinite games
        val finalState = (0 until turnLimit).runningFold(initialState) { current, _ ->
            if (current.phase == GamePhase.Completed) return@runningFold current

            val topCard = current.deck.asList().first()
            val afterReveal = engine.applyAction(current, PlayerAction.RevealTopCard)

            val guess = topCard.hintFlavors.maxByOrNull { flavor ->
                current.flavorGroups[flavor]?.size ?: 0
            } ?: topCard.hintFlavors.first()

            engine.applyAction(afterReveal, PlayerAction.GuessFlavor(guess))
        }.last { it.phase == GamePhase.Completed }

        // Validate win condition
        val winner = finalState.players.firstOrNull {
            it.cone.scoops.size >= game.board.requiredScoopsToWin
        }

        assertThat(winner != null).isTrue()
        assertThat(finalState.phase).isEqualTo(GamePhase.Completed)
        assertThat(finalState.history.last().state).isEqualToWithGivenProperties(
            finalState,
            GameState::players,
            GameState::flavorGroups,
            GameState::deck,
            GameState::turn,
            GameState::maxScoopsToWin,
            GameState::phase
        )
    }
}
