package hashi

import hashi.search.BFS
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(JUnitPlatform::class)
class SolverSpec : Spek({

    val solver = Solver(BFS(BoardReduceStrategy))

    describe("solver") {

        it("should solve a simple hashi board") {
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

            val solution = solver.solve(board)

            assertTrue(solution.last().isSolved())
        }

        it("should solve a medium hashi board") {
            val board = Board.fromString("""
                03004020
                20000002
                02030010
                00002004
                50040020
                01003000
                40010003
                02003050
                00020103
                20001030
                02030203
            """.trimIndent())

            val solution = solver.solve(board)
            println(solution.last().printBoard())
            assertTrue(solution.last().isSolved())
        }

        it("should solve a hard hashi board") {
            val board = Board.fromString("""
                2030020010
                0002000302
                2050403020
                0000000003
                4050400200
                0000010003
                0201000020
                3000030302
                0400502010
                3000000404
                0302020000
                3000103010
                0020030202
                3000203020
            """.trimIndent())

            val expected = Board.fromString("""
                /2-3--2--1 /
                /| |2===3-2/
                /2 5=4=3-2|/
                /| !     |3/
                /4=5-4--2|!/
                /|   !1 ||3/
                /|2-1!| |2|/
                /3|  !3-3|2/
                /!4==5|2|1|/
                /3|  ||!4-4/
                /|3=2|2!! !/
                /3   1|3!1!/
                /! 2==3|2|2/
                /3---2-3-2 /
                """.trimIndent())
            val solution = solver.solve(board)

            assertEquals(expected, solution.last())
        }
    }
})