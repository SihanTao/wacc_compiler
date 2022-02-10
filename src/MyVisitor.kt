import antlr.WACCParser
import antlr.WACCParserBaseVisitor
import node.*
import node.expr.ExprNode
import node.stat.*
import type.Type


class MyVisitor : WACCParserBaseVisitor<Node>() {

    private var symbolTable: SymbolTable? = null

    override fun visitProgram(ctx: WACCParser.ProgramContext?): Node {
        val functionList = ArrayList<FuncNode>()
        for (f in ctx!!.func()) {
            val funcNode = visitFunc(f) as FuncNode
            functionList.add(funcNode)
        }

        val body = visit(ctx.stat()) as StatNode

        return ProgramNode(functionList, body)
    }

    override fun visitFunc(ctx: WACCParser.FuncContext?): Node {
        // TODO: don't want TypeNode
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

    override fun visitParam(ctx: WACCParser.ParamContext?): Node {
        return ParamNode(visit(ctx!!.type()) as Type, visit(ctx.ident()) as IdentNode)
    }

    override fun visitType(ctx: WACCParser.TypeContext?): Node {
        return visitChildren(ctx)
    }

    /* =========================================================
    *                   Statement Visitors
    * =========================================================
    */

    override fun visitSkipStat(ctx: WACCParser.SkipStatContext?): Node {
        return SkipNode()
    }

    override fun visitDeclareStat(ctx: WACCParser.DeclareStatContext?): Node {
        val declareStatNode = DeclareStatNode(
            visit(ctx!!.type()) as Type, visit(ctx.ident()) as IdentNode, visit(ctx.assignrhs()) as RhsNode
        )

        return declareStatNode
    }

    override fun visitAssignStat(ctx: WACCParser.AssignStatContext): Node {

        val lhs = visit(ctx.assignlhs()) as LhsNode
        val rhs = visit(ctx.assignrhs()) as RhsNode

        // TODO: check the type of lhs and rhs
        val node: AssignNode = AssignNode(lhs, rhs)
        return node
    }

    override fun visitReadStat(ctx: WACCParser.ReadStatContext?): Node {
        val assignLhs = visit(ctx!!.assignlhs()) as LhsNode
        // TODO: need to check type
        val readNode = ReadNode(assignLhs)
        return readNode
    }

    override fun visitFreeStat(ctx: WACCParser.FreeStatContext?): Node {
        val exprNode: ExprNode = visit(ctx!!.expr()) as ExprNode
        val type: Type? = exprNode.type

        /* TODO: check if the reference has correct type(array or pair) */

        val node: StatNode = FreeNode(exprNode)
        node.scope = symbolTable

        return node
    }

    override fun visitReturnStat(ctx: WACCParser.ReturnStatContext?): Node {
        val exprNode: ExprNode = visit(ctx!!.expr()) as ExprNode
        val type: Type? = exprNode.type

        // TODO: need to check the expected return type is the same

        val node: StatNode = ReturnNode(exprNode)
        node.scope = symbolTable
        return node
    }

    override fun visitExitStat(ctx: WACCParser.ExitStatContext?): Node {
        val exitCode: ExprNode = visit(ctx!!.expr()) as ExprNode
        val exitCodeType: Type? = exitCode.type

        // TODO: type check here : return code must be int

        val node: StatNode = ExitNode(exitCode)
        node.scope = symbolTable

        return node
    }

    override fun visitPrintStat(ctx: WACCParser.PrintStatContext?): Node {
        // TODO: syntax error: cannot print char[] directly
        val printContent: ExprNode = visit(ctx!!.expr()) as ExprNode
        val node: StatNode = PrintNode(printContent)
        node.scope = symbolTable

        return node
    }

    override fun visitPrintlnStat(ctx: WACCParser.PrintlnStatContext?): Node {
        // TODO: syntax error: cannot print char[] directly
        val printContent: ExprNode = visit(ctx!!.expr()) as ExprNode
        val node: StatNode = PrintlnNode(printContent)
        node.scope = symbolTable

        return node
    }

    override fun visitIfStat(ctx: WACCParser.IfStatContext?): Node {
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

    override fun visitWhileStat(ctx: WACCParser.WhileStatContext?): Node {
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

    override fun visitScopeStat(ctx: WACCParser.ScopeStatContext?): Node {
        return super.visitScopeStat(ctx)
    }

    override fun visitSequenceStat(ctx: WACCParser.SequenceStatContext?): Node {
        return super.visitSequenceStat(ctx)
    }
}