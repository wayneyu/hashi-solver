package hashi.search

interface SearchNode {
    val neighbors: Set<SearchNode>
    fun isEnd(): Boolean
}