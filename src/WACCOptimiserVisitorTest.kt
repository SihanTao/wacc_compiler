
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

    @Test
    fun testSimpleSubtract() {
        val tree = BinopNode(IntNode(10), IntNode(7), Utils.Binop.MINUS)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertTrue(optimisedTree is IntNode)
        if (optimisedTree is IntNode) {
            assertEquals(optimisedTree.value, 3)
        }
    }

    @Test
    fun testSimpleSubtractNegative() {
        val tree = BinopNode(IntNode(7), IntNode(10), Utils.Binop.MINUS)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertTrue(optimisedTree is IntNode)
        if (optimisedTree is IntNode) {
            assertEquals(optimisedTree.value, -3)
        }
    }

    @Test
    fun testSimpleMultiply() {
        val tree = BinopNode(IntNode(3), IntNode(5), Utils.Binop.MUL)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertTrue(optimisedTree is IntNode)
        if (optimisedTree is IntNode) {
            assertEquals(optimisedTree.value, 15)
        }
    }

    @Test
    fun testSimpleDivide() {
        val tree = BinopNode(IntNode(6), IntNode(2), Utils.Binop.DIV)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertTrue(optimisedTree is IntNode)
        if (optimisedTree is IntNode) {
            assertEquals(optimisedTree.value, 3)
        }
    }

    @Test
    fun testSimpleDivideFloor() {
        val tree = BinopNode(IntNode(7), IntNode(2), Utils.Binop.DIV)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertTrue(optimisedTree is IntNode)
        if (optimisedTree is IntNode) {
            assertEquals(optimisedTree.value, 3)
        }
    }

    @Test
    fun testSimpleModulus() {
        val tree = BinopNode(IntNode(7), IntNode(2), Utils.Binop.MOD)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertTrue(optimisedTree is IntNode)
        if (optimisedTree is IntNode) {
            assertEquals(optimisedTree.value, 1)
        }
    }

    @Test
    fun testLeftNested() {
        val tree = BinopNode(
                BinopNode(IntNode(2), IntNode(4), Utils.Binop.PLUS)
                , IntNode(5),
                Utils.Binop.MINUS)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertTrue(optimisedTree is IntNode)
        if (optimisedTree is IntNode) {
            assertEquals(optimisedTree.value, 1)
        }
    }

    @Test
    fun testRightNested() {
        val tree = BinopNode(IntNode(3),
                BinopNode(IntNode(8), IntNode(4), Utils.Binop.MINUS),
                Utils.Binop.MUL)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertTrue(optimisedTree is IntNode)
        if (optimisedTree is IntNode) {
            assertEquals(optimisedTree.value, 12)
        }
    }

    @Test
    fun testDeepNested() {
        val tree = BinopNode(
                BinopNode(
                        BinopNode(IntNode(12), IntNode(7), Utils.Binop.PLUS),
                        BinopNode(IntNode(7), IntNode(2), Utils.Binop.MINUS),
                        Utils.Binop.MOD),
                BinopNode(
                        BinopNode(IntNode(2), IntNode(8), Utils.Binop.MINUS),
                        BinopNode(IntNode(2), IntNode(1), Utils.Binop.PLUS),
                        Utils.Binop.DIV),
                Utils.Binop.MUL)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertTrue(optimisedTree is IntNode)
        if (optimisedTree is IntNode) {
            assertEquals(optimisedTree.value, -8)
        }
    }
}