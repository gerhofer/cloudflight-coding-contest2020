package team.echo.codingcontest

import kotlin.math.min

fun main(args: Array<String>) {
    lvl4("example")
    (1..5).forEach {
        //lvl4(it.toString())
    }
}

fun lvl1(example: String) {
    val prices = LevelReader.read(1, example)
        .drop(1)
        .map { it -> it.toLong() }

    val minimum = prices.minByOrNull { it }
    val cheapestAt = prices.indexOfFirst { it == minimum }
    LevelReader.write(1, example, listOf(cheapestAt.toString()))
}

fun lvl2(example: String) {
    val lines = LevelReader.read(2, example)
    val nOfPrices = lines[0].toInt()
    val prices = lines.drop(1)
        .take(nOfPrices)
        .map { Price(it.toLong()) }

    val nOfTasks = lines.drop(1 + nOfPrices)[0].toInt()
    val tasks = lines.drop(1 + nOfPrices + 1)
        .take(nOfTasks)
        .map { Task(it.split(" ")[0].toInt(), it.split(" ")[1].toInt()) }

    val taskLengths = tasks.map { it.completionTime }.distinct()
        .sorted()

    var pricingTable = mutableListOf<PriceTableEntry>()
    for (i in 0 until prices.size) {
        val priceRanges = mutableListOf<Long>()
        var sum = 0L
        for (j in i until prices.size) {
            sum += prices[j].price;
            priceRanges.add(sum)
        }
        pricingTable.add(PriceTableEntry(i, priceRanges))
    }

    val lengthToStartingTime = taskLengths.map {taskLength ->
         val start = pricingTable
             .filter { it.cumulativeSum.size >= taskLength }
             .minByOrNull { it.cumulativeSum[taskLength - 1] }!!.start
        taskLength to start
    }.toMap()

    val resultLines = listOf(
        tasks.size.toString(),
        tasks.map { "${it.id} ${lengthToStartingTime[it.completionTime]}" }.joinToString("\n")
    )
    LevelReader.write(2, example, resultLines)
}

fun lvl3(example: String) {
    val lines = LevelReader.read(3, example)
    val nOfPrices = lines[0].toInt()
    val prices = lines.drop(1)
        .take(nOfPrices)
        .map { Price(it.toLong()) }

    val nOfTasks = lines.drop(1 + nOfPrices)[0].toInt()
    val tasks = lines.drop(1 + nOfPrices + 1)
        .take(nOfTasks)
        .map { Task3(it.split(" ")[0].toInt(), it.split(" ")[1].toInt(), it.split(" ")[2].toInt(), it.split(" ")[3].toInt()) }

    val resultLines = tasks.map { task ->
        val subList = prices.subList(task.startInterval, task.endInterval + 1)
        val minimum = subList.minByOrNull { it.price }
        val startTime = subList.indexOfFirst { it == minimum }
        "${task.id} ${task.startInterval + startTime} ${task.power}"
    }

    LevelReader.write(3, example, listOf(resultLines.size.toString()) + resultLines)
}

fun lvl4(example: String) {
    val lines = LevelReader.read(4, example)
    val maxPower = lines[0].toInt()
    val maxElectricityBill = lines[1].toLong()
    val nOfPrices = lines[2].toInt()
    val prices = lines.drop(3)
        .take(nOfPrices)
        .map { Price(it.toLong()) }

    val nOfTasks = lines.drop(3 + nOfPrices)[0].toInt()
    val tasks = lines.drop(3 + nOfPrices + 1)
        .take(nOfTasks)
        .map { Task3(it.split(" ")[0].toInt(), it.split(" ")[1].toInt(), it.split(" ")[2].toInt(), it.split(" ")[3].toInt()) }

    val powerMinutes = prices.mapIndexed { index, price ->
        PowerMinute(index, price.price, maxPower, maxPower)
    }

    var currentCost = 0L

    val resultLines = tasks.map { task ->
        val usages = mutableListOf<PowerUsage>()
        var availableMinutes = powerMinutes.filter { it -> it.idx >= task.startInterval && it.idx < task.endInterval }
            .filter { it.capacity > 0 }
        var requiredPower = task.power
        while (requiredPower > 0 && availableMinutes.isNotEmpty()) {
            val minimum = availableMinutes.minByOrNull { it.price }!!
            val startTime = availableMinutes.indexOfFirst { it == minimum }
            if (requiredPower < minimum.capacity) {
                currentCost += requiredPower*minimum.price
                requiredPower = 0
                minimum.capacity -= requiredPower
                usages.add(PowerUsage(startTime, minimum.capacity))
            } else {
                requiredPower -= minimum.capacity
                currentCost += minimum.capacity*minimum.price
                minimum.capacity = 0
                usages.add(PowerUsage(startTime, minimum.capacity))
            }
            availableMinutes = powerMinutes.filter { it -> it.idx >= task.startInterval && it.idx < task.endInterval }
                .filter { it.capacity > 0 }
        }

        val minimum = availableMinutes.minByOrNull { it.price }
        val startTime = availableMinutes.indexOfFirst { it == minimum }
        val usagesAsString = usages.map { "${it.startInterval} ${it.amount}" }.joinToString(" ")
        "${task.id} ${usagesAsString}"
    }

    println("current cost is : $currentCost")

    LevelReader.write(4, example, listOf(resultLines.size.toString()) + resultLines)
}

data class PowerUsage(
    val startInterval: Int,
    val amount: Int
)

data class PowerMinute(
    val idx: Int,
    val price: Long,
    var capacity: Int,
    val initialCapacity: Int
)

data class Task3(
    val id: Int,
    val power: Int,
    val startInterval: Int,
    val endInterval: Int
)

data class PriceTableEntry(
    val start: Int,
    val cumulativeSum: List<Long>
)