package team.echo.codingcontest

import kotlin.math.min

fun main(args: Array<String>) {
    lvl4("example")
    (1..5).forEach {
        lvl4(it.toString())
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

    val lengthToStartingTime = taskLengths.map { taskLength ->
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
        .map {
            Task3(
                it.split(" ")[0].toInt(),
                it.split(" ")[1].toInt(),
                it.split(" ")[2].toInt(),
                it.split(" ")[3].toInt(),
                mutableListOf(),
                mutableSetOf()
            )
        }

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
        .map {
            Task3(
                it.split(" ")[0].toInt(),
                it.split(" ")[1].toInt(),
                it.split(" ")[2].toInt(),
                it.split(" ")[3].toInt(),
                mutableListOf(),
                mutableSetOf()
            )
        }

    val powerMinutes = prices.mapIndexed { index, price ->
        PowerMinute(index, price.price, maxPower, maxPower, mutableListOf())
    }

    tasks.forEach { task ->
        (task.startInterval..task.endInterval).forEach { minuteIdx ->
            task.addMinute(powerMinutes[minuteIdx])
            powerMinutes[minuteIdx].applicableTasks += task
        }
    }

    var currentCost = 0L

    currentCost = calculate(powerMinutes, currentCost)


    println("current cost is : $currentCost and limit is $maxElectricityBill")
    if (currentCost > maxElectricityBill) {
        println("TOOOOOOOO HIGH")
    }

    if (powerMinutes.any { it.openTasks().isNotEmpty() }) {
        println("NOT ALL DONE :( try again")
        var minutesInvolved = powerMinutes.filter { it.openTasks().isNotEmpty() }.flatMap { it.openTasks() }
            .map { (it.endInterval - it.startInterval) }

        powerMinutes.filter { it.idx in minutesInvolved }
            .forEach { minute ->
                minute.applicableTasks.forEach { task ->
                    task.removeUsage(minute)
                }
                minute.capacity = minute.initialCapacity
                currentCost -= minute.capacity*minute.price
            }

        currentCost = calculate(powerMinutes, currentCost)
    }

    if (powerMinutes.any { it.openTasks().isNotEmpty() }) {
        println("STILL NOT ALL DONE :( try again")
    }

    val resultLines = tasks.map {
        val usagesAsString = it.usages.map { "${it.startInterval} ${it.amount}" }.joinToString(" ")
        "${it.id} ${usagesAsString}"
    }

    LevelReader.write(4, example, listOf(resultLines.size.toString()) + resultLines)
}

private fun calculate(
    powerMinutes: List<PowerMinute>,
    currentCost: Long
): Long {
    var currentCost1 = currentCost
    powerMinutes.sortedBy { it.price }
        .forEach { minute ->
            val tasksByPriority = minute.openTasks()
            for (task in tasksByPriority) {
                if (minute.capacity <= 0) {
                    break
                }

                val deductedPower = min(task.getRemainingPower(), minute.capacity)
                currentCost1 += deductedPower * minute.price
                minute.capacity -= deductedPower
                task.usages.add(PowerUsage(minute.idx, deductedPower))
                if (minute.capacity <= 0) {
                    minute.applicableTasks.forEach { it.minuteUsed(minute) }
                }
            }
        }
    return currentCost1
}

data class PowerUsage(
    val startInterval: Int,
    val amount: Int
)

data class PowerMinute(
    val idx: Int,
    val price: Long,
    var capacity: Int,
    val initialCapacity: Int,
    val applicableTasks: MutableList<Task3>
) {

    fun openTasks(): MutableList<Task3> {
        return applicableTasks.filter { it.getRemainingPower() > 0 }
            .sortedBy { it.getPriority() }
            .toMutableList()
    }

}

class Task3(
    val id: Int,
    val power: Int,
    val startInterval: Int,
    val endInterval: Int,
    val usages: MutableList<PowerUsage>,
    val availableSlots: MutableSet<PowerMinute>
) {

    fun getPriority(): Int {
        if (endInterval == startInterval) {
            return 1000000
        }
        return getRemainingPower() * 100 / (availableSlots.size)
    }

    fun getRemainingPower(): Int {
        return power - usages.map { it.amount }.sum()
    }

    fun minuteUsed(minute: PowerMinute) {
        availableSlots.remove(minute)
    }

    fun addMinute(minute: PowerMinute) {
        availableSlots.add(minute)
    }

    fun removeUsage(minute: PowerMinute) {
        if (usages.any { it.startInterval == minute.idx }) {
            usages.removeIf { it.startInterval == minute.idx }
        }
    }
}

data class PriceTableEntry(
    val start: Int,
    val cumulativeSum: List<Long>
)