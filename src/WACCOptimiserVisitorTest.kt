import node.expr.*
import node.stat.*
import node.FuncNode
import node.ProgramNode
import org.junit.Test
import type.BasicType
import type.BasicTypeEnum
import type.Utils
import kotlin.test.assertIs
import kotlin.test.assertEquals


internal class SampleTest {

    private val optimiser: WACCOptimiserVisitor = WACCOptimiserVisitor();
    
    /***************************************/
    /* Integer Expression operations tests */
    /***************************************/

    @Test
    fun testSum() {
        val tree = BinopNode(IntNode(3), IntNode(5), Utils.Binop.PLUS)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertIs<IntNode>(optimisedTree)
        assertEquals(optimisedTree.value, 8)
    }

    @Test
    fun testSubtract() {
        val tree = BinopNode(IntNode(10), IntNode(7), Utils.Binop.MINUS)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertIs<IntNode>(optimisedTree)
        assertEquals(optimisedTree.value, 3)
    }

    @Test
    fun testSubtractNegative() {
        val tree = BinopNode(IntNode(7), IntNode(10), Utils.Binop.MINUS)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertIs<IntNode>(optimisedTree)
        assertEquals(optimisedTree.value, -3)
    }

    @Test
    fun testMultiply() {
        val tree = BinopNode(IntNode(3), IntNode(5), Utils.Binop.MUL)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertIs<IntNode>(optimisedTree)
        assertEquals(optimisedTree.value, 15)
    }

    @Test
    fun testDivide() {
        val tree = BinopNode(IntNode(6), IntNode(2), Utils.Binop.DIV)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertIs<IntNode>(optimisedTree)
        assertEquals(optimisedTree.value, 3)
    }

    @Test
    fun testDivideFloor() {
        val tree = BinopNode(IntNode(7), IntNode(2), Utils.Binop.DIV)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertIs<IntNode>(optimisedTree)
        assertEquals(optimisedTree.value, 3)
    }

    @Test
    fun testModulus() {
        val tree = BinopNode(IntNode(7), IntNode(2), Utils.Binop.MOD)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertIs<IntNode>(optimisedTree)
        assertEquals(optimisedTree.value, 1)
    }

    @Test
    fun testLeftNestedArithmetic() {
        val tree = BinopNode(
                BinopNode(IntNode(2), IntNode(4), Utils.Binop.PLUS)
                , IntNode(5),
                Utils.Binop.MINUS)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertIs<IntNode>(optimisedTree)
        assertEquals(optimisedTree.value, 1)
    }

    @Test
    fun testRightNestedArithmetic() {
        val tree = BinopNode(IntNode(3),
                BinopNode(IntNode(8), IntNode(4), Utils.Binop.MINUS),
                Utils.Binop.MUL)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertIs<IntNode>(optimisedTree)
        assertEquals(optimisedTree.value, 12)
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
        assertIs<IntNode>(optimisedTree)
        assertEquals(optimisedTree.value, -8)
    }

    @Test
    fun testIntegerGreaterThan() {
        val tree = BinopNode(IntNode(3), IntNode(1), Utils.Binop.GREATER)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertIs<BoolNode>(optimisedTree)
        assertEquals(optimisedTree.`val`, true)
    }

    @Test
    fun testIntegerGreaterThanEqualToGreater() {
        val tree = BinopNode(IntNode(3), IntNode(1), Utils.Binop.GREATER_EQUAL)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertIs<BoolNode>(optimisedTree)
        assertEquals(optimisedTree.`val`, true)
    }

    @Test
    fun testIntegerGreaterThanEqualToEqual() {
        val tree = BinopNode(IntNode(3), IntNode(3), Utils.Binop.GREATER_EQUAL)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertIs<BoolNode>(optimisedTree)
        assertEquals(optimisedTree.`val`, true)
    }

    @Test
    fun testIntegerLessThan() {
        val tree = BinopNode(IntNode(3), IntNode(1), Utils.Binop.LESS)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertIs<BoolNode>(optimisedTree)
        assertEquals(optimisedTree.`val`, false)
    }

    @Test
    fun testIntegerLessThanEqualToLess() {
        val tree = BinopNode(IntNode(3), IntNode(5), Utils.Binop.LESS_EQUAL)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertIs<BoolNode>(optimisedTree)
        assertEquals(optimisedTree.`val`, true)
    }

