import ErrorHandler.Companion.SEMANTIC_ERROR_CODE
import ErrorHandler.Companion.invalidFuncArgCount
import ErrorHandler.Companion.invalidPairError
import ErrorHandler.Companion.returnFromMainError
import ErrorHandler.Companion.symbolRedeclare
import antlr.WACCParser.*
import antlr.WACCParserBaseVisitor
import node.*
import node.expr.*
import node.stat.*
import type.*
import type.Utils.Companion.ARRAY_T
import type.Utils.Companion.BOOL_T
import type.Utils.Companion.CHAR_T
import type.Utils.Companion.CmpEnumMapping
import type.Utils.Companion.EqEnumMapping
import type.Utils.Companion.INT_T
import type.Utils.Companion.LogicOpEnumMapping
import type.Utils.Companion.PAIR_T
import type.Utils.Companion.STRING_T
import type.Utils.Companion.binopEnumMapping
import type.Utils.Companion.compareStatAllowedTypes
import type.Utils.Companion.freeStatAllowedTypes
import type.Utils.Companion.readStatAllowedTypes
import type.Utils.Companion.typeCheck
import type.Utils.Companion.unopEnumMapping
import type.Utils.Companion.unopTypeMapping
import kotlin.system.exitProcess

class WACCSemanticErrorVisitor : WACCParserBaseVisitor<Node>() {

    private var symbolTable: SymbolTable? = null
    private var globalFuncTable: MutableMap<String, FuncNode>? = null
    private var isMainFunction = false
    private var expectedFunctionReturn: Type? = null
    private var currDeclareType: Type? = null
    private var semanticError = false

    init {
        symbolTable = null
        globalFuncTable = HashMap()
        isMainFunction = false
        expectedFunctionReturn = null
        currDeclareType = null
    }

    override fun visitProgram(ctx: ProgramContext): Node {
        /* add the identifiers and parameter list of functions in the globalFuncTable first */
        for (f in ctx.func()) {
            val funcName: String = f.ident().IDENT().text

            /* check if the function is defined already */
            if (globalFuncTable!!.containsKey(funcName)) {
                symbolRedeclare(ctx, funcName)
                semanticError = true
            }

            /* get the return type of the function */
            val returnType: Type = (visit(f.type()) as TypeNode).type
            /* store the parameters in a list of IdentNode */
            val paramList: MutableList<IdentNode> = ArrayList()
            if (f.paramlist() != null) {
                for (param in f.paramlist().param()) {
                    val paramType: Type = (visit(param.type()) as TypeNode).type
                    val paramNode = IdentNode(paramType, param.ident().IDENT().text)
                    paramList.add(paramNode)
                }
            }
            globalFuncTable!![funcName] = FuncNode(returnType, paramList)
        }

        /* then iterate through a list of function declarations to visit the function body */
        for (f in ctx.func()) {
            val funcName: String = f.ident().IDENT().text
            val functionBody: StatNode = visitFunc(f) as StatNode
            globalFuncTable!![funcName]!!.functionBody = functionBody
        }

        /* visit the body of the program and create the root SymbolTable here */
        isMainFunction = true
        symbolTable = SymbolTable(symbolTable)

        val body: StatNode = visit(ctx.stat()) as StatNode
        body.scope = symbolTable
        symbolTable = symbolTable!!.parentSymbolTable
        if (semanticError) {
            exitProcess(SEMANTIC_ERROR_CODE)
        }
        return if (body !is SequenceNode) {
            ProgramNode(globalFuncTable!!, SequenceNode(body))
        } else ProgramNode(globalFuncTable!!, body)
    }

    override fun visitFunc(ctx: FuncContext?): Node {
        val funcNode = globalFuncTable!![ctx!!.ident().IDENT().text]!!

        /* visit the function body */
        expectedFunctionReturn = funcNode.returnType
        symbolTable = SymbolTable(symbolTable)

        for (param in funcNode.paramList!!) {
            semanticError = semanticError or symbolTable!!.add(param!!.name, param)
        }

        val functionBody: StatNode = visit(ctx.stat()) as StatNode
        functionBody.scope = symbolTable
        symbolTable = symbolTable!!.parentSymbolTable

        return functionBody
    }

    override fun visitParam(ctx: ParamContext?): Node {
        val type: TypeNode = visit(ctx!!.type()) as TypeNode
        return IdentNode(type.type, ctx.ident().IDENT().text)
    }

//    override fun visitType(ctx: TypeContext?): Node? {
//        return visitChildren(ctx)
//    }

