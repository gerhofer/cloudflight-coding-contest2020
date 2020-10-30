package team.echo.codingcontest

import java.io.File

object LevelReader {

    fun read(level: Int, example: String): List<String> {
        return File("lvl${level}/level${level}_${example}.in")
            .readLines()
    }

    fun write(level: Int, example: String, content: List<String>) {
        File("lvl${level}/level${level}_${example}.out").writeText(content.joinToString("\n"));
    }

}

data class Price(val price: Long)

data class Task(val id: Int, val completionTime: Int)