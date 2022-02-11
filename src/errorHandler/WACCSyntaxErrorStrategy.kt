package errorHandler

import org.antlr.v4.runtime.DefaultErrorStrategy
import org.antlr.v4.runtime.Parser
import org.antlr.v4.runtime.RecognitionException

class WACCSyntaxErrorStrategy : DefaultErrorStrategy() {
    override fun reportError(parser: Parser, e: RecognitionException) {
        parser.notifyErrorListeners(e.offendingToken, e.message, e)
    }
}