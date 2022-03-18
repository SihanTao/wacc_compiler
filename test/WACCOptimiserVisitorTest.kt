import node.expr.*
import node.stat.*
import node.FuncNode
import node.ProgramNode
import org.junit.Test
import type.ArrayType
import type.BasicType
import type.BasicTypeEnum
import type.Utils
import kotlin.test.assertIs
import kotlin.test.assertEquals
import kotlin.test.assertIsNot
import kotlin.test.assertNull

internal class WACCOptimiserVisitorTest {

    private val optimiser: WACCOptimiserVisitor = WACCOptimiserVisitor(1)
    private val optimiserO3: WACCOptimiserVisitor = WACCOptimiserVisitor(3)
    
    /***************************************/
    /* Integer Expression operations tests */
    /***************************************/

    @Test
    fun testSum() {
        val tree = BinopNode(IntNode(3), IntNode(5), Utils.Binop.PLUS)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertEquals((optimisedTree as IntNode).value, 8)
    }

    @Test
    fun testSubtract() {
        val tree = BinopNode(IntNode(10), IntNode(7), Utils.Binop.MINUS)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertEquals((optimisedTree as IntNode).value, 3)
    }

    @Test
    fun testSubtractNegative() {
        val tree = BinopNode(IntNode(7), IntNode(10), Utils.Binop.MINUS)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertEquals((optimisedTree as IntNode).value, -3)
    }

    @Test
    fun testMultiply() {
        val tree = BinopNode(IntNode(3), IntNode(5), Utils.Binop.MUL)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertEquals((optimisedTree as IntNode).value, 15)
    }

    @Test
    fun testDivide() {
        val tree = BinopNode(IntNode(6), IntNode(2), Utils.Binop.DIV)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertEquals((optimisedTree as IntNode).value, 3)
    }

    @Test
    fun testDivideFloor() {
        val tree = BinopNode(IntNode(7), IntNode(2), Utils.Binop.DIV)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertEquals((optimisedTree as IntNode).value, 3)
    }

    @Test
    fun testModulus() {
        val tree = BinopNode(IntNode(7), IntNode(2), Utils.Binop.MOD)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertEquals((optimisedTree as IntNode).value, 1)
    }

    @Test
    fun testLeftNestedArithmetic() {
        val tree = BinopNode(
                BinopNode(IntNode(2), IntNode(4), Utils.Binop.PLUS)
                , IntNode(5),
                Utils.Binop.MINUS)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertEquals((optimisedTree as IntNode).value, 1)
    }

    @Test
    fun testRightNestedArithmetic() {
        val tree = BinopNode(IntNode(3),
                BinopNode(IntNode(8), IntNode(4), Utils.Binop.MINUS),
                Utils.Binop.MUL)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertEquals((optimisedTree as IntNode).value, 12)
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
        assertEquals((optimisedTree as IntNode).value, -8)
    }

    @Test
    fun testIntegerGreaterThan() {
        val tree = BinopNode(IntNode(3), IntNode(1), Utils.Binop.GREATER)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertEquals((optimisedTree as BoolNode).`val`, true)
    }

    @Test
    fun testIntegerGreaterThanEqualToGreater() {
        val tree = BinopNode(IntNode(3), IntNode(1), Utils.Binop.GREATER_EQUAL)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertEquals((optimisedTree as BoolNode).`val`, true)
    }

    @Test
    fun testIntegerGreaterThanEqualToEqual() {
        val tree = BinopNode(IntNode(3), IntNode(3), Utils.Binop.GREATER_EQUAL)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertEquals((optimisedTree as BoolNode).`val`, true)
    }

    @Test
    fun testIntegerLessThan() {
        val tree = BinopNode(IntNode(3), IntNode(1), Utils.Binop.LESS)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertEquals((optimisedTree as BoolNode).`val`, false)
    }

