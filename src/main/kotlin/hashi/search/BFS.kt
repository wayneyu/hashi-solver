package hashi.search

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class BFS(private val searchReduceStrategy: SearchReduceStrategy) : ShortestPathFinder {

    override val logger: Logger = LoggerFactory.getLogger(BFS::class.toString())

    private val maxIterations = 10

    override fun search(root: SearchNode): SearchResult {

        val visited = mutableSetOf<SearchNode>()
        val queue = mutableListOf<SearchNode>()
        val shortestDistToNode = mutableMapOf<SearchNode, Int>()
        val shortestParentToNode = mutableMapOf<SearchNode, SearchNode>()

        var node = root
        queue.add(root)
        visited.add(root)
        shortestDistToNode[root] = 0

        var iter = 0
        while(queue.isNotEmpty() && iter < maxIterations) {
            iter++
            val oldNode = node
            node = searchReduceStrategy.reduce(queue.removeAt(0))

            println("it: $iter")
            if (!node.isEnd()) {
                val distToSearchNode = shortestDistToNode[oldNode] ?: throw Exception("Should not have no match") // shouldnt return no match
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
                shortestParentToNode[node] = oldNode
                break
            }
        }

        return SearchResult(node, shortestParentToNode, iter)
    }

}

interface SearchReduceStrategy {
    fun reduce(node: SearchNode): SearchNode
}