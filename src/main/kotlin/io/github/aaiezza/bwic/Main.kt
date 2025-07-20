package io.github.aaiezza.bwic

import kotlin.random.Random

fun main() {
    println("\n\u001B[35m==============================\u001B[0m")
    println("\uD83C\uDF66\u001B[1m Welcome to The Best Worst Ice Cream! \u001B[0m\uD83C\uDF66")
    println("\u001B[35m==============================\u001B[0m\n")
    val numBots = promptBotCount()
    val players = buildPlayers(numBots)
    val game = Game(players)
    game.play()
}

fun promptBotCount(): Int {
    while (true) {
        println("How many bots would you like to play with? (1-4) 🤖")
        val input = readln().toIntOrNull()
        if (input in 1..4) return input!!
        println("\u001B[31mInvalid input. Please enter a number between 1 and 4.❌\u001B[0m")
    }
}

fun buildPlayers(numBots: Int): List<Player> {
    val players = mutableListOf<Player>(HumanPlayer("👨‍🍳 You"))
    repeat(numBots) { i ->
        players.add(BotPlayer("🤖 Bot ${i + 1}"))
    }
    return players
}

enum class Flavor(val emoji: String) {
    CHUNKY_TUNA("🦟"),
    SNOT_SWIRL("🧮"),
    DIRTY_SOCKS("🧾"),
    PICKLE_PIE("🌶"),
    MUSTARD_MASH("🧂")
}

data class Card(val options: List<Flavor>, val actual: Flavor) {
    init {
        require(options.contains(actual))
    }

    override fun toString(): String {
        return "${actual.emoji} ${actual.name} (from ${options.joinToString { it.emoji }})"
    }
}

class Deck(private val cards: MutableList<Card>) {
    companion object {
        fun create(shuffled: Boolean = true, seed: Long? = null): Deck {
            val flavors = Flavor.entries
            val rng = if (seed != null) Random(seed) else Random.Default
            val cards = mutableListOf<Card>()
            repeat(60) {
                val options = flavors.shuffled(rng).take(3)
                val actual = options.random(rng)
                cards.add(Card(options, actual))
            }
            if (shuffled) cards.shuffle(rng)
            return Deck(cards)
        }
    }

    fun draw(): Card? = if (cards.isEmpty()) null else cards.removeAt(0)
    fun isEmpty() = cards.isEmpty()
}

class FigureSlot(val flavor: Flavor) {
    val pile: MutableList<Card> = mutableListOf()
    override fun toString(): String = "${flavor.emoji} ${flavor.name}: ${pile.size} card(s)"
}

interface Player {
    val name: String
    val cone: MutableList<Card>
    fun takeTurn(deck: Deck, slots: List<FigureSlot>): Int
}

class HumanPlayer(override val name: String) : Player {
    override val cone = mutableListOf<Card>()

    override fun takeTurn(deck: Deck, slots: List<FigureSlot>): Int {
        val card = deck.draw() ?: return -1
        println("\n[36m$name's Turn! [0m🏃")
        println("Card options: ${card.options.map { it.emoji }}")
        slots.forEachIndexed { i, slot -> println("[$i] ${slot.flavor.emoji} ${slot.flavor.name} -> ${slot.pile.size} card(s)") }
        while (true) {
            print("💬 Guess which flavor the card is (choose index): ")
            val guess = readln().toIntOrNull()
            if (guess in 0 until slots.size) {
                val chosenSlot = slots[guess!!]
                val matched = card.actual == chosenSlot.flavor
                println("🔍 Actual flavor: ${card.actual.emoji} ${card.actual.name}")
                if (matched) {
                    println("\u001B[32m🎉 Correct! You collect ${chosenSlot.pile.size + 1} card(s)! [0m")
                    cone.addAll(chosenSlot.pile)
                    cone.add(card)
                    chosenSlot.pile.clear()
                } else {
                    println("\u001B[31m❌ Wrong! Card added to actual flavor pile: ${card.actual.emoji} [0m")
                    val actualSlot = slots.first { it.flavor == card.actual }
                    actualSlot.pile.add(card)
                }
                return guess
            }
            println("\u001B[31mInvalid index.❌\u001B[0m")
        }
    }
}

class BotPlayer(override val name: String) : Player {
    override val cone = mutableListOf<Card>()

    override fun takeTurn(deck: Deck, slots: List<FigureSlot>): Int {
        val card = deck.draw() ?: return -1
        val guess = card.options.random()
        val chosenSlot = slots.first { it.flavor == guess }
        val matched = card.actual == chosenSlot.flavor
        println("\n[33m$name's Turn! [0m🤖 Guessed: ${guess.emoji} | Actual: ${card.actual.emoji}")
        if (matched) {
            println("\u001B[32m👏 $name guessed correctly and collects ${chosenSlot.pile.size + 1} card(s)!\u001B[0m")
            cone.addAll(chosenSlot.pile)
            cone.add(card)
            chosenSlot.pile.clear()
        } else {
            println("\u001B[31m❌ $name guessed wrong. Card added to actual flavor pile: ${card.actual.emoji}\u001B[0m")
            val actualSlot = slots.first { it.flavor == card.actual }
            actualSlot.pile.add(card)
        }
        return slots.indexOf(chosenSlot)
    }
}

class Game(private val players: List<Player>) {
    private val deck = Deck.create()
    private val slots = Flavor.entries.map { FigureSlot(it) }

    fun play() {
        repeat(5) {
            deck.draw()?.let { card ->
                val slot = slots.first { it.flavor == card.actual }
                slot.pile.add(card)
            }
        }

        var turnIndex = 0
        while (players.none { it.cone.size >= 9 } && !deck.isEmpty()) {
            val player = players[turnIndex % players.size]
            player.takeTurn(deck, slots)
            printCurrentBoard()
            turnIndex++
        }

        printFinalBoard()
        declareWinner()
    }

    private fun printCurrentBoard() {
        println("\n\u001B[34m╔═══════════════════ BOARD ═══════════════════╗\u001B[0m")
        slots.forEach { println("  ${it}") }
        println("\u001B[34m╚═════════════════════════════════════════════╝\u001B[0m")
        println("\n[1mScoreboard:[0m")
        players.forEach {
            println("  ${it.name.padEnd(12)} 🍦 ${it.cone.size} scoops | ${it.cone.joinToString(" ") { c -> c.actual.emoji }}")
        }
        println("\n-----------------------------------------------")
    }

    private fun printFinalBoard() {
        println("\n\u001B[1;35m═ FINAL BOARD ════════════════════════════════\u001B[0m")
        slots.forEach { println("  ${it}") }
        println("\n[1mFinal Scores:[0m")
        players.forEach {
            println("  ${it.name.padEnd(12)} 🍦 ${it.cone.size} scoops | ${it.cone.joinToString(" ") { c -> c.actual.emoji }}")
        }
    }

    private fun declareWinner() {
        val winner = players.maxByOrNull { it.cone.size }!!
        println("\n[1;32m🏆 ${winner.name} wins with ${winner.cone.size} scoops! 🎉[0m")
    }
}
