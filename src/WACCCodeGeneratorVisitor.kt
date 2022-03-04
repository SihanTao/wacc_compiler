import instruction.*
import instruction.addressing_mode.AddressingMode
import instruction.addressing_mode.ImmOffset
import instruction.addressing_mode.ImmPreIndex
import instruction.addressing_mode.Sign
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
    private val symbolManager = SymbolManager()

    fun visitProgramNode(node: ProgramNode) {
        generator.addCode(Global("main"))
        node.functions.forEach{ident, func ->
            generator.addCode(LABEL("f_$ident"))
            visitFuncNode(func)
        }

        generator.addCode(LABEL("main"))
        generator.addCode(PUSH(Register.LR))
        visitStatNode(node.body)
        generator.addCode(LDR(Register.R0, 0))
        generator.addCode(POP(Register.PC))
    }


    private fun visitFuncNode(node: FuncNode) {
        symbolManager.newTable()
        var paramsSize = 0
        node.paramList?.forEach { param ->
            paramsSize += typeSize(param!!.type!!)
        }
        symbolManager.setCurrScopeVarsSize(paramsSize)
        node.paramList?.reversed()?.forEach { param ->
            symbolManager.addSymbol(param!!.name, typeSize(param.type!!))
        }


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

        generator.addCode(STORE(resultReg, destination!!, node.lhs.type!!))

        registerAllocator.freeRegister(resultReg)
    }


    private fun visitDeclareStatNode(node: DeclareStatNode) {
        val assignExpr = node.rhs!!
        val resultReg = registerAllocator.peekRegister()
        val stackLocation = symbolManager.addSymbol(node.identifier, typeSize(node.rhs.type!!))
        visitExprNode(assignExpr)
        generator.addCode(STORE(resultReg, ImmOffset(Register.SP, stackLocation), assignExpr.type!!))
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
        var scopeSize = 0
        if (node.body.isEmpty()) return
        if (node.body[0] is DeclareStatNode) {
            val decStat = node.body[0] as DeclareStatNode
            scopeSize += typeSize(decStat.rhs!!.type!!)
        } else if (node.body[0] is SequenceNode) {
            val seqStat = node.body[0] as SequenceNode
            seqStat.body.forEach{ stat -> if (stat is DeclareStatNode) scopeSize += typeSize(stat.rhs!!.type!!)}
        }

        symbolManager.nextScope()
        symbolManager.setCurrScopeVarsSize(scopeSize)

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
        computeArrayElemLocation(node)
        val resultReg = registerAllocator.peekRegister()
        if (typeSize(node.type!!) == 1) {
            generator.addCode(LDRSB(resultReg, ImmOffset(resultReg)))
        } else {
            generator.addCode(LDR(resultReg, ImmOffset(resultReg)))
        }
    }

    private fun visitArrayNode(node: ArrayNode) {
        var arraySize = typeSize(BasicType(BasicTypeEnum.INTEGER))
        if (node.length > 0) {
            arraySize += node.length * typeSize(node.contentType!!)
        }

        generator.addCode(LDR(Register.R0, arraySize))
        generator.addCode(BL("malloc"))
        val dest = registerAllocator.consumeRegister()
        generator.addCode(MOV(dest, Register.R0))

        var elemDest = 4
        for (expr in node.content) {
            visitExprNode(expr)
            val exprReg = registerAllocator.peekRegister()
            val elemSize = typeSize(node.contentType!!)
            generator.addCode(STORE(exprReg, ImmOffset(exprReg, elemDest), node.contentType))
            elemDest += elemSize
        }

        val lengthReg = registerAllocator.peekRegister()
        generator.addCode(LDR(lengthReg, node.length))
        generator.addCode(STR(lengthReg, ImmOffset(dest)))
        registerAllocator.freeRegister(dest)

    }

    private fun visitBinopNode(node: BinopNode) {
        val op: (Register, Register) -> List<ARM11Instruction>
        when (node.operator) {
            Utils.Binop.PLUS -> {
                op = {l, r -> listOf(
                        ADD(l, l, r),
                        BL("p_throw_overflow_error").on(Cond.VS)
                )}
                generator.addCodeDependency(ThrowOverflowError())
            }
            Utils.Binop.MINUS -> {
                op = {l, r -> listOf(
                        SUB(l, l, r),
                        BL("p_throw_overflow_error").on(Cond.VS)
                )}
                generator.addCodeDependency(ThrowOverflowError())
            }
            Utils.Binop.MUL -> {
                op = {l, r -> listOf(
                        SMULL(l, r, l, r),
                        CMP(r, ShiftImm(l, Shift.ASR, 31)),
                        BL("p_throw_overflow_error").on(Cond.NE)
                )}
                generator.addCodeDependency(ThrowOverflowError())
            }
            Utils.Binop.DIV -> {
                op = {l, r -> listOf(
                        MOV(Register.R0, l),
                        MOV(Register.R1, r),
                        BL("p_check_divide_by_zero"),
                        BL("__aeabi_idiv"),
                        MOV(l, Register.R0)
                )}
                generator.addCodeDependency(CheckDivideByZero())
            }
            Utils.Binop.MOD -> {
                op = {l, r -> listOf(
                        MOV(Register.R0, l),
                        MOV(Register.R1, r),
                        BL("p_check_divide_by_zero"),
                        BL("__aeabi_idivmod"),
                        MOV(l, Register.R1)
                )}
                generator.addCodeDependency(CheckDivideByZero())
            }
            Utils.Binop.GREATER -> {
                op = {l, r -> listOf(
                        CMP(l, r),
                        MOV(l, 1).on(Cond.GT),
                        MOV(l, 0).on(Cond.LE)
                )}
            }
            Utils.Binop.GREATER_EQUAL -> {
                op = {l, r -> listOf(
                        CMP(l, r),
                        MOV(l, 1).on(Cond.GE),
                        MOV(l, 0).on(Cond.LT)
                )}
            }
            Utils.Binop.LESS -> {
                op = {l, r -> listOf(
                        CMP(l, r),
                        MOV(l, 1).on(Cond.LT),
                        MOV(l, 0).on(Cond.GE)
                )}
            }
            Utils.Binop.LESS_EQUAL -> {
                op = {l, r -> listOf(
                        CMP(l, r),
                        MOV(l, 1).on(Cond.LE),
                        MOV(l, 0).on(Cond.GT)
                )}
            }
            Utils.Binop.EQUAL -> {
                op = {l, r -> listOf(
                        CMP(l, r),
                        MOV(l, 1).on(Cond.EQ),
                        MOV(l, 0).on(Cond.NE)
                )}
            }
            Utils.Binop.INEQUAL -> {
                op = {l, r -> listOf(
                        CMP(l, r),
                        MOV(l, 1).on(Cond.NE),
                        MOV(l, 0).on(Cond.EQ)
                )}
            }
            Utils.Binop.AND -> {
                op = {l, r -> listOf(AND(l, l, r))}
            }
            Utils.Binop.OR -> {
                op = {l, r -> listOf(ORR(l, l, r))}

            }
        }
        computeBinopExpr(node.expr1, node.expr2, op)
    }


    private fun visitBoolNode(node: BoolNode) {
        val value = if (node.`val`) 1 else 0
        val dest = registerAllocator.peekRegister()
        generator.addCode(MOV(dest, value))
    }

    private fun visitCharNode(node: CharNode) {
        val dest = registerAllocator.peekRegister()
        generator.addCode(MOV(dest, node.char.code))
    }

    private fun visitFunctionCallNode(node: FunctionCallNode) {
        var paramsSize = 0
        node.params.reversed().forEach { param ->
            visitExprNode(param)
            val exprReg = registerAllocator.peekRegister()
            val paramSize = typeSize(param.type!!)
            generator.addCode(STORE(exprReg, ImmPreIndex(Register.SP, Pair(Sign.MINUS, paramSize)), param.type!!))
            symbolManager.incStackBy(paramSize)
            paramsSize += paramSize
        }

        generator.addCode(BL("f_${node.function.identifier}"))
        val resultReg = registerAllocator.peekRegister()
        if (paramsSize > 0) generator.addCode(ADD(Register.SP, Register.SP, paramsSize))
        generator.addCode(MOV(resultReg, Register.R0))
        repeat(node.params.size) {symbolManager.prevScope()}

    }

    private fun visitIdentNode(node: IdentNode) {
        val stackLocation = symbolManager.lookup(node.name)
        val resultReg = registerAllocator.peekRegister()
        if (typeSize(node.type!!) == 1) {
            generator.addCode(LDRSB(resultReg, ImmOffset(Register.SP, stackLocation)))
        } else {
            generator.addCode(LDR(resultReg, ImmOffset(Register.SP, stackLocation)))
        }
    }

    private fun visitIntNode(node: IntNode) {
        val dest = registerAllocator.peekRegister()
        generator.addCode(LDR(dest, node.value))
    }

    private fun visitPairElemNode(node: PairElemNode) {
        computePairElemLocation(node)
        val resultReg = registerAllocator.peekRegister()
        if (typeSize(node.type!!) == 1) {
            LDRSB(resultReg, ImmOffset(resultReg))
        } else {
            LDR(resultReg, ImmOffset(resultReg))
        }
    }

    private fun visitPairNode(node: PairNode) {
        val destReg = registerAllocator.consumeRegister()
        if (node.fst == null && node.snd == null) {
            generator.addCode(LDR(destReg, 0))
        } else {
            generator.addCode(LDR(Register.R0, 8))
            generator.addCode(BL("malloc"))
            generator.addCode((MOV(destReg, Register.R0)))

            listOf(node.fst!!, node.snd!!).forEachIndexed{ i, expr ->
                visitExprNode(expr)
                generator.addCode(LDR(Register.R0, typeSize(expr.type!!)))
                generator.addCode(BL("malloc"))
                generator.addCode(STORE(destReg, ImmOffset(Register.R0), expr.type!!))
                generator.addCode(STR(Register.R0, ImmOffset(destReg, 4*i)))
            }
        }
        registerAllocator.freeRegister(destReg)
    }

    private fun visitStringNode(node: StringNode) {
        val msgCode = generator.addDataElement(node.string)
        val resulrReg = registerAllocator.peekRegister()
        generator.addCode(LDR(resulrReg, "msg_$msgCode"))
    }

    private fun visitUnopNode(node: UnopNode) {
        visitExprNode(node.expr)
        val exprReg = registerAllocator.peekRegister()
        when (node.operator) {
            Utils.Unop.NOT -> generator.addCode(EOR(exprReg, exprReg, 1))
            Utils.Unop.LEN -> generator.addCode(LDR(exprReg, ImmOffset(exprReg)))
            Utils.Unop.MINUS -> {
                generator.addCode(RSB(exprReg, exprReg, 0).S())
                generator.addCode(BL("p_throw_overflow_error").on(Cond.VS))
                generator.addCodeDependency(ThrowOverflowError())
            }
            else -> { /*Do Nothing */}
        }

    }

    /* =======================================================
     *                      Helper
     * =======================================================
 */
    private fun computePairElemLocation(node: PairElemNode) {
        /* compute the address of PairElem and store it in first available register */
        val locationReg = registerAllocator.peekRegister()
        visitExprNode(node.pair)
        generator.addCode(MOV(Register.R0, locationReg))
        generator.addCode(BL("p_check_null_pointer"))
        if (node.isFirst) {
            generator.addCode(LDR(locationReg, ImmOffset(locationReg)))
        } else {
            generator.addCode(LDR(locationReg, ImmOffset(locationReg, 4)))
        }
        generator.addCodeDependency(CheckNullPointer())
    }

    private fun computeArrayElemLocation(node: ArrayElemNode) {
        /* compute the address of PairElem and store it in first available register */
        val locationReg = registerAllocator.consumeRegister()
        val arrayLocation = symbolManager.lookup(node.arrayIdent)
        generator.addCode(ADD(locationReg, Register.SP, arrayLocation))
        for (index in node.index) {
            visitExprNode(index)
            val indexExprReg = registerAllocator.peekRegister()
            generator.addCode(LDR(locationReg, ImmOffset(locationReg)))
            generator.addCode(MOV(Register.R0, indexExprReg))
            generator.addCode(MOV(Register.R1, locationReg))
            generator.addCode(BL("p_check_array_bounds"))
            generator.addCode(ADD(locationReg, locationReg, 4))
            generator.addCode(ADD(locationReg, locationReg, ShiftImm(indexExprReg, Shift.LSL, log2(typeSize(node.type!!)))))
        }
        registerAllocator.freeRegister(locationReg)
        generator.addCodeDependency(CheckArrayBounds())
    }

    private fun computeBinopExpr(lhs: ExprNode, rhs: ExprNode, op: (lexpr: Register, rexpr: Register) -> List<ARM11Instruction>) {
        if (registerAllocator.noOfFreeRegisters() > 2) {
            visitExprNode(lhs)
            val lhsReg = registerAllocator.consumeRegister()
            visitExprNode(rhs)
            val rhsReg = registerAllocator.peekRegister()
            op(lhsReg, rhsReg).forEach { ins -> generator.addCode(ins) }
            registerAllocator.freeRegister(lhsReg)
        } else {
            visitExprNode(rhs)
            var rhsReg = registerAllocator.consumeRegister()
            generator.addCode(PUSH(rhsReg))
            symbolManager.incStackBy(4)
            registerAllocator.freeRegister(rhsReg)

            visitExprNode(lhs)
            val lhsReg = registerAllocator.consumeRegister()
            rhsReg = registerAllocator.peekRegister()
            generator.addCode(POP(rhsReg))
            symbolManager.prevScope()
            op(lhsReg, rhsReg).forEach { ins -> generator.addCode(ins) }
            registerAllocator.freeRegister(lhsReg)
        }
    }

    private fun STORE(reg: Register, addr: AddressingMode, type: Type): ARM11Instruction {
        if(typeSize(type) == 1) {
            return STRB(reg, addr)
        } else {
            return STR(reg, addr)
        }
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
    private var nextDeclarationPos = 0

    fun newTable() {
        symbolTable = SymbolTable(null)
        currentScopeDepth = 0
        nextDeclarationPos = 0
    }

    fun nextScope() {
        symbolTable = SymbolTable(symbolTable)
        currentScopeDepth++
        symbolTable!!.add("#PREV_NEXT_DECLARATION", Pair(nextDeclarationPos, currentScopeDepth))
        nextDeclarationPos = 0
    }

    fun incStackBy(byte: Int) {
        nextScope()
        symbolTable!!.add("#LOCAL_VARIABLE_SIZE_$currentScopeDepth", Pair(byte, currentScopeDepth))
    }

    fun prevScope() {
        nextDeclarationPos = symbolTable!!.lookup("#PREV_NEXT_DECLARATION")!!.first
        symbolTable = symbolTable!!.parentSymbolTable
        currentScopeDepth--
    }

    fun addSymbol(symbol: String, size: Int): Int {
        nextDeclarationPos -= size
        symbolTable!!.add(symbol, Pair(nextDeclarationPos, currentScopeDepth))
        return nextDeclarationPos
    }

    fun getScopeSize(): Int {
        return symbolTable!!.lookup("#LOCAL_VARIABLE_SIZE_$currentScopeDepth")!!.first
    }

    fun lookup(identifier: String): Int {
        println(identifier)
        val res = symbolTable!!.lookupAll(identifier)!!
        val destScopeDepth = res.second
        var offset = res.first
        println(destScopeDepth)
        println(currentScopeDepth)
        for (i in currentScopeDepth downTo destScopeDepth + 1) {
            offset += symbolTable!!.lookupAll("#LOCAL_VARIABLE_SIZE_$i")!!.first
        }
        dumpStack()
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

    fun getNextDeclarationPos(): Int {
        return nextDeclarationPos
    }

    fun setCurrScopeVarsSize(size: Int) {
        symbolTable!!.add("#LOCAL_VARIABLE_SIZE_$currentScopeDepth", Pair(size, currentScopeDepth))
        nextDeclarationPos = size
    }

    fun dumpStack() {
        for (i in currentScopeDepth downTo 0) {
            val size = symbolTable!!.lookupAll("#LOCAL_VARIABLE_SIZE_$i")!!.first
            println("i: $i, $size")
        }
    }

}