package hashi

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(JUnitPlatform::class)
class BoardSpec : Spek({

    describe("board") {

        val node1 = Node(1, 1, 0, 0)
        val node2 = Node(2, 1, 0, 4)
        val node3 = Node(3, 1, 2, 2)
        val node4 = Node(4, 1, 4, 0)
        val node5 = Node(5, 1, 4, 4)
        val board = Board(5,5, listOf(node1, node2, node3, node4, node5))

        it("should find neareast island to the west") {
            assertEquals(node1, board.getIslandToWest(node2))
        }

        it("should find neareast island to the east") {
            assertEquals(node2, board.getIslandToEast(node1))
        }

        it("should find neareast island to the north") {
            assertEquals(node1, board.getIslandToNorth(node4))
        }

        it("should find neareast island to the south") {
            assertEquals(node5, board.getIslandToSouth(node2))
        }

        it("should return null if no next island is found") {
            assertEquals(null, board.getIslandToEast(node3))
            assertEquals(null, board.getIslandToWest(node3))
            assertEquals(null, board.getIslandToNorth(node3))
            assertEquals(null, board.getIslandToSouth(node3))
        }

        it("should return all neighbor nodes") {
            assertEquals(listOf(node1, node5), board.getNeighborIslands(node2))
        }

        it("should return only neighbors that are connectable") {
            val board = Board.fromString("""
                00101
                00-00
                10-03
                00-00
                1-200
            """.trimIndent())

            assertEquals(board.getNeighborIslands(board.findNode(2, 4)), listOf(board.findNode(0, 4)))
        }

        it("should return true if the board is solved") {
            val node1 = Node(1, 1, 0, 0)
            val node2 = Node(2, 3, 0, 4)
            val node3 = Node(3, 2, 4, 4)
            val board = Board(5,5, listOf(node1, node2, node3))

            assertTrue(board.copy(bridges = listOf(Bridge(node3, node2), Bridge(node3, node2), Bridge(node3,node1))).isSolved())
        }

        it("should return board after connecting two nodes") {
            val node1 = Node(1, 1, 0, 0)
            val node2 = Node(2, 3, 0, 4)
            val node3 = Node(3, 2, 4, 4)
            val board = Board(5,5, listOf(node1, node2, node3))
            val actual = board.connect(node1, node2)
            val expected = Board(5,5, listOf(node1.copy(connected = 1), node2.copy(connected = 1), node3), listOf(Bridge(node1, node2)))

            assertEquals(expected, actual)
        }

        it("should connect two nodes by x, y coordinate") {
            val board = Board.fromString("""
                101
            """.trimIndent())
            val expected = Board.fromString("""
                1-1
            """.trimIndent())

            assertEquals(expected, board.connect(0, 0, 0, 2))
            assertEquals(expected.islands.map{it.connected}, board.connect(0, 0, 0, 2).islands.map{it.connected})
        }

        it("should throw error if building bridge that crosses another bridge") {
            val board = Board.fromString("""
                0010
                20-1
                0020
            """.trimIndent())

            println(board.connect(1, 0, 1, 3))
            println(board.getNeighborIslands(board.findNode(1, 0)))
            assertFails("failed"){board.connect(1, 0, 1, 3)}
        }

        it("should double connect two nodes") {
            val board = Board.fromString("""
                202
            """.trimIndent())
            val expected = Board.fromString("""
                2=2
            """.trimIndent())

            assertEquals(expected, board.connect2(0, 0, 0, 2))
            assertEquals(expected.islands.map{it.connected}, board.connect2(0, 0, 0, 2).islands.map{it.connected})
            assertEquals(expected.islands.map{it.connected}, board.connect2(board.findNode(0, 0), board.findNode(0, 2)).islands.map{it.connected})
        }

        it("should print out board in 2d") {
            val node1 = Node(1, 1, 0, 0)
            val node2 = Node(2, 3, 0, 2)
            val node3 = Node(3, 4, 0, 4)
            val node4 = Node(4, 3, 2, 4)
            val node5 = Node(5, 1, 4, 4)
            val board = Board(5,5, listOf(node1, node2, node3, node4, node5))
                    .connect(node1, node2)
                    .connect(node3, node2).connect(node3, node2)
                    .connect(node3, node4).connect(node3, node4)
                    .connect(node4, node5)

            val actual = board.printBoard()
            val expected = """
                1-3=4
                0000=
                00003
                0000-
                00001
            """.trimIndent()

            assertEquals(expected, actual)
        }

        it("should translate a 2d map of a board") {
            val layout = """
                3-5=4
                =0=0=
                4-4-4
                -000-
                2-101
            """.trimIndent()

            val actual = Board.fromString(layout)

            assertEquals(layout, actual.printBoard())
        }

        it("should translate a 2d map of a board") {
            val layout = """
                0010
                10-1
                0010
            """.trimIndent()

            val actual = Board.fromString(layout)

            assertEquals(layout, actual.printBoard())
        }

        it("should convert a 2d layout to a board with bridges") {
            val layout = """
                1-2-3
                0000=
                00002
                00000
                00000
            """.trimIndent()


            val node1 = Node(1, 1,0, 0, 0)
            val node2 = Node(2, 2,0, 2, 0)
            val node3 = Node(3, 3,0, 4, 0)
            val node4 = Node(4, 2,2, 4, 0)
            val expected = Board(5, 5, listOf(node1, node2, node3, node4))
                    .connect(node1, node2)
                    .connect(node2, node3)
                    .connect(node3, node4).connect(node3, node4)

            val actual = Board.fromString(layout)
            assertEquals(expected.islands, actual.islands)
        }


        it("should return not reachable if one node cannot connect to another") {
            val board = Board.fromString("""
                00101
                00-00
                10-03
                00-00
                1-200
            """.trimIndent())

            assertFalse { board.reachable(board.findNode(2, 0), board.findNode(0, 4)) }
        }
    }
})