package WACCSemanticAnalyser;

import antlr.WACCParser;
import antlr.WACCParserBaseVisitor;

public class WACCSemanticErrorVisitor<T> extends WACCParserBaseVisitor<T> {

	private ErrorRecorder errorRecorder;
	
	public WACCSemanticErrorVisitor(ErrorRecorder errorRecorder) {
		this.errorRecorder = errorRecorder;
	}

}
