package hashi

object Solver {

    private val REDUCE_STRATEGIES: List<ReduceStrategy> = listOf(OneNonConnectedNeighbor, MoreThanThreeBridgesAndTwoNeighbors)

    fun solve(board: Board): Board {
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
}

fun main(args:Array<String>) {
    val node1 = Node(1, 1, 0, 0)
    val node2 = Node(2, 2, 4, 4)
    val node3 = Node(3, 3, 0, 4)
    val board = Board(5,5, listOf(node1, node2, node3))

    val solved = Solver.solve(board)
    println(solved.nodes.joinToString("\n"))
    println(solved.printBridges())
    println("isSolved: ${solved.isSolved()}")
}