package backend.instructionGenerator

import backend.ARMRegister
import backend.ARMRegisterAllocator
import backend.ASTVisitor
import backend.instructions.*
import backend.instructions.IOInstruction.Companion.addPrint
import backend.instructions.addressing.ImmAddressing
import backend.instructions.addressing.LabelAddressing
import backend.instructions.operand.Immediate
import backend.instructions.operand.Operand2
import node.ProgramNode
import node.expr.BoolNode
import node.expr.CharNode
import node.expr.IntNode
import node.expr.StringNode
import node.stat.*
import type.Utils.Companion.BOOL_T
import type.Utils.Companion.CHAR_ARRAY_T
import type.Utils.Companion.CHAR_T
import type.Utils.Companion.INT_T
import type.Utils.Companion.STRING_T

class InstructionGenerator : ASTVisitor<Void?> {

    private val instructions: MutableList<Instruction>
    private val msgLabelGenerator: LabelGenerator = LabelGenerator("msg_")
    val dataSegment: MutableMap<Label, String>
    private val existedHelperFunction: MutableSet<IOInstruction>

    // The list stores the instructions of helper functions
    private val armHelperFunctions: MutableList<Instruction>

    init {
        instructions = ArrayList()
        dataSegment = HashMap()
        existedHelperFunction = HashSet()
        armHelperFunctions = ArrayList()
    }

    fun getInstructions(): MutableList<Instruction> {
        instructions.addAll(armHelperFunctions)
        return instructions
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
        visit(node.exitCode)
        // MOV r0, r4
        instructions.add(Mov(ARMRegister.R0, Operand2(ARMRegister.R4)))
        // BL exit
        instructions.add(BL(SyscallInstruction.EXIT.toString()))

        return null
    }

    override fun visitSequenceNode(node: SequenceNode): Void? {
        val nodes: List<StatNode> = node.body

        // visit all the nodes
        for (elem in nodes) {
            visit(elem)
        }

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

    override fun visitPrintNode(node: PrintNode): Void? {
        visit(node.expr!!)
        instructions.add(
            Mov(
                ARMRegister.R0,
                Operand2(ARMRegisterAllocator.curr())
            )
        )

        val io: IOInstruction = when (node.expr.type!!) {
            STRING_T, CHAR_ARRAY_T -> IOInstruction.PRINT_STRING
            INT_T -> IOInstruction.PRINT_INT
            CHAR_T -> IOInstruction.PRINT_CHAR
            BOOL_T -> IOInstruction.PRINT_BOOL
            else -> TODO("NOT IMPLEMENTED YET in visitPrintNode")
        }

        instructions.add(BL(io.toString()))

        checkAndAddPrint(io)

        ARMRegisterAllocator.free()

        return null
    }

    override fun visitPrintlnNode(node: PrintlnNode): Void? {
        visit(PrintNode(node.expr))
        instructions.add(BL(IOInstruction.PRINT_LN.toString()))
        val io = IOInstruction.PRINT_LN

        checkAndAddPrint(io)

        return null
    }

    private fun checkAndAddPrint(io: IOInstruction) {
        if (!existedHelperFunction.contains(io)) {
            existedHelperFunction.add(io)
            val helperFunctions = addPrint(io, labelGenerator = msgLabelGenerator, dataSegment = dataSegment)
            armHelperFunctions.addAll(helperFunctions)
        }
    }

    override fun visitStringNode(node: StringNode): Void? {
        val str = node.string
        val label = msgLabelGenerator.getLabel()
        dataSegment[label] = str

        instructions.add(LDR(ARMRegisterAllocator.allocate(), LabelAddressing(label)))

        return null
    }

    override fun visitBoolNode(node: BoolNode): Void? {
        val register = ARMRegisterAllocator.allocate()
        val boolValue: Int = if (node.value) 1 else 0
        val operand2 = Operand2(boolValue)
        instructions.add(Mov(register, operand2))
        return null
    }

    override fun visitCharNode(node: CharNode): Void? {
        val register = ARMRegisterAllocator.allocate()
        instructions.add(Mov(register, Operand2(Immediate(node.char.code, true))))
        return null
    }

    override fun visitIfNode(node: IfNode): Void? {
        TODO("Not yet implemented")
    }
}