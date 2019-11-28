package hashi

import hashi.search.SearchNode

interface ReduceStrategy {
    fun reduceBoard(board: Board): Board = board.islands.fold(board){ newBoard, node ->
        val newNode = newBoard.findNode(node.x, node.y)
        if (applicable(newNode, newBoard)) {
            println("applicable: $newNode, strategy: ${this.javaClass.simpleName} ")
            val reduced = reduce(newNode, newBoard)
            if (reduced.isValid()) {
                reduced
            } else {
                println(reduced.printBoard())
                newBoard
            }
        } else newBoard
    }
    fun reduce(node: Node, board: Board): Board
    fun applicable(node: Node, board: Board): Boolean
}

object MoreThanThreeBridgesAndTwoNeighbors : ReduceStrategy {
    /**
     * 1-[3]-3
     */
    override fun applicable(node: Node, board: Board): Boolean {
        return node.remaining() >= 3 && board.getNeighborIslands(node).size == 2
    }

    override fun reduce(node: Node, board: Board): Board {
        val neighbors = node.run { board.getNeighborIslands(this) }
        return neighbors.fold(board) { newBoard, neighbor -> newBoard.connect(node, neighbor) }
    }
}

object OneNonConnectedNeighbor : ReduceStrategy {
    /**
     * [1]-2
     */
    override fun applicable(node: Node, board: Board): Boolean {
        val neighbors = board.getNeighborIslands(node)
        return neighbors.size == 1 && !neighbors.first().isFull() &&  neighbors.first().maxUnconnected() >= node.remaining() && node.remaining() <= 2 && node.remaining() > 0
    }

    override fun reduce(node: Node, board: Board): Board {
        val neighbors = node.run { board.getNeighborIslands(this) }
        val neighbor1 = neighbors.first()
        return if (node.remaining() == 2)
            board.connect2(node, neighbor1)
        else
            board.connect(node, neighbor1)
    }
}

object NeighborsWithSameRemainingBridges : ReduceStrategy {
    /**
     * 2-[2]-1
     */
    override fun applicable(node: Node, board: Board): Boolean {
        val neighbors = board.getNeighborIslands(node)
//        println("${board.printBoard()}\n$node, neighbors: $neighbors")
        return node.remaining() > 0 && neighbors.sumBy { it.maxUnconnected() } == node.remaining()
    }

    override fun reduce(node: Node, board: Board): Board {
        val neighbors = board.getNeighborIslands(node)
        return neighbors.fold(board) {newBoard, neighbor ->
            when (neighbor.maxUnconnected()) {
                2 -> newBoard.connect2(node, neighbor)
                1 -> newBoard.connect(node, neighbor)
                else -> newBoard
            }
        }
    }
}