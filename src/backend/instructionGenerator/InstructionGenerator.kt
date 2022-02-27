package backend.instructionGenerator

import backend.ARMRegister
import backend.ARMRegisterAllocator
import backend.ASTVisitor
import backend.instructions.*
import backend.instructions.addressing.ImmAddressing
import backend.instructions.addressing.LabelAddressing
import backend.instructions.operand.Operand2
import node.ProgramNode
import node.expr.IntNode
import node.expr.StringNode
import node.stat.*
import type.Type
import type.Utils.Companion.BOOL_T
import type.Utils.Companion.CHAR_T
import type.Utils.Companion.INT_T
import type.Utils.Companion.STRING_T

class InstructionGenerator : ASTVisitor<Void?> {

    val instructions: MutableList<Instruction>
    val msgLabelGenerator: LabelGenerator = LabelGenerator("msg_")
    private val dataSegment: MutableMap<Label, String>

    init {
        instructions = ArrayList()
        dataSegment = HashMap()
    }

    private val typeRoutineMap: Map<Type, IOInstruction> =
        mapOf(
            INT_T to IOInstruction.PRINT_INT,
            CHAR_T to IOInstruction.PRINT_CHAR,
            BOOL_T to IOInstruction.PRINT_BOOL,
            STRING_T to IOInstruction.PRINT_STRING,
        )

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

    override fun visitPrintNode(node: PrintNode): Void? {
        /*
            0	.data
            1
            2	msg_0:
            3		.word 13
            4		.ascii	"Hello World!\n"
            5	msg_1:
            6		.word 5
            7		.ascii	"%.*s\0"
            8
            9	.text
            10
            11	.global main
            12	main:
            13		PUSH {lr}
            14		LDR r4, =msg_0
            15		MOV r0, r4
            16		BL p_print_string
            17		LDR r0, =0
            18		POP {pc}
            19		.ltorg
            20	p_print_string:
            21		PUSH {lr}
            22		LDR r1, [r0]
            23		ADD r2, r0, #4
            24		LDR r0, =msg_1
            25		ADD r0, r0, #4
            26		BL printf
            27		MOV r0, #0
            28		BL fflush
            29		POP {pc}
            30
        * */
        visit(node.expr!!)
        instructions.add(
            Mov(
                ARMRegister.R0,
                Operand2(ARMRegisterAllocator.curr())
            )
        )

        val type: Type = node.expr.type!!
        val io: IOInstruction? = typeRoutineMap[type]

        instructions.add(BL(io.toString()))

        // TODO: branch not added

        ARMRegisterAllocator.free()

        return null
    }

    override fun visitStringNode(node: StringNode): Void? {
        val str = node.string
        val label = msgLabelGenerator.getLabel()
        dataSegment[label] = str

        instructions.add(LDR(ARMRegisterAllocator.allocate(), LabelAddressing(label)))

        return null
    }
}