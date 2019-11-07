package hashi

data class Board(val xSize: Int, val ySize: Int, val nodes: List<Node>, val bridges: List<Bridge> = emptyList()) {
    /**
    / x, y direction
    / 0,0,  0,1  ...
    / 1,0,  1,1  ...
    / ...   ...
     **/

    fun getIslandToWest(node: Node): Node? = nodes
            .filter { it -> it.x == node.x && it.y < node.y }
            .maxBy { it.y }

    fun getIslandToEast(node: Node): Node? = nodes
            .filter { it -> it.x == node.x && it.y > node.y }
            .minBy { it.y }

    fun getIslandToNorth(node: Node): Node? = nodes
            .filter { it -> it.y == node.y && it.x < node.x }
            .maxBy { it.x }

    fun getIslandToSouth(node: Node): Node? = nodes
            .filter { it -> it.y == node.y && it.x > node.x }
            .minBy { it.x }

    fun getNeighborIslands(node: Node): List<Node> =
            listOfNotNull(
                    getIslandToEast(node),
                    getIslandToWest(node),
                    getIslandToNorth(node),
                    getIslandToSouth(node))

    fun copyNode(node: Node, newNode: Node): Board {
        val newNodes = nodes.toMutableList().apply { this[this.indexOf(node)] = newNode }
        return this.copy(nodes = newNodes)
    }

    override fun equals(other: Any?): Boolean {
        return if (other is Board)
            other.xSize == xSize && other.ySize == ySize && other.nodes == nodes &&
            other.bridges.sorted() == bridges.sorted()
        else false
    }

    fun printBridges(): String {
        return bridges.sorted().joinToString("\n") { it -> "(${it.node1.x}, ${it.node1.y}) -> (${it.node2.x}, ${it.node2.y})" }
    }

    fun isSolved(): Boolean {
        return bridges.size == nodes.map{it.bridges}.sum() / 2
    }

    fun connect(node1: Node, node2: Node): Board {
        assert(this.nodes.contains(node1) && this.nodes.contains(node2)) {"Cannot connect. $node1 or $node2 is not part of the board"}
        assert(node1.x == node2.x || node1.y == node2.y) {"Bridge cannot be connected diagonally. node1: $node1, node2: $node2"}
        assert(node1.x != node2.x || node1.y != node2.y) {"Cannot connect node to itself, source is same as destination"}

        return this
                .copyNode(node1, node1.copy(connected = node1.connected + 1))
                .copyNode(node2, node2.copy(connected = node2.connected + 1))
                .copy(bridges = this.bridges + Bridge(node1, node2))
    }

    fun printBoard(): String {
        val board = (1..ySize).map{ (1..xSize).map{"0"}.toMutableList() }.toMutableList()
        nodes.forEach { node -> board[node.x][node.y] = node.bridges.toString() }
        bridges
                .groupBy{it}
                .mapValues{ it.value.size }
                .forEach { (bridge, size) ->
                    val (node1, node2) = listOf(bridge.node1, bridge.node2).sorted()
                    if (bridge.direction() == 1)
                        ((node1.y + 1) until node2.y).forEach {i -> board[node1.x][i] = if (size == 2) "=" else "-"}
                    else
                        ((node1.x + 1) until node2.x).forEach {i -> board[i][node1.y] = if (size == 2) "=" else "-"}
                }

        return board.joinToString("\n"){ it -> it.joinToString("")}
    }

    companion object {
        fun fromString(layout: String): Board {
            val xys = layout.split("\n").map{it.toCharArray().toList().map{c -> c.toString()}}
            val xSize = xys.size
            val ySize = xys[0].size

            val nodes = xys.mapIndexedNotNull { x, row ->
                row.mapIndexedNotNull { y, c ->
                    if (c != "0") c.toIntOrNull()?.let{ bridges -> Node(xSize * x + y, bridges, x, y)}
                    else null
                }
            }.flatten()

            var startx = IntArray(ySize){-1}
            val bridges: List<Bridge> = xys.mapIndexed{ x, row ->
                var starty = -1
//                println("startx: ${startx.joinToString(", ")}")
//                println("x: $x")
                row.mapIndexed { y, c ->
//                    println("y: $y, starty: $starty")
                    if (c == "-" || c == "=") {
                        if (y > 0 && row[y - 1].toIntOrNull()?.let{it > 0} == true) //start of bridge in y direction
                            starty = y - 1
                        if (x > 0 && xys[x - 1][y].toIntOrNull()?.let{it > 0} == true) //start of bridge in x direction
                            startx[y] = x - 1
                        emptyList<Bridge>()
                    } else {
                        (if (y > 0 && starty > -1 && (row[y - 1] == "-" || row[y-1] == "=")) {
                            val node1 = nodes.find { it.x == x && it.y == starty }!!
                            val node2 = nodes.find { it.x == x && it.y == y }!!
                            starty = y
                            listOf(Bridge(node1, node2)) + (if (row[y - 1] == "=") listOf(Bridge(node1, node2)) else emptyList<Bridge>())
                        } else emptyList<Bridge>()) +
                        if (x > 0 && startx[y] > -1 && (xys[x - 1][y] == "-" || xys[x - 1][y] == "=")) {
                            val node1 = nodes.find { it.x == startx[y] && it.y == y }!!
                            val node2 = nodes.find { it.x == x && it.y == y }!!
                            startx[y] = x
                            listOf(Bridge(node1, node2)) + (if (xys[x - 1][y] == "=") listOf(Bridge(node1, node2)) else emptyList<Bridge>())
                        } else emptyList<Bridge>()
                    }
                }.flatten()
            }.flatten()

            //set connected in nodes
            val bridgeNodesCount = bridges.map{listOf(it.node1, it.node2)}.reduce{a, b -> a+b}.groupBy { it }.mapValues { it.value.size }
            val nodesConnected = nodes.map{it.copy(connected = bridgeNodesCount[it] ?: 0)}

            return Board(xSize, ySize, nodesConnected, bridges)
        }
    }
}


data class Node(val id: Int = -1, val bridges: Int, val x: Int, val y: Int, val connected: Int = 0) : Comparable<Node> {

    fun isFull(): Boolean = connected == bridges

    fun remaining(): Int = bridges - connected

    override fun equals(other: Any?): Boolean {
        return if(other is Node) bridges == other.bridges && x == other.x && y == other.y
            else false
    }

    override fun compareTo(other: Node): Int {
        return compareBy<Node>({it.x }, {it.y}).compare(this, other)
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + bridges
        result = 31 * result + x
        result = 31 * result + y
        return result
    }
}

data class Bridge(val node1: Node, val node2: Node) : Comparable<Bridge>{
    override fun equals(other: Any?): Boolean {
        return if (other is Bridge)
            (other.node1 == node1 && other.node2 == node2) ||  (other.node1 == node2 && other.node2 == node1)
        else
            false
    }

    override fun compareTo(other: Bridge): Int {
        return compareBy<Bridge>({it.node1.x }, {it.node1.y }, {it.node1.bridges }, {it.node2.x }, {it.node2.y }, {it.node2.bridges }).compare(this, other)
    }

    fun direction(): Int {
        return when {
            node1.x == node2.x -> 1
            node1.y == node2.y -> 0
            else -> throw IllegalStateException("Bridge cannot be connected diagonally. node1: $node1, node2: $node2")
        }
    }

    override fun hashCode(): Int {
        var result = 1
        if (node1 > node2) {
            result = node1.hashCode()
            result = 31 * result + node2.hashCode()
        } else {
            result = node2.hashCode()
            result = 31 * result + node1.hashCode()
        }
        return result
    }
}