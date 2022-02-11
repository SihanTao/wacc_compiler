import antlr.WACCParser
import errorHandler.WACCSyntaxErrorStrategy.Companion.SYNTAX_ERROR_CODE
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.tree.TerminalNode
import type.Type
import kotlin.system.exitProcess

class ErrorHandler private constructor() {
    /**
     * SemanticErrorHandler will check all possible semantic/syntax errors during the SemanticChecker
     * visit. Notice that some error handler functions will not exit with status directly in order to
     * support checking of multiple errors in the same program.
     */


    /* add a private constructor to prevent this class from instantization */
    init {
        throw IllegalStateException("SemanticErrorHandler cannot be instantiated!")
    }

    companion object {
        const val SEMANTIC_ERROR_CODE = 200

        fun typeMismatch(ctx: ParserRuleContext, expected: Type, actual: Type) {
            val msg = "Incompatible type at " + ctx.text + ": Expected type " + expected +
                    ", but the actual type is " + actual
            errorHandler(ctx, msg)
        }

        fun typeMismatch(ctx: ParserRuleContext, expected: Set<Type?>, actual: Type) {
            val msg = "Incompatible type at " + ctx.text + ": Expected types are " + expected +
                    ", but the actual type is " + actual
            errorHandler(ctx, msg)
        }

        fun typeMismatch(ctx: ParserRuleContext, ident: String, expected: Type, actual: Type) {
            val msg = "Incompatible type at " + ctx.text + ": Expected type " + expected +
                    " for variable " + ident + ", but the actual type is " + actual
            errorHandler(ctx, msg)
        }

        fun invalidFuncArgCount(ctx: ParserRuleContext?, expected: Int, actual: Int) {
            val msg = ("Invalid number of arguments: Expected " + expected + " argument(s), but actual count is "
                    + actual + "argument(s)")
            errorHandler(ctx, msg)
            exitProcess(SEMANTIC_ERROR_CODE)
        }

        fun symbolNotExist(ctx: ParserRuleContext?, ident: String) {
            val msg = "Symbol $ident is not found in the current scope of the program"
            errorHandler(ctx, msg)
            exitProcess(SEMANTIC_ERROR_CODE)
        }

        fun symbolRedeclare(ctx: ParserRuleContext?, ident: String) {
            val msg = "Symbol $ident has already been declared in the current scope of the program"
            errorHandler(ctx, msg)
        }

        fun indexOutOfBoundError(ctx: ParserRuleContext?, type: Type, indexDepth: Int) {
            val msg = "Array declared as $type, but called with index depth $indexDepth"
            errorHandler(ctx, msg)
            exitProcess(SEMANTIC_ERROR_CODE)
        }

        fun returnFromMainError(ctx: ParserRuleContext?) {
            val msg = "Call return in main function body is not allowed"
            errorHandler(ctx, msg)
        }

        fun invalidPairError(ctx: ParserRuleContext?) {
            val msg = "Calling fst/snd on uninitialised pair expr is not allowed"
            errorHandler(ctx, msg)
            exitProcess(SEMANTIC_ERROR_CODE)
        }

        fun integerRangeError(ctx: ParserRuleContext?, intText: String) {
            val msg = "Integer $intText format not compatible with 32bit int"
            errorHandler(ctx, msg)
            exitProcess(SYNTAX_ERROR_CODE)
        }

        fun charOperatorRangeError(ctx: ParserRuleContext?, intText: String) {
            val msg = ("chr operator will only accept integer in the range of 0-127, but the actual integer is "
                    + intText)
            errorHandler(ctx, msg)
            exitProcess(SYNTAX_ERROR_CODE)
        }

        fun invalidFunctionReturnExit(ctx: ParserRuleContext?, funcName: String) {
            val msg = "Function $funcName has not returned or exited properly."
            errorHandler(ctx, msg)
            exitProcess(SYNTAX_ERROR_CODE)
        }

        fun functionJunkAfterReturn(ctx: WACCParser.SequenceStatContext?) {
            val msg = "Other statements exist after function return statement."
            errorHandler(ctx, msg)
            exitProcess(SYNTAX_ERROR_CODE)
        }

        /* private common handler of all types of errors */
        private fun errorHandler(ctx: ParserRuleContext?, msg: String) {
            val lineNum: Int
            val charPos: Int

            /* when ctx is null, it indicates that there is a funcJunk error or symbolRedeclare error
           in SemanticChecker */
            if (ctx == null) {
                System.err.println(msg)
                return
            }
            if (ctx is TerminalNode) {
                lineNum = (ctx as TerminalNode).symbol.line
                charPos = (ctx as TerminalNode).symbol.charPositionInLine
            } else {
                lineNum = ctx.getStart().line
                charPos = ctx.getStart().charPositionInLine
            }

            /* print line number and char position before the error message */
            System.err.println("line $lineNum:$charPos $msg")
        }
    }
}