import node.FuncNode
import node.ParamNode
import node.ProgramNode
import node.expr.*
import node.stat.*
import type.*

class WACCCodeGeneratorVisitor(val representation: WACCAssembleRepresentation) {

    fun visitProgramNode(node: ProgramNode) {
        representation.addCode(".global main")
        node.functions.forEach{ident, func ->
            representation.addCode("f_$ident:")
            visitFuncNode(func)
        }

        representation.addCode("main:")
        representation.addCode("\tPUSH {lr}")
        visitStatNode(node.body)
        representation.addCode("\tLDR r0, =0")
        representation.addCode("\tPOP {pc}")

        if (representation.hasPrintStringFunc()) {
            generatePrintStringCode()
        }
        if (representation.hasPrintInFunc()) {
            generatePrintlnCode()
        }
        if (representation.hasPrintBoolFunc()) {
            generatePrintBoolCode()
        }
        if (representation.hasPrintIntFunc()) {
            generatePrintIntCode()
        }
    }

    private fun visitFuncNode(node: FuncNode) {

    }

    private fun visitParamNode(node: ParamNode) {}

    private fun visitStatNode(node: StatNode) {
        when (node) {
            is AssignNode -> visitAssignNode(node)
            is DeclareStatNode -> visitDeclareStatNode(node)
            is ExitNode -> visitExitNode(node)
            is FreeNode -> visitFreeNode(node)
            is IfNode -> visitIfNode(node)
            is PrintlnNode -> visitPrintlnNode(node)
            is PrintNode -> visitPrintNode(node)
            is ReadNode -> visitReadNode(node)
            is ReturnNode -> visitReturnNode(node)
            is ScopeNode -> visitScopeNode(node)
            is SequenceNode -> visitSequenceNode(node)
            is SkipNode -> visitSkipNode(node)
            is WhileNode -> visitWhileNode(node)
        }
    }

    private fun visitExprNode(node: ExprNode) {
        when (node) {
            is ArrayElemNode -> visitArrayElemNode(node)
            is ArrayNode -> visitArrayNode(node)
            is BinopNode -> visitBinopNode(node)
            is BoolNode -> visitBoolNode(node)
            is CharNode -> visitCharNode(node)
            is FunctionCallNode -> visitFunctionCallNode(node)
            is IdentNode -> visitIdentNode(node)
            is IntNode -> visitIntNode(node)
            is PairElemNode -> visitPairElemNode(node)
            is PairNode -> visitPairNode(node)
            is StringNode -> visitStringNode(node)
            is UnopNode -> visitUnopNode(node)
        }
    }

    /* =========================================================
    *                   Statement Visitors
    * =========================================================
    */

    private fun visitAssignNode(node: AssignNode) {

    }
    private fun visitDeclareStatNode(node: DeclareStatNode) {}

    private fun visitExitNode(node: ExitNode) {
        visitExprNode(node.exitCode) // CONTRACT: MUST END UP IN R4
        representation.addCode("\tMOV r0, r4")
        representation.addCode("\tBL exit")
    }

    private fun visitFreeNode(node: FreeNode) {}
    private fun visitIfNode(node: IfNode) {}
    private fun visitPrintlnNode(node: PrintlnNode) {
        visitPrintNode(PrintNode(node.expr))
        representation.addCode("\tBL p_print_ln")
        representation.addPrintInFunc()
    }

    private fun visitPrintNode(node: PrintNode) {
        visitExprNode(node.expr!!)
        representation.addCode("\tMOV r0, r4")

        when (val type = node.expr.type!!) {
            is ArrayType -> printArrayType(node.expr)
            is BasicType -> when (type.typeEnum) {
                                BasicTypeEnum.INTEGER -> {
                                    representation.addCode("\tBL p_print_int")
                                    representation.addPrintIntFunc()
                                }
                                BasicTypeEnum.BOOLEAN -> {
                                    println("<1>")
                                    representation.addCode("\tBL p_print_bool")
                                    representation.addPrintBoolFunc()
                                }
                                BasicTypeEnum.CHAR -> {
                                    representation.addCode("\tBL putchar")
                                }
                                BasicTypeEnum.STRING -> {
                                    representation.addCode("\tBL p_print_string")
                                    representation.addPrintStringFunc()
                                }
                            }
            is PairType -> {}
        }

    }

