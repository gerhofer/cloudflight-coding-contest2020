package team.echo.codingcontest

import kotlin.math.min

fun main(args: Array<String>) {
    lvl5("example")
    (1..5).forEach {
        lvl5(it.toString())
    }
}

fun lvl5(example: String) {
    val lines = LevelReader.read(5, example)
    val maxPower = lines[0].toInt()
    val maxElectricityBill = lines[1].toLong()
    val maxConcurrentTask = lines[2].toLong()
    val nOfPrices = lines[3].toInt()
    val prices = lines.drop(4)
        .take(nOfPrices)
        .map { Price(it.toLong()) }

    val nOfTasks = lines.drop(4 + nOfPrices)[0].toInt()
    val tasks = lines.drop(4 + nOfPrices + 1)
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

    currentCost = calculate(powerMinutes, currentCost, maxConcurrentTask)

    println("current cost is : $currentCost and limit is $maxElectricityBill")
    if (currentCost > maxElectricityBill) {
        println("TOOOOOOOO HIGH")
    }

    var notDone = powerMinutes.filter { it.openTasks().isNotEmpty() }
    if (notDone.isNotEmpty()) {
        val unfinished = notDone.flatMap { it.openTasks() }.distinct().map { it.id }.joinToString(",")
        println("NOT ALL DONE :( try again ${unfinished}")
    }

    var withTooMany = tasks.flatMap { it.usages }.groupingBy { it.startInterval }.eachCount().filter { it.value > maxConcurrentTask }
    if (withTooMany.isNotEmpty()) {
        println("TOO MUCH concurrrrency in minutes ${withTooMany.keys}")
    }

    val resultLines = tasks.map {
        val usagesAsString = it.usages.map { "${it.startInterval} ${it.amount}" }.joinToString(" ")
        "${it.id} ${usagesAsString}"
    }

    LevelReader.write(5, example, listOf(resultLines.size.toString()) + resultLines)
}


private fun calculate(
    powerMinutes: List<PowerMinute>,
    currentCost: Long,
    maxConcurrency: Long
): Long {
    var currentCost1 = currentCost
    powerMinutes.sortedBy { it.price }
        .forEach { minute ->
            val tasksByPriority = minute.openTasks().take(maxConcurrency.toInt())
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

private fun calculate_2(
    tasks: List<Task3>,
): Long {
    var currentCost = 0L
    tasks.sortedBy { -it.getPriority() }
        .forEach { task ->
            val availableByPrice = task.availableSlots
                .filter { it.capacity > 0 }
                .sortedBy { it.price }

            for (minute in availableByPrice) {
                if (task.getRemainingPower() <= 0) {
                    break
                }

                val deductedPower = min(task.getRemainingPower(), minute.capacity)
                currentCost += deductedPower * minute.price
                minute.capacity -= deductedPower
                task.usages.add(PowerUsage(minute.idx, deductedPower))
                if (minute.capacity <= 0) {
                    minute.applicableTasks.forEach { it.minuteUsed(minute) }
                }
            }

        }

    return currentCost
}

data class PowerUsage(
    val startInterval: Int,
    val amount: Int
)

class PowerMinute(
    val idx: Int,
    val price: Long,
    var capacity: Int,
    val initialCapacity: Int,
    val applicableTasks: MutableList<Task3>
) {

    fun openTasks(): MutableList<Task3> {
        return applicableTasks.filter { it.getRemainingPower() > 0 }
            .sortedBy { -it.getPriority() }
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
        if (endInterval == startInterval || availableSlots.size <= 1) {
            return 1000000
        }
        return getRemainingPower() * 100 / (availableSlots.map { it.capacity }.sum())
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