    /* =========================================================
    *                   Statement Visitors
    * =========================================================
    */

    override fun visitSkipStat(ctx: SkipStatContext?): Node {
        return SkipNode()
    }

    override fun visitDeclareStat(ctx: DeclareStatContext): Node {
        val expr: ExprNode? = visit(ctx.assignrhs()) as ExprNode?
        val varName: String = ctx.ident().IDENT().text
        val varType: Type = (visit(ctx.type()) as TypeNode).type
        currDeclareType = varType

        if (expr != null) {
            val exprType = expr.type
            semanticError = semanticError or typeCheck(ctx.assignrhs(), varName, exprType, varType)
            /* need to set the type of the rhs expression */
            expr.type = varType
        }

        val node: StatNode = DeclareStatNode(varName, expr)
        node.scope = symbolTable

        semanticError = semanticError || symbolTable!!.add(varName, expr)


        return node
    }

    override fun visitAssignStat(ctx: AssignStatContext): Node {

        /* check if the type of lhs and rhs are equal */
        val lhs: ExprNode? = visit(ctx.assignlhs()) as ExprNode?
        val rhs: ExprNode? = visit(ctx.assignrhs()) as ExprNode?

        if (rhs != null && lhs != null) {
            val lhsType = lhs.type
            val rhsType = rhs.type
            semanticError = semanticError or typeCheck(ctx.assignrhs(), lhsType, rhsType!!)
        }

        val node: StatNode = AssignNode(lhs, rhs)
        node.scope = symbolTable

        return node
    }

    override fun visitReadStat(ctx: ReadStatContext): Node {
        val exprNode: ExprNode = visit(ctx.assignlhs()) as ExprNode

        val inputType = exprNode.type
        semanticError = semanticError or typeCheck(ctx.assignlhs(), readStatAllowedTypes, inputType!!)

        val readNode = ReadNode(exprNode)

        readNode.scope = symbolTable

        return readNode
    }

    override fun visitFreeStat(ctx: FreeStatContext?): Node {
        val exprNode: ExprNode = visit(ctx!!.expr()) as ExprNode
        val type: Type? = exprNode.type

        semanticError = semanticError or typeCheck(ctx.expr(), freeStatAllowedTypes, type!!)

        val node: StatNode = FreeNode(exprNode)
        node.scope = symbolTable

        return node
    }

    override fun visitReturnStat(ctx: ReturnStatContext?): Node {
        val returnNum: ExprNode = visit(ctx!!.expr()) as ExprNode

        if (isMainFunction) {
            returnFromMainError(ctx)
            semanticError = true
        }

        val returnType = returnNum.type
        semanticError = semanticError or typeCheck(ctx.expr(), expectedFunctionReturn, returnType!!)

        val node: StatNode = ReturnNode(returnNum)
        node.scope = symbolTable
        return node
    }

    override fun visitExitStat(ctx: ExitStatContext?): Node {
        val exitCode: ExprNode = visit(ctx!!.expr()) as ExprNode
        val exitCodeType: Type? = exitCode.type

        semanticError = semanticError || typeCheck(ctx.expr(), INT_T, exitCodeType!!)

        val node: StatNode = ExitNode(exitCode)
        node.scope = symbolTable

        return node
    }

    override fun visitPrintStat(ctx: PrintStatContext?): Node {
        val printContent: ExprNode? = visit(ctx!!.expr()) as ExprNode?
        val node: StatNode = PrintNode(printContent)

        return node
    }

    override fun visitPrintlnStat(ctx: PrintlnStatContext?): Node {
        val printContent: ExprNode? = visit(ctx!!.expr()) as ExprNode?
        val node: StatNode = PrintlnNode(printContent)
        node.scope = symbolTable

        return node
    }

    override fun visitIfStat(ctx: IfStatContext?): Node {
        // 'if' <expr> than <stat> else <stat>
        val condition: ExprNode = visit(ctx!!.expr()) as ExprNode
        val conditionType = condition.type

        /* check that the condition of if statement is of type boolean */
        semanticError = semanticError or typeCheck(ctx.expr(), BOOL_T, conditionType!!)

        symbolTable = SymbolTable(symbolTable)
        val ifBody: StatNode = visit(ctx.stat(0)) as StatNode
        symbolTable = symbolTable!!.parentSymbolTable

        /* create the StatNode for the else body and generate new child scope */
        symbolTable = SymbolTable(symbolTable)
        val elseBody: StatNode = visit(ctx.stat(1)) as StatNode
        symbolTable = symbolTable!!.parentSymbolTable

        val node: StatNode = IfNode(condition, SequenceNode(ifBody), SequenceNode(elseBody))

        node.scope = symbolTable

        return node
    }

