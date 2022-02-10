// import ANTLR's runtime libraries
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

// import ANTLR package
import antlr.*;

import WACCSyntaxAnalyser.WACCSyntaxErrorListener;
import WACCSyntaxAnalyser.WACCSyntaxErrorVisitor;
import WACCSyntaxAnalyser.WACCSyntaxErrorStrategy;

import WACCSemanticAnalyser.WACCSemanticErrorVisitor;
import WACCSemanticAnalyser.ErrorRecorder;

public class Main {

	private static final int SYNTAX_ERROR_EXIT_CODE = 100;
	private static final int SEMANTIC_ERROR_EXIT_CODE = 200;

	public static void main(String[] args) throws Exception {

		ANTLRInputStream input;
		if (args.length == 0) {
			// Read from standard in if file not supplied
			input = new ANTLRInputStream(System.in);
		} else {
			input = new ANTLRFileStream(args[0]);
		}

		WACCLexer lexer = new WACCLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		WACCParser parser = new WACCParser(tokens);

		parser.setErrorHandler(new WACCSyntaxErrorStrategy());

		parser.removeErrorListeners();
		parser.addErrorListener(new WACCSyntaxErrorListener());

		ParseTree tree = parser.program();
		new WACCSyntaxErrorVisitor(parser).visit(tree);

		if (parser.getNumberOfSyntaxErrors() != 0) {
			System.out.println(parser.getNumberOfSyntaxErrors() + " syntax errors detected, "
				+ " failing with exit code " + SYNTAX_ERROR_EXIT_CODE);
			System.exit(SYNTAX_ERROR_EXIT_CODE);
		}

		ErrorRecorder semanticErrorRecorder = new ErrorRecorder();
		new WACCSemanticErrorVisitor(semanticErrorRecorder).visit(tree);

		if (semanticErrorRecorder.getErrors() != 0) {
			System.out.println(semanticErrorRecorder.getErrors() + " semantic errors detected, "
				+ " failing with exit code " + SEMANTIC_ERROR_EXIT_CODE);
			System.exit(SEMANTIC_ERROR_EXIT_CODE);
		}

	}

}

