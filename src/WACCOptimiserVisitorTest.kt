
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
    
    /***************************************/
    /* Integer Expression operations tests */
    /***************************************/

    @Test
    fun testSum() {
        val tree = BinopNode(IntNode(3), IntNode(5), Utils.Binop.PLUS)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertTrue(optimisedTree is IntNode)
        if (optimisedTree is IntNode) {
            assertEquals(optimisedTree.value, 8)
        }
    }

    @Test
    fun testSubtract() {
        val tree = BinopNode(IntNode(10), IntNode(7), Utils.Binop.MINUS)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertTrue(optimisedTree is IntNode)
        if (optimisedTree is IntNode) {
            assertEquals(optimisedTree.value, 3)
        }
    }

    @Test
    fun testSubtractNegative() {
        val tree = BinopNode(IntNode(7), IntNode(10), Utils.Binop.MINUS)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertTrue(optimisedTree is IntNode)
        if (optimisedTree is IntNode) {
            assertEquals(optimisedTree.value, -3)
        }
    }

    @Test
    fun testMultiply() {
        val tree = BinopNode(IntNode(3), IntNode(5), Utils.Binop.MUL)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertTrue(optimisedTree is IntNode)
        if (optimisedTree is IntNode) {
            assertEquals(optimisedTree.value, 15)
        }
    }

    @Test
    fun testDivide() {
        val tree = BinopNode(IntNode(6), IntNode(2), Utils.Binop.DIV)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertTrue(optimisedTree is IntNode)
        if (optimisedTree is IntNode) {
            assertEquals(optimisedTree.value, 3)
        }
    }

    @Test
    fun testDivideFloor() {
        val tree = BinopNode(IntNode(7), IntNode(2), Utils.Binop.DIV)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertTrue(optimisedTree is IntNode)
        if (optimisedTree is IntNode) {
            assertEquals(optimisedTree.value, 3)
        }
    }

    @Test
    fun testModulus() {
        val tree = BinopNode(IntNode(7), IntNode(2), Utils.Binop.MOD)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertTrue(optimisedTree is IntNode)
        if (optimisedTree is IntNode) {
            assertEquals(optimisedTree.value, 1)
        }
    }

    @Test
    fun testLeftNestedArithmetic() {
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
    fun testRightNestedArithmetic() {
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
    fun testDeepNestedArithmetic() {
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

    @Test
    fun testIntegerGreaterThan() {
        val tree = BinopNode(IntNode(3), IntNode(1), Utils.Binop.GREATER)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertTrue(optimisedTree is BoolNode)
        if (optimisedTree is BoolNode) {
            assertEquals(optimisedTree.`val`, true)
        }
    }

    @Test
    fun testIntegerGreaterThanEqualToGreater() {
        val tree = BinopNode(IntNode(3), IntNode(1), Utils.Binop.GREATER_EQUAL)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertTrue(optimisedTree is BoolNode)
        if (optimisedTree is BoolNode) {
            assertEquals(optimisedTree.`val`, true)
        }
    }

    @Test
    fun testIntegerGreaterThanEqualToEqual() {
        val tree = BinopNode(IntNode(3), IntNode(3), Utils.Binop.GREATER_EQUAL)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertTrue(optimisedTree is BoolNode)
        if (optimisedTree is BoolNode) {
            assertEquals(optimisedTree.`val`, true)
        }
    }

    @Test
    fun testIntegerLessThan() {
        val tree = BinopNode(IntNode(3), IntNode(1), Utils.Binop.LESS)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertTrue(optimisedTree is BoolNode)
        if (optimisedTree is BoolNode) {
            assertEquals(optimisedTree.`val`, false)
        }
    }

    @Test
    fun testIntegerLessThanEqualToLess() {
        val tree = BinopNode(IntNode(3), IntNode(5), Utils.Binop.LESS_EQUAL)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertTrue(optimisedTree is BoolNode)
        if (optimisedTree is BoolNode) {
            assertEquals(optimisedTree.`val`, true)
        }
    }

    @Test
    fun testIntegerLessThanEqualToEqual() {
        val tree = BinopNode(IntNode(3), IntNode(3), Utils.Binop.LESS_EQUAL)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertTrue(optimisedTree is BoolNode)
        if (optimisedTree is BoolNode) {
            assertEquals(optimisedTree.`val`, true)
        }
    }

    @Test
    fun testIntegerGreaterThanNested() {
        val tree = BinopNode(
                BinopNode(
                        BinopNode(IntNode(12), IntNode(7), Utils.Binop.PLUS),
                        BinopNode(IntNode(7), IntNode(2), Utils.Binop.MINUS),
                        Utils.Binop.MOD),
                BinopNode(
                        BinopNode(IntNode(2), IntNode(8), Utils.Binop.MINUS),
                        BinopNode(IntNode(2), IntNode(1), Utils.Binop.PLUS),
                        Utils.Binop.DIV),
                Utils.Binop.GREATER)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertTrue(optimisedTree is BoolNode)
        if (optimisedTree is BoolNode) {
            assertEquals(optimisedTree.`val`, true)
        }
    }

    /***************************************/
    /* Char Expressions operations tests   */
    /***************************************/

    @Test
    fun testCharGreaterThan() {
        val tree = BinopNode(CharNode("'e'"), CharNode("'c'"), Utils.Binop.GREATER)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertTrue(optimisedTree is BoolNode)
        if (optimisedTree is BoolNode) {
            assertEquals(optimisedTree.`val`, true)
        }
    }

    @Test
    fun testCharGreaterThanEqualToGreater() {
        val tree = BinopNode(CharNode("'e'"), CharNode("'a'"), Utils.Binop.GREATER_EQUAL)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertTrue(optimisedTree is BoolNode)
        if (optimisedTree is BoolNode) {
            assertEquals(optimisedTree.`val`, true)
        }
    }

    @Test
    fun testCharGreaterThanEqualToEqual() {
        val tree = BinopNode(CharNode("'c'"), CharNode("'c'"), Utils.Binop.GREATER_EQUAL)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertTrue(optimisedTree is BoolNode)
        if (optimisedTree is BoolNode) {
            assertEquals(optimisedTree.`val`, true)
        }
    }

    @Test
    fun testCharLessThan() {
        val tree = BinopNode(CharNode("'e'"), CharNode("'c'"), Utils.Binop.LESS)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertTrue(optimisedTree is BoolNode)
        if (optimisedTree is BoolNode) {
            assertEquals(optimisedTree.`val`, false)
        }
    }

    @Test
    fun testCharLessThanEqualToLess() {
        val tree = BinopNode(CharNode("'f'"), CharNode("'j'"), Utils.Binop.LESS_EQUAL)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertTrue(optimisedTree is BoolNode)
        if (optimisedTree is BoolNode) {
            assertEquals(optimisedTree.`val`, true)
        }
    }

    @Test
    fun testCharLessThanEqualToEqual() {
        val tree = BinopNode(CharNode("'f'"), CharNode("'f'"), Utils.Binop.LESS_EQUAL)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertTrue(optimisedTree is BoolNode)
        if (optimisedTree is BoolNode) {
            assertEquals(optimisedTree.`val`, true)
        }
    }

    /***************************************/
    /* Bool Expressions operations tests   */
    /***************************************/

    @Test
    fun testBoolAndTrue() {
        val tree = BinopNode(BoolNode(true), BoolNode(true), Utils.Binop.AND)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertTrue(optimisedTree is BoolNode)
        if (optimisedTree is BoolNode) {
            assertEquals(optimisedTree.`val`, true)
        }
    }

    @Test
    fun testBoolAndFalse() {
        val tree = BinopNode(BoolNode(true), BoolNode(false), Utils.Binop.AND)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertTrue(optimisedTree is BoolNode)
        if (optimisedTree is BoolNode) {
            assertEquals(optimisedTree.`val`, false)
        }
    }

    @Test
    fun testBoolOrTrue() {
        val tree = BinopNode(BoolNode(true), BoolNode(false), Utils.Binop.OR)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertTrue(optimisedTree is BoolNode)
        if (optimisedTree is BoolNode) {
            assertEquals(optimisedTree.`val`, true)
        }
    }

    @Test
    fun testBoolOrFalse() {
        val tree = BinopNode(BoolNode(false), BoolNode(false), Utils.Binop.OR)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertTrue(optimisedTree is BoolNode)
        if (optimisedTree is BoolNode) {
            assertEquals(optimisedTree.`val`, false)
        }
    }

    @Test
    fun testBoolNested() {
        val tree = BinopNode(
                BinopNode(BoolNode(false), BoolNode(true), Utils.Binop.AND),
                BinopNode(BoolNode(true), BoolNode(true), Utils.Binop.AND),
                Utils.Binop.OR)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertTrue(optimisedTree is BoolNode)
        if (optimisedTree is BoolNode) {
            assertEquals(optimisedTree.`val`, true)
        }
    }

    @Test
    fun testBoolDeepNested() {
        val tree = BinopNode(
                BinopNode(
                        BinopNode(BoolNode(true), BoolNode(false), Utils.Binop.AND),
                        BinopNode(BoolNode(true), BoolNode(true), Utils.Binop.OR),
                        Utils.Binop.AND),
                BinopNode(
                        BinopNode(BoolNode(true), BoolNode(true), Utils.Binop.OR),
                        BinopNode(BoolNode(true), BoolNode(false), Utils.Binop.OR),
                        Utils.Binop.AND),
                Utils.Binop.OR)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertTrue(optimisedTree is BoolNode)
        if (optimisedTree is BoolNode) {
            assertEquals(optimisedTree.`val`, true)
        }
    }

    /***************************************/
    /* Full AST tests                      */
    /***************************************/

    /***************************************/
    /* Variable replacement tests          */
    /***************************************/

    /***************************************/
    /* Equality tests                      */
    /***************************************/

    /***************************************/
    /* Complicated expression tests        */
    /***************************************/
}