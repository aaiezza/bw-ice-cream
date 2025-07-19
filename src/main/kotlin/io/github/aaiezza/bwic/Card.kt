package io.github.aaiezza.bwic

data class Scoop(val flavor: IceCreamFlavor)

data class Card(
    val hintFlavors: List<IceCreamFlavor>,
    val actualScoop: Scoop
)

@JvmInline
value class TurnNumber(val value: Int)

@JvmInline
value class MaxScoopsToWin(val value: Int)


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
