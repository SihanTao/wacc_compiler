import com.sun.source.tree.Scope
import node.FuncNode
import node.ProgramNode
import node.expr.*
import node.stat.*
import type.*

class WACCCodeGeneratorVisitor(val representation: WACCAssembleRepresentation) {
    private val availableRegister
            = mutableListOf<Register>(Register.R4,
                Register.R5, Register.R6, Register.R7, Register.R8, Register.R9,
                Register.R10, Register.R11)
    private var symbolTable: SymbolTable<Pair<Int, Int>>? = null
    private var currStackOffset = 0
    private var currScopeDepth = 0
    private var labelCounter = 0

    fun visitProgramNode(node: ProgramNode) {
        representation.addCode(".global main")
        node.functions.forEach{ident, func ->
            representation.addCode("f_$ident:")
            visitFuncNode(func)
        }

        representation.addCode("main:")
        representation.addCode("\tPUSH {lr}")
        visitStatNode(node.body, 0)
        representation.addCode("\tLDR r0, =0")
        representation.addCode("\tPOP {pc}")

        if (representation.hasPrintStringFunc() || representation.hasPrintThrowOverflowErrorFunc()
                || representation.hasPrintDivByZeroErrorFunc() || representation.hasCheckArrayBoundsFunc()
                || representation.hasCheckNullPointerFunc()) {
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

        if (representation.hasPrintDivByZeroErrorFunc() || representation.hasPrintThrowOverflowErrorFunc()
                || representation.hasCheckArrayBoundsFunc() || representation.hasCheckNullPointerFunc()
                || representation.hasFreePairFunc()) {
            generateThrowRuntimeError()
        }

        if (representation.hasCheckArrayBoundsFunc()) {
            generateCheckArrayBoundFunc()
        }

        if (representation.hasReadIntFunc()) {
            generateReadInt()
        }

        if (representation.hasReadCharFunc()) {
            generateReadChar()
        }

        if (representation.hasPrintRefFunction()) {
            generatePrintRefFunc()
        }

        if (representation.hasCheckNullPointerFunc()) {
            generateCheckNullPointerFunc()
        }

        if (representation.hasFreePairFunc()) {
            generateFreePairFunc()
        }
    }


    private fun visitFuncNode(node: FuncNode) {
        symbolTable = SymbolTable(null)
        if (node.paramList != null) {
            var offset = 0
            for (param in node.paramList!!) {
                val p = param as IdentNode
                symbolTable!!.add(p.name, Pair(offset, 0))
                offset += typeSize(p.type!!)
            }
        }
        representation.addCode("\tPUSH {lr}")
        symbolTable = SymbolTable(symbolTable)
        currScopeDepth ++
        symbolTable!!.add("#localVariableNo_$currScopeDepth", Pair(4, currScopeDepth))
        visitScopeNode(ScopeNode(node.functionBody!!), 0)
        representation.addCode("\tPOP {pc}")
        currScopeDepth --
    }

    private fun visitStatNode(node: StatNode, incStack:Int) {
        when (node) {
            is AssignNode -> visitAssignNode(node, incStack)
            is DeclareStatNode -> visitDeclareStatNode(node, incStack)
            is ExitNode -> visitExitNode(node, incStack)
            is FreeNode -> visitFreeNode(node, incStack)
            is IfNode -> visitIfNode(node, incStack)
            is PrintlnNode -> visitPrintlnNode(node, incStack)
            is PrintNode -> visitPrintNode(node, incStack)
            is ReadNode -> visitReadNode(node, incStack)
            is ReturnNode -> visitReturnNode(node, incStack)
            is ScopeNode -> visitScopeNode(node, incStack)
            is SequenceNode -> visitSequenceNode(node, incStack)
            is SkipNode -> visitSkipNode(node, incStack)
            is WhileNode -> visitWhileNode(node, incStack)
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

    private fun visitAssignNode(node: AssignNode, incStack: Int) {
        visitExprNode(node.rhs!!)
        when (node.lhs!!) {
            is IdentNode -> {
                val ident = node.lhs as IdentNode
                val res = symbolTable!!.lookupAll(ident.name)!!
                val destScopeDepth = res.second
                var offset = res.first
                for (i in currScopeDepth downTo destScopeDepth + 1) {
                    offset += symbolTable!!.lookupAll("#localVariableNo_$i")!!.first
                }
                val opcode = if (typeSize(node.rhs!!.type!!) == 1) "STRB" else "STR"
                val operand = if (offset == 0) "[sp]" else "[sp, #$offset]"
                representation.addCode("\t" + opcode + " ${availableRegister[0].name}, " + operand)
            }
            is ArrayElemNode -> {
                val expr = nextAvailableRegister()
                val reg = nextAvailableRegister()
                val ident = (node.lhs as ArrayElemNode).arrayIdent
                val res = symbolTable!!.lookupAll(ident)!!
                val destScopeDepth = res.second
                var offset = res.first
                for (i in currScopeDepth downTo destScopeDepth + 1) {
                    offset += symbolTable!!.lookupAll("#localVariableNo_$i")!!.first
                }
                representation.addCode("\tADD ${reg.name}, sp, #${offset}")

                for (index in node.lhs.index) {
                    visitExprNode(index)
                    representation.addCode("\tLDR ${reg.name}, [${reg.name}]")
                    representation.addCode("\tMOV r0, ${availableRegister[0]}")
                    representation.addCode("\tMOV r1, ${reg.name}")
                    representation.addCode("\tBL p_check_array_bounds")
                    representation.addCode("\tADD ${reg.name}, ${reg.name}, #4")
                    representation.addCode("\tADD ${reg.name}, ${reg.name}, ${availableRegister[0]}, LSL #${log2(typeSize(node.lhs.type!!))}")
                }
                val opcode = if (typeSize(node.rhs!!.type!!) == 1) "STRB" else "STR"
                representation.addCode("\t$opcode ${expr.name}, [${reg.name}]")
                representation.addCheckErrorBoundsFunc()
                freeRegister(reg)
                freeRegister(expr)
            }
            is PairElemNode -> {
                val expr = nextAvailableRegister()
                visitExprNode((node.lhs as PairElemNode).pair)
                representation.addCode("\tMOV r0, ${availableRegister[0]}")
                representation.addCode("\tBL p_check_null_pointer")
                var type: Type
                if (node.lhs.isFirst) {
                    representation.addCode("\tLDR ${availableRegister[0]}, [${availableRegister[0]}]")
                    type = node.lhs.type!!
                } else {
                    representation.addCode("\tLDR ${availableRegister[0]}, [${availableRegister[0]}, #4]")
                    type = node.lhs.type!!
                }
                val opcode = if (typeSize(type)==1) "STRB" else "STR"
                representation.addCode("\t$opcode ${expr.name}, [${availableRegister[0]}]")
                representation.addCheckNullPointerError()
                freeRegister(expr)
            }
        }
    }
    private fun visitDeclareStatNode(node: DeclareStatNode, incStack: Int) {
        visitExprNode(node.rhs!!)
        val pos = currStackOffset - typeSize(node.rhs!!.type!!)
        symbolTable!!.add(node.identifier, Pair(pos, currScopeDepth))
        val opcode = if (typeSize(node.rhs!!.type!!) == 1) "STRB" else "STR"
        val operand = if (pos == 0) "[sp]" else "[sp, #$pos]"
        representation.addCode("\t" + opcode + " ${availableRegister[0].name}, " + operand)
        currStackOffset -= typeSize(node.rhs!!.type!!)
    }

    private fun visitExitNode(node: ExitNode, incStack: Int) {
        visitExprNode(node.exitCode) // CONTRACT: MUST END UP IN R4
        representation.addCode("\tMOV r0, r4")
        representation.addCode("\tBL exit")
    }

    private fun visitFreeNode(node: FreeNode, incStack: Int) {
        visitExprNode(node.expr)
        representation.addCode("\tMOV r0, ${availableRegister[0]}")
        representation.addCode("\tBL p_free_pair")
        representation.addFreePairFunc()
    }

    private fun visitIfNode(node: IfNode, incStack: Int) {
        val elseLabel = (labelCounter)++
        val fiLabel = (labelCounter)++
        visitExprNode(node.condition)
        representation.addCode("\tCMP ${availableRegister[0]}, #0")
        representation.addCode("\tBEQ L${elseLabel}")
        visitStatNode(node.ifBody!!, incStack)
        representation.addCode("\tB L${fiLabel}")
        representation.addCode("L${elseLabel}:")
        visitStatNode(node.elseBody!!, incStack)
        representation.addCode("L${fiLabel}:")

    }
    private fun visitPrintlnNode(node: PrintlnNode, incStack: Int) {
        visitPrintNode(PrintNode(node.expr), incStack)
        representation.addCode("\tBL p_print_ln")
        representation.addPrintInFunc()
    }

    private fun visitPrintNode(node: PrintNode, incStack: Int) {
        visitExprNode(node.expr!!)
        representation.addCode("\tMOV r0, r4")

        when (val type = node.expr.type!!) {
            is ArrayType -> {
                if (type.getContentType() is BasicType && (type.getContentType() as BasicType).typeEnum == BasicTypeEnum.CHAR) {
                    representation.addCode("\tBL p_print_string")
                    representation.addPrintStringFunc()
                } else {
                    representation.addCode("\tBL p_print_reference")
                    representation.addPrintReference()
                }
            }

            is PairType -> {
                representation.addCode("\tBL p_print_reference")
                representation.addPrintReference()
            }
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
        }

    }

    private fun visitReadNode(node: ReadNode, incStack: Int) {
        val reg = nextAvailableRegister()
        var res: Pair<Int, Int> =
            when (node.inputExpr) {
                is IdentNode -> symbolTable!!.lookupAll(node.inputExpr.name)!!
                is ArrayElemNode -> Pair(-1, -1)
                is PairElemNode -> Pair(-1, -1)
                else -> Pair(-1, -1)
            }
//        val res = symbolTable!!.lookupAll(node.inputExpr)!!
        val destScopeDepth = res.second
        var offset = res.first
        for (i in currScopeDepth downTo (destScopeDepth + 1)) {
            offset += symbolTable!!.lookupAll("#localVariableNo_$i")!!.first
        }
        representation.addCode("\tADD ${reg}, sp, #$offset")
        representation.addCode("\tMOV r0, ${reg.name}")

        when (node.inputExpr.type!!) {
            is BasicType -> {
                val typeEnum = (node.inputExpr.type as BasicType).typeEnum
                when (typeEnum) {
                    BasicTypeEnum.INTEGER -> {representation.addCode("\tBL p_read_int");representation.addReadInt()}
                    BasicTypeEnum.CHAR -> {representation.addCode("\tBL p_read_char");representation.addReadCharFunc()}
                    else -> {}
                }
            }
        }
        representation.addReadInt()
        freeRegister(reg)
    }

    private fun visitReturnNode(node: ReturnNode, incStack: Int) {
        visitExprNode(node.expr)
        representation.addCode("\tMOV r0, ${availableRegister[0]}")
        var temp = 0
        var currST = symbolTable
        var i = currScopeDepth
        while (currST != null && i > 1) {
            temp += currST.lookupAll("#localVariableNo_$i")!!.first
            currST = currST.parentSymbolTable
            i--
        }
        if (temp > 0) representation.addCode("\tADD sp, sp, #$temp")
        representation.addCode("\tPOP {pc}")
    }

    private fun visitScopeNode(node: ScopeNode, incStack: Int) {
        var temp = 0
        if (node.body.isEmpty()) return
        if (node.body[0] is DeclareStatNode) {
            val dec = node.body[0] as DeclareStatNode
            temp+=typeSize(dec.rhs!!.type!!)
        } else if (node.body[0] is SequenceNode){
            val seq = node.body[0] as SequenceNode
            for (stat in seq.body) { if (stat is DeclareStatNode) temp+=typeSize(stat.rhs!!.type!!) }
        }
        val prevStackOffset = currStackOffset
        currStackOffset = temp
        symbolTable = SymbolTable(symbolTable)
        currScopeDepth += 1
        symbolTable!!.add("#localVariableNo_$currScopeDepth", Pair(temp, currScopeDepth))
        if (temp > 0) {
            var temp2 = temp
            while (temp2 > 1024) {
                representation.addCode("\tSUB sp, sp, #1024")
                temp2 -= 1024
            }
            if (temp2 > 0) representation.addCode("\tSUB sp, sp, #$temp2")
        }
        node.body.forEach{stat -> visitStatNode(stat, incStack = temp)}
        if (temp > 0) {
            var temp2 = temp
            while (temp2 > 1024) {
                representation.addCode("\tADD sp, sp, #1024")
                temp2 -= 1024
            }
            if (temp2 > 0) representation.addCode("\tADD sp, sp, #$temp2")
        }
        symbolTable = symbolTable!!.parentSymbolTable
        currScopeDepth -= 1
        currStackOffset = prevStackOffset
    }

    private fun visitSequenceNode(node: SequenceNode, incStack: Int) {
        node.body.forEach{stat -> visitStatNode(stat, incStack)}
    }

    private fun visitSkipNode(node: SkipNode, incStack: Int) {
        return
    }
    private fun visitWhileNode(node: WhileNode, incStack: Int) {
        val whileLabel = (labelCounter)++
        val doLabel = (labelCounter)++
        representation.addCode("\tB L${whileLabel}")
        representation.addCode("L${doLabel}:")
        visitStatNode(node.body, incStack)
        representation.addCode("L${whileLabel}:")
        visitExprNode(node.cond)
        representation.addCode("\tCMP ${availableRegister[0]}, #1")
        representation.addCode("\tBEQ L${doLabel}")
    }

    /* =======================================================
     *                  Expression Visitors
     * =======================================================
     */
    /* To implement expression evaluation using using
        registers defined in availableRegisters. If availabelRegister is left with 1
        register, resort to using accumulator.
        CONTRACT: result of evaluated expression will be on nextAvailableRegister
     */

    private fun visitArrayElemNode(node: ArrayElemNode) {
        val res = symbolTable!!.lookupAll(node.arrayIdent)!!
        val destScopeDepth = res.second
        var offset = res.first
        for (i in currScopeDepth downTo (destScopeDepth + 1)) {
            offset += symbolTable!!.lookupAll("#localVariableNo_$i")!!.first
        }
        val reg = nextAvailableRegister()
        representation.addCode("\tADD ${reg.name}, sp, #${offset}")

        for (index in node.index) {
            visitExprNode(index)
            representation.addCode("\tLDR ${reg.name}, [${reg.name}]")
            representation.addCode("\tMOV r0, ${availableRegister[0]}")
            representation.addCode("\tMOV r1, ${reg.name}")
            representation.addCode("\tBL p_check_array_bounds")
            representation.addCode("\tADD ${reg.name}, ${reg.name}, #4")
            representation.addCode("\tADD ${reg.name}, ${reg.name}, ${availableRegister[0]}, LSL #${log2(typeSize(node.type!!))}")
        }
        representation.addCode("\tLDR ${reg.name}, [${reg.name}]")
        freeRegister(reg)
        representation.addCheckErrorBoundsFunc()
    }

    private fun visitArrayNode(node: ArrayNode) {
        var arraySize = typeSize(BasicType(BasicTypeEnum.INTEGER))
        if (node.length > 0) {
            arraySize += node.length * typeSize(node.contentType!!)
        }
        representation.addCode("\tLDR r0, =$arraySize")
        representation.addCode("\tBL malloc")
        val arrayDest = nextAvailableRegister()
        representation.addCode("\tMOV ${arrayDest.name}, r0")
        var exprDest = 4
        for (expr in node.content) {
            visitExprNode(expr)
            val opcode = if (typeSize(node.contentType!!) == 1) "STRB" else "STR"
            representation.addCode("\t$opcode ${availableRegister[0]}, [${arrayDest.name}, #$exprDest]")
            exprDest += typeSize(node.contentType!!)
        }
        representation.addCode("\tLDR ${availableRegister[0]}, =${node.length}")
        representation.addCode("\tSTR ${availableRegister[0]}, [${arrayDest.name}]")
        freeRegister(arrayDest)
    }

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
                    representation.addCode("\tMOV ${l.name}, r0")
                }
                binOpAlgo2(node.expr1, node.expr2, div)
                representation.addPrintDivByZeroFunc()
            }
            Utils.Binop.MOD -> {
                val mod = {l:Register,r:Register -> representation.addCode("\tMOV r0, $l");
                    representation.addCode("\tMOV r1, $r");
                    representation.addCode("\tBL p_check_divide_by_zero")
                    representation.addCode("\tBL __aeabi_idivmod")
                    representation.addCode("\tMOV ${l.name}, r0")
                }
                binOpAlgo2(node.expr1, node.expr2, mod)
                representation.addPrintDivByZeroFunc()
            }
            Utils.Binop.GREATER -> {
                val gt = {l:Register,r:Register -> representation.addCode("\tCMP $l, $r");
                    representation.addCode("\tMOVGT ${l.name}, #1");
                    representation.addCode("\tMOVLE ${l.name}, #0");
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
                val add = {l:Register,r:Register -> representation.addCode("\tAND $l, $r")}
                binOpAlgo2(node.expr1, node.expr2, add)
            }
            Utils.Binop.OR -> {
                val add = {l:Register,r:Register -> representation.addCode("\tORR $l, $l, $r")}
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
        val dest = nextAvailableRegister()
        representation.addCode("\tMOV ${dest.name}, #${node.char.toInt()}")
        freeRegister(dest)
    }

    private fun visitFunctionCallNode(node: FunctionCallNode) {
        val currSymbolTable = symbolTable
        val scopeDepth = currScopeDepth
        var paramsSize = 0;
        for (param in node.params.reversed()) {
            visitExprNode(param)
            val size = typeSize(param.type!!)
            val opcode = if (size == 1) "STRB" else "STR"
            paramsSize += size
            representation.addCode("\t$opcode ${availableRegister[0]}, [sp, #-${size}]!")
            symbolTable = SymbolTable(symbolTable)
            currScopeDepth += 1
            symbolTable!!.add("#localVariableNo_$currScopeDepth", Pair(size, currScopeDepth))
        }
        representation.addCode("\tBL f_${node.function.identifier}")
        if (paramsSize > 0) representation.addCode("\tADD sp, sp, #$paramsSize")
        representation.addCode("\tMOV ${availableRegister[0]}, r0")
        currScopeDepth = scopeDepth
        symbolTable = currSymbolTable
    }

    private fun visitIdentNode(node: IdentNode) {
        val res = symbolTable!!.lookupAll(node.name)!!
        val destScopeDepth = res.second
        var offset = res.first
        for (i in currScopeDepth downTo (destScopeDepth + 1)) {
            offset += symbolTable!!.lookupAll("#localVariableNo_$i")!!.first
        }
        val opcode = if (typeSize(node.type!!)==1) "LDRSB" else "LDR"
        val operand = if (offset == 0) "[sp]" else "[sp, #$offset]"
        val dest = nextAvailableRegister()
        representation.addCode("\t$opcode $dest, $operand")
        freeRegister(dest)
    }

    private fun visitIntNode(node: IntNode) {
        val dest = nextAvailableRegister()
        representation.addCode("\tLDR ${dest.name}, =${node.value}")
        freeRegister(dest)
    }
    private fun visitPairElemNode(node: PairElemNode) {
        visitExprNode(node.pair)
        representation.addCode("\tMOV r0, ${availableRegister[0]}")
        representation.addCode("\tBL p_check_null_pointer")
        var type: Type
        if (node.isFirst) {
            representation.addCode("\tLDR ${availableRegister[0]}, [${availableRegister[0]}]")
            // WRONG
            type = node.fst().type!!
        } else {
            representation.addCode("\tLDR ${availableRegister[0]}, [${availableRegister[0]}, #4]")
            // WRONG
            type = node.snd().type!!
        }
        val opcode = if (typeSize(type)==1) "LDRSB" else "LDR"
        representation.addCode("\t$opcode ${availableRegister[0]}, [${availableRegister[0]}]")
        representation.addCheckNullPointerError()

    }

    private fun visitPairNode(node: PairNode) {
        if (node.fst == null && node.snd == null) {
            representation.addCode("\tLDR ${availableRegister[0]}, =0")
        } else {
            val pairLocation = nextAvailableRegister()
            representation.addCode("\tLDR r0, =8")
            representation.addCode("\tBL malloc")
            representation.addCode("\tMOV ${pairLocation.name}, r0")

            visitExprNode(node.fst!!)
            representation.addCode("\tLDR r0, =${typeSize(node.fst!!.type!!)}")
            representation.addCode("\tBL malloc")
            val opcode1 = if (typeSize(node.fst!!.type!!) == 1) "STRB" else "STR"
            representation.addCode("\t$opcode1 ${availableRegister[0]}, [r0]")
            representation.addCode("\tSTR r0, [${pairLocation.name}]")

            visitExprNode(node.snd!!)
            representation.addCode("\tLDR r0, =${typeSize(node.snd!!.type!!)}")
            representation.addCode("\tBL malloc")
            val opcode2 = if (typeSize(node.snd!!.type!!) == 1) "STRB" else "STR"
            representation.addCode("\t$opcode2 ${availableRegister[0]}, [r0]")
            representation.addCode("\tSTR r0, [${pairLocation.name}, #4]")

            freeRegister(pairLocation)
        }
    }

    private fun visitStringNode(node: StringNode) {
        val msgInt = representation.addStringToTable(node.string, node.length - 2)
        representation.addCode("\tLDR r4, =msg_$msgInt")
    }

    private fun visitUnopNode(node: UnopNode) {
        when (node.operator) {
            Utils.Unop.NOT -> {
                visitExprNode(node.expr)
                representation.addCode("\tEOR ${availableRegister[0]}, ${availableRegister[0]}, #1")
            }
            Utils.Unop.LEN -> {
                visitExprNode(node.expr)
                representation.addCode("\tLDR ${availableRegister[0]}, [${availableRegister[0]}]")
            }
            Utils.Unop.MINUS -> {
                visitExprNode(node.expr)
                representation.addCode("\tRSBS ${availableRegister[0]}, ${availableRegister[0]}, #0");
                representation.addCode("\tBLVS p_throw_overflow_error");
                representation.addPrintThrowErrorOverflowFunc()
            }
            Utils.Unop.ORD -> { visitExprNode(node.expr) }
            Utils.Unop.CHR -> { visitExprNode(node.expr) }
        }
    }

    /* =======================================================
     *                  Print Helper
     * =======================================================
     */


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

    private fun generateCheckArrayBoundFunc() {
        val code1 = representation.addStringToTable("\"ArrayIndexOutOfBoundsError: negative index\\n\\0\"", 44)
        val code2 = representation.addStringToTable("\"ArrayIndexOutOfBoundsError: index too large\\n\\0\"", 45)
        representation.addCode("p_check_array_bounds:")
        representation.addCode("\tPUSH {lr}")
        representation.addCode("\tCMP r0, #0")
        representation.addCode("\tLDRLT r0, =msg_$code1")
        representation.addCode("\tBLLT p_throw_runtime_error")
        representation.addCode("\tLDR r1, [r1]")
        representation.addCode("\tCMP r0, r1")
        representation.addCode("\tLDRCS r0, =msg_$code2")
        representation.addCode("\tBLCS p_throw_runtime_error")
        representation.addCode("\tPOP {pc}")
    }

    private fun generateReadInt() {
        val code = representation.addStringToTable("\"%d\\0\"", 3)
        representation.addCode("p_read_int:")
        representation.addCode("\tPUSH {lr}")
        representation.addCode("\tMOV r1, r0")
        representation.addCode("\tLDR r0, =msg_$code")
        representation.addCode("\tADD r0, r0, #4")
        representation.addCode("\tBL scanf")
        representation.addCode("\tPOP {pc}")
    }

    private fun generateReadChar() {
        val code = representation.addStringToTable("\" %c\\0\"", 4)
        representation.addCode("p_read_char:")
        representation.addCode("\tPUSH {lr}")
        representation.addCode("\tMOV r1, r0")
        representation.addCode("\tLDR r0, =msg_$code")
        representation.addCode("\tADD r0, r0, #4")
        representation.addCode("\tBL scanf")
        representation.addCode("\tPOP {pc}")
    }

    private fun generatePrintRefFunc() {
        val code = representation.addStringToTable("\"%p\\0\"", 3)
        representation.addCode("p_print_reference:")
        representation.addCode("\tPUSH {lr}")
        representation.addCode("\tMOV r1, r0")
        representation.addCode("\tLDR r0, =msg_$code")
        representation.addCode("\tADD r0, r0, #4")
        representation.addCode("\tBL printf")
        representation.addCode("\tMOV r0, #0")
        representation.addCode("\tBL fflush")
        representation.addCode("\tPOP {pc}")
    }

    private fun generateCheckNullPointerFunc() {
        val code = representation.addStringToTable("\"NullReferenceError: dereference a null reference\\n\\0\"", 50)
        representation.addCode("p_check_null_pointer:")
        representation.addCode("\tPUSH {lr}")
        representation.addCode("\tCMP r0, #0")
        representation.addCode("\tLDREQ r0, =msg_$code")
        representation.addCode("\tBLEQ p_throw_runtime_error")
        representation.addCode("\tPOP {pc}")
    }

    private fun generateFreePairFunc() {
        val code = representation.addStringToTable("\"NullReferenceError: dereference a null reference\\n\\0\"", 50)
        representation.addCode("p_free_pair:")
        representation.addCode("\tPUSH {lr}")
        representation.addCode("\tCMP r0, #0")
        representation.addCode("\tLDREQ r0, =msg_$code")
        representation.addCode("\tBEQ p_throw_runtime_error")
        representation.addCode("\tPUSH {r0}")
        representation.addCode("\tLDR r0, [r0]")
        representation.addCode("\tBL free")
        representation.addCode("\tLDR r0, [sp]")
        representation.addCode("\tLDR r0, [r0, #4]")
        representation.addCode("\tBL free")
        representation.addCode("\tPOP {r0}")
        representation.addCode("\tBL free")
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

    /* =======================================================
     *             Variable Assignment Helper
     * =======================================================
 */
    private fun typeSize(type: Type): Int {
        when (type) {
            is BasicType -> when (type.typeEnum) {
                BasicTypeEnum.INTEGER -> return 4
                BasicTypeEnum.BOOLEAN -> return 1
                BasicTypeEnum.CHAR -> return 1
                BasicTypeEnum.STRING -> return 4
            }
            is ArrayType -> return 4
            is PairType -> return 4
        }
        return 0
    }

    private fun log2(x: Int): Int {
        return when (x) {
            4 -> 2
            1 -> 0
            else -> -1
        }
    }


}

enum class Register(val asRep: String){
    R0("r0"), R1("r1"), R2("r2"), R3("r3"), R4("r4"), R5("r5"),
    R6("r6"), R7("r7"), R8("r8"), R9("r9"), R10("r10"),
    R11("r11"), R12("r12"), LR("r13"), SP("r14"), PC("pc")
}