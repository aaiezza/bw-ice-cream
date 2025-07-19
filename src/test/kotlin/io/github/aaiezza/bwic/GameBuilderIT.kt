package io.github.aaiezza.bwic

import assertk.assertThat
import assertk.assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GameBuilderIT {

    lateinit var subject: Game.Builder

    @BeforeEach
    fun init() {
        subject = Game.Builder()
    }

    @Test
    fun `should build default game with 2 players`() {
        val game = subject
            .withNumberOfPlayers(2)
            .withDefaultBoardSetUp()
            .withDefaultDeck
            .build()

        assertThat(game.players).hasSize(2)
        assertThat(game.players.map { it.id.value }).containsExactly("Player 1", "Player 2")

        assertThat(game.boardSetUp.flavors).isNotEmpty()
        assertThat(game.deck.numberOfCards).isGreaterThan(0)
        assertThat(game.deck.hintFlavorsPerCard).isEqualTo(3)
    }

    @Test
    fun `should build game with 3 named players`() {
        val game = subject
            .withPlayers(Player.Id("Alex"), Player.Id("Bob"), Player.Id("Casey"))
            .withDefaultBoardSetUp()
            .withDefaultDeck()
            .build()

        assertThat(game.players.map { it.id.value }).containsExactly("Alex", "Bob", "Casey")
        assertThat(game.boardSetUp.flavors).isNotEmpty()
        assertThat(game.deck.numberOfCards).isGreaterThan(0)
    }

    @Test
    fun `should build game with custom settings`() {
        val game = subject
            .withNumberOfPlayers(2)
            .withBoardSetUp(
                BoardSetUp.Builder()
                    .withFlavors(
                        IceCreamFlavor.RED,
                        IceCreamFlavor.WORMS,
                        IceCreamFlavor.BLUE,
                        IceCreamFlavor.PIZZA,
                        IceCreamFlavor.GREEN
                    )
                    .withInitialNumberOfPrePopulatedCards(7)
                    .withNumberOfClaimedCardsToWin(5)
                    .build()
            )
            .withDeck(
                Deck.Builder()
                    .withDefaultNumberOfHintFlavorsPerCard() // 3
                    .withNumberOfCards(15)
                    .build()
            )
            .build()

        assertThat(game.players).hasSize(2)
        assertThat(game.boardSetUp.flavors).containsExactly(
            IceCreamFlavor.RED,
            IceCreamFlavor.WORMS,
            IceCreamFlavor.BLUE,
            IceCreamFlavor.PIZZA,
            IceCreamFlavor.GREEN
        )
        assertThat(game.boardSetUp.initialPrePopulatedCards).isEqualTo(7)
        assertThat(game.boardSetUp.claimedCardsToWin).isEqualTo(5)
        assertThat(game.deck.numberOfCards).isEqualTo(15)
        assertThat(game.deck.hintFlavorsPerCard).isEqualTo(3)
    }

    @Test
    fun `should override default deck and board if set explicitly`() {
        val customBoard = BoardSetUp.Builder()
            .withFlavors(IceCreamFlavor.Custom("Anchovy"))
            .withInitialNumberOfPrePopulatedCards(1)
            .withNumberOfClaimedCardsToWin(2)
            .build()

        val customDeck = Deck.Builder()
            .withNumberOfCards(5)
            .withDefaultNumberOfHintFlavorsPerCard()
            .build()

        val game = subject
            .withNumberOfPlayers(1)
            .withBoardSetUp(customBoard)
            .withDeck(customDeck)
            .build()

        assertThat(game.players).hasSize(1)
        assertThat(game.boardSetUp.flavors).containsExactly(IceCreamFlavor.Custom("Anchovy"))
        assertThat(game.boardSetUp.initialPrePopulatedCards).isEqualTo(1)
        assertThat(game.boardSetUp.claimedCardsToWin).isEqualTo(2)
        assertThat(game.deck.numberOfCards).isEqualTo(5)
    }

    @Test
    fun `should handle zero players and still build`() {
        val game = subject
            .withPlayers()
            .withDefaultBoardSetUp()
            .withDefaultDeck()
            .build()

        assertThat(game.players).isEmpty()
    }
}
