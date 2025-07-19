package io.github.aaiezza.bwic

data class Player(val id: Id) {
    data class Id(val value: String)
}

sealed class PlayerAction {
    data object RevealTopCard : PlayerAction()
    data class GuessFlavor(val flavor: IceCreamFlavor) : PlayerAction()
}


data class PlayerState(
    val player: Player,
    val cone: Cone
)

data class Cone(val scoops: List<Scoop>) {
    fun addScoops(newScoops: List<Scoop>) = Cone(scoops + newScoops)
    fun scoopsOf(flavor: IceCreamFlavor): Int = scoops.count { it.flavor == flavor }
}
