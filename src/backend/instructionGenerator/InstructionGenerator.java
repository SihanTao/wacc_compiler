package backend.instructionGenerator;

import backend.ARMRegister;
import backend.instructions.*;
import backend.instructions.Addressing.ImmAddressing;
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
        instructions.add(new Push(ARMRegister.LR));
        // set the exit value:
        instructions.add(new LDR(ARMRegister.R0, new ImmAddressing(0)));
        // POP {pc}
        instructions.add(new Pop(ARMRegister.PC));
        // .ltorg

        return null;
    }

    @Override
    public Void visitSkipNode(SkipNode node) {
        return null;
    }
}
