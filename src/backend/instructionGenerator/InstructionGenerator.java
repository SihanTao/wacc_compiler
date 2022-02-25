package backend.instructionGenerator;

import node.Node;
import node.ProgramNode;
import node.stat.SkipNode;

public class InstructionGenerator implements ASTVisitor<Void> {

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
        return null;
    }

    @Override
    public Void visitSkipNode(SkipNode node) {
        return null;
    }
}
