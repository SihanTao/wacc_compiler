package backend.instructionGenerator;

import backend.instructions.Instruction;
import backend.instructions.Label;
import node.Node;
import node.ProgramNode;
import node.stat.SkipNode;

import java.util.ArrayList;
import java.util.List;

public class InstructionGenerator implements ASTVisitor<Void> {

    private final List<Instruction> instructions;

    public InstructionGenerator() {
        this.instructions = new ArrayList<>();
    }

    @Override
    public Void visit(Node node) {
        return ASTVisitor.super.visit(node);
    }

    @Override
    public Void visitProgramNode(ProgramNode node) {
        /*
        * .text
        *
        * .global main
        * main:
        *   PUSH {lr}
        *   LDR r0, =0
        *   POP {pc}
        *   .ltorg
        * */

        // main:
        instructions.add(new Label("main"));
        // PUSH {lr}

        return null;
    }

    @Override
    public Void visitSkipNode(SkipNode node) {
        return null;
    }
}
