package errorHandler

import org.antlr.v4.runtime.DefaultErrorStrategy
import org.antlr.v4.runtime.Parser
import org.antlr.v4.runtime.RecognitionException

class WACCSyntaxErrorStrategy : DefaultErrorStrategy() {
//    override fun reportError(parser: Parser, e: RecognitionException) {
//        if (e.message == null) {
//            parser.notifyErrorListeners(e.offendingToken, "error", e)
//        } else {
//            parser.notifyErrorListeners(e.offendingToken, e.message, e)
//        }
//    }
//    return
}