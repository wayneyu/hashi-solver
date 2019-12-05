package hashi

import hashi.search.SearchNode

interface ReduceStrategy {
    fun reduceBoard(board: Board): Board = board.islands.fold(board){ newBoard, node ->
        val newNode = newBoard.findNode(node.x, node.y)
        if (applicable(newNode, newBoard)) {
            println("applicable: $newNode, strategy: ${this.javaClass.simpleName} ")
            val reduced = reduce(newNode, newBoard)
            if (reduced.isValid()) {
//                if (this.javaClass.simpleName == "TwoBridgesTwoNeighborsStrategy")  println(reduced.printBoard())
                println("reduce applied: \n${reduced.printBoard()}")
                reduced
            } else {
                newBoard
            }

        } else newBoard
    }
    fun reduce(node: Node, board: Board): Board
    fun applicable(node: Node, board: Board): Boolean
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

object TwoBridgesTwoNeighborsStrategy : ReduceStrategy {
    override fun applicable(node: Node, board: Board): Boolean {
        val neighbors = board.getNeighborIslands(node)
        return node.bridges == 2 && node.remaining() == 2 && neighbors.size == 2 && neighbors.any { it.bridges <= 2 }
    }

    override fun reduce(node: Node, board: Board): Board {
        val neighbors = board.getNeighborIslands(node)
        return neighbors.fold(board) {newBoard, neighbor ->
            val theOtherNeighbor = neighbors.find { it != neighbor }!!
            when (theOtherNeighbor.bridges) {
                1, 2 -> newBoard.connect(node, neighbor)
                else -> newBoard
            }
        }
    }

}

object ConnectOneForEachNeigbors : ReduceStrategy {
    override fun applicable(node: Node, board: Board): Boolean {
        val neighbors = board.getNeighborIslands(node)
        return when(neighbors.size) {
            2 -> node.remaining() == 3 || (node.bridges == 3 && node.connected == 1)
            3 -> node.remaining() == 5
            4 -> node.remaining() == 7
            else -> false
        }
    }

    override fun reduce(node: Node, board: Board): Board {
        val neighbors = board.getNeighborIslands(node)
        return neighbors.fold(board) {newBoard, neighbor ->
            if (board.bridges.none { it == Bridge(node, neighbor) }) newBoard.connect(node, neighbor)
            else newBoard
        }
    }
}

object TwoBridgesTwoSingleBridgeNeighbors : ReduceStrategy {
    override fun applicable(node: Node, board: Board): Boolean {
        val neighbors = board.getNeighborIslands(node)
        return node.bridges == 2 && neighbors.size == 3 && neighbors.filter{it.bridges == 1}.size == 2
    }

    override fun reduce(node: Node, board: Board): Board {
        val neighbors = board.getNeighborIslands(node)
        return neighbors.fold(board) {newBoard, neighbor ->
            if (neighbor.bridges > 1) newBoard.connect(node, neighbor)
            else newBoard
        }
    }
}