    @Test
    fun testIntegerLessThanEqualToLess() {
        val tree = BinopNode(IntNode(3), IntNode(5), Utils.Binop.LESS_EQUAL)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertEquals((optimisedTree as BoolNode).`val`, true)
    }

    @Test
    fun testIntegerLessThanEqualToEqual() {
        val tree = BinopNode(IntNode(3), IntNode(3), Utils.Binop.LESS_EQUAL)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertEquals((optimisedTree as BoolNode).`val`, true)
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
        assertEquals((optimisedTree as BoolNode).`val`, true)
    }

    @Test
    fun testIntegerEqual() {
        val tree = BinopNode(IntNode(5), IntNode(5), Utils.Binop.EQUAL)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertEquals((optimisedTree as BoolNode).`val`, true)
    }

    @Test
    fun testIntegerUnequal() {
        val tree = BinopNode(IntNode(5), IntNode(7), Utils.Binop.INEQUAL)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertEquals((optimisedTree as BoolNode).`val`, true)
    }

    /***************************************/
    /* Char Expressions operations tests   */
    /***************************************/

    @Test
    fun testCharGreaterThan() {
        val tree = BinopNode(CharNode("'e'"), CharNode("'c'"), Utils.Binop.GREATER)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertEquals((optimisedTree as BoolNode).`val`, true)
    }

    @Test
    fun testCharGreaterThanEqualToGreater() {
        val tree = BinopNode(CharNode("'e'"), CharNode("'a'"), Utils.Binop.GREATER_EQUAL)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertEquals((optimisedTree as BoolNode).`val`, true)
    }

    @Test
    fun testCharGreaterThanEqualToEqual() {
        val tree = BinopNode(CharNode("'c'"), CharNode("'c'"), Utils.Binop.GREATER_EQUAL)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertEquals((optimisedTree as BoolNode).`val`, true)
    }

    @Test
    fun testCharLessThan() {
        val tree = BinopNode(CharNode("'e'"), CharNode("'c'"), Utils.Binop.LESS)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertEquals((optimisedTree as BoolNode).`val`, false)
    }

    @Test
    fun testCharLessThanEqualToLess() {
        val tree = BinopNode(CharNode("'f'"), CharNode("'j'"), Utils.Binop.LESS_EQUAL)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertEquals((optimisedTree as BoolNode).`val`, true)
    }

    @Test
    fun testCharLessThanEqualToEqual() {
        val tree = BinopNode(CharNode("'f'"), CharNode("'f'"), Utils.Binop.LESS_EQUAL)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertEquals((optimisedTree as BoolNode).`val`, true)
    }

    @Test
    fun testCharEqual() {
        val tree = BinopNode(CharNode("'c'"), CharNode("'c'"), Utils.Binop.EQUAL)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertEquals((optimisedTree as BoolNode).`val`, true)
    }

    @Test
    fun testCharUnequal() {
        val tree = BinopNode(CharNode("'a'"), CharNode("'c'"), Utils.Binop.INEQUAL)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertEquals((optimisedTree as BoolNode).`val`, true)
    }

    /***************************************/
    /* Bool Expressions operations tests   */
    /***************************************/

    @Test
    fun testBoolAndTrue() {
        val tree = BinopNode(BoolNode(true), BoolNode(true), Utils.Binop.AND)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertEquals((optimisedTree as BoolNode).`val`, true)
    }

    @Test
    fun testBoolAndFalse() {
        val tree = BinopNode(BoolNode(true), BoolNode(false), Utils.Binop.AND)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertEquals((optimisedTree as BoolNode).`val`, false)
    }

    @Test
    fun testBoolOrTrue() {
        val tree = BinopNode(BoolNode(true), BoolNode(false), Utils.Binop.OR)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertEquals((optimisedTree as BoolNode).`val`, true)
    }

