package team.echo.codingcontest

import kotlin.math.min
import kotlin.math.round

fun main(args: Array<String>) {
    lvl7("example")
    (1..5).forEach {
        lvl7(it.toString())
    }
}
/*
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
                mutableSetOf(),
                it.split(" ")[1].toInt()
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

    println("before tasks each")

    currentCost = calculate(powerMinutes, currentCost, maxConcurrentTask, maxPower)

    println("current cost is : $currentCost and limit is $maxElectricityBill")
    if (currentCost > maxElectricityBill) {
        println("TOOOOOOOO HIGH")
    }

    var notDone = powerMinutes.filter { it.openTasks().isNotEmpty() }
    if (notDone.isNotEmpty()) {
        val unfinished = notDone.flatMap { it.openTasks() }.distinct().map { it.id }.joinToString(",")
        println("NOT ALL DONE :( try again ${unfinished}")
    }

    var withTooMany =
        tasks.flatMap { it.usages }.groupingBy { it.startInterval }.eachCount().filter { it.value > maxConcurrentTask }
    if (withTooMany.isNotEmpty()) {
        println("TOO MUCH concurrrrency in minutes ${withTooMany.keys}")
    }

    val resultLines = tasks.map {
        val usagesAsString = it.usages.map { "${it.startInterval} ${it.amount}" }.joinToString(" ")
        "${it.id} ${usagesAsString}"
    }

    LevelReader.write(5, example, listOf(resultLines.size.toString()) + resultLines)
}

fun lvl6(example: String) {
    val lines = LevelReader.read(6, example)
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
                mutableSetOf(),
                it.split(" ")[1].toInt()
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

    println("before tasks each")

    currentCost = calculate(powerMinutes, currentCost, maxConcurrentTask, maxPower)

    println("current cost is : $currentCost and limit is $maxElectricityBill")
    if (currentCost > maxElectricityBill) {
        println("TOOOOOOOO HIGH")
    }

    var notDone = powerMinutes.filter { it.openTasks().isNotEmpty() }
    if (notDone.isNotEmpty()) {
        val unfinished = notDone.flatMap { it.openTasks() }.distinct().map { it.id }.joinToString(",")
        println("NOT ALL DONE :( try again ${unfinished}")
    }

    var withTooMany =
        tasks.flatMap { it.usages }.groupingBy { it.startInterval }.eachCount().filter { it.value > maxConcurrentTask }
    if (withTooMany.isNotEmpty()) {
        println("TOO MUCH concurrrrency in minutes ${withTooMany.keys}")
    }

    val resultLines = tasks.map {
        val usagesAsString = it.usages.map { "${it.startInterval} ${it.amount}" }.joinToString(" ")
        "${it.id} ${usagesAsString}"
    }

    LevelReader.write(6, example, listOf(resultLines.size.toString()) + resultLines)
}
*/
fun lvl7(example: String) {
    val lines = LevelReader.read(7, example)
    val maxPower = lines[0].toInt()
    val maxElectricityBill = lines[1].toLong()
    val maxConcurrentTask = lines[2].toLong()
    val nOfPrices = lines[3].toInt()
    val prices = lines.drop(4)
        .take(nOfPrices)
        .map { Price(it.toLong()) }
    val powerMinutes = prices.mapIndexed { index, price ->
        PowerMinute(index, price.price, maxPower, maxPower, mutableListOf())
    }

    val nOfHouseholds = lines.drop(4 + nOfPrices)[0].toInt()
    val households = mutableListOf<Household>()
    var offset = 5
    for (i in 1..nOfHouseholds) {
        val nOfTasks = lines.drop(offset + nOfPrices)[0].toInt()
        val tasks = lines.drop(offset + nOfPrices + 1)
            .take(nOfTasks)
            .map {
                Task3(
                    it.split(" ")[0].toInt(),
                    it.split(" ")[1].toInt(),
                    it.split(" ")[2].toInt(),
                    it.split(" ")[3].toInt(),
                    mutableListOf(),
                    mutableSetOf(),
                    it.split(" ")[1].toInt(),
                    i
                )
            }

        tasks.forEach { task ->
            (task.startInterval..task.endInterval).forEach { minuteIdx ->
                task.addMinute(powerMinutes[minuteIdx])
                powerMinutes[minuteIdx].applicableTasks += task
            }
        }
        offset += nOfTasks + 1
        households.add(Household(i, tasks))
    }

    val tasks = households.flatMap { it.tasks }

    var currentCost = 0L

    currentCost = calculate(powerMinutes, currentCost, maxConcurrentTask, maxPower)

    println("current cost is : $currentCost and limit is $maxElectricityBill")
    if (currentCost > maxElectricityBill) {
        println("TOOOOOOOO HIGH")
    }

    var notDone = powerMinutes.filter { it.openTasks().isNotEmpty() }
    if (notDone.isNotEmpty()) {
        val unfinished = notDone.flatMap { it.openTasks() }.distinct().map { it.id }.joinToString(",")
        println("NOT ALL DONE :( try again ${unfinished}")
    }

    var withTooMany =
        tasks.flatMap { it.usages }.groupingBy { it.startInterval }.eachCount().filter { it.value > maxConcurrentTask }
    if (withTooMany.isNotEmpty()) {
        println("TOO MUCH concurrrrency in minutes ${withTooMany.keys}")
    }

    val resultLines = households.map {
        val tasksPerHousehold = it.tasks.map {
            "${it.id} " + it.usages.map { "${it.startInterval} ${it.amount}" }.joinToString(" ")
        }.joinToString("\n")
        "${it.id}\n${it.tasks.size}\n${tasksPerHousehold}"
    }

    LevelReader.write(7, example, listOf(resultLines.size.toString()) + resultLines)
}

private fun calculate(
    powerMinutes: List<PowerMinute>,
    currentCost: Long,
    maxConcurrency: Long,
    maxPower: Int
): Long {
    var count = 0
    var currentCost1 = currentCost
    powerMinutes.sortedBy { it.price }
        .forEach { minute ->
            //println("calculating number ${count++}")
            val tasksByPriority = minute.openTasks().take(maxConcurrency.toInt())
            for (task in tasksByPriority) {
                if (minute.capacity <= 0) {
                    break
                }
                val deductedPower = min(task.remainingPower, minute.capacity)
                currentCost1 += (1 + round(deductedPower / maxPower.toDouble()).toInt()) * minute.price
                minute.capacity -= deductedPower
                task.usages.add(PowerUsage(minute.idx, deductedPower))
                task.remainingPower -= deductedPower
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
                if (task.remainingPower <= 0) {
                    break
                }

                val deductedPower = min(task.remainingPower, minute.capacity)
                currentCost += deductedPower * minute.price
                minute.capacity -= deductedPower
                task.usages.add(PowerUsage(minute.idx, deductedPower))
                task.remainingPower -= deductedPower
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
        return applicableTasks.filter { it.remainingPower > 0 }
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
    val availableSlots: MutableSet<PowerMinute>,
    var remainingPower: Int,
    val householdId: Int
) {

    fun getPriority(): Int {
        if (endInterval == startInterval || availableSlots.size <= 1) {
            return 1000000
        }
        return remainingPower * 100 / (availableSlots.map { it.capacity }.sum())
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

data class Household(
    val id: Int,
    val tasks: List<Task3>
)