package hashi

import hashi.search.*

class Solver(private val pathFinder: ShortestPathFinder) {
    fun solve(board: Board): List<Board> {
        val shortestPath = pathFinder.shortestPath(BoardSearchNode(board))
        return shortestPath.map{it as BoardSearchNode}.map{it.board}
    }
}

class BoardSearchNode(val board: Board): SearchNode {
    override val neighbors: Set<SearchNode>
        get() = board.neighbors().map{BoardSearchNode(it)}.toSet()

    override fun isEnd() = board.isSolved()
}

object BoardReduceStrategy : SearchReduceStrategy {

    private val REDUCE_STRATEGIES: List<ReduceStrategy> = listOf(
            OneNonConnectedNeighbor,
            NeighborsWithSameRemainingBridges,
            TwoBridgesTwoNeighborsStrategy,
            ConnectOneForEachNeigbors,
            TwoBridgesTwoSingleBridgeNeighbors)

    override fun reduce(node: SearchNode): SearchNode {
        return BoardSearchNode((node as BoardSearchNode).run { reduce(board) })
    }

    fun reduce(board: Board): Board {
        val niter = 5
        var newBoard = board
        loop@ for (i in 1..niter) {
            println("iter: $i")
            newBoard = reduceBridges(newBoard)
            if (newBoard.isSolved() || !newBoard.isValid()) break@loop
        }
        if (newBoard != board && newBoard.isValid()) println("reduced new: \n${newBoard.printBoard()}")
        return newBoard
    }

    private fun reduceBridges(board: Board): Board {
        return REDUCE_STRATEGIES.fold(board) { newBoard, rule -> rule.reduceBoard(newBoard)}
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

    val solution = Solver(BFS(BoardReduceStrategy)).solve(board)

    solution.forEach {it -> println(it.printBoard()); println()}
}