package hashi

import hashi.search.BFS
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
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
    }
})