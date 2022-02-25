package backend;

import java.util.ArrayList;
import java.util.List;

public class CodeGenerator {
  private List<Directive> directives;

  public CodeGenerator(Data data, Text text, Code code) {
    this.directives = List.of(data, text, code);
  }

  public String generate() {
    StringBuilder assemblyCodeBuilder = new StringBuilder();
    for (Directive directive : directives) {
      assemblyCodeBuilder.append(directive.toString());
    }
    return assemblyCodeBuilder.toString();
  }
}
