package hashi

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import kotlin.test.assertEquals

@RunWith(JUnitPlatform::class)
class ReduceStrategySpec : Spek({

    describe("reduceStrategy") {

        it("should reduce island with 3 bridges and only 2 neighbors") {
            val node1 = Node(1, 1, 0, 0)
            val node2 = Node(2, 2, 4, 4)
            val node3 = Node(3, 3, 0, 4)
            val board = Board(5,5, listOf(node1, node2, node3))

            val expected = board.connect(node3, node1).connect(node3, node2)
            val actual = board.let { it -> MoreThanThreeBridgesAndTwoNeighbors.reduceBoard(it) }

            assertEquals(expected, actual)
        }

        it("should reduce island with only one neighbor") {
            val node1 = Node(1, 1, 0, 0)
            val node2 = Node(2, 2, 0, 4)
            val board = Board(5,5, listOf(node1, node2))

            val expected = board.connect(node1, node2)
            val actual = board.let { it -> OneNonConnectedNeighbor.reduceBoard(it) }
            assertEquals(expected, actual)
        }

        it("should reduce island when all neighbors have same remaining bridges") {
            val board =  Board.fromString("""
                00200
                00000
                10400
                00000
                002-1
            """.trimIndent())

            val expected =  Board.fromString("""
                00200
                00=00
                1-400
                00-00
                002-1
            """.trimIndent())

            val actual = board.let { NeighborsWithSameRemainingBridges.reduceBoard(it) }
            println(actual.printBridges())
            assertEquals(expected.printBoard(), actual.printBoard())
        }
    }
})