package WACCSemanticAnalyser;

import antlr.WACCParser;
import antlr.WACCParserBaseVisitor;

public class WACCSemanticErrorVisitor<T> extends WACCParserBaseVisitor<T> {

	private ErrorRecorder errorRecorder;
	
	public WACCSemanticErrorVisitor(ErrorRecorder errorRecorder) {
		this.errorRecorder = errorRecorder;
	}

	@Override public T visitStat(WACCParser.StatContext ctx) {
		if (ctx.IF() == null && ctx.stat(1) != null && isClosedStatement(ctx.stat(0))) {
			errorRecorder.addUnreachableCodeError(ctx.stat(1));
		}
		return visitChildren(ctx);
	}

	// helper function to check if a statement is "closed" i.e. does not
	// allow logic to proceed
	private boolean isClosedStatement(WACCParser.StatContext ctx) {
		if (ctx.RETURN() != null) {
			return true;
		} else if (ctx.IF() != null) {
			return isClosedStatement(ctx.stat(0)) && isClosedStatement(ctx.stat(1));
		} else {
		return false;
		}
	}

}
