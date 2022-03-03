import instruction.*
import instruction.addressing_mode.AddressingMode
import instruction.addressing_mode.ImmOffset
import instruction.shifter_operand.Shift
import instruction.shifter_operand.ShiftImm
import instruction.waccLibrary.*
import node.FuncNode
import node.ProgramNode
import node.expr.*
import node.stat.*
import register.Register
import register.RegisterAllocator
import type.*

class WACCCodeGeneratorVisitor(val generator: WACCCodeGenerator) {
    private val registerAllocator = RegisterAllocator()
    private var symbolTable: SymbolTable<Pair<Int, Int>>? = null
    private val symbolManager = SymbolManager()

    fun visitProgramNode(node: ProgramNode) {
        generator.addCode(Global("main"))
        node.functions.forEach{ident, func ->
            generator.addCode(LABEL("f_$ident"))
            visitFuncNode(func)
        }

        generator.addCode(LABEL("main"))
        generator.addCode(PUSH(Register.LR))
        symbolManager.newTable()
        visitStatNode(node.body)
        generator.addCode(LDR(Register.R0, 0))
        generator.addCode(POP(Register.PC))
    }


    private fun visitFuncNode(node: FuncNode) {
        symbolManager.newTable()
        node.paramList?.forEach { param ->
            symbolManager.addSymbol(param!!.name, typeSize(param.type!!))
        }
        symbolManager.finalise()

        generator.addCode(PUSH(Register.LR))
        symbolManager.incStackBy(4)
        visitScopeNode(ScopeNode(node.functionBody!!))
        generator.addCode(POP(Register.PC))

    }

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
        visitExprNode(node.rhs!!)
        val resultReg = registerAllocator.consumeRegister()

        var destination: AddressingMode? = null
        when (val lhs = node.lhs!!) {
            is IdentNode -> {
                val stackLocation = symbolManager.lookup(lhs.name)
                destination = ImmOffset(Register.SP, stackLocation)
            }
            is ArrayElemNode -> {
                computeArrayElemLocation(lhs)
                val locationReg = registerAllocator.peekRegister()
                destination = ImmOffset(locationReg)
            }
            is PairElemNode -> {
                computePairElemLocation(lhs)
                val locationReg = registerAllocator.peekRegister()
                destination = ImmOffset(locationReg)
            }
        }

        if (typeSize(node.lhs.type!!) == 1) {
            generator.addCode(STRB(resultReg, destination!!))
        } else {
            generator.addCode(STR(resultReg, destination!!))
        }

