import antlr.WACCParser
import antlr.WACCParser.*
import antlr.WACCParserBaseVisitor
import node.*
import node.expr.*
import node.stat.*
import org.antlr.v4.runtime.ParserRuleContext
import type.ArrayType
import type.Type
import type.Utils
import type.Utils.Companion.unopEnumMapping
import type.Utils.Companion.unopTypeMapping


class MyVisitor() : WACCParserBaseVisitor<Node>() {

    private var symbolTable: SymbolTable? = null
    private var globalFuncTable: Map<String, FuncNode>? = null
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
    override fun visitProgram(ctx: ProgramContext?): Node {
        val functionList = ArrayList<FuncNode>()
        for (f in ctx!!.func()) {
            val funcNode = visitFunc(f) as FuncNode
            functionList.add(funcNode)
        }

        val body = visit(ctx.stat()) as StatNode

        return ProgramNode(functionList, body)
    }

    override fun visitFunc(ctx: WACCParser.FuncContext?): Node {
        val returnType = visit(ctx!!.type()) as Type
        val paramList = ArrayList<IdentNode>()
        for (param in ctx.paramlist().param()) {
            val paramType: Type = visit(param.type()) as Type
            val paramNode = IdentNode(paramType, param.ident().IDENT().text)
            paramList.add(paramNode)
        }

        val functionBody = visitChildren(ctx) as StatNode

        /* if the function declaration is not terminated with a return/exit statement, then throw the semantic error */
        if (!functionBody.isReturned) {
            // TODO: semantic error : function not return
        }

        return FuncNode(returnType, functionBody, paramList)
    }

    override fun visitParam(ctx: ParamContext?): Node {
        return ParamNode(visit(ctx!!.type()) as Type, visit(ctx.ident()) as IdentNode)
    }

    override fun visitType(ctx: TypeContext?): Node {
        return visitChildren(ctx)
    }

    /* =========================================================
    *                   Statement Visitors
    * =========================================================
    */

    override fun visitSkipStat(ctx: SkipStatContext?): Node {
        return SkipNode()
    }

    override fun visitDeclareStat(ctx: DeclareStatContext?): Node {
        val declareStatNode = DeclareStatNode(
            visit(ctx!!.type()) as Type, visit(ctx.ident()) as IdentNode, visit(ctx.assignrhs()) as RhsNode
        )

        return declareStatNode
    }

    override fun visitAssignStat(ctx: AssignStatContext): Node {

        val lhs = visit(ctx.assignlhs()) as LhsNode
        val rhs = visit(ctx.assignrhs()) as RhsNode

        // TODO: check the type of lhs and rhs
        val node = AssignNode(lhs, rhs)
        return node
    }

    override fun visitReadStat(ctx: ReadStatContext?): Node {
        val assignLhs = visit(ctx!!.assignlhs()) as LhsNode
        // TODO: need to check type
        val readNode = ReadNode(assignLhs)
        return readNode
    }

    override fun visitFreeStat(ctx: FreeStatContext?): Node {
        val exprNode: ExprNode = visit(ctx!!.expr()) as ExprNode
        val type: Type? = exprNode.type

        /* TODO: check if the reference has correct type(array or pair) */

        val node: StatNode = FreeNode(exprNode)
        node.scope = symbolTable

        return node
    }

    override fun visitReturnStat(ctx: ReturnStatContext?): Node {
        val exprNode: ExprNode = visit(ctx!!.expr()) as ExprNode
        val type: Type? = exprNode.type

        // TODO: need to check the expected return type is the same

        val node: StatNode = ReturnNode(exprNode)
        node.scope = symbolTable
        return node
    }

    override fun visitExitStat(ctx: ExitStatContext?): Node {
        val exitCode: ExprNode = visit(ctx!!.expr()) as ExprNode
        val exitCodeType: Type? = exitCode.type

        // TODO: type check here : return code must be int

        val node: StatNode = ExitNode(exitCode)
        node.scope = symbolTable

        return node
    }

    override fun visitPrintStat(ctx: PrintStatContext?): Node {
        // TODO: syntax error: cannot print char[] directly
        val printContent: ExprNode = visit(ctx!!.expr()) as ExprNode
        val node: StatNode = PrintNode(printContent)
        node.scope = symbolTable

        return node
    }

