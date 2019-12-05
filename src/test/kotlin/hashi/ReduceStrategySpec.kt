package hashi

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertTrue

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

        it("should reduce island with only one neighbor") {
            verifyStrategy(
            """
                10202
                00000
                00000
                00000
                00000
            """.trimIndent(),
            """
                1-202
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
                00!00
                1-400
                00|00
                002-1
            """.trimIndent(), NeighborsWithSameRemainingBridges)
        }

        it("should reduce island with six bridges and three neighbors"){
            verifyStrategy(
                    """
                10200
                |0000
                30600
                00000
                1-4-1
            """.trimIndent(),
                    """
                10200
                |0!00
                3=600
                00!00
                1-4-1
            """.trimIndent(), NeighborsWithSameRemainingBridges)
        }

        it("should reduce island with four bridges and two neighbors"){
            verifyStrategy(
                    """
                00000
                00000
                1---3
                00000
                02004
            """.trimIndent(),
                    """
                00000
                00000
                1---3
                0000!
                02==4
            """.trimIndent(), NeighborsWithSameRemainingBridges)

            verifyStrategy(
                    """
                00200
                00000
                205-1
                00000
                00000
            """.trimIndent(),
                    """
                00200
                00!00
                2=5-1
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
                00!00
                1-5-1
                00|00
                00100
            """.trimIndent(), NeighborsWithSameRemainingBridges)
        }


        it("should reduce island with five bridges and two neighbors"){
            verifyStrategy(
                    """
                00200
                00000
                206-1
                00000
                00100
            """.trimIndent(),
                    """
                00200
                00!00
                2=6-1
                00|00
                00100
            """.trimIndent(), NeighborsWithSameRemainingBridges)

            verifyStrategy(
                    """
                00200
                00000
                20501
                00000
                00000
            """.trimIndent(),
                    """
                00200
                00!00
                2=5-1
                00000
                00000
            """.trimIndent(), NeighborsWithSameRemainingBridges)
        }

    }

    describe("boardReduceStrategy") {
        it("should reduce board") {
            val board = Board.fromString("""
                2020020
                0000001
                6050300
                0100003
                3010010
                0300805
                4020000
                0200502
                2001000
                0020503 
            """.trimIndent())

            val expected = Board.fromString("""
                202--20
                !0|00|1
                6=5-3||
                !1|0!|3
                3|10!1!
                |3==8=5
                4=20!0|
                |2==502
                2--1|0|
                002=5=3
            """.trimIndent())

            val actual = BoardReduceStrategy.reduce(board)

            assertEquals(expected, actual)
            assertTrue(actual.isSolved())
        }
    }

    describe("twoBridgesTwoNeighborsStrategy") {
        it("should reduce board with two bridge island with two neighbors") {
            verifyStrategy("""
                20003
                00000
                00102
            """.trimIndent(),
            """
                20003
                0000|
                00102
            """.trimIndent(), TwoBridgesTwoNeighborsStrategy)
        }
    }

    describe("ConnectOneForEachNeighbor") {

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
                0000|
                0000|
                0000|
                00002
            """.trimIndent(), ConnectOneForEachNeigbors)
        }

        it("should connect 5 bridges island with 3 neighbors") {
            verifyStrategy("""
                00100
                00000
                20502
            """.trimIndent(),
            """
                00100
                00|00
                2-5-2
            """.trimIndent(), ConnectOneForEachNeigbors)
        }

        it("should connect 7 bridges island with 4 neighbors") {
            verifyStrategy("""
                00100
                00000
                20702
                00000
                00200
            """.trimIndent(),
            """
                00100
                00|00
                2-7-2
                00|00
                00200
            """.trimIndent(), ConnectOneForEachNeigbors)
        }
        
        it("should connect 3 bridges island with 2 neighbors, scenario 2") {
            verifyStrategy("""
                2-3--20010
                0002===302
                205040302|
                00|00000|3
                4-5-4002||
                |0000100|3
                |201000020
                3|00030302
                |400502010
                3|00000404
                03-202000|
                300010301|
                |02==30202
                3---203-20
            """.trimIndent(),
            """
                2-3--20010
                0002===302
                205040302|
                00|00000|3
                4-5-4002||
                |0000100|3
                |20100002| 
                3|00030302
                |400502010
                3|00000404
                |3-202000|
                300010301|
                |02==30202
                3---203-20
            """.trimIndent(), ConnectOneForEachNeigbors)
        }
    }

    describe("TwoBridgesThreeNeighbors") {
        it("should connect with one bridge") {
            verifyStrategy("""
                20201
                00000
                10100
            """.trimIndent(),
                    """
                2-201
                00000
                10100
            """.trimIndent(), TwoBridgesTwoSingleBridgeNeighbors)
        }
    }

})