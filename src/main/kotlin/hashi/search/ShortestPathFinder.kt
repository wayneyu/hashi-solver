package hashi.search

import org.slf4j.Logger

interface ShortestPathFinder {

    val logger: Logger

    fun search(root: SearchNode): SearchResult

    fun shortestPath(root: SearchNode): List<SearchNode> {
        val searchResult = search(root)
        val shortestPath = shortestPathFromEndToStart(searchResult.endNode, searchResult.shortestParent).reversed()
        logger.info("Search finished in ${searchResult.iterations} iterations, shortest path: ${shortestPath.size - 1} steps")
        return shortestPath
    }

    fun shortestPathFromEndToStart(node: SearchNode, shortestParent: Map<SearchNode, SearchNode>): List<SearchNode> {
        val parent = shortestParent[node] ?: throw Exception("should not have no match")
        return if (parent == node) listOf(node) else listOf(node).plus(shortestPathFromEndToStart(parent, shortestParent))
    }
}