    override fun visitWhileStat(ctx: WhileStatContext?): Node {
        val cond: ExprNode = visit(ctx!!.expr()) as ExprNode
        val condType = cond.type
        semanticError = semanticError || typeCheck(ctx.expr(), BOOL_T, condType!!)

        symbolTable = SymbolTable(symbolTable)
        val doBody = visit(ctx.stat()) as StatNode
        symbolTable = symbolTable!!.parentSymbolTable

        val whileNode: StatNode = WhileNode(cond, SequenceNode(doBody))

        whileNode.scope = symbolTable

        return whileNode
    }

    override fun visitScopeStat(ctx: ScopeStatContext?): Node {
        /* simply create a new SymbolTable to represent a BEGIN ... END statement */
        symbolTable = SymbolTable(symbolTable)
        val body: StatNode = visit(ctx!!.stat()) as StatNode
        val scopeNode = SequenceNode(body)
        scopeNode.scope = symbolTable
        symbolTable = symbolTable!!.parentSymbolTable

        return scopeNode
    }

    override fun visitSequenceStat(ctx: SequenceStatContext?): Node {
        val stat1: StatNode = visit(ctx!!.stat(0)) as StatNode
        val stat2: StatNode = visit(ctx.stat(1)) as StatNode

        val node: StatNode = SequenceNode(stat1, stat2)

        /* ensure all statNode has scope not null */
        node.scope = symbolTable
        return node
    }

    /* =======================================================
     *                  Expression Visitors
     * =======================================================
     */

    override fun visitIdent(ctx: IdentContext?): Node {
        val varName = ctx!!.IDENT().text
        val symbol: Symbol? = symbolTable!!.lookupAll(varName)
        if (symbol == null) {
            ErrorHandler.symbolNotExist(ctx, varName)
        }

        return IdentNode(symbol!!.node!!.type!!, varName, symbol)
    }

    override fun visitArray_elem(ctx: Array_elemContext?): Node {
        val arrayIdent: String = ctx!!.ident().text
        val symbol = symbolTable!!.lookupAll(arrayIdent)

        if (symbol == null) {
            ErrorHandler.symbolNotExist(ctx, arrayIdent)
        }

        val array: ExprNode? = symbol!!.node

        /* special case: if ident is not array, cannot call asArrayType on it, exit directly */
        if (typeCheck(ctx, ARRAY_T, array!!.type!!)
        ) {
            exitProcess(SEMANTIC_ERROR_CODE)
        }

        val indexList: MutableList<ExprNode> = java.util.ArrayList()

        for (exprContext in ctx.expr()) {
            val index: ExprNode = visit(exprContext) as ExprNode
            semanticError = semanticError || typeCheck(exprContext, INT_T, index.type!!)
            indexList.add(index)
        }

        val arrayType = array.type as ArrayType

        return ArrayElemNode(array, indexList, arrayType.getContentType(), arrayIdent, symbol)
    }

    override fun visitArrayExpr(ctx: ArrayExprContext?): Node {
        return visitArray_elem(ctx!!.array_elem())
    }

    override fun visitIntExpr(ctx: IntExprContext?): Node {
        return IntNode(ctx!!.intLiter().text.toInt())
    }

    override fun visitBoolExpr(ctx: BoolExprContext): Node {
        return BoolNode(ctx.boolLiter().text.equals("true"))
    }

    override fun visitCharExpr(ctx: CharExprContext): Node {
        return CharNode(ctx.charLiter().text)
    }

    override fun visitStrExpr(ctx: StrExprContext): Node {
        return StringNode(ctx.strLiter().text)
    }

    override fun visitPairExpr(ctx: PairExprContext?): Node {
        return PairNode()
    }

    override fun visitUnopExpr(ctx: UnopExprContext): Node {
        val literal: String = ctx.unaryOper().text
        val unop: Utils.Unop? = unopEnumMapping[literal]
        val targetType: Type? = unopTypeMapping[literal]

        /* parsed directly as a negative number(IntNode) */
        val exprText = ctx.expr().text
        if (unop!! == Utils.Unop.MINUS && exprText.toIntOrNull() != null) {
            val intVal: Int = exprText.toInt()
            return IntNode(intVal)
        }

        val expr: ExprNode = visit(ctx.expr()) as ExprNode
        val exprType = expr.type

        semanticError = semanticError || typeCheck(ctx.expr(), targetType, exprType!!)

        return UnopNode(expr, unop)
    }