        registerAllocator.freeRegister(resultReg)
    }


    private fun visitDeclareStatNode(node: DeclareStatNode) {
        val assignExpr = node.rhs!!
        val resultReg = registerAllocator.peekRegister()
        val stackLocation = symbolManager.lookup(node.identifier)
        visitExprNode(assignExpr)
        if (typeSize(assignExpr.type!!) == 1) {
            generator.addCode(STRB(resultReg, ImmOffset(Register.SP, stackLocation)))
        } else {
            generator.addCode(STR(resultReg, ImmOffset(Register.SP, stackLocation)))
        }
    }

    private fun visitExitNode(node: ExitNode) {
        val resultReg = registerAllocator.peekRegister()
        visitExprNode(node.exitCode)
        generator.addCode(MOV(Register.R0, resultReg))
        generator.addCode(BL("exit"))
    }

    private fun visitFreeNode(node: FreeNode) {
        val resultReg = registerAllocator.peekRegister()
        visitExprNode(node.expr)
        generator.addCode(MOV(Register.R0, resultReg))
        generator.addCode(BL("p_free_pair"))
        generator.addCodeDependency(FreePair())
    }

    private fun visitIfNode(node: IfNode) {
        val elseLabel = LABEL.nextNo()
        val fiLabel = LABEL.nextNo()
        val condReg = registerAllocator.peekRegister()

        visitExprNode(node.condition)
        generator.addCode(CMP(condReg, 0))
        generator.addCode(B(elseLabel.label).on(Cond.EQ))
        visitStatNode(node.ifBody!!)
        generator.addCode(B(fiLabel.label))
        generator.addCode(elseLabel)
        visitStatNode(node.elseBody!!)
        generator.addCode(fiLabel)

    }
    private fun visitPrintlnNode(node: PrintlnNode) {
        visitPrintNode(PrintNode(node.expr))
        generator.addCode(BL("p_print_ln"))
        generator.addCodeDependency(Println())
    }

    private fun visitPrintNode(node: PrintNode) {
        val resultReg = registerAllocator.peekRegister()
        visitExprNode(node.expr!!)
        generator.addCode(MOV(Register.R0, resultReg))

        when (val type = node.expr.type!!) {
            is BasicType -> when (type.typeEnum) {
                                BasicTypeEnum.INTEGER -> { generator.addCode(BL("p_print_int"))
                                                            generator.addCodeDependency(PrintInt()) }
                                BasicTypeEnum.BOOLEAN -> { generator.addCode(BL("p_print_bool"))
                                                            generator.addCodeDependency(PrintBool()) }
                                BasicTypeEnum.CHAR -> generator.addCode(BL("putchar"))
                                BasicTypeEnum.STRING -> { generator.addCode(BL("p_print_string"))
                                                            generator.addCodeDependency(PrintString()) }
                            }
            is ArrayType -> if (type.getContentType() is BasicType && (type.getContentType() as BasicType).typeEnum == BasicTypeEnum.CHAR) {
                                generator.addCode(BL("p_print_string"))
                                generator.addCodeDependency(PrintString())
                            } else {
                                generator.addCode(BL("p_print_reference"))
                                generator.addCodeDependency(PrintReference())
                            }
            is PairType  -> { generator.addCode(BL("p_print_reference"))
                                generator.addCodeDependency(PrintReference())
                            }
        }

    }

    private fun visitReadNode(node: ReadNode) {
        val destinationReg = registerAllocator.peekRegister()
        when (val expr = node.inputExpr) {
            is IdentNode -> {
                val stackLocation = symbolManager.lookup(expr.name)
                generator.addCode(ADD(destinationReg, Register.SP, stackLocation))
            }
            is ArrayElemNode -> {
                computeArrayElemLocation(expr)
            }
            is PairElemNode -> {
                computePairElemLocation(expr)
            }
        }

        generator.addCode(MOV(Register.R0, destinationReg))

        val type = node.inputExpr.type as BasicType
        when (type.typeEnum) {
            BasicTypeEnum.INTEGER -> {
                generator.addCode(BL("p_read_int"))
                generator.addCodeDependency(ReadInt())
            }
            BasicTypeEnum.CHAR -> {
                generator.addCode(BL("p_read_char"))
                generator.addCodeDependency(ReadChar())
            }
            else -> error("Invalid type for Read")
        }
    }


    private fun visitReturnNode(node: ReturnNode) {
        val exprReg = registerAllocator.peekRegister()
        visitExprNode(node.expr)
        generator.addCode(MOV(Register.R0, exprReg))
        val height = symbolManager.height()
        if (height > 0) generator.addCode(ADD(Register.SP, Register.SP, height))
        generator.addCode(POP(Register.PC))
    }

    private fun visitScopeNode(node: ScopeNode) {
        /* Add all symbol of current scope to symbolManager */
        symbolManager.nextScope()
        if (node.body.isEmpty()) return
        if (node.body[0] is DeclareStatNode) {
            val decStat = node.body[0] as DeclareStatNode
            symbolManager.addSymbol(decStat.identifier, typeSize(decStat.rhs!!.type!!))
        } else if (node.body[0] is SequenceNode) {
            val seqStat = node.body[0] as SequenceNode
            seqStat.body.forEach{ stat -> if (stat is DeclareStatNode) symbolManager.addSymbol(stat.identifier, typeSize(stat.rhs!!.type!!))}
        }
        symbolManager.finalise()

        val scopeSize = symbolManager.getScopeSize()
        if (scopeSize > 0) generator.addCode(SUB(Register.SP, Register.SP, scopeSize))
        node.body.forEach{stat -> visitStatNode(stat)}
        if (scopeSize > 0) generator.addCode(ADD(Register.SP, Register.SP, scopeSize))
        symbolManager.prevScope()

    }

    private fun visitSequenceNode(node: SequenceNode) {
        node.body.forEach{stat -> visitStatNode(stat)}
    }

    private fun visitSkipNode(node: SkipNode) {
        return
    }

    private fun visitWhileNode(node: WhileNode) {
        val whileLabel = LABEL.nextNo()
        val doLabel = LABEL.nextNo()
        val condReg = registerAllocator.peekRegister()
        generator.addCode(B(whileLabel.label))
        generator.addCode(doLabel)
        visitStatNode(node.body)
        generator.addCode(whileLabel)
        visitExprNode(node.cond)
        generator.addCode(CMP(condReg, 1))
        generator.addCode(B(doLabel.label).on(Cond.EQ))

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
        generator.addCode("\tADD ${reg.name}, sp, #${offset}")

        for (index in node.index) {
            visitExprNode(index)
            generator.addCode("\tLDR ${reg.name}, [${reg.name}]")
            generator.addCode("\tMOV r0, ${availableRegister[0]}")
            generator.addCode("\tMOV r1, ${reg.name}")
            generator.addCode("\tBL p_check_array_bounds")
            generator.addCode("\tADD ${reg.name}, ${reg.name}, #4")
            generator.addCode("\tADD ${reg.name}, ${reg.name}, ${availableRegister[0]}, LSL #${log2(typeSize(node.type!!))}")
        }
        generator.addCode("\tLDR ${reg.name}, [${reg.name}]")
        freeRegister(reg)
        generator.addCheckErrorBoundsFunc()


    }

    private fun visitArrayNode(node: ArrayNode) {
        val arraySize = node.length * typeSize(node.contentType!!) + typeSize(BasicType(BasicTypeEnum.INTEGER))
        generator.addCode("\tLDR r0, =$arraySize")
        generator.addCode("\tBL malloc")
        val arrayDest = nextAvailableRegister()
        generator.addCode("\tMOV ${arrayDest.name}, r0")
        var exprDest = 4
        for (expr in node.content) {
            visitExprNode(expr)
            val opcode = if (typeSize(node.contentType!!) == 1) "STRB" else "STR"
            generator.addCode("\t$opcode ${availableRegister[0]}, [${arrayDest.name}, #$exprDest]")
            exprDest += typeSize(node.contentType!!)
        }
        generator.addCode("\tLDR ${availableRegister[0]}, =${node.length}")
        generator.addCode("\tSTR ${availableRegister[0]}, [${arrayDest.name}]")
        freeRegister(arrayDest)
    }

    private fun visitBinopNode(node: BinopNode) {
        when (node.operator) {
            Utils.Binop.PLUS -> {
                val add = {l:Register,r:Register -> generator.addCode("\tADD $l, $l, $r");
                    generator.addCode("\tBLVS p_throw_overflow_error")}
                binOpAlgo2(node.expr1, node.expr2, add)
                generator.addPrintThrowErrorOverflowFunc()
            }
            Utils.Binop.MINUS -> {
                val sub = {l:Register,r:Register -> generator.addCode("\tSUB $l, $l, $r");
                    generator.addCode("\tBLVS p_throw_overflow_error")}
                binOpAlgo2(node.expr1, node.expr2, sub)
                generator.addPrintThrowErrorOverflowFunc()
            }
            Utils.Binop.MUL -> {
                val mult = {l:Register,r:Register -> generator.addCode("\tSMULL $l, $r, $l, $r");
                        generator.addCode("\tCMP $r, $l, ASR #31");
                        generator.addCode("\tBLNE p_throw_overflow_error")}
                binOpAlgo2(node.expr1, node.expr2, mult)
                generator.addPrintThrowErrorOverflowFunc()
            }
            Utils.Binop.DIV -> {
                val div = {l:Register,r:Register -> generator.addCode("\tMOV r0, $l");
                    generator.addCode("\tMOV r1, $r");
                    generator.addCode("\tBL p_check_divide_by_zero")
                    generator.addCode("\tBL __aeabi_idiv")
                    generator.addCode("\tMOV ${l.name}, r0")
                }
                binOpAlgo2(node.expr1, node.expr2, div)
                generator.addPrintDivByZeroFunc()
            }
            Utils.Binop.MOD -> {
                val mod = {l:Register,r:Register -> generator.addCode("\tMOV r0, $l");
                    generator.addCode("\tMOV r1, $r");
                    generator.addCode("\tBL p_check_divide_by_zero")
                    generator.addCode("\tBL __aeabi_idivmod")
                    generator.addCode("\tMOV ${l.name}, r0")
                }
                binOpAlgo2(node.expr1, node.expr2, mod)
                generator.addPrintDivByZeroFunc()
            }
            Utils.Binop.GREATER -> {
                val gt = {l:Register,r:Register -> generator.addCode("\tCMP $l, $r");
                    generator.addCode("\tMOVGT ${l.name}, #1");
                    generator.addCode("\tMOVLE ${l.name}, #0");
                }
                binOpAlgo2(node.expr1, node.expr2, gt)
            }
            Utils.Binop.GREATER_EQUAL -> {
                val ge = {l:Register,r:Register -> generator.addCode("\tCMP $l, $r");
                    generator.addCode("\tMOVGE $l, #1");
                    generator.addCode("\tMOVLT $l, #0");
                }
                binOpAlgo2(node.expr1, node.expr2, ge)
            }
            Utils.Binop.LESS -> {
                val gt = {l:Register,r:Register -> generator.addCode("\tCMP $l, $r");
                    generator.addCode("\tMOVLT $l, #1");
                    generator.addCode("\tMOVGE $l, #0");
                }
                binOpAlgo2(node.expr1, node.expr2, gt)
            }
            Utils.Binop.LESS_EQUAL -> {
                val gt = {l:Register,r:Register -> generator.addCode("\tCMP $l, $r");
                    generator.addCode("\tMOVLE $l, #1");
                    generator.addCode("\tMOVGT $l, #0");
                }
                binOpAlgo2(node.expr1, node.expr2, gt)
            }
            Utils.Binop.EQUAL -> {
                val gt = {l:Register,r:Register -> generator.addCode("\tCMP $l, $r");
                    generator.addCode("\tMOVEQ $l, #1");
                    generator.addCode("\tMOVNE $l, #0");
                }
                binOpAlgo2(node.expr1, node.expr2, gt)
            }
            Utils.Binop.INEQUAL -> {
                val gt = {l:Register,r:Register -> generator.addCode("\tCMP $l, $r");
                    generator.addCode("\tMOVNE $l, #1");
                    generator.addCode("\tMOVEQ $l, #0");
                }
                binOpAlgo2(node.expr1, node.expr2, gt)
            }
            Utils.Binop.AND -> {
                val add = {l:Register,r:Register -> generator.addCode("\tAND $l, $r")}
                binOpAlgo2(node.expr1, node.expr2, add)
            }
            Utils.Binop.OR -> {
                val add = {l:Register,r:Register -> generator.addCode("\tORR $l, $l, $r")}
                binOpAlgo2(node.expr1, node.expr2, add)
            }
        }
    }


    private fun visitBoolNode(node: BoolNode) {
        val value = if (node.`val`) 1 else 0
        val dest = nextAvailableRegister()
        generator.addCode("\tMOV ${dest.name}, #$value")
        freeRegister(dest)
    }

    private fun visitCharNode(node: CharNode) {
        val dest = registerAllocator.peekRegister()
        generator.addCode(MOV(dest, node.char.code))
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
            generator.addCode("\t$opcode ${availableRegister[0]}, [sp, #-${size}]!")
            symbolTable = SymbolTable(symbolTable)
            currScopeDepth += 1
            symbolTable!!.add("#localVariableNo_$currScopeDepth", Pair(size, currScopeDepth))
        }
        generator.addCode("\tBL f_${node.function.identifier}")
        if (paramsSize > 0) generator.addCode("\tADD sp, sp, #$paramsSize")
        generator.addCode("\tMOV ${availableRegister[0]}, r0")
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
        generator.addCode("\t$opcode $dest, $operand")
        freeRegister(dest)
    }

    private fun visitIntNode(node: IntNode) {
        val dest = nextAvailableRegister()
        generator.addCode("\tLDR ${dest.name}, =${node.value}")
        freeRegister(dest)
    }
    private fun visitPairElemNode(node: PairElemNode) {
        visitExprNode(node.pair)
        generator.addCode("\tMOV r0, ${availableRegister[0]}")
        generator.addCode("\tBL p_check_null_pointer")
        var type: Type
        if (node.isFirst) {
            generator.addCode("\tLDR ${availableRegister[0]}, [${availableRegister[0]}]")
            // WRONG
            type = node.fst().type!!
        } else {
            generator.addCode("\tLDR ${availableRegister[0]}, [${availableRegister[0]}, #4]")
            // WRONG
            type = node.snd().type!!
        }
        val opcode = if (typeSize(type)==1) "LDRSB" else "LDR"
        generator.addCode("\t$opcode ${availableRegister[0]}, [${availableRegister[0]}]")
        generator.addCheckNullPointerError()

    }

    private fun visitPairNode(node: PairNode) {
        if (node.fst == null && node.snd == null) {
            generator.addCode("\tLDR ${availableRegister[0]}, =0")
        } else {
            val pairLocation = nextAvailableRegister()
            generator.addCode("\tLDR r0, =8")
            generator.addCode("\tBL malloc")
            generator.addCode("\tMOV ${pairLocation.name}, r0")

            visitExprNode(node.fst!!)
            generator.addCode("\tLDR r0, =${typeSize(node.fst!!.type!!)}")
            generator.addCode("\tBL malloc")
            val opcode1 = if (typeSize(node.fst!!.type!!) == 1) "STRB" else "STR"
            generator.addCode("\t$opcode1 ${availableRegister[0]}, [r0]")
            generator.addCode("\tSTR r0, [${pairLocation.name}]")

            visitExprNode(node.snd!!)
            generator.addCode("\tLDR r0, =${typeSize(node.snd!!.type!!)}")
            generator.addCode("\tBL malloc")
            val opcode2 = if (typeSize(node.snd!!.type!!) == 1) "STRB" else "STR"
            generator.addCode("\t$opcode2 ${availableRegister[0]}, [r0]")
            generator.addCode("\tSTR r0, [${pairLocation.name}, #4]")

            freeRegister(pairLocation)
        }
    }

    private fun visitStringNode(node: StringNode) {
        val msgInt = generator.addStringToTable(node.string, node.length - 2)
        generator.addCode("\tLDR r4, =msg_$msgInt")
    }

    private fun visitUnopNode(node: UnopNode) {
        when (node.operator) {
            Utils.Unop.NOT -> {
                visitExprNode(node.expr)
                generator.addCode("\tEOR ${availableRegister[0]}, ${availableRegister[0]}, #1")
            }
            Utils.Unop.LEN -> {
                visitExprNode(node.expr)
                generator.addCode("\tLDR ${availableRegister[0]}, [${availableRegister[0]}]")
            }
            Utils.Unop.MINUS -> {
                visitExprNode(node.expr)
                generator.addCode("\tRSBS ${availableRegister[0]}, ${availableRegister[0]}, #0");
                generator.addCode("\tBLVS p_throw_overflow_error");
                generator.addPrintThrowErrorOverflowFunc()
            }
            Utils.Unop.ORD -> { visitExprNode(node.expr) }
            Utils.Unop.CHR -> { visitExprNode(node.expr) }
        }
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
            generator.addCode("\tPUSH {${dest.name}}")
            freeRegister(dest)

            visitExprNode(expr2)
            dest = nextAvailableRegister()
            val lastReg = nextAvailableRegister()
            generator.addCode("\tPOP {${lastReg.name}}")
            op(dest, lastReg)
            freeRegister(lastReg)
            freeRegister(dest)
        }
    }

    /* =======================================================
     *             Variable Assignment Helper
     * =======================================================
 */
    private fun computePairElemLocation(lhs: PairElemNode) {
        /* compute the address of PairElem and store it in first available register */
        val locationReg = registerAllocator.peekRegister()
        visitExprNode(lhs.pair)
        generator.addCode(MOV(Register.R0, locationReg))
        generator.addCode(BL("p_check_null_pointer"))
        if (lhs.isFirst) {
            generator.addCode(LDR(locationReg, ImmOffset(locationReg)))
        } else {
            generator.addCode(LDR(locationReg, ImmOffset(locationReg, 4)))
        }
        generator.addCodeDependency(CheckNullPointer())
    }

    private fun computeArrayElemLocation(lhs: ArrayElemNode) {
        /* compute the address of PairElem and store it in first available register */
        val locationReg = registerAllocator.consumeRegister()
        val arrayLocation = symbolManager.lookup(lhs.arrayIdent)
        generator.addCode(ADD(locationReg, Register.SP, arrayLocation))
        for (index in lhs.index) {
            visitExprNode(index)
            val indexExprReg = registerAllocator.peekRegister()
            generator.addCode(LDR(locationReg, ImmOffset(locationReg)))
            generator.addCode(MOV(Register.R0, indexExprReg))
            generator.addCode(MOV(Register.R1, locationReg))
            generator.addCode(BL("p_check_array_bounds"))
            generator.addCode(ADD(locationReg, locationReg, 4))
            generator.addCode(ADD(locationReg, locationReg, ShiftImm(indexExprReg, Shift.LSL, log2(typeSize(lhs.type!!)))))
        }
        registerAllocator.freeRegister(locationReg)
        generator.addCodeDependency(CheckArrayBounds())
    }

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