    @Test
    fun testIntegerLessThanEqualToEqual() {
        val tree = BinopNode(IntNode(3), IntNode(3), Utils.Binop.LESS_EQUAL)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertIs<BoolNode>(optimisedTree)
        assertEquals(optimisedTree.`val`, true)
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
        assertIs<BoolNode>(optimisedTree)
        assertEquals(optimisedTree.`val`, true)
    }

    /***************************************/
    /* Char Expressions operations tests   */
    /***************************************/

    @Test
    fun testCharGreaterThan() {
        val tree = BinopNode(CharNode("'e'"), CharNode("'c'"), Utils.Binop.GREATER)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertIs<BoolNode>(optimisedTree)
        assertEquals(optimisedTree.`val`, true)
    }

    @Test
    fun testCharGreaterThanEqualToGreater() {
        val tree = BinopNode(CharNode("'e'"), CharNode("'a'"), Utils.Binop.GREATER_EQUAL)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertIs<BoolNode>(optimisedTree)
        assertEquals(optimisedTree.`val`, true)
    }

    @Test
    fun testCharGreaterThanEqualToEqual() {
        val tree = BinopNode(CharNode("'c'"), CharNode("'c'"), Utils.Binop.GREATER_EQUAL)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertIs<BoolNode>(optimisedTree)
        assertEquals(optimisedTree.`val`, true)
    }

    @Test
    fun testCharLessThan() {
        val tree = BinopNode(CharNode("'e'"), CharNode("'c'"), Utils.Binop.LESS)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertIs<BoolNode>(optimisedTree)
        assertEquals(optimisedTree.`val`, false)
    }

    @Test
    fun testCharLessThanEqualToLess() {
        val tree = BinopNode(CharNode("'f'"), CharNode("'j'"), Utils.Binop.LESS_EQUAL)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertIs<BoolNode>(optimisedTree)
        assertEquals(optimisedTree.`val`, true)
    }

    @Test
    fun testCharLessThanEqualToEqual() {
        val tree = BinopNode(CharNode("'f'"), CharNode("'f'"), Utils.Binop.LESS_EQUAL)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertIs<BoolNode>(optimisedTree)
        assertEquals(optimisedTree.`val`, true)
    }

    /***************************************/
    /* Bool Expressions operations tests   */
    /***************************************/

    @Test
    fun testBoolAndTrue() {
        val tree = BinopNode(BoolNode(true), BoolNode(true), Utils.Binop.AND)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertIs<BoolNode>(optimisedTree)
        assertEquals(optimisedTree.`val`, true)
    }

    @Test
    fun testBoolAndFalse() {
        val tree = BinopNode(BoolNode(true), BoolNode(false), Utils.Binop.AND)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertIs<BoolNode>(optimisedTree)
        assertEquals(optimisedTree.`val`, false)
    }

    @Test
    fun testBoolOrTrue() {
        val tree = BinopNode(BoolNode(true), BoolNode(false), Utils.Binop.OR)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertIs<BoolNode>(optimisedTree)
        assertEquals(optimisedTree.`val`, true)
    }

    @Test
    fun testBoolOrFalse() {
        val tree = BinopNode(BoolNode(false), BoolNode(false), Utils.Binop.OR)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertIs<BoolNode>(optimisedTree)
        assertEquals(optimisedTree.`val`, false)
    }

    @Test
    fun testBoolNested() {
        val tree = BinopNode(
                BinopNode(BoolNode(false), BoolNode(true), Utils.Binop.AND),
                BinopNode(BoolNode(true), BoolNode(true), Utils.Binop.AND),
                Utils.Binop.OR)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertIs<BoolNode>(optimisedTree)
        assertEquals(optimisedTree.`val`, true)
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
        assertIs<BoolNode>(optimisedTree)
        assertEquals(optimisedTree.`val`, true)
    }

    /***************************************/
    /* Unop Expressions tests              */
    /***************************************/

    @Test
    fun testNot() {
        val tree = UnopNode(BoolNode(false), Utils.Unop.NOT)
        val optimisedTree = optimiser.visitUnopNode(tree)
        assertIs<BoolNode>(optimisedTree)
        assertEquals(optimisedTree.`val`, true)
    }

    @Test
    fun testMinus() {
        val tree = UnopNode(IntNode(5), Utils.Unop.MINUS)
        val optimisedTree = optimiser.visitUnopNode(tree)
        assertIs<IntNode>(optimisedTree)
        assertEquals(optimisedTree.value, -5)
    }

