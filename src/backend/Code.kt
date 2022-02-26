package backend;

import backend.instructions.Instruction;

import java.util.List;

public class Code implements Directive {

  private final List<Instruction> instructions;

  public Code(List<Instruction> instructions) {
    this.instructions = instructions;
  }

  @Override
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("\t.global main");
    for (Instruction instruction : instructions) {
      stringBuilder.append(instruction.toString()).append("\n");
    }
    return stringBuilder.toString();
  }
}
