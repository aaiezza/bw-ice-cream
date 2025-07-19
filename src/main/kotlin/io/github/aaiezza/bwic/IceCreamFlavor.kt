package io.github.aaiezza.bwic

sealed class IceCreamFlavor(open val name: String) {
    object RED : IceCreamFlavor("Red")
    object BLUE : IceCreamFlavor("Blue")
    object GREEN : IceCreamFlavor("Green")
    object WORMS : IceCreamFlavor("Worms")
    object PIZZA : IceCreamFlavor("Pizza")

    data class Custom(override val name: String) : IceCreamFlavor(name)

    companion object {
        val defaults: List<IceCreamFlavor> = listOf(RED, BLUE, GREEN, WORMS, PIZZA)
    }

    override fun toString(): String = name
}
