package io.github.aaiezza.bwic

import assertk.assertThat
import assertk.assertions.*
import org.junit.jupiter.api.Test

class GameEngineTest {

    val engine = GameEngine()

    @Test
    fun `player claims scoops on correct guess`() {
        val card = Card(
            hintFlavors = listOf(IceCreamFlavor.RED, IceCreamFlavor.BLUE, IceCreamFlavor.GREEN),
            actualScoop = Scoop(IceCreamFlavor.RED)
        )

        val state = GameState(
            players = listOf(PlayerState(Player(Player.Id("A")), Cone(emptyList()))),
            flavorGroups = mapOf(
                IceCreamFlavor.RED to listOf(
                    card.copy(actualScoop = Scoop(IceCreamFlavor.RED)),
                    card.copy(actualScoop = Scoop(IceCreamFlavor.RED))
                )
            ),
            deck = Deck(listOf(card)),
            turn = TurnState(TurnNumber(1), 0, TurnPhase.GuessFlavor),
            maxScoopsToWin = MaxScoopsToWin(3),
            phase = GamePhase.InProgress,
            history = emptyList()
        )

        val next = engine.applyAction(state, PlayerAction.GuessFlavor(IceCreamFlavor.RED))

        val updatedPlayer = next.players.first()
        assertThat(updatedPlayer.cone.scoops).hasSize(3)
        assertThat(updatedPlayer.cone.scoops.map { it.flavor }).each { it.isEqualTo(IceCreamFlavor.RED) }
        assertThat(next.phase).isEqualTo(GamePhase.Completed)
    }
}
