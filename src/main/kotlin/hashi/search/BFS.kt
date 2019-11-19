package hashi.search

import org.slf4j.LoggerFactory

object BFS : ShortestPathFinder {

    override val logger = LoggerFactory.getLogger(BFS::class.toString())

    private const val maxIterations = 100

    override fun search(root: SearchNode): SearchResult {

        val visited = mutableSetOf<SearchNode>()
        val queue = mutableListOf<SearchNode>()
        val shortestDistToNode = mutableMapOf<SearchNode, Int>()
        val shortestParentToNode = mutableMapOf<SearchNode, SearchNode>()

        var node = root
        queue.add(root)
        visited.add(root)
        shortestDistToNode[root] = 0
        shortestParentToNode[root] = root // set parent of root to root

        var iter = 0
        while(queue.isNotEmpty() || iter < maxIterations) {
            iter++
            node = queue.removeAt(0)

            if (!node.isEnd()) {
                val distToSearchNode = shortestDistToNode[node] ?: throw Exception("Should not have no match") // shouldnt return no match
                val neighbors = node.neighbors
                neighbors.forEach {
                    if (shortestDistToNode.getOrElse(it){Integer.MAX_VALUE} > distToSearchNode + 1) { // not exist = not reached, set to Int.MAX_VALUE
                        shortestDistToNode[it] = distToSearchNode + 1
                        shortestParentToNode[it] = node
                    }
                }
                val notVisitedNeighbors = neighbors.filterNot { visited.contains(it) }
                queue.addAll(notVisitedNeighbors)
                visited.addAll(notVisitedNeighbors)
            } else {
                break
            }
        }

        return SearchResult(node, shortestParentToNode, iter)
    }

}