
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import node.expr.*
import node.stat.*
import node.FuncNode
import node.ProgramNode
import org.junit.Test
import type.Utils

internal class SampleTest {

    private val optimiser: WACCOptimiserVisitor = WACCOptimiserVisitor();

    @Test
    fun testSimpleSum() {
        val tree = BinopNode(IntNode(3), IntNode(5), Utils.Binop.PLUS)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertTrue(optimisedTree is IntNode)
        if (optimisedTree is IntNode) {
            assertEquals(optimisedTree.value, 8)
        }
    }
}