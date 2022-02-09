import antlr.WACCParser
import antlr.WACCParserBaseVisitor
import node.*
import node.stat.*
import type.TypeNode


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
        val returnType = visit(ctx!!.type()) as TypeNode
        val paramList = ArrayList<IdentNode>()
        for (param in ctx.paramlist().param()) {
            val paramType: TypeNode = visit(param.type()) as TypeNode
            val paramNode = IdentNode(paramType, param.ident().IDENT().text)
            paramList.add(paramNode)
        }

        val functionBody = visitChildren(ctx) as StatNode

        return FuncNode(returnType, functionBody, paramList)
    }

    override fun visitParam(ctx: WACCParser.ParamContext?): Node {
        return ParamNode(visit(ctx!!.type()) as TypeNode, visit(ctx.ident()) as IdentNode)
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
            visit(ctx!!.type()) as TypeNode,
            visit(ctx.ident()) as IdentNode,
            visit(ctx.assignrhs()) as RhsNode
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

    override fun visitReturnStat(ctx: WACCParser.ReturnStatContext?): Node {
        return super.visitReturnStat(ctx)
    }

    override fun visitExitStat(ctx: WACCParser.ExitStatContext?): Node {
        return super.visitExitStat(ctx)
    }

    override fun visitFreeStat(ctx: WACCParser.FreeStatContext?): Node {
        return super.visitFreeStat(ctx)
    }

    override fun visitPrintlnStat(ctx: WACCParser.PrintlnStatContext?): Node {
        return super.visitPrintlnStat(ctx)
    }

    override fun visitPrintStat(ctx: WACCParser.PrintStatContext?): Node {
        return super.visitPrintStat(ctx)
    }
}