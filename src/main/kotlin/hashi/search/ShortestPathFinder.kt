package hashi.search

import hashi.BoardSearchNode
import org.slf4j.Logger

interface ShortestPathFinder {

    val logger: Logger

    fun search(root: SearchNode): SearchResult

    fun shortestPath(root: SearchNode): List<SearchNode> {
        val searchResult = search(root)
        val shortestPath = shortestPathFromEndToStart(searchResult.endNode, searchResult.shortestParent).reversed()
        logger.info("Search finished in ${searchResult.iterations} iterations, shortest path: ${shortestPath.size - 1} steps")
        if (searchResult.endNode.isEnd()) logger.info("Board solved") else logger.info("Search finished without finding a solution")
        return shortestPath
    }

    fun shortestPathFromEndToStart(node: SearchNode, shortestParent: Map<SearchNode, SearchNode>): List<SearchNode> {
        return shortestParent[node]?.let{parent -> listOf(node).plus(shortestPathFromEndToStart(parent, shortestParent))} ?: listOf(node)
    }
}