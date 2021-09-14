package chess

import java.util.*
import kotlin.math.abs

private val chessBoard = arrayOf(
    Array(8) { " " },
    Array(8) { "W" },
    Array(8) { " " },
    Array(8) { " " },
    Array(8) { " " },
    Array(8) { " " },
    Array(8) { "B" },
    Array(8) { " " }
)

fun main() {

    println("Pawns-Only Chess")

    val whitePlayer = getPlayerName("First")
    val blackPlayer = getPlayerName("Second")

    printChessboard()

    runMoves(whitePlayer, blackPlayer)
}

fun getPlayerName(s: String): String {
    print("$s Player's name:\n> ")
    return readLine()!!
}

fun printChessboard() {
    printBorderLine()

    for (i in 8 downTo 1) {
        printSquares(i, chessBoard[i - 1])
        printBorderLine()
    }

    printAH()
}

fun printBorderLine() {
    print("  +")
    repeat(8) {
        print("---+")
    }
    println()
}

fun printSquares(i: Int, array: Array<String>) {
    print(i)
    repeat(8) {
        print(" | ${array[it]}")
    }
    println(" |")
}

fun printAH() {
    print(" ")
    val row = 'a'
    repeat(8) {
        print("   ${row + it}")
    }
    println()
}

fun runMoves(whitePlayer: String,
             blackPlayer: String
) {
    val coordinatesOfMove = "[a-h][1-8][a-h][1-8]".toRegex()
    val alphabet = listOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h')
    var pawnMove: Array<Int>
    var previousMove = arrayOf(0, 0, 0, 0)

    var whiteTurn = true
    while (true) {
        if (whiteTurn) {
            print("$whitePlayer's turn:\n> ")
        } else {
            print("$blackPlayer's turn:\n> ")
        }
        val inputMove = readLine()!!.lowercase(Locale.getDefault())

        when {
            inputMove == "exit" -> break
            !inputMove.matches(coordinatesOfMove) -> {
                println("Invalid Input")
                continue
            }
        }

        pawnMove = arrayOf(alphabet.indexOf(inputMove[0]),
            inputMove[1].digitToInt() - 1,
            alphabet.indexOf(inputMove[2]),
            inputMove[3].digitToInt() - 1
        )

        when {
            !isPawnOnSquare(pawnMove[0], pawnMove[1], whiteTurn) -> {
                val color = if (whiteTurn) "white" else "black"
                println("No $color pawn at ${inputMove[0]}${inputMove[1]}")
                continue
            }
            canMove(pawnMove, whiteTurn) ||
                    canBeatPawn(pawnMove, whiteTurn) -> {
                // do change after when
            }
            checkEnPassant(pawnMove, whiteTurn, previousMove) -> {
                chessBoard[previousMove[3]][previousMove[2]] = " "
            }
            else -> {
                println("Invalid Input")
                continue
            }
        }
        chessBoard[pawnMove[1]][pawnMove[0]] = " "
        chessBoard[pawnMove[3]][pawnMove[2]] = if (whiteTurn) "W" else "B"
        printChessboard()

        when {
            isWin(whiteTurn) || isCapturing(whiteTurn) -> {
                println("${if (whiteTurn) "White" else "Black"} Wins!")
                break
            }
            isStalemate(whiteTurn) -> {
                println("Stalemate!")
                break
            }
        }
        whiteTurn = !whiteTurn
        previousMove = pawnMove.clone()
    }
    println("Bye!")
}

fun isPawnOnSquare(col: Int, raw: Int, whiteTurn: Boolean): Boolean {
    return if (whiteTurn) chessBoard[raw][col] == "W" else chessBoard[raw][col] == "B"
}

fun canMove(pawnMove: Array<Int>, whiteTurn: Boolean): Boolean {
    if (chessBoard[pawnMove[3]][pawnMove[2]] != " ") {
        return false
    }
    if (pawnMove[0] != pawnMove[2]) {
        return false
    }
    if (whiteTurn) {
        return if (pawnMove[1] == 1 && pawnMove[3] == 3 &&
                    chessBoard[2][pawnMove[2]] == " ") {
            true
        } else pawnMove[1] + 1 == pawnMove[3]
    } else {
        return if (pawnMove[1] == 6 && pawnMove[3] == 4 &&
                    chessBoard[5][pawnMove[2]] == " ") {
            true
        } else pawnMove[1] - 1 == pawnMove[3]
    }
}

fun canBeatPawn(pawnMove: Array<Int>, whiteTurn: Boolean): Boolean {
    if (whiteTurn) {
        return chessBoard[pawnMove[3]][pawnMove[2]] == "B" &&
                pawnMove[1] + 1 == pawnMove[3] &&
                abs(pawnMove[2] - pawnMove[0]) == 1
    } else {
        return chessBoard[pawnMove[3]][pawnMove[2]] == "W" &&
                pawnMove[1] - 1 == pawnMove[3] &&
                abs(pawnMove[2] - pawnMove[0]) == 1
    }
}

fun checkEnPassant(pawnMove: Array<Int>, whiteTurn: Boolean, previousMove: Array<Int>): Boolean {
    if (whiteTurn) {
        return previousMove[1] == 6 && previousMove[3] == 4 &&
            pawnMove[1] == 4 && pawnMove[3] == 5 &&
            pawnMove[2] == previousMove[2] &&
            abs(pawnMove[2] - pawnMove[0]) == 1
    } else {
        return previousMove[1] == 1 && previousMove[3] == 3 &&
                pawnMove[1] == 3 && pawnMove[3] == 2 &&
                pawnMove[2] == previousMove[2] &&
                abs(pawnMove[2] - pawnMove[0]) == 1
    }
}

fun isWin(whiteTurn: Boolean): Boolean {
    val rawNum = if (whiteTurn) 7 else 0
    return chessBoard[rawNum].contains(if (whiteTurn) "W" else "B")
}

fun isCapturing(whiteTurn: Boolean): Boolean {
    val nextTurn = if (whiteTurn) "B" else "W"
    for (raw in chessBoard) {
        if (raw.contains(nextTurn)) {
            return false
        }
    }
    return true
}

fun isStalemate(whiteTurn: Boolean): Boolean {
    for (raw in chessBoard.indices) {
        for (col in chessBoard[raw].indices) {
            if (whiteTurn && chessBoard[raw][col] == "B") {
                when {
                    raw == 0 -> return true
                    chessBoard[raw - 1][col] == " " -> return false
                    canBeatPawn(arrayOf(col, raw, (col + 7) % 8, raw - 1), !whiteTurn) ->
                        return false
                    canBeatPawn(arrayOf(col, raw, (col + 1) % 8, raw - 1), !whiteTurn) ->
                        return false
                }
            } else if (!whiteTurn && chessBoard[raw][col] == "W") {
                when {
                    raw == 7 -> return true
                    chessBoard[raw + 1][col] == " " -> return false
                    canBeatPawn(arrayOf(col, raw, (col + 7) % 8, raw + 1), !whiteTurn) ->
                        return false
                    canBeatPawn(arrayOf(col, raw, (col + 1) % 8, raw + 1), !whiteTurn) ->
                        return false
                }
            }
        }
    }
    return true
}