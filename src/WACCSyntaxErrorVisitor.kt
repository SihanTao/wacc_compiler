import type.Utils.Companion.typeCheck
import antlr.WACCParser
import antlr.WACCParserBaseVisitor
import antlr.WACCParser.ProgramContext
import node.stat.StatNode
import antlr.WACCParser.IntLiterContext
import java.lang.NumberFormatException
import antlr.WACCParser.CharLiterContext
import antlr.WACCParser.SequenceStatContext
import antlr.WACCParser.PrintlnStatContext
import antlr.WACCParser.PrintStatContext
import node.Node
import node.expr.ExprNode
import node.stat.SequenceNode
import type.Utils.Companion.notPrintable

class WACCSyntaxErrorVisitor(private val parser: WACCParser) : WACCParserBaseVisitor<Node>() {
    private var isMainFunction = false
    override fun visitProgram(ctx: ProgramContext): Node? {
        isMainFunction = true
        for (f in ctx.func()) {
            val functionBody = visitFunc(f) as StatNode?

            /* if the function declaration is not terminated with a return/exit statement, then throw the semantic error */
            if (!functionBody!!.isReturned) {
                parser.notifyErrorListeners(
                    ctx.getStart(),
                    "Function has no adequate return or exit statements",
                    null
                )
            }
        }
        return null
    }

    override fun visitIntLiter(ctx: IntLiterContext): Node? {
        println("In IntLiter")
        println(ctx.getStart().text)
        try {
            ctx.text.toInt()
        } catch (e: NumberFormatException) {
            parser.notifyErrorListeners(
                ctx.getStart(),
                "Int Literal " + ctx.text + " overflowed as it is too large",
                null
            )
        }
        return null
    }

    override fun visitCharLiter(ctx: CharLiterContext): Node? {
        val c = ctx.text[0]
        if (c.code > CHARACTER_MAX_VALUE) {
            parser.notifyErrorListeners(
                ctx.getStart(),
                "Char literal " + ctx.text + " is not defined for WACC",
                null
            )
        }
        return null
    }

    override fun visitSequenceStat(ctx: SequenceStatContext): Node {
        println("HERE!!!!!!!!!!!!")
        println(ctx.stat().toString())
        val before = visit(ctx.stat(0)) as StatNode?
        val after = visit(ctx.stat(1)) as StatNode?
        if (!isMainFunction && before!!.isReturned) {
            parser.notifyErrorListeners(ctx.getStart(), "Code after return statement", null)
        }
        return SequenceNode(before, after)
    }

    override fun visitPrintlnStat(ctx: PrintlnStatContext): Node? {
        val printContent = visit(ctx.expr()) as ExprNode?
        val type = printContent?.type!!
        if (typeCheck(ctx, notPrintable, type)) {
            parser.notifyErrorListeners(ctx.getStart(), "Cannot print char[] directly in WACC", null)
        }
        return visitChildren(ctx)
    }

    override fun visitPrintStat(ctx: PrintStatContext): Node? {
        println("In visitPrintStat")
        println(ctx.getStart().line.toString() + ":" + ctx.getStart().charPositionInLine + ctx.expr().text)
        val printContent = visit(ctx.expr()) as ExprNode?

        val type = printContent?.type
        if (typeCheck(ctx, notPrintable, type!!)) {
            parser.notifyErrorListeners(ctx.getStart(), "Cannot print char[] directly in WACC", null)
        }
        return visitChildren(ctx)
    }

    private fun printCharArrayError(ctx: PrintlnStatContext): Node? {
        val printContent = visit(ctx.expr()) as ExprNode?
        val type = printContent?.type!!
        if (typeCheck(ctx, notPrintable, type)) {
            parser.notifyErrorListeners(ctx.getStart(), "Cannot print char[] directly in WACC", null)
        }
        return visitChildren(ctx)
    }

    companion object {
        private const val CHARACTER_MAX_VALUE = 255
    }
}