import node.FuncNode
import node.ParamNode
import node.ProgramNode
import node.expr.*
import node.stat.*
import type.*

class WACCCodeGeneratorVisitor(val representation: WACCAssembleRepresentation) {
    private val availableRegister
            = mutableListOf<Register>(Register.R4,
                Register.R5, Register.R6, Register.R7, Register.R8, Register.R9,
                Register.R10, Register.R11)

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

        if (representation.hasPrintStringFunc() || representation.hasPrintThrowOverflowErrorFunc() || representation.hasPrintDivByZeroErrorFunc()) {
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

        if (representation.hasPrintThrowOverflowErrorFunc()) {
            generateThrowOverflowError()
        }

        if (representation.hasPrintDivByZeroErrorFunc()) {
            generateDivideByZeroError()
        }

        if (representation.hasPrintDivByZeroErrorFunc() || representation.hasPrintThrowOverflowErrorFunc()) {
            generateThrowRuntimeError()
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
    private fun visitDeclareStatNode(node: DeclareStatNode) {
        visitExprNode(node.rhs!!)
    }

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
    /* To implement expression evaluation using using
        registers defined in availableRegisters. If availabelRegister is left with 1
        register, resort to using accumulator.
        CONTRACT: result of evaluated expression will be on nextAvailableRegister
     */

    private fun visitArrayElemNode(node: ArrayElemNode) {}
    private fun visitArrayNode(node: ArrayNode) {}
    private fun visitBinopNode(node: BinopNode) {
        when (node.operator) {
            Utils.Binop.PLUS -> {
                val add = {l:Register,r:Register -> representation.addCode("\tADD $l, $l, $r");
                    representation.addCode("\tBLVS p_throw_overflow_error")}
                binOpAlgo2(node.expr1, node.expr2, add)
                representation.addPrintThrowErrorOverflowFunc()
            }
            Utils.Binop.MINUS -> {
                val sub = {l:Register,r:Register -> representation.addCode("\tSUB $l, $l, $r");
                    representation.addCode("\tBLVS p_throw_overflow_error")}
                binOpAlgo2(node.expr1, node.expr2, sub)
                representation.addPrintThrowErrorOverflowFunc()
            }
            Utils.Binop.MUL -> {
                val mult = {l:Register,r:Register -> representation.addCode("\tSMULL $l, $r, $l, $r");
                        representation.addCode("\tCMP $r, $l, ASR #31");
                        representation.addCode("\tBLNE p_throw_overflow_error")}
                binOpAlgo2(node.expr1, node.expr2, mult)
                representation.addPrintThrowErrorOverflowFunc()
            }
            Utils.Binop.DIV -> {
                val div = {l:Register,r:Register -> representation.addCode("\tMOV r0, $l");
                    representation.addCode("\tMOV r1, $r");
                    representation.addCode("\tBL p_check_divide_by_zero")
                    representation.addCode("\tBL __aeabi_idiv")
                    representation.addCode("\tMOV $l, r0")
                }
                binOpAlgo2(node.expr1, node.expr2, div)
                representation.addPrintDivByZeroFunc()
            }
            Utils.Binop.MOD -> {
                val mod = {l:Register,r:Register -> representation.addCode("\tMOV r0, $l");
                    representation.addCode("\tMOV r1, $r");
                    representation.addCode("\tBL p_check_divide_by_zero")
                    representation.addCode("\tBL __aeabi_imod")
                    representation.addCode("\tMOV $l, r0")
                }
                binOpAlgo2(node.expr1, node.expr2, mod)
                representation.addPrintDivByZeroFunc()
            }
            Utils.Binop.GREATER -> {
                val gt = {l:Register,r:Register -> representation.addCode("\tCMP $l, $r");
                    representation.addCode("\tMOVGT $l, #1");
                    representation.addCode("\tMOVLE $l, #0");
                }
                binOpAlgo2(node.expr1, node.expr2, gt)
            }
            Utils.Binop.GREATER_EQUAL -> {
                val ge = {l:Register,r:Register -> representation.addCode("\tCMP $l, $r");
                    representation.addCode("\tMOVGE $l, #1");
                    representation.addCode("\tMOVLT $l, #0");
                }
                binOpAlgo2(node.expr1, node.expr2, ge)
            }
            Utils.Binop.LESS -> {
                val gt = {l:Register,r:Register -> representation.addCode("\tCMP $l, $r");
                    representation.addCode("\tMOVLT $l, #1");
                    representation.addCode("\tMOVGE $l, #0");
                }
                binOpAlgo2(node.expr1, node.expr2, gt)
            }
            Utils.Binop.LESS_EQUAL -> {
                val gt = {l:Register,r:Register -> representation.addCode("\tCMP $l, $r");
                    representation.addCode("\tMOVLE $l, #1");
                    representation.addCode("\tMOVGT $l, #0");
                }
                binOpAlgo2(node.expr1, node.expr2, gt)
            }
            Utils.Binop.EQUAL -> {
                val gt = {l:Register,r:Register -> representation.addCode("\tCMP $l, $r");
                    representation.addCode("\tMOVEQ $l, #1");
                    representation.addCode("\tMOVNE $l, #0");
                }
                binOpAlgo2(node.expr1, node.expr2, gt)
            }
            Utils.Binop.INEQUAL -> {
                val gt = {l:Register,r:Register -> representation.addCode("\tCMP $l, $r");
                    representation.addCode("\tMOVNE $l, #1");
                    representation.addCode("\tMOVEQ $l, #0");
                }
                binOpAlgo2(node.expr1, node.expr2, gt)
            }
            Utils.Binop.AND -> {
                val add = {l:Register,r:Register -> representation.addCode("\tAND $l, $l, $r")}
                binOpAlgo2(node.expr1, node.expr2, add)
            }
            Utils.Binop.OR -> {
                val add = {l:Register,r:Register -> representation.addCode("\tOR $l, $l, $r")}
                binOpAlgo2(node.expr1, node.expr2, add)
            }
        }
    }


    private fun visitBoolNode(node: BoolNode) {
        val value = if (node.`val`) 1 else 0
        val dest = nextAvailableRegister()
        representation.addCode("\tMOV ${dest.name}, #$value")
        freeRegister(dest)
    }

    private fun visitCharNode(node: CharNode) {
        representation.addCode("\tMOV r4, #'${node.char}'")
    }
    private fun visitFunctionCallNode(node: FunctionCallNode) {}
    private fun visitIdentNode(node: IdentNode) {}
    private fun visitIntNode(node: IntNode) {
        val dest = nextAvailableRegister()
        representation.addCode("\tLDR ${dest.name}, =${node.value}")
        freeRegister(dest)
    }
    private fun visitPairElemNode(node: PairElemNode) {

    }
    private fun visitPairNode(node: PairNode) {

    }

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

    private fun generateThrowOverflowError() {
        val code = representation.addStringToTable("\"OverflowError: the result is too small/large to store in a 4-byte signed-integer.\\n\\0\"", 83)
        representation.addCode("p_throw_overflow_error:")
        representation.addCode("\tLDR r0, =msg_$code")
        representation.addCode("\tBL p_throw_runtime_error")
    }

    private fun generateThrowRuntimeError() {
        representation.addCode("p_throw_runtime_error:")
        representation.addCode("\tBL p_print_string")
        representation.addCode("\tMOV r0, #-1")
        representation.addCode("\tBL exit")
    }

    private fun generateDivideByZeroError() {
        val code = representation.addStringToTable("\"DivideByZeroError: divide or modulo by zero\\n\\0\"", 45)
        representation.addCode("p_check_divide_by_zero:")
        representation.addCode("\tPUSH {lr}")
        representation.addCode("\tCMP r1, #0")
        representation.addCode("\tLDREQ r0, =msg_$code")
        representation.addCode("\tBLEQ p_throw_runtime_error")
        representation.addCode("\tPOP {pc}")
    }



    /* =======================================================
     *             Register Allocation Helper
     * =======================================================
     */
    private fun nextAvailableRegister(): Register {
        return availableRegister.removeAt(0)
    }

    private fun freeRegister(reg: Register) {
        availableRegister.add(0, reg)
    }

    private fun numberOfAvailableRegisters(): Int {
        return availableRegister.count()
    }


    private fun binOpAlgo2(expr1: ExprNode, expr2: ExprNode, op: (lexpr: Register, rexpr: Register) -> Unit) {
        if (numberOfAvailableRegisters() > 2) {
            visitExprNode(expr1)
            val dest1 = nextAvailableRegister()
            visitExprNode(expr2)
            val dest2 = nextAvailableRegister()
            op(dest1, dest2)
            freeRegister(dest2)
            freeRegister(dest1)
        } else {
            visitExprNode(expr1)
            var dest = nextAvailableRegister()
            representation.addCode("\tPUSH {${dest.name}}")
            freeRegister(dest)

            visitExprNode(expr2)
            dest = nextAvailableRegister()
            val lastReg = nextAvailableRegister()
            representation.addCode("\tPOP {${lastReg.name}}")
            op(dest, lastReg)
            freeRegister(lastReg)
            freeRegister(dest)
        }
    }

}

enum class Register(val asRep: String){
    R0("r0"), R1("r1"), R2("r2"), R3("r3"), R4("r4"), R5("r5"),
    R6("r6"), R7("r7"), R8("r8"), R9("r9"), R10("r10"),
    R11("r11"), R12("r12"), LR("r13"), SP("r14"), PC("pc")
}