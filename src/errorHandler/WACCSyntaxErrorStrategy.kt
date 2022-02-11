package errorHandler

import org.antlr.v4.runtime.DefaultErrorStrategy
import org.antlr.v4.runtime.Parser
import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.Token
import kotlin.system.exitProcess

class WACCSyntaxErrorStrategy : DefaultErrorStrategy() {
    override fun reportError(recognizer: Parser?, e: RecognitionException?) {
        super.reportError(recognizer, e)
        exitProcess(SYNTAX_ERROR_CODE)
    }

    /* override recoverInLine to properly exit the program after the ANTLR missingSymbol error */
    override fun recoverInline(recognizer: Parser): Token? {
        super.recoverInline(recognizer)
        recognizer.exitRule()
        exitProcess(SYNTAX_ERROR_CODE)
    }

    /* override reportUnwantedToken to properly exit the program after the ANTLR extraneousInput error */
    override fun reportUnwantedToken(recognizer: Parser?) {
        super.reportUnwantedToken(recognizer)
        exitProcess(SYNTAX_ERROR_CODE)
    }

    companion object{
        val SYNTAX_ERROR_CODE = 100
    }
}