    @Test
    fun testBoolOrFalse() {
        val tree = BinopNode(BoolNode(false), BoolNode(false), Utils.Binop.OR)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertEquals((optimisedTree as BoolNode).`val`, false)
    }

    @Test
    fun testBoolNested() {
        val tree = BinopNode(
                BinopNode(BoolNode(false), BoolNode(true), Utils.Binop.AND),
                BinopNode(BoolNode(true), BoolNode(true), Utils.Binop.AND),
                Utils.Binop.OR)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertEquals((optimisedTree as BoolNode).`val`, true)
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
        assertEquals((optimisedTree as BoolNode).`val`, true)
    }

    @Test
    fun testBoolEqual() {
        val tree = BinopNode(BoolNode(false), BoolNode(false), Utils.Binop.EQUAL)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertEquals((optimisedTree as BoolNode).`val`, true)
    }

    @Test
    fun testBoolUnequal() {
        val tree = BinopNode(BoolNode(false), BoolNode(true), Utils.Binop.INEQUAL)
        val optimisedTree = optimiser.visitBinopNode(tree)
        assertEquals((optimisedTree as BoolNode).`val`, true)
    }

    /***************************************/
    /* Unop Expressions tests              */
    /***************************************/

    @Test
    fun testNot() {
        val tree = UnopNode(BoolNode(false), Utils.Unop.NOT)
        val optimisedTree = optimiser.visitUnopNode(tree)
        assertEquals((optimisedTree as BoolNode).`val`, true)
    }

    @Test
    fun testMinus() {
        val tree = UnopNode(IntNode(5), Utils.Unop.MINUS)
        val optimisedTree = optimiser.visitUnopNode(tree)
        assertEquals((optimisedTree as IntNode).value, -5)
    }

    @Test
    fun testOrd() {
        val tree = UnopNode(CharNode("'c'"), Utils.Unop.ORD)
        val optimisedTree = optimiser.visitUnopNode(tree)
        assertEquals((optimisedTree as IntNode).value, 99)
    }

    @Test
    fun testChr() {
        val tree = UnopNode(IntNode(99), Utils.Unop.CHR)
        val optimisedTree = optimiser.visitUnopNode(tree)
        assertEquals((optimisedTree as CharNode).char, 'c')
    }

    @Test
    fun testOrdChrInverse() {
        val tree = UnopNode(UnopNode(IntNode(110), Utils.Unop.CHR), Utils.Unop.ORD)
        val optimisedTree = optimiser.visitUnopNode(tree)
        assertEquals((optimisedTree as IntNode).value, 110)
    }

    @Test
    fun testChrOrdInverse() {
        val tree = UnopNode(UnopNode(CharNode("'f'"), Utils.Unop.ORD),Utils.Unop.CHR)
        val optimisedTree = optimiser.visitUnopNode(tree)
        assertEquals((optimisedTree as CharNode).char, 'f')
    }

    @Test
    fun testLen() {
        optimiserO3.currSymbolTable = SymbolTable(null)
        optimiserO3.currSymbolTable!!.add("x", ArrayNode(BasicType(BasicTypeEnum.INTEGER),
            mutableListOf(IntNode(1), IntNode(2), IntNode(3)), 3))
        val tree = UnopNode(IdentNode(ArrayType(BasicType(BasicTypeEnum.INTEGER)),"x"), Utils.Unop.LEN)
        val optimisedTree = optimiserO3.visitUnopNode(tree)
        assertEquals((optimisedTree as IntNode).value, 3)
    }

