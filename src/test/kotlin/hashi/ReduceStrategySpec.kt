package hashi

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.xit
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import kotlin.test.assertEquals

@RunWith(JUnitPlatform::class)
class ReduceStrategySpec : Spek({

    fun verifyStrategy(initialBoard: String, finalBoard: String, reduceStrategy: ReduceStrategy) {
        val board =  Board.fromString(initialBoard)
        val expected =  Board.fromString(finalBoard)
        val actual = board.let { it -> reduceStrategy.reduceBoard(it) }
        println("expected\n${expected.printBoard()}\n")
        println("actual\n${actual.printBoard()}\n")
        assertEquals(expected, actual)
    }

    describe("reduceStrategy") {

        it("should reduce island with 3 bridges and only 2 neighbors") {
            verifyStrategy(
            """
                10003
                00000
                00000
                00000
                00002
            """.trimIndent(),
            """
                1---3
                0000-
                0000-
                0000-
                00002
            """.trimIndent(), MoreThanThreeBridgesAndTwoNeighbors)
        }

        it("should reduce island with only one neighbor") {
            verifyStrategy(
            """
                10002
                00000
                00000
                00000
                00000
            """.trimIndent(),
            """
                1---2
                00000
                00000
                00000
                00000
            """.trimIndent(), OneNonConnectedNeighbor)
        }

        it("should reduce island when all neighbors have same remaining bridges") {
            verifyStrategy(
                    """
                00200
                00000
                10400
                00000
                002-1
            """.trimIndent(),
                    """
                00200
                00=00
                1-400
                00-00
                002-1
            """.trimIndent(), NeighborsWithSameRemainingBridges)
        }

        it("should reduce island with six bridges and three neighbors"){
            verifyStrategy(
                    """
                00200
                00000
                30600
                00000
                1-4-1
            """.trimIndent(),
                    """
                00200
                00=00
                3=600
                00=00
                1-4-1
            """.trimIndent(), NeighborsWithSameRemainingBridges)
        }

        it("should reduce island with four bridges and two neighbors"){
            verifyStrategy(
                    """
                00200
                00000
                30003
                00000
                04004
            """.trimIndent(),
                    """
                00200
                00000
                30003
                0000=
                04==4
            """.trimIndent(), NeighborsWithSameRemainingBridges)

            verifyStrategy(
                    """
                00200
                00000
                305-1
                00000
                00000
            """.trimIndent(),
                    """
                00200
                00=00
                3=5-1
                00000
                00000
            """.trimIndent(), NeighborsWithSameRemainingBridges)

            verifyStrategy(
                    """
                00200
                00000
                105-1
                00000
                00100
            """.trimIndent(),
                    """
                00200
                00=00
                1-5-1
                00-00
                00100
            """.trimIndent(), NeighborsWithSameRemainingBridges)
        }


        it("should reduce island with five bridges and two neighbors"){
            verifyStrategy(
                    """
                00200
                00000
                306-1
                00000
                00100
            """.trimIndent(),
                    """
                00200
                00=00
                3=6-1
                00-00
                00100
            """.trimIndent(), NeighborsWithSameRemainingBridges)

            verifyStrategy(
                    """
                00200
                00000
                30501
                00000
                00000
            """.trimIndent(),
                    """
                00200
                00=00
                3=5-1
                00000
                00000
            """.trimIndent(), NeighborsWithSameRemainingBridges)
        }


    }

})