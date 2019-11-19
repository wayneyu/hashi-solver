package hashi

import hashi.search.BFS
import hashi.search.SearchNode
import hashi.search.ShortestPathFinder

class Solver(private val pathFinder: ShortestPathFinder, private val reduceStrategy: BoardReduceStrategy) {
    fun solve(board: Board): List<Board> {
        val shortestPath = pathFinder.shortestPath(BoardNode(board, reduceStrategy))
        return shortestPath.map{it as BoardNode}.map{it.board}
    }
}

object SolverReduceStrategy : BoardReduceStrategy {

    private val REDUCE_STRATEGIES: List<ReduceStrategy> = listOf(OneNonConnectedNeighbor, MoreThanThreeBridgesAndTwoNeighbors, NeighborsWithSameRemainingBridges)

    override fun reduce(board: Board): Board {
        val niter = 5
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
}

data class BoardNode(val board: Board, val reduceStrategy: BoardReduceStrategy): SearchNode {

    override val neighbors: Set<SearchNode>
        get() = reduceStrategy.reduce(board).islands.flatMap{ island -> board.getNeighborIslands(island).map{ neighbor -> board.connect(island, neighbor)}}
                .map{BoardNode(it, reduceStrategy)}.toSet()

    override fun isEnd(): Boolean {
        return board.islands.all { it.isFull() }
    }

}

fun main(args:Array<String>) {
    val board = Board.fromString("""
        2020020
        0000001
        6050300
        0100003
        3010010
        0300805
        4020000
        0200502
        2001000
        0020503
    """.trimIndent())

    val solution = Solver(BFS, SolverReduceStrategy).solve(board)
    solution.forEach {it -> println(it.printBoard()); println()}
}