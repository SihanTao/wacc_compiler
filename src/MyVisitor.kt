import antlr.WACCParser
import antlr.WACCParserBaseVisitor
import node.*


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
        val paramList = ArrayList<ParamNode>()
        if (ctx!!.paramlist() != null) {
            for (param in ctx.paramlist().param()) {
                paramList += visit(param) as ParamNode
            }
        }

        val funcNode = FuncNode(
            visit(ctx.type()) as TypeNode,
            visit(ctx.ident()) as IdentNode,
            paramList,
            visit(ctx.stat()) as StatNode
        )
        return funcNode
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
}