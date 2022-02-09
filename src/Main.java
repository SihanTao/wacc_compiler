// import ANTLR's runtime libraries
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

// import ANTLR package
import antlr.*;

import WACCSyntaxAnalyser.WACCSyntaxErrorListener;
import WACCSyntaxAnalyser.WACCSyntaxErrorVisitor;

public class Main {

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

    parser.removeErrorListeners();
    parser.addErrorListener(new WACCSyntaxErrorListener());
    
    ParseTree tree = parser.program();
    new WACCSyntaxErrorVisitor(parser).visit(tree);

  }

}
