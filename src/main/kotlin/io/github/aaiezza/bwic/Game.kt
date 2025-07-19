package io.github.aaiezza.bwic

class Game(
    val players: List<Player>,
    val boardSetUp: BoardSetUp,
    val deck: Deck
) {
    class Builder {
        private var players: List<Player> = emptyList()
        private var boardSetUp: BoardSetUp = BoardSetUp.Builder().build()
        private var deck: Deck = Deck.Builder().build()

        fun withNumberOfPlayers(count: Int): Builder = apply {
            players = (1..count).map { Player(Player.Id("Player $it")) }
        }

        fun withPlayers(vararg ids: Player.Id): Builder = apply {
            this.players = ids.map { Player(it) }
        }

        fun withDefaultBoardSetUp(): Builder = apply {
            this.boardSetUp = BoardSetUp.Builder().build()
        }

        fun withBoardSetUp(boardSetUp: BoardSetUp): Builder = apply {
            this.boardSetUp = boardSetUp
        }

        val withDefaultDeck: Builder
            get() = withDeck(Deck.Builder().build())

        fun withDefaultDeck(): Builder = withDeck(Deck.Builder().build())

        fun withDeck(deck: Deck): Builder = apply {
            this.deck = deck
        }

        fun build(): Game {
            return Game(players, boardSetUp, deck)
        }
    }
}
