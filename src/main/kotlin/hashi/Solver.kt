package hashi

import hashi.search.BFS
import hashi.search.SearchNode
import hashi.search.ShortestPathFinder

class Solver(private val pathFinder: ShortestPathFinder) {

    private val REDUCE_STRATEGIES: List<ReduceStrategy> = listOf(OneNonConnectedNeighbor, MoreThanThreeBridgesAndTwoNeighbors, NeighborsWithSameRemainingBridges)

    fun reduce(board: Board): Board {
        val niter = 10
        var newBoard = board
        loop@ for (i in 1..niter) {
            println("iter: $i")
            newBoard = reduceBridges(newBoard)
            if (newBoard.isSolved()) break@loop
        }
        return newBoard
    }

    private fun reduceBridges(board: Board): Board {
        return REDUCE_STRATEGIES.fold(board) { newBoard, rule -> rule.reduceBoard(newBoard)}
    }

    fun solve(board: Board): List<Board> {
        val shortestPath = pathFinder.shortestPath(board)
        return shortestPath.map{ it as Board }
    }

    fun isSolved(board: Board): Boolean = board.isEnd()
}

fun main(args:Array<String>) {
    val board = Board.fromString("""
        2000000
        0000000
        6000000
        0000000
        3000000
        0000000
        4000000
        0000000
        0000000
        0000000
    """.trimIndent())

    val solution = Solver(BFS).solve(board)
    solution.forEach {it -> println(it.printBoard()); println()}
}