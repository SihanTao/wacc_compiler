package errorHandler

import org.antlr.v4.runtime.DefaultErrorStrategy
import org.antlr.v4.runtime.*
import org.antlr.v4.runtime.misc.*

class WACCSyntaxErrorStrategy : DefaultErrorStrategy() {
    fun reportError(parser: Parser, e: RecognitionException) {
        when (e) {
            is NoViableAltException -> {
                reportNoViableAlternative(parser,
                        e as NoViableAltException)
            }
            is FailedPredicateException -> {
                reportFailedPredicate(parser,
                        e as FailedPredicateException)
            }
            else -> {
                parser.notifyErrorListeners(e.getOffendingToken(),
                        e.getMessage(), e)
            }
        }
    }

    private fun reportNoViableAlternative(parser: Parser,
                                          e: NoViableAltException) {
        val tokens: TokenStream = parser.getInputStream()
        val input: String = if (tokens != null) {
            if (e.getStartToken().getType() === -1) {
                "<EOF>"
            } else {
                tokens.getText(e.getStartToken(), e.getOffendingToken())
            }
        } else {
            "<unknown input>"
        }
        val msg = "No viable alternative at input $input"
        parser.notifyErrorListeners(e.getOffendingToken(), msg, e)
    }

    private fun reportFailedPredicate(parser: Parser,
                                      e: FailedPredicateException) {
        val ruleName: String = parser.getRuleNames().get(parser.getContext().getRuleIndex())
        val msg = "Rule " + ruleName + " " + e.getMessage()
        parser.notifyErrorListeners(e.getOffendingToken(), msg, e)
    }

}