package backend.instructionGenerator

import backend.ARMRegister
import backend.instructions.*
import backend.instructions.Addressing.ImmAddressing
import node.Node
import node.ProgramNode
import node.stat.SkipNode
import java.util.ArrayList
import java.util.List

class InstructionGenerator : ASTVisitor<Void?> {
    fun getInstructions(): List<Instruction> {
        return instructions
    }

    private val instructions: List<Instruction>

    init {
        instructions = ArrayList()
    }

    @Override
    fun visit(node: Node?): Void {
        return super@ASTVisitor.visit(node)
    }

    @Override
    fun visitProgramNode(node: ProgramNode?): Void? {
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
        instructions.add(Label("main"))
        // PUSH {lr}
        instructions.add(Push(ARMRegister.LR))
        // set the exit value:
        instructions.add(LDR(ARMRegister.R0, ImmAddressing(0)))
        // POP {pc}
        instructions.add(Pop(ARMRegister.PC))
        // .ltorg
        instructions.add(LTORG())
        return null
    }

    @Override
    fun visitSkipNode(node: SkipNode?): Void? {
        return null
    }
}