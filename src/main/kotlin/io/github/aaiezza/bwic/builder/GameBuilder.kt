package io.github.aaiezza.bwic.builder

import io.github.aaiezza.bwic.*

object GameBuilder {
    fun start(): PlayerStep = Impl()

    private class Impl : PlayerStep, BoardSetupStep, DeckSetupStep, FinalStep {
        private lateinit var players: List<Player>
        private lateinit var board: Board
        private lateinit var deck: Deck

        override fun withPlayers(vararg ids: Player.Id): BoardSetupStep = apply {
            players = ids.map { Player(it) }
        }

        override fun withNumberOfPlayers(count: Int): BoardSetupStep = apply {
            players = (1..count).map { Player(Player.Id("Player $it")) }
        }

        override fun withBoardSetUp(config: BoardSetUp.Builder.() -> Unit): DeckSetupStep = apply {
            val setup = BoardSetUp.Builder().apply(config).build()
            board = Board(setup)
        }

        override fun withDefaultBoardSetUp(): DeckSetupStep = apply {
            val setup = BoardSetUp.Builder().build()
            board = Board(setup)
        }

        override fun withDeck(config: Deck.Builder.() -> Unit): FinalStep = apply {
            deck = Deck.Builder().apply(config).buildWithFlavors(board.flavors)
        }

        override fun withDefaultDeck(): FinalStep = apply {
            deck = Deck.Builder()
                .withDefaultNumberOfHintFlavorsPerCard()
                .withNumberOfCards(100)
                .buildWithFlavors(board.flavors)
        }

        override fun build(): Game {
            return Game(players, board, deck)
        }
    }
}

interface PlayerStep {
    fun withPlayers(vararg ids: Player.Id): BoardSetupStep
    fun withNumberOfPlayers(count: Int): BoardSetupStep
}

interface BoardSetupStep {
    fun withBoardSetUp(config: BoardSetUp.Builder.() -> Unit): DeckSetupStep
    fun withDefaultBoardSetUp(): DeckSetupStep
}

interface DeckSetupStep {
    fun withDeck(config: Deck.Builder.() -> Unit): FinalStep
    fun withDefaultDeck(): FinalStep
}

interface FinalStep {
    fun build(): Game
}