class SymbolManager {
    private var symbolTable: SymbolTable<Pair<Int, Int>>? = null
    private var currentScopeDepth = 0
    private var currentScopeSize = 0

    fun newTable() {
        symbolTable = null
        currentScopeDepth = 0
        currentScopeSize = 0
    }

    fun nextScope() {
        symbolTable = SymbolTable(symbolTable)
        currentScopeDepth++
        currentScopeSize = 0
    }

    fun incStackBy(byte: Int) {
        nextScope()
        symbolTable!!.add("#LOCAL_VARIABLE_SIZE_$currentScopeDepth", Pair(byte, currentScopeDepth))
    }

    fun prevScope() {
        symbolTable = symbolTable!!.parentSymbolTable
        currentScopeDepth--
    }

    /* Add symbol to symbolTable of currentScope.
    *  All symbol of current scope MUST be added at first step of evaluation of scope */
    fun addSymbol(symbol: String, size: Int) {
        var tempSize = 0
        symbolTable!!.add(symbol, Pair(size, currentScopeDepth))
        tempSize += size
        currentScopeSize += size
    }

    fun getScopeSize(): Int {
        return symbolTable!!.lookup("#LOCAL_VARIABLE_SIZE_$currentScopeDepth").first
    }

    fun finalise() {
        symbolTable!!.add("#LOCAL_VARIABLE_SIZE_$currentScopeDepth", Pair(currentScopeSize, currentScopeDepth))
    }

    fun lookup(identifier: String): Int {
        val res = symbolTable!!.lookupAll(identifier)!!
        val destScopeDepth = res.second
        var offset = res.first
        for (i in currentScopeDepth downTo destScopeDepth + 1) {
            offset += symbolTable!!.lookupAll("#LOCAL_VARIABLE_SIZE_$i")!!.first
        }
        offset += currentScopeSize
        return offset
    }

    fun height(): Int {
        /* find the the size of local variable of current scope all the way to the second
        parent scope, since the first parent scope is pushed by parent function
         */
        var res = 0
        var currST = symbolTable
        var i = currentScopeDepth
        while (currST != null && i > 1) {
            res += currST.lookupAll("#LOCAL_VARIABLE_SIZE_$i")!!.first
            currST = currST.parentSymbolTable
            i--
        }
        return res
    }
}