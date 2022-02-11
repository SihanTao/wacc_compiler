import antlr.WACCParser;
import antlr.WACCParserBaseVisitor;
import node.expr.ExprNode;
import node.stat.StatNode;
import type.Type;
import type.Utils;

import static antlr.WACCParser.FuncContext;
import static antlr.WACCParser.ProgramContext;

public class WACCSyntaxErrorVisitor<T> extends WACCParserBaseVisitor<T> {

  private static final int CHARACTER_MAX_VALUE = 255;

  private final WACCParser parser;
  private boolean isMainFunction;


  public WACCSyntaxErrorVisitor(WACCParser parser) {
    this.parser = parser;
    this.isMainFunction = false;
  }

  @Override
  public T visitProgram(ProgramContext ctx) {
    isMainFunction = true;

    for (FuncContext f : ctx.func()) {
      String funcName = f.ident().IDENT().getText();

      StatNode functionBody = (StatNode) visitFunc(f);

      /* if the function declaration is not terminated with a return/exit statement, then throw the semantic error */
      if (!functionBody.isReturned()) {
        parser.notifyErrorListeners(
            ctx.getStart(),
            "Function has no adequate return or exit statements",
                null);
      }
    }
    return visitChildren(ctx);
  }

  @Override
  public T visitIntLiter(WACCParser.IntLiterContext ctx) {
    try {
      int i = Integer.parseInt(ctx.getText());
    } catch (NumberFormatException e) {
      parser.notifyErrorListeners(
          ctx.getStart(),
          "Int Literal " + ctx.getText() + " overflowed as it is too large",
              null);
    }
    return visitChildren(ctx);
  }

  @Override
  public T visitCharLiter(WACCParser.CharLiterContext ctx) {
    char c = ctx.getText().charAt(0);
    if (c > CHARACTER_MAX_VALUE) {
      parser.notifyErrorListeners(
          ctx.getStart(),
          "Char literal " + ctx.getText() + " is not defined for WACC",
              null);
    }
    return visitChildren(ctx);
  }

  @Override
  public T visitSequenceStat(WACCParser.SequenceStatContext ctx) {
    StatNode before = (StatNode) visit(ctx.stat(0));
    StatNode after = (StatNode) visit(ctx.stat(1));
    if (!isMainFunction && before.isReturned()) {
      parser.notifyErrorListeners(ctx.getStart(), "Code after return statement", null);
    }

    return visitChildren(ctx);
  }

  @Override
  public T visitPrintlnStat(WACCParser.PrintlnStatContext ctx) {
    return printCharArrayError(ctx);
  }

  @Override
  public T visitPrintStat(WACCParser.PrintStatContext ctx) {
    ExprNode printContent = (ExprNode) visit(ctx.expr());
    Type type = printContent.getType();
    assert type != null;
    if(Utils.typeCheck(ctx, Utils.notPrintable, type)) {
      parser.notifyErrorListeners(ctx.getStart(), "Cannot print char[] directly in WACC", null);
    }
    return visitChildren(ctx);
  }

  private T printCharArrayError(WACCParser.PrintlnStatContext ctx) {
    ExprNode printContent = (ExprNode) visit(ctx.expr());
    Type type = printContent.getType();
    assert type != null;
    if(Utils.typeCheck(ctx, Utils.notPrintable, type)) {
      parser.notifyErrorListeners(ctx.getStart(), "Cannot print char[] directly in WACC", null);
    }
    return visitChildren(ctx);
  }

}
