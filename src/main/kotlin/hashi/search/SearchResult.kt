package hashi.search

data class SearchResult(val endNode: SearchNode,
                        val shortestParent: Map<SearchNode, SearchNode>,
                        val iterations: Int = -1)