package errorHandler;

import org.antlr.v4.runtime.*;

public class WACCSyntaxErrorListener extends BaseErrorListener {

    @Override
        public void syntaxError(Recognizer<?, ?> recognizer,
                Object offendingSymbol,
                int line, 
                int charPositionInLine,
                String msg,
                RecognitionException e) {

            System.err.println("Syntax Error detected at " + line + ":" +
                    charPositionInLine + " msg: " + msg);

        }
}
