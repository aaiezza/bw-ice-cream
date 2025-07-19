package io.github.aaiezza.bwic

data class Board(
    val flavors: List<IceCreamFlavor>,
    val requiredScoopsToWin: Int,
    val initialPrePopulatedCards: Int,
    val availableScoops: Map<IceCreamFlavor, List<Card>>
) {
    constructor(setup: BoardSetUp) : this(
        flavors = setup.flavors,
        requiredScoopsToWin = setup.claimedCardsToWin,
        initialPrePopulatedCards = setup.initialPrePopulatedCards,
        availableScoops = emptyMap()
    )
}


class BoardSetUp(
    val flavors: List<IceCreamFlavor>,
    val initialPrePopulatedCards: Int,
    val claimedCardsToWin: Int
) {
    class Builder {
        private val flavors = IceCreamFlavor.defaults.toMutableList()
        private var initialPrePopulatedCards: Int = 5
        private var claimedCardsToWin: Int = 9

        fun withFlavors(vararg flavors: IceCreamFlavor): Builder = apply {
            this.flavors.clear()
            this.flavors.addAll(flavors)
        }

        fun withInitialNumberOfPrePopulatedCards(n: Int): Builder = apply {
            this.initialPrePopulatedCards = n
        }

        fun withNumberOfClaimedCardsToWin(n: Int): Builder = apply {
            this.claimedCardsToWin = n
        }

        fun build(): BoardSetUp {
            return BoardSetUp(flavors.toList(), initialPrePopulatedCards, claimedCardsToWin)
        }
    }
}
