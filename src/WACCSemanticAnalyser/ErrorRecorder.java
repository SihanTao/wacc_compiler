package WACCSemanticAnalyser;

import org.antlr.v4.runtime.ParserRuleContext;

public class ErrorRecorder {

	private int errorCount;

	public ErrorRecorder() {
		this.errorCount = 0;
	}

	public int getErrors() {
		return errorCount;
	}

	public void addUnreachableCodeError(ParserRuleContext ctx) {
		addError(ctx, "unreachable code detected");
	}

	private void addError(ParserRuleContext ctx, String msg) {
		System.out.println("Semantic error detected at " + ctx.getStart().getLine() + ":" + ctx.getStart().getCharPositionInLine() + " msg: " + msg);
		errorCount = errorCount + 1;
	}

}
