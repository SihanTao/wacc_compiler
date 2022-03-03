package frontend

import antlr.WACCParser
import antlr.WACCParserBaseVisitor
import antlr.WACCParser.IntLiterContext
import java.lang.NumberFormatException
import antlr.WACCParser.CharLiterContext
import antlr.WACCParser.SequenceStatContext
import antlr.WACCParser.PrintlnStatContext
import antlr.WACCParser.PrintStatContext

typealias isEndReturnedOrExited = Boolean

class WACCSyntaxErrorVisitor(private val parser: WACCParser) : WACCParserBaseVisitor<isEndReturnedOrExited>() {

    override fun visitFunc(ctx: WACCParser.FuncContext): isEndReturnedOrExited {

        /* visit the function body */

        val functionBody: isEndReturnedOrExited = visit(ctx.stat())
        if (!functionBody) {
            parser.notifyErrorListeners(
                    ctx.getStart(),
                    "Function "+ ctx.ident().IDENT().text + " is not ended with a return or an exit statement",
                    null
            )
            return false
        }


        return true
    }

    override fun visitIntLiter(ctx: IntLiterContext): isEndReturnedOrExited {
        try {
            ctx.text.toInt()
        } catch (e: NumberFormatException) {
            parser.notifyErrorListeners(
                ctx.getStart(),
                "Integer value " + ctx.text + " is too large for a 32-bit signed integer",
                null
            )
        }
        return false
    }

    override fun visitCharLiter(ctx: CharLiterContext): isEndReturnedOrExited {
        val c = ctx.text[0]

        if (c.code > CHARACTER_MAX_VALUE) {
            parser.notifyErrorListeners(
                ctx.getStart(),
                "Char literal " + ctx.text + " is not defined for WACC",
                null
            )
        }
        return false
    }

    override fun visitSkipStat(ctx: WACCParser.SkipStatContext?): isEndReturnedOrExited {
        return false
    }

    override fun visitDeclareStat(ctx: WACCParser.DeclareStatContext?): isEndReturnedOrExited {
        super.visitDeclareStat(ctx)
        return false
    }

    override fun visitAssignStat(ctx: WACCParser.AssignStatContext?): isEndReturnedOrExited {
        super.visitAssignStat(ctx)
        return false
    }

    override fun visitReadStat(ctx: WACCParser.ReadStatContext?): isEndReturnedOrExited {
        super.visitReadStat(ctx)
        return false
    }

    override fun visitFreeStat(ctx: WACCParser.FreeStatContext?): isEndReturnedOrExited {
        super.visitFreeStat(ctx)
        return false
    }

    override fun visitReturnStat(ctx: WACCParser.ReturnStatContext?): isEndReturnedOrExited {
        super.visitReturnStat(ctx)
        return true
    }

    override fun visitExitStat(ctx: WACCParser.ExitStatContext?): isEndReturnedOrExited {
        super.visitExitStat(ctx)
        return true
    }

    override fun visitWhileStat(ctx: WACCParser.WhileStatContext?): isEndReturnedOrExited {
        super.visitWhileStat(ctx)
        return false
    }

    override fun visitScopeStat(ctx: WACCParser.ScopeStatContext?): isEndReturnedOrExited {
        super.visitScopeStat(ctx)
        return false
    }


    override fun visitSequenceStat(ctx: SequenceStatContext): isEndReturnedOrExited {
        var res = false
        if (ctx.stat(1) != null && visit(ctx.stat(1))  != null) res = visit(ctx.stat(1))

        return res
    }

    override fun visitIfStat(ctx: WACCParser.IfStatContext): isEndReturnedOrExited {
        super.visitIfStat(ctx)
        val ifBody = if (ctx.stat(0) != null) visit(ctx.stat(0)) else false
        val elseBody = if (ctx.stat(1) != null) visit(ctx.stat(1)) else false

        return ifBody && elseBody
    }


    override fun visitPrintlnStat(ctx: PrintlnStatContext): isEndReturnedOrExited {
        super.visitPrintlnStat(ctx)
        return false
    }

    override fun visitPrintStat(ctx: PrintStatContext): isEndReturnedOrExited {
        super.visitPrintStat(ctx)
        return false
    }

    companion object {
        private const val CHARACTER_MAX_VALUE = 255
    }
}