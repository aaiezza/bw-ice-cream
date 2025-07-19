package io.github.aaiezza.bwic

class Board {
}

class BoardSetUp(
    val flavors: List<IceCreamFlavor>,
    val initialPrePopulatedCards: Int,
    val claimedCardsToWin: Int
) {
    class Builder {
        private val flavors = IceCreamFlavor.defaults.toMutableList()
        private var initialPrePopulatedCards: Int = 0
        private var claimedCardsToWin: Int = 0

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
