package WACCSyntaxAnalyser

import org.antlr.v4.runtime.*;

public class WACCSyntaxErrorStrategy extends DefaultErrorStrategy {
	
	@Override
	public void reportError(Parser parser, RecognitionException e) {
		parser.notifyErrorListeners(e.getOffendingToken(), e.getMessage(), e);
	}

}