    @Test
    fun testOrd() {
        val tree = UnopNode(CharNode("'c'"), Utils.Unop.ORD)
        val optimisedTree = optimiser.visitUnopNode(tree)
        assertIs<IntNode>(optimisedTree)
        assertEquals(optimisedTree.value, 99)
    }

    @Test
    fun testChr() {
        val tree = UnopNode(IntNode(99), Utils.Unop.CHR)
        val optimisedTree = optimiser.visitUnopNode(tree)
        assertIs<CharNode>(optimisedTree)
        assertEquals(optimisedTree.char, 'c')
    }

    @Test
    fun testOrdChrInverse() {
        val tree = UnopNode(UnopNode(IntNode(110), Utils.Unop.CHR), Utils.Unop.ORD)
        val optimisedTree = optimiser.visitUnopNode(tree)
        assertIs<IntNode>(optimisedTree)
        assertEquals(optimisedTree.value, 110)
    }

    @Test
    fun testChrOrdInverse() {
        val tree = UnopNode(UnopNode(CharNode("'f'"), Utils.Unop.ORD),Utils.Unop.CHR)
        val optimisedTree = optimiser.visitUnopNode(tree)
        assertIs<CharNode>(optimisedTree)
        assertEquals(optimisedTree.char, 'f')
    }

    /***************************************/
    /* Statement tests                     */
    /***************************************/

    @Test
    fun testAssign() {
        val tree = AssignNode(
                IdentNode(BasicType(BasicTypeEnum.INTEGER), "x"),
                BinopNode(IntNode(3), IntNode(5), Utils.Binop.PLUS))
        optimiser.visitAssignNode(tree)
        assertIs<IntNode>(tree.rhs)
    }

    @Test
    fun testDeclare() {
        val tree = DeclareStatNode(
                "x", BinopNode(IntNode(3), IntNode(5), Utils.Binop.PLUS))
        optimiser.visitDeclareStatNode(tree)
        assertIs<IntNode>(tree.rhs)
    }

    @Test
    fun testExit() {
        val tree = ExitNode(BinopNode(IntNode(3), IntNode(5), Utils.Binop.PLUS))
        optimiser.visitExitNode(tree)
        assertIs<IntNode>(tree.exitCode)
    }

    @Test
    fun testIf() {
        val tree = IfNode(BinopNode(IntNode(3), IntNode(5), Utils.Binop.LESS),
            SkipNode(), SkipNode())
        optimiser.visitIfNode(tree)
        assertIs<BoolNode>(tree.condition)
    }

    @Test
    fun testPrintln() {
        val tree = PrintlnNode(BinopNode(IntNode(3), IntNode(5), Utils.Binop.PLUS))
        optimiser.visitPrintlnNode(tree)
        assertIs<IntNode>(tree.expr)
    }

    @Test
    fun testPrint() {
        val tree = PrintNode(BinopNode(IntNode(3), IntNode(5), Utils.Binop.PLUS))
        optimiser.visitPrintNode(tree)
        assertIs<IntNode>(tree.expr)
    }

    @Test
    fun testReturn() {
        val tree = ReturnNode(BinopNode(IntNode(3), IntNode(5), Utils.Binop.PLUS))
        optimiser.visitReturnNode(tree)
        assertIs<IntNode>(tree.expr)
    }

    @Test
    fun testScope() {
        val tree = ScopeNode(ReturnNode(
                BinopNode(IntNode(3), IntNode(5), Utils.Binop.PLUS)))
        optimiser.visitScopeNode(tree)
        val stat = tree.body[0]
        assertIs<ReturnNode>(stat)
        assertIs<IntNode>(stat.expr)
    }

    @Test
    fun testSequence() {
        val tree = SequenceNode(DeclareStatNode("x",
                    BinopNode(IntNode(3), IntNode(5), Utils.Binop.PLUS)),
                ReturnNode(BinopNode(IntNode(3), IntNode(5), Utils.Binop.PLUS)))
        optimiser.visitSequenceNode(tree)
        val stat1 = tree.body[0]
        val stat2 = tree.body[1]
        assertIs<DeclareStatNode>(stat1)
        assertIs<IntNode>(stat1.rhs)
        assertIs<ReturnNode>(stat2)
        assertIs<IntNode>(stat2.expr)
    }

    /***************************************/
    /* Other node tests                    */
    /***************************************/



    /***************************************/
    /* Variable replacement tests          */
    /***************************************/

    /***************************************/
    /* If simplification tests             */
    /***************************************/

    /***************************************/
    /* Equality tests                      */
    /***************************************/
}