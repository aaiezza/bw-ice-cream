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


class Deck(val cards: List<Card>) {
    val size: Int get() = cards.size
    val glanceAtTopCard: List<IceCreamFlavor>? get() = cards.getOrNull(0)?.hintFlavors

    fun draw(): Pair<Deck, Card> {
        require(cards.isNotEmpty()) { "Cannot draw from an empty deck." }
        val topCard = cards.first()
        val remaining = cards.drop(1)
        return Deck(remaining) to topCard
    }

    fun asList(): List<Card> = cards

    class Builder {
        private var numberOfCards: Int = 60
        private var hintFlavorsPerCard: Int = 3
        private var seed: Long? = null

        fun withNumberOfCards(n: Int): Builder = apply {
            numberOfCards = n
        }

        fun withDefaultNumberOfHintFlavorsPerCard(): Builder = apply {
            hintFlavorsPerCard = 3
        }

        fun withSeed(seed: Long): Builder = apply {
            this.seed = seed
        }

        fun buildWithFlavors(flavors: List<IceCreamFlavor>): Deck {
            val random = seed?.let { kotlin.random.Random(it) } ?: kotlin.random.Random.Default

            val cards = List(numberOfCards) {
                val actualFlavor = flavors.random(random)
                val hintFlavors = (flavors - actualFlavor)
                    .shuffled(random)
                    .take(hintFlavorsPerCard - 1)
                    .plus(actualFlavor)
                    .shuffled(random)
                Card(hintFlavors, Scoop(actualFlavor))
            }
            return Deck(cards.shuffled(random))
        }
    }
}