    override fun visitPrintlnStat(ctx: PrintlnStatContext?): Node {
        // TODO: syntax error: cannot print char[] directly
        val printContent: ExprNode = visit(ctx!!.expr()) as ExprNode
        val node: StatNode = PrintlnNode(printContent)
        node.scope = symbolTable

        return node
    }

    override fun visitIfStat(ctx: IfStatContext?): Node {
        // 'if' <expr> than <stat> else <stat>

        /* check that the condition of if statement is of type boolean */
        val condition: ExprNode = visit(ctx!!.expr()) as ExprNode
        val conditionType = condition.type
        // TODO: check type bool

        symbolTable = SymbolTable(symbolTable)
        val ifBody: StatNode = visit(ctx.stat(0)) as StatNode
        symbolTable = symbolTable!!.getParentSymbolTable()

        /* create the StatNode for the else body and generate new child scope */
        symbolTable = SymbolTable(symbolTable)
        val elseBody: StatNode = visit(ctx.stat(1)) as StatNode
        symbolTable = symbolTable!!.getParentSymbolTable()

        val node: StatNode = IfNode(condition, ScopeNode(ifBody), ScopeNode(elseBody))

        node.scope = symbolTable

        return node
    }

    override fun visitWhileStat(ctx: WhileStatContext?): Node {
        val cond: ExprNode = visit(ctx!!.expr()) as ExprNode
        // TODO: check type of cond is BOOL
        val condType = cond.type

        symbolTable = SymbolTable(symbolTable)
        val doBody = visit(ctx.stat()) as StatNode
        symbolTable = symbolTable!!.getParentSymbolTable()

        val whileNode : StatNode = WhileNode(cond, ScopeNode(doBody))

        whileNode.scope = symbolTable

        return whileNode
    }

    override fun visitScopeStat(ctx: ScopeStatContext?): Node {

        /* simply create a new SymbolTable to represent a BEGIN ... END statement */
        val curr = SymbolTable(symbolTable)
        val body: StatNode = visit(ctx!!.stat()) as StatNode
        val scopeNode = ScopeNode(body)
        scopeNode.scope = curr

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
        val value: ExprNode? = symbolTable!!.lookupAll(varName)
        if (value == null) {
            // TODO: semantic error: symbol not found
        }

        // TODO: may not be correct
        return IdentNode(value!!.type!!, varName)
    }

    override fun visitArrayElem(ctx: ArrayElemContext?): Node {
        val arrayIdent: String = ctx!!.array_elem().ident().text
        val array: ExprNode? = symbolTable!!.lookupAll(arrayIdent)
        // TODO: typeCheck and Symbol find?

        val indexList: MutableList<ExprNode> = java.util.ArrayList()

        for (exprContext in ctx.array_elem().expr()) {
            val index: ExprNode = visit(exprContext) as ExprNode
            // TODO: check every expr has type int
            val elemType = index.type
            indexList.add(index)
        }

        val arrayType = array?.type as ArrayType

        return ArrayElemNode(array, indexList, arrayType.getContentType())
    }

    override fun visitIntExpr(ctx: IntExprContext?): Node {
        // TODO: check int out of bound in syntax error
        return IntNode(ctx!!.intLiter().text.toInt())
    }

    override fun visitBoolExpr(ctx: BoolExprContext): Node {
        return BoolNode(ctx.boolLiter().text.equals("true"))
    }

    override fun visitCharExpr(ctx: CharExprContext): Node {
        return CharNode(ctx.charLiter().text[0])
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
        semanticError = semanticError or typeCheck(ctx.expr(), targetType, exprType!!)

        return UnopNode(expr, unop)
    }

    override fun visitArithmeticExpr(ctx: ArithmeticExprContext?): Node {
        return super.visitArithmeticExpr(ctx)
    }

    fun typeCheck(ctx: ParserRuleContext?, expected: Type?, actual: Type): Boolean {
        if (actual != expected) {
            ErrorHandler.typeMismatch(ctx!!, expected!!, actual)
            return true
        }
        return false
    }


    fun typeCheck(
        ctx: ParserRuleContext?, varName: String?, expected: Type?,
        actual: Type
    ): Boolean {
        if (actual != expected) {
            ErrorHandler.typeMismatch(ctx!!, varName!!, expected!!, actual)
            return true
        }
        return false
    }
}