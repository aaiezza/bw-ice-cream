package io.github.aaiezza.bwic

class Card {
}

class Deck(
    val numberOfCards: Int,
    val hintFlavorsPerCard: Int
) {
    class Builder {
        private var numberOfCards = 10
        private var hintFlavorsPerCard = 3

        fun withNumberOfCards(n: Int): Builder = apply {
            numberOfCards = n
        }

        fun withDefaultNumberOfHintFlavorsPerCard(): Builder = apply {
            hintFlavorsPerCard = 3
        }

        fun build(): Deck {
            return Deck(numberOfCards, hintFlavorsPerCard)
        }
    }
}
