package hashi

interface ReduceStrategy {
    fun reduceBoard(board: Board): Board = board.nodes.fold(board){newBoard, node ->
        if (applicable(node, board)) reduce(node, newBoard) else newBoard}
    fun reduce(node: Node, board: Board): Board
    fun applicable(node: Node, board: Board): Boolean
}

object MoreThanThreeBridgesAndTwoNeighbors : ReduceStrategy {
    /**
     * 1-[3]-3
     */
    override fun applicable(node: Node, board: Board): Boolean {
        return (node.bridges - node.connected) >= 3 && board.getNeighborIslands(node).size == 2
    }

    override fun reduce(node: Node, board: Board): Board {
        val neighbors = node.run { board.getNeighborIslands(this) }
        val neighbor1 = neighbors[0]
        val neighbor2 = neighbors[1]
        return board
                .replaceNode(node, node.copy(connected = node.connected + 2))
                .replaceNode(neighbor1, neighbor1.copy(connected = neighbor1.connected + 1))
                .replaceNode(neighbor2, neighbor2.copy(connected = neighbor2.connected + 1))
                .copy(bridges = board.bridges + Bridge(node, neighbor2) + Bridge(node, neighbor1))
    }
}

object OneNonConnectedNeighbor : ReduceStrategy {
    /**
     * [1]-2
     */
    override fun applicable(node: Node, board: Board): Boolean {
        val neighbors = board.getNeighborIslands(node)
        val neighbor1 = neighbors.first()
        return neighbors.size == 1 && !neighbor1.isFull() && neighbor1.maxUnconnected() >= node.remaining() && node.remaining() <= 2
    }

    override fun reduce(node: Node, board: Board): Board {
        val neighbors = node.run { board.getNeighborIslands(this) }
        val neighbor1 = neighbors.first()
        return if (node.remaining() == 2)
            board.connect(node, neighbor1).connect(node, neighbor1)
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
        return neighbors.sumBy { it.maxUnconnected() } == node.remaining()
    }

    override fun reduce(node: Node, board: Board): Board {
        val neighbors = node.run { board.getNeighborIslands(this) }
        return neighbors.fold(board) {newBoard, neighbor ->
            when (neighbor.maxUnconnected()) {
                2 -> newBoard.connect2(node, neighbor)
                1 -> newBoard.connect(node, neighbor)
                else -> newBoard
            }
        }
    }
}