    /***************************************/
    /* Other expressions tests             */
    /***************************************/

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
        assertIs<IntNode>((stat as ReturnNode).expr)
    }

    @Test
    fun testSequence() {
        val tree = SequenceNode(DeclareStatNode("x",
                    BinopNode(IntNode(3), IntNode(5), Utils.Binop.PLUS)),
                ReturnNode(BinopNode(IntNode(3), IntNode(5), Utils.Binop.PLUS)))
        optimiser.visitSequenceNode(tree)
        assertIs<IntNode>((tree.body[0] as DeclareStatNode).rhs)
        assertIs<IntNode>((tree.body[1] as ReturnNode).expr)
    }

    /***************************************/
    /* Other node tests                    */
    /***************************************/

    @Test
    fun testFunc() {
        val tree = FuncNode("x", BasicType(BasicTypeEnum.INTEGER),
                ReturnNode(BinopNode(IntNode(3), IntNode(5),
                        Utils.Binop.PLUS)), listOf())
        optimiser.visitFuncNode(tree)
        val stat = tree.functionBody
        assertIs<IntNode>((stat as ReturnNode).expr)
    }

    @Test
    fun testProgramFunctions() {
        val functions = mutableMapOf(
                "x" to FuncNode("x", BasicType(BasicTypeEnum.INTEGER),
                ReturnNode(BinopNode(IntNode(3), IntNode(5),
                        Utils.Binop.PLUS)),
                listOf())
        )
        val body = SkipNode()
        val tree = ProgramNode(functions, body)
        optimiser.visitProgramNode(tree)
        assertIs<IntNode>((tree.functions["x"]?.functionBody
                as ReturnNode).expr)
    }

    @Test
    fun testProgramBody() {
        val functions = mutableMapOf<String, FuncNode>()
        val body = ReturnNode(BinopNode(IntNode(3), IntNode(5),
                Utils.Binop.PLUS))
        val tree = ProgramNode(functions, body)
        optimiser.visitProgramNode(tree)
        assertIs<IntNode>((tree.body as ReturnNode).expr)
    }

    /***************************************/
    /* Constant propagation tests          */
    /***************************************/

    @Test
    fun testIdentReplacement() {
        optimiserO3.currSymbolTable = SymbolTable(null)
        optimiserO3.currSymbolTable!!.add("x", IntNode(5))
        val tree = IdentNode(BasicType(BasicTypeEnum.INTEGER),"x")
        val newTree = optimiserO3.visitIdentNode(tree)
        assertEquals((newTree as IntNode).value, 5)
    }

    @Test
    fun testIdentReplacementInEquation() {
        optimiserO3.currSymbolTable = SymbolTable(null)
        optimiserO3.currSymbolTable!!.add("x", IntNode(5))
        val tree = BinopNode(IdentNode(BasicType(BasicTypeEnum.INTEGER),"x"),
                IntNode(5), Utils.Binop.PLUS)
        val newTree = optimiserO3.visitBinopNode(tree)
        assertEquals((newTree as IntNode).value, 10)
    }

    @Test
    fun testIdentEquationReplacement() {
        optimiserO3.currSymbolTable = SymbolTable(null)
        optimiserO3.currSymbolTable!!.add("x",
            BinopNode(IntNode(2), IntNode(1), Utils.Binop.PLUS))
        val tree = BinopNode(IdentNode(BasicType(BasicTypeEnum.INTEGER),"x"),
                IntNode(5), Utils.Binop.PLUS)
        val newTree = optimiserO3.visitBinopNode(tree)
        assertEquals((newTree as IntNode).value, 8)
    }

    @Test
    fun testArrayElemEvaluated() {
        val array = ArrayNode(BasicType(BasicTypeEnum.INTEGER),
                mutableListOf(IntNode(1), IntNode(2), IntNode(3)), 3)
        val tree = ArrayElemNode("x", array, mutableListOf(IntNode(1)),
            BasicType(BasicTypeEnum.INTEGER))
        val newTree = optimiserO3.visitArrayElemNode(tree)
        assertEquals((newTree as IntNode).value, 2)
    }

    @Test
    fun testArrayDepthTwoElemEvaluated() {
        val array = ArrayNode(ArrayType(BasicType(BasicTypeEnum.INTEGER)),
                mutableListOf(ArrayNode(BasicType(BasicTypeEnum.INTEGER),
                        mutableListOf(IntNode(1), IntNode(2)), 2),
                        ArrayNode(BasicType(BasicTypeEnum.INTEGER),
                                mutableListOf(IntNode(3), IntNode(4)), 2)), 2)
        val tree = ArrayElemNode("x", array, mutableListOf(IntNode(0), IntNode(1)),
                BasicType(BasicTypeEnum.INTEGER))
        val newTree = optimiserO3.visitArrayElemNode(tree)
        assertEquals((newTree as IntNode).value, 2)
    }

    /***************************************/
    /* CFA tests                           */
    /***************************************/

    @Test
    fun testIfTrue() {
        val tree = IfNode(BoolNode(true), DeclareStatNode("x", IntNode(5)),
                ReturnNode(IntNode(3)))
        val newTree = optimiserO3.visitIfNode(tree)
        assertIs<DeclareStatNode>(newTree)
    }

    @Test
    fun testO1OptimiserDoesNotOptimiserIfTrue() {
        val tree = IfNode(BoolNode(true), DeclareStatNode("x", IntNode(5)),
                ReturnNode(IntNode(3)))
        val newTree = optimiser.visitIfNode(tree)
        assertIsNot<DeclareStatNode>(newTree)
    }

    @Test
    fun testIfFalse() {
        val tree = IfNode(BoolNode(false), DeclareStatNode("x", IntNode(5)),
                ReturnNode(IntNode(3)))
        val newTree = optimiserO3.visitIfNode(tree)
        assertIs<ReturnNode>(newTree)
    }

    @Test
    fun testWhileFalse() {
        val tree = WhileNode(BoolNode(false), ReturnNode(IntNode(3)))
        val newTree = optimiserO3.visitWhileNode(tree)
        assertIs<SkipNode>(newTree)
    }

    /***************************************/
    /* Runtime error tests                 */
    /***************************************/

    @Test
    fun testArrayElemOutOfBounds() {
        val array = ArrayNode(BasicType(BasicTypeEnum.INTEGER),
                mutableListOf(IntNode(1), IntNode(2), IntNode(3)), 3)
        val tree = ArrayElemNode("x", array, mutableListOf(IntNode(3)),
                BasicType(BasicTypeEnum.INTEGER))
        val newTree = optimiserO3.visitArrayElemNode(tree)
        assertNull(newTree)
    }

    @Test
    fun testArrayElemNegOutOfBounds() {
        val array = ArrayNode(BasicType(BasicTypeEnum.INTEGER),
                mutableListOf(IntNode(1), IntNode(2), IntNode(3)), 3)
        val tree = ArrayElemNode("x", array, mutableListOf(IntNode(-1)),
                BasicType(BasicTypeEnum.INTEGER))
        val newTree = optimiserO3.visitArrayElemNode(tree)
        assertNull(newTree)
    }

    @Test
    fun testDivideByZero() {
        val tree = BinopNode(IntNode(5), IntNode(0), Utils.Binop.DIV)
        val newTree = optimiser.visitBinopNode(tree)
        assertNull(newTree)
    }

    @Test
    fun testAddOverflow() {
        val tree = BinopNode(IntNode(2_000_000_000), IntNode(2_000_000_000), Utils.Binop.PLUS)
        val newTree = optimiser.visitBinopNode(tree)
        assertNull(newTree)
    }

    @Test
    fun testMulOverflow() {
        val tree = BinopNode(IntNode(2_000_000_000), IntNode(2_000_000_000), Utils.Binop.MUL)
        val newTree = optimiser.visitBinopNode(tree)
        assertNull(newTree)
    }

    @Test
    fun testSubtractUnderflow() {
        val tree = BinopNode(IntNode(-2_000_000_000), IntNode(2_000_000_000), Utils.Binop.MINUS)
        val newTree = optimiser.visitBinopNode(tree)
        assertNull(newTree)
    }
}