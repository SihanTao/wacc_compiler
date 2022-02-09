package WACCSyntaxAnalyser;

import antlr.WACCParser;
import antlr.WACCParserBaseVisitor;
import org.antlr.v4.runtime.RecognitionException;

import static antlr.WACCParser.*;

public class WACCSyntaxErrorVisitor<T> extends WACCParserBaseVisitor<T> {

	private WACCParser parser;

	public WACCSyntaxErrorVisitor(WACCParser parser) {
		this.parser = parser;
	}

    @Override public T visitFunc(WACCParser.FuncContext ctx) {
		if (!hasReturnStatement(ctx.stat())) {
			parser.notifyErrorListeners(ctx.getStart(), "Function " + ctx.ident()
			+ " has no adequate return or exit statements", (RecognitionException) null);
		}
		return visitChildren(ctx);
    }

	// helper function to check if a function has appropriate return statements
    private boolean hasReturnStatement(WACCParser.StatContext ctx) {
		if (ctx.RETURN() != null || ctx.EXIT() != null) {
			return true;
		} else if (ctx.IF() != null || (ctx.stat(0) != null && ctx.stat(1) != null)) {
			return hasReturnStatement(ctx.stat(0)) && hasReturnStatement(ctx.stat(1));
		} else {
			return false;
		}
    }

	@Override public T visitIntLiter(WACCParser.IntLiterContext ctx) {
		try {
			int i = Integer.parseInt(ctx.getText());
		} catch(NumberFormatException e) {
			parser.notifyErrorListeners(ctx.getStart(), "Int Literal " + ctx.getText()
			+ "overflowed as it is too large", (RecognitionException) null);
		}
		return visitChildren(ctx);
	}

	@Override public T visitCharLiter(WACCParser.CharLiterContext ctx) {
		char c = ctx.getText().charAt(0);
		if (c > 255) {
			parser.notifyErrorListeners(ctx.getStart(), "Char literal " + ctx.getText()
			+ "is not defined for WACC", (RecognitionException) null);
		}
}