    private fun visitReadNode(node: ReadNode) {}
    private fun visitReturnNode(node: ReturnNode) {}

    private fun visitScopeNode(node: ScopeNode) {
        node.body.forEach{stat -> visitStatNode(stat)}
    }

    private fun visitSequenceNode(node: SequenceNode) {
        node.body.forEach{stat -> visitStatNode(stat)}
    }

    private fun visitSkipNode(node: SkipNode) {
        return
    }
    private fun visitWhileNode(node: WhileNode) {}

    /* =======================================================
     *                  Expression Visitors
     * =======================================================
     */

    private fun visitArrayElemNode(node: ArrayElemNode) {}
    private fun visitArrayNode(node: ArrayNode) {}
    private fun visitBinopNode(node: BinopNode) {}
    private fun visitBoolNode(node: BoolNode) {}
    private fun visitCharNode(node: CharNode) {
        representation.addCode("\tMOV r4, #'${node.char}'")
    }
    private fun visitFunctionCallNode(node: FunctionCallNode) {}
    private fun visitIdentNode(node: IdentNode) {}
    private fun visitIntNode(node: IntNode) {
        representation.addCode("\tLDR r4, =${node.value}")
    }
    private fun visitPairElemNode(node: PairElemNode) {}
    private fun visitPairNode(node: PairNode) {}

    private fun visitStringNode(node: StringNode) {
        val msgInt = representation.addStringToTable(node.string, node.length - 2)
        representation.addCode("\tLDR r4, =msg_$msgInt")
    }

    private fun visitUnopNode(node: UnopNode) {}

    /* =======================================================
     *                  Print Helper
     * =======================================================
     */

    private fun printArrayType(expr: ExprNode) {

    }

    private fun printIntegerType(expr: ExprNode) {

    }

    private fun printBooleanType(expr: ExprNode) {

    }

    private fun printCharType(expr: ExprNode) {

    }

    private fun generatePrintStringCode() {
        val code = representation.addStringToTable("\"%.*s\\0\"", 5)
        representation.addCode("p_print_string:")
        representation.addCode("\tPUSH {lr}")
        representation.addCode("\tLDR r1, [r0]")
        representation.addCode("\tADD r2, r0, #4")
        representation.addCode("\tLDR r0, =msg_$code")
        representation.addCode("\tADD r0, r0, #4")
        representation.addCode("\tBL printf")
        representation.addCode("\tMOV r0, #0")
        representation.addCode("\tBL fflush")
        representation.addCode("\tPOP {pc}")
    }


    private fun generatePrintlnCode() {
        val code = representation.addStringToTable("\"\\0\"", 1)
        representation.addCode("p_print_ln:")
        representation.addCode("\tPUSH {lr}")
        representation.addCode("\tLDR r0, =msg_$code")
        representation.addCode("\tADD r0, r0, #4")
        representation.addCode("\tBL puts")
        representation.addCode("\tMOV r0, #0")
        representation.addCode("\tBL fflush")
        representation.addCode("\tPOP {pc}")
    }

    private fun generatePrintBoolCode() {
        val code1 = representation.addStringToTable("\"true\\0\"", 5)
        val code2 = representation.addStringToTable("\"false\\0\"", 6)
        representation.addCode("p_print_bool:")
        representation.addCode("\tPUSH {lr}")
        representation.addCode("\tCMP r0, #0")
        representation.addCode("\tLDRNE r0, =msg_$code1")
        representation.addCode("\tLDREQ r0, =msg_$code2")
        representation.addCode("\tADD r0, r0, #4")
        representation.addCode("\tBL printf")
        representation.addCode("\tMOV r0, #0")
        representation.addCode("\tBL fflush")
        representation.addCode("\tPOP {pc}")
    }

    private fun generatePrintIntCode() {
        val code = representation.addStringToTable("\"%d\\0\"", 3)
        representation.addCode("p_print_int:")
        representation.addCode("\tPUSH {lr}")
        representation.addCode("\tMOV r1, r0")
        representation.addCode("\tLDR r0, =msg_$code")
        representation.addCode("\tADD r0, r0, #4")
        representation.addCode("\tBL printf")
        representation.addCode("\tMOV r0, #0")
        representation.addCode("\tBL fflush")
        representation.addCode("\tPOP {pc}")


    }



}