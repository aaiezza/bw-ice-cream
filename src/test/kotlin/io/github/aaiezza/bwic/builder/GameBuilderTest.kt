package io.github.aaiezza.bwic.builder

import assertk.assertThat
import assertk.assertions.*
import io.github.aaiezza.bwic.BoardSetUp
import io.github.aaiezza.bwic.Deck
import io.github.aaiezza.bwic.IceCreamFlavor
import io.github.aaiezza.bwic.Player
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GameBuilderTest {

    lateinit var subject: GameBuilder

    @BeforeEach
    fun init() {
        subject = GameBuilder
    }

    @Test
    fun `should build default game with 2 players`() {
        val game = subject
            .start()
            .withNumberOfPlayers(2)
            .withDefaultBoardSetUp()
            .withDefaultDeck()
            .build()

        assertThat(game.players).hasSize(2)
        assertThat(game.players.map { it.id.value }).containsExactly("Player 1", "Player 2")

        assertThat(game.board.flavors).isNotEmpty()
        assertThat(game.deck.size).isGreaterThan(0)
        val (deck, card) = game.deck.draw()
        assertThat(card.hintFlavors.size).isEqualTo(3)
        assertThat(deck.size).isEqualTo(game.deck.size - 1)
    }

    @Test
    fun `should build game with 3 named players`() {
        val game = subject.start()
            .withPlayers(Player.Id("Alex"), Player.Id("Bob"), Player.Id("Casey"))
            .withDefaultBoardSetUp()
            .withDefaultDeck()
            .build()

        assertThat(game.players.map { it.id.value }).containsExactly("Alex", "Bob", "Casey")
        assertThat(game.board.flavors).isNotEmpty()
        assertThat(game.deck.size).isGreaterThan(0)
    }

    @Test
    fun `should build game with custom settings`() {
        val game = subject.start()
            .withNumberOfPlayers(2)
            .withBoardSetUp({
                withFlavors(
                    IceCreamFlavor.RED,
                    IceCreamFlavor.WORMS,
                    IceCreamFlavor.BLUE,
                    IceCreamFlavor.GREEN
                )
                    .withInitialNumberOfPrePopulatedCards(7)
                    .withNumberOfClaimedCardsToWin(5)
            })
            .withDeck({
                withDefaultNumberOfHintFlavorsPerCard() // 3
                    .withNumberOfCards(15)
            })
            .build()

        assertThat(game.players).hasSize(2)
        assertThat(game.board.flavors).containsExactly(
            IceCreamFlavor.RED,
            IceCreamFlavor.WORMS,
            IceCreamFlavor.BLUE,
            IceCreamFlavor.GREEN
        )
        assertThat(game.board.initialPrePopulatedCards).isEqualTo(7)
        assertThat(game.board.requiredScoopsToWin).isEqualTo(5)
        assertThat(game.deck.size).isEqualTo(15)
        assertThat(game.deck.cards[0].hintFlavors).hasSize(3)
    }

    @Test
    fun `should override default deck and board if set explicitly`() {
        val customBoard: BoardSetUp.Builder.() -> Unit = {
            withFlavors(IceCreamFlavor.Custom("Anchovy"))
                .withInitialNumberOfPrePopulatedCards(1)
                .withNumberOfClaimedCardsToWin(2)
        }

        val customDeck: Deck.Builder.() -> Unit = {
            withNumberOfCards(5)
                .withDefaultNumberOfHintFlavorsPerCard()
        }

        val game = subject.start()
            .withNumberOfPlayers(1)
            .withBoardSetUp(customBoard)
            .withDeck(customDeck)
            .build()

        assertThat(game.players).hasSize(1)
        assertThat(game.board.flavors).containsExactly(IceCreamFlavor.Custom("Anchovy"))
        assertThat(game.board.initialPrePopulatedCards).isEqualTo(1)
        assertThat(game.board.requiredScoopsToWin).isEqualTo(2)
        assertThat(game.deck.size).isEqualTo(5)
    }

    @Test
    fun `should handle zero players and still build`() {
        val game = subject.start()
            .withPlayers()
            .withDefaultBoardSetUp()
            .withDefaultDeck()
            .build()

        assertThat(game.players).isEmpty()
    }
}
