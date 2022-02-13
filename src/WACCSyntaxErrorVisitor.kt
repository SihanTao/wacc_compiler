import type.Utils.Companion.typeCheck
import antlr.WACCParser
import antlr.WACCParserBaseVisitor
import antlr.WACCParser.ProgramContext
import antlr.WACCParser.IntLiterContext
import java.lang.NumberFormatException
import antlr.WACCParser.CharLiterContext
import antlr.WACCParser.SequenceStatContext
import antlr.WACCParser.PrintlnStatContext
import antlr.WACCParser.PrintStatContext
import node.Node
import node.expr.CharNode
import node.expr.ExprNode
import node.expr.IntNode
import node.stat.*
import type.Utils.Companion.notPrintable

class WACCSyntaxErrorVisitor(private val parser: WACCParser) : WACCParserBaseVisitor<Node>() {
//    private var isMainFunction = false
//    override fun visitProgram(ctx: ProgramContext): Node? {
//        isMainFunction = true
//        for (f in ctx.func()) {
//            val functionBody = visitFunc(f) as StatNode?
//
//            /* if the function declaration is not terminated with a return/exit statement, then throw the semantic error */
//            if (!functionBody!!.isReturned) {
//                parser.notifyErrorListeners(
//                    ctx.getStart(),
//                    "Function has no adequate return or exit statements",
//                    null
//                )
//            }
//        }
//        return null
//    }

    override fun visitFunc(ctx: WACCParser.FuncContext): Node {

        /* visit the function body */

        val functionBody: StatNode = visit(ctx.stat()) as StatNode
        if (!functionBody.isReturned) {
            parser.notifyErrorListeners(
                    ctx.getStart(),
                    "Function func is not ended with a return or an exit statement",
                    null
            )
        }


        return functionBody
    }

    override fun visitIntLiter(ctx: IntLiterContext): Node? {
//        println("In IntLiter")
//        println(ctx.getStart().text)
        try {
            ctx.text.toInt()
        } catch (e: NumberFormatException) {
            parser.notifyErrorListeners(
                ctx.getStart(),
                "Int Literal " + ctx.text + " overflowed as it is too large",
                null
            )
        }
        return IntNode(0)
    }

    override fun visitCharLiter(ctx: CharLiterContext): Node {
        val c = ctx.text[0]
        /*
            Fixed an error
         */
        if (c.toInt() > CHARACTER_MAX_VALUE) {
            parser.notifyErrorListeners(
                ctx.getStart(),
                "Char literal " + ctx.text + " is not defined for WACC",
                null
            )
        }
        return CharNode('x')
    }

    override fun visitSkipStat(ctx: WACCParser.SkipStatContext?): Node {
        return SkipNode()
    }

    override fun visitDeclareStat(ctx: WACCParser.DeclareStatContext?): Node {
        super.visitDeclareStat(ctx)
        return DeclareStatNode("some identifier", null);
    }

    override fun visitAssignStat(ctx: WACCParser.AssignStatContext?): Node {
        super.visitAssignStat(ctx)
        return AssignNode(null, null)
    }

    override fun visitReadStat(ctx: WACCParser.ReadStatContext?): Node {
        super.visitReadStat(ctx)
        return ReadNode(IntNode(0))
    }

    override fun visitFreeStat(ctx: WACCParser.FreeStatContext?): Node {
        super.visitFreeStat(ctx)
        return FreeNode(IntNode(0))
    }

    override fun visitReturnStat(ctx: WACCParser.ReturnStatContext?): Node {
        super.visitReturnStat(ctx)
        return ReturnNode(IntNode(0))
    }

    override fun visitExitStat(ctx: WACCParser.ExitStatContext?): Node {
        super.visitExitStat(ctx)
        return ExitNode(IntNode(0))
    }

    override fun visitWhileStat(ctx: WACCParser.WhileStatContext?): Node {
        super.visitWhileStat(ctx)
        return WhileNode(IntNode(0), SkipNode())
    }

    override fun visitScopeStat(ctx: WACCParser.ScopeStatContext?): Node {
        super.visitScopeStat(ctx)
        return ScopeNode(SkipNode());
    }


    override fun visitSequenceStat(ctx: SequenceStatContext): Node {
//        println("HERE!!!!!!!!!!!!")
//        println(ctx.stat().toString())
//        val after = visit(ctx.stat(1)) as StatNode?
//        if (!isMainFunction && before!!.isReturned) {
//            parser.notifyErrorListeners(ctx.getStart(), "Code after return statement", null)
//        }
//        return SequenceNode(before, after)
        val stat2: StatNode = visit(ctx.stat(1)) as StatNode
        val statNode: StatNode = SequenceNode(null, stat2)

        if (stat2.isReturned) {
            statNode.isReturned = true;
        }

        return statNode
    }

    override fun visitIfStat(ctx: WACCParser.IfStatContext): Node {

        super.visitIfStat(ctx)
        val ifBody: StatNode = visit(ctx.stat(0)) as StatNode
        val elseBody: StatNode = visit(ctx.stat(1)) as StatNode
        val ifNode = IfNode(IntNode(0), ifBody, elseBody)

        if (ifBody.isReturned && elseBody.isReturned) {
            ifNode.isReturned = true
        }
        return ifNode
    }


    override fun visitPrintlnStat(ctx: PrintlnStatContext): Node? {
//        val printContent = visit(ctx.expr()) as ExprNode?
//        val type = printContent?.type!!
//        if (typeCheck(ctx, notPrintable, type)) {
//            parser.notifyErrorListeners(ctx.getStart(), "Cannot print char[] directly in WACC", null)
//        }
//        return visitChildren(ctx)
        super.visitPrintlnStat(ctx)
        return PrintlnNode(null);
    }

    override fun visitPrintStat(ctx: PrintStatContext): Node? {
//        println("In visitPrintStat")
//        println(ctx.getStart().line.toString() + ":" + ctx.getStart().charPositionInLine + ctx.expr().text)
//        val printContent = visit(ctx.expr()) as ExprNode?
//
//        val type = printContent?.type
//        if (typeCheck(ctx, notPrintable, type!!)) {
//            parser.notifyErrorListeners(ctx.getStart(), "Cannot print char[] directly in WACC", null)
//        }
//        return visitChildren(ctx)
        return PrintNode(null);
    }

//
//    private fun printCharArrayError(ctx: PrintlnStatContext): Node? {
//        val printContent = visit(ctx.expr()) as ExprNode?
//        val type = printContent?.type!!
//        if (typeCheck(ctx, notPrintable, type)) {
//            parser.notifyErrorListeners(ctx.getStart(), "Cannot print char[] directly in WACC", null)
//        }
//        return visitChildren(ctx)
//    }

    companion object {
        private const val CHARACTER_MAX_VALUE = 255
    }
}