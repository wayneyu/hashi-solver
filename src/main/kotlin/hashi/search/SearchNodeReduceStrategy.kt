package hashi.search

interface SearchNodeReduceStrategy {
    fun reduce(node: SearchNode): SearchNode
}