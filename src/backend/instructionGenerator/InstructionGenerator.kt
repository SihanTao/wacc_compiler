package backend.instructionGenerator

import backend.ARMRegister
import backend.ARMRegisterAllocator
import backend.ASTVisitor
import backend.instructions.*
import backend.instructions.Addressing.ImmAddressing
import backend.instructions.operand.Operand2
import node.Node
import node.ProgramNode
import node.expr.IntNode
import node.stat.ExitNode
import node.stat.ScopeNode
import node.stat.SkipNode
import node.stat.StatNode

class InstructionGenerator : ASTVisitor<Void?> {

    val instructions: MutableList<Instruction>

    init {
        instructions = ArrayList<Instruction>()
    }

    override fun visit(node: Node): Void? {
        return super.visit(node)
    }

    override fun visitProgramNode(node: ProgramNode): Void? {
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
        // Load the main body
        visit(node.body)
        // set the exit value:
        instructions.add(LDR(ARMRegister.R0, ImmAddressing(0)))
        // POP {pc}
        instructions.add(Pop(ARMRegister.PC))
        // .ltorg
        instructions.add(LTORG())
        return null
    }

    override fun visitSkipNode(node: SkipNode): Void? {
        return null
    }

    override fun visitExitNode(node: ExitNode): Void? {
        /*
            LDR r4, $EXIT_CODE
            MOV r0, r4
            BL exit
         */
        val exitVal = (node.exitCode as IntNode).value

        // LDR r4, $EXIT_CODE
        instructions.add(LDR(ARMRegister.R4, ImmAddressing(exitVal)))
        // MOV r0, r4
        instructions.add(Mov(ARMRegister.R0, Operand2(ARMRegister.R4)))
        // BL exit
        instructions.add(BL("exit"))

        return null
    }

    override fun visitScopeNode(node: ScopeNode): Void? {
        val nodes: List<StatNode> = node.body

        // visit all the nodes
        for (elem in nodes) {
            visit(elem)
        }

        return null
    }

    override fun visitIntNode(node: IntNode): Void? {
        // First allocate the register: start from R4 if
        val register: ARMRegister = ARMRegisterAllocator.allocate()
        instructions.add(LDR(register, ImmAddressing(node.value)))
        return null
    }
}