    override fun visitArithmeticExpr(ctx: ArithmeticExprContext): Node {
        val literal: String = ctx.binaryOper.text
        val binop: Utils.Binop = binopEnumMapping[literal]!!

        val expr1: ExprNode = visit(ctx.expr(0)) as ExprNode
        val expr2: ExprNode = visit(ctx.expr(1)) as ExprNode
        val expr1Type = expr1.type
        val expr2Type = expr2.type

        semanticError =
            semanticError or typeCheck(ctx.expr(0), INT_T, expr1Type!!) or typeCheck(ctx.expr(1), INT_T, expr2Type!!)

        return BinopNode(expr1, expr2, binop)
    }

    override fun visitCmpExpr(ctx: CmpExprContext): Node {
        val literal: String = ctx.binaryOper.text
        val binop: Utils.Binop = CmpEnumMapping[literal]!!

        val expr1: ExprNode = visit(ctx.expr(0)) as ExprNode
        val expr1Type = expr1.type
        val expr2: ExprNode = visit(ctx.expr(1)) as ExprNode
        val expr2Type = expr2.type

        semanticError = semanticError ||
                typeCheck(ctx.expr(0), compareStatAllowedTypes, expr1Type!!) ||
                typeCheck(ctx.expr(1), compareStatAllowedTypes, expr2Type!!) ||
                typeCheck(ctx.expr(0), expr1Type, expr2Type)

        return BinopNode(expr1, expr2, binop)
    }

    override fun visitEqExpr(ctx: EqExprContext): Node {
        val literal: String = ctx.binaryOper.text
        val binop: Utils.Binop = EqEnumMapping[literal]!!

        val expr1: ExprNode = visit(ctx.expr(0)) as ExprNode
        val exrp1Type = expr1.type
        val expr2: ExprNode = visit(ctx.expr(1)) as ExprNode
        val expr2Type = expr2.type

        semanticError = semanticError or typeCheck(ctx.expr(0), exrp1Type, expr2Type!!)

        return BinopNode(expr1, expr2, binop)
    }

    override fun visitAndOrExpr(ctx: AndOrExprContext): Node {
        val literal: String = ctx.binaryOper.text
        val binop: Utils.Binop = LogicOpEnumMapping[literal]!!

        val expr1: ExprNode = visit(ctx.expr(0)) as ExprNode
        val expr1Type = expr1.type
        val expr2: ExprNode = visit(ctx.expr(1)) as ExprNode
        val expr2Type = expr2.type

        semanticError = semanticError ||
                typeCheck(ctx.expr(0), BOOL_T, expr1Type!!) ||
                typeCheck(ctx.expr(1), BOOL_T, expr2Type!!)

        return BinopNode(expr1, expr2, binop)
    }

    override fun visitParenExpr(ctx: ParenExprContext): Node? {
        return visit(ctx.expr())
    }

    override fun visitFstExpr(ctx: FstExprContext): Node {
        val exprNode: ExprNode = visit(ctx.expr()) as ExprNode
        val pairType = exprNode.type
        val pairElemType: Type? = (pairType as PairType).fstType

        semanticError = semanticError or typeCheck(ctx.expr(), PAIR_T, pairType)

        if (pairElemType == null) {
            invalidPairError(ctx.expr())
        }

        return PairElemNode(exprNode, pairElemType).fst()
    }

    override fun visitSndExpr(ctx: SndExprContext): Node {
        val exprNode: ExprNode = visit(ctx.expr()) as ExprNode
        val pairType = exprNode.type
        val pairElemType: Type? = (pairType as PairType).sndType

        semanticError = semanticError or typeCheck(ctx.expr(), PAIR_T, pairType)

        if (pairElemType == null) {
            invalidPairError(ctx.expr())
        }

        return PairElemNode(exprNode, pairElemType).snd()
    }

    override fun visitFunctionCall(ctx: FunctionCallContext): Node {
        val funcName: String = ctx.ident().IDENT().text
        val function = globalFuncTable!![funcName]

        // Check whether function is defined
        if (function == null) {
            ErrorHandler.symbolNotExist(ctx, funcName)
        }

        val params: MutableList<ExprNode> = java.util.ArrayList()

        /* check whether function has same number of parameter */
        val expectedParamNum = function!!.paramList!!.size
        if (expectedParamNum != 0) {
            if (ctx.arglist() == null) {
                invalidFuncArgCount(ctx, expectedParamNum, 0)
            } else if (expectedParamNum != ctx.arglist().expr().size) {
                invalidFuncArgCount(ctx, expectedParamNum, ctx.arglist().expr().size)
            }

            /* given argument number is not 0, generate list */
            for ((exprIndex, e) in ctx.arglist().expr().withIndex()) {
                val param: ExprNode = visit(e) as ExprNode? ?: continue
                val paramType = param.type
                val targetType = function.paramList!![exprIndex]!!.type

                /* check param types */
                semanticError = semanticError or typeCheck(
                    ctx.arglist().expr(exprIndex), targetType,
                    paramType!!
                )
                params.add(param)
            }
        }

        symbolTable = SymbolTable(symbolTable)
        val node: Node = FunctionCallNode(function, params, symbolTable)
        symbolTable = symbolTable!!.parentSymbolTable

        return node
    }

    override fun visitNewPair(ctx: NewPairContext?): Node {
        val fst: ExprNode = visit(ctx!!.expr(0)) as ExprNode
        val snd: ExprNode = visit(ctx.expr(1)) as ExprNode
        return PairNode(fst, snd)
    }

    override fun visitIdentExpr(ctx: IdentExprContext): Node {
        val name: String = ctx.ident().IDENT().text
        val symbol = symbolTable!!.lookupAll(name)
        if (symbol == null) {
            ErrorHandler.symbolNotExist(ctx, name)
        }

        return IdentNode(symbol!!.node!!.type, name, symbol)
    }

    override fun visitArray_type(ctx: Array_typeContext): Node? {
        var type: TypeNode? = null
        if (ctx.array_type() != null) {
            type = visitArray_type(ctx.array_type()) as TypeNode?
        } else if (ctx.base_type() != null) {
            type = visit(ctx.base_type()) as TypeNode?
        } else if (ctx.pair_type() != null) {
            type = visitPair_type(ctx.pair_type()) as TypeNode?
        }
        if (type == null) {
            return null
        }
        return TypeNode(ArrayType(type.type))
    }

    override fun visitArrayLiter(ctx: ArrayLiterContext): Node {
        val length: Int = ctx.expr().size
        if (length == 0) {
            return ArrayNode(currDeclareType, length)
        }
        val firstExpr: ExprNode = visit(ctx.expr(0)) as ExprNode
        val firstContentType = firstExpr.type
        val list: MutableList<ExprNode> = mutableListOf()
        for (context in ctx.expr()) {
            val expr: ExprNode = visit(context) as ExprNode
            val exprType = expr.type
            // Check all type are same
            semanticError = semanticError or typeCheck(context, firstContentType, exprType!!)
            list.add(expr)

        }
        return ArrayNode(firstContentType, list, length)
    }

    override fun visitExprNode(ctx: ExprNodeContext): Node {
        return super.visitExprNode(ctx)
    }

    /* =======================================================
   *                     Type visitors
   * =======================================================
   */
    override fun visitIntType(ctx: IntTypeContext?): Node {
        return TypeNode(INT_T)
    }

    override fun visitBoolType(ctx: BoolTypeContext?): Node {
        return TypeNode(BOOL_T)
    }

    override fun visitCharType(ctx: CharTypeContext?): Node {
        return TypeNode(CHAR_T)
    }

    override fun visitStringType(ctx: StringTypeContext?): Node {
        return TypeNode(STRING_T)
    }

    override fun visitArrayType(ctx: ArrayTypeContext): Node? {
        return visitArray_type(ctx.array_type())
    }

    override fun visitPairType(ctx: PairTypeContext): Node {
        return visitPair_type(ctx.pair_type())
    }

    override fun visitPairElemPairType(ctx: PairElemPairTypeContext?): Node {
        return TypeNode(PairType())
    }

    override fun visitPair_type(ctx: Pair_typeContext): Node {
        val leftChild: TypeNode = visit(ctx.pairElemType(0)) as TypeNode
        val rightChild: TypeNode = visit(ctx.pairElemType(1)) as TypeNode
        val type: Type = PairType(leftChild.type, rightChild.type)
        return TypeNode(type)
    }
}