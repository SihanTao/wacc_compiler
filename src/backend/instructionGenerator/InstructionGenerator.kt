package backend.instructionGenerator

import SymbolTable
import backend.ARMRegister
import backend.ARMRegisterAllocator
import backend.ASTVisitor
import backend.Cond
import backend.instructions.*
import backend.instructions.IOInstruction.Companion.addPrint
import backend.instructions.LDR.LdrMode
import backend.instructions.RuntimeErrorInstruction.Companion.addCheckArrayBound
import backend.instructions.addressing.AddressingMode2
import backend.instructions.addressing.AddressingMode2.AddrMode2
import backend.instructions.addressing.ImmAddressing
import backend.instructions.addressing.LabelAddressing
import backend.instructions.arithmeticLogic.Add
import backend.instructions.operand.Immediate
import backend.instructions.operand.Operand2
import backend.instructions.operand.Operand2.Operand2Operator
import node.ProgramNode
import node.expr.*
import node.stat.*
import type.Type.Companion.POINTERSIZE
import type.Type.Companion.WORDSIZE
import type.Utils.Companion.BOOL_T
import type.Utils.Companion.CHAR_ARRAY_T
import type.Utils.Companion.CHAR_T
import type.Utils.Companion.INT_T
import type.Utils.Companion.STRING_T

class InstructionGenerator : ASTVisitor<Void?> {

    // The list stores the instructions of helper functions
    private val armHelperFunctions: MutableList<Instruction> = ArrayList()
    private val instructions: MutableList<Instruction> = ArrayList()

    val dataSegment: MutableMap<Label, String> = HashMap()
    private val existedHelperFunction: MutableSet<Instruction> = HashSet()

    private val msgLabelGenerator: LabelGenerator = LabelGenerator(MSG_LABEL)
    private val branchLabelGenerator = LabelGenerator(BRANCH_LABEL)

    private var currentSymbolTable: SymbolTable? = null

    // Mark the ExprNode is on rhs or lhs
    private var isExprLhs = false

    companion object {
        private const val MAX_STACK_STEP = 1024
        private const val BRANCH_LABEL = "L"
        private const val MSG_LABEL = "msg_"
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

        // SUB enough space in stack
        val stackSize: Int = node.size()
        var temp = stackSize
        while (temp > 0) {
            val stackStep =
                if (temp >= MAX_STACK_STEP) MAX_STACK_STEP else temp
            instructions.add(
                Sub(
                    ARMRegister.SP,
                    ARMRegister.SP,
                    Operand2(stackStep)
                )
            )
            temp -= stackStep
        }

        // visit all the nodes
        // Set up the current Symbol Table
        currentSymbolTable = node.scope
        for (elem in nodes) {
            visit(elem)
        }
        // All stat in the node are visited, returned to parent scope
        currentSymbolTable = currentSymbolTable!!.parentSymbolTable

        // restore the stack
        temp = stackSize
        while (temp > 0) {
            val stackStep = if (temp >= MAX_STACK_STEP) MAX_STACK_STEP else temp
            instructions.add(
                Add(
                    ARMRegister.SP,
                    ARMRegister.SP,
                    Operand2(stackStep)
                )
            )
            temp -= stackStep
        }
        return null
    }

    override fun visitDeclareStatNode(node: DeclareStatNode): Void? {
        visit(node.rhs!!)

        val offset =
            currentSymbolTable!!.tableSize - node.scope!!.lookup(node.identifier)!!.offset

        // Check the size of type to decide to use STR or STRB
        val typeSize = node.rhs.type!!.size()
        val strMode = if (typeSize == 1) STR.STRMode.STRB else STR.STRMode.STR

        instructions.add(
            STR(
                ARMRegisterAllocator.curr(),
                AddressingMode2(
                    AddrMode2.OFFSET,
                    ARMRegister.SP,
                    offset
                ),
                strMode
            )
        )
        ARMRegisterAllocator.free()
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
            val helperFunctions = addPrint(
                io,
                labelGenerator = msgLabelGenerator,
                dataSegment = dataSegment
            )
            armHelperFunctions.addAll(helperFunctions)
        }
    }


    private fun checkAndAddRuntimeError(runtimeErrorInstruction: RuntimeErrorInstruction) {

        if (!existedHelperFunction.contains(runtimeErrorInstruction)) {
            existedHelperFunction.add(runtimeErrorInstruction)
            val helper = when (runtimeErrorInstruction){
                RuntimeErrorInstruction.CHECK_ARRAY_BOUND -> addCheckArrayBound(labelGenerator = msgLabelGenerator, dataSegment)
                RuntimeErrorInstruction.THROW_RUNTIME_ERROR -> TODO()
                RuntimeErrorInstruction.CHECK_DIVIDE_BY_ZERO -> TODO()
                RuntimeErrorInstruction.CHECK_NULL_POINTER -> TODO()
                RuntimeErrorInstruction.THROW_OVERFLOW_ERROR -> TODO()
                RuntimeErrorInstruction.FREE_ARRAY -> TODO()
                RuntimeErrorInstruction.FREE_PAIR -> TODO()
            }
            armHelperFunctions.addAll(helper)


        }
    }


    override fun visitStringNode(node: StringNode): Void? {
        val str = node.string
        val label = msgLabelGenerator.getLabel()
        dataSegment[label] = str

        instructions.add(
            LDR(
                ARMRegisterAllocator.allocate(),
                LabelAddressing(label)
            )
        )

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
        instructions.add(
            Mov(
                register,
                Operand2(Immediate(node.char.code, true))
            )
        )
        return null
    }

    override fun visitIfNode(node: IfNode): Void? {
        val ifLabel = branchLabelGenerator.getLabel()
        val exitIfStatLabel = branchLabelGenerator.getLabel()

        visit(node.condition)
        val condRegister = ARMRegisterAllocator.curr()
        instructions.add(Cmp(condRegister, Operand2(0)))
        instructions.add(B(ifLabel, Cond.EQ))

        ARMRegisterAllocator.free()

        // First add the instructions from else
        visit(node.elseBody)
        instructions.add(B(exitIfStatLabel.label))

        // Then add ifBody
        instructions.add(ifLabel)
        visit(node.ifBody)

        // Add if stat end label
        instructions.add(exitIfStatLabel)

        return null
    }

    override fun visitWhileNode(node: WhileNode): Void? {
        val testLabel = branchLabelGenerator.getLabel()
        instructions.add(B(testLabel, Cond.NONE))

        val loopLabel = branchLabelGenerator.getLabel()
        instructions.add(loopLabel)

        // loop body
        visit(node.body)

        instructions.add(testLabel)

        visit(node.cond)

        instructions.add(Cmp(ARMRegisterAllocator.curr(), Operand2(1)))
        instructions.add(B(cond = Cond.EQ, label = loopLabel))

        ARMRegisterAllocator.free()

        return null
    }

    override fun visitIdentNode(node: IdentNode): Void? {
        val typeSize = node.type!!.size()

        val offset =
            currentSymbolTable!!.tableSize - currentSymbolTable!!.getStackOffset(
                node.name,
                node.symbol!!
            )

        val mode = if (typeSize > 1) LdrMode.LDR else LdrMode.LDRSB

        if (isExprLhs) {
            // only add address
            instructions.add(
                Add(
                    ARMRegisterAllocator.allocate(),
                    ARMRegister.SP,
                    Operand2(offset)
                )
            )
        } else {
            instructions.add(
                LDR(
                    ARMRegisterAllocator.allocate(),
                    AddressingMode2(
                        AddrMode2.OFFSET,
                        ARMRegister.SP,
                        offset
                    ),
                    mode
                )
            )
        }

        return null
    }

    override fun visitAssignNode(node: AssignNode): Void? {
        // visit rhs
        visit(node.rhs!!)

        // visit lhs
        isExprLhs = true
        visit(node.lhs!!)

        // reset
        isExprLhs = false

        val armRegister = ARMRegisterAllocator.last()
        val strMode =
            if (node.rhs.type!!.size() > 1) STR.STRMode.STR else STR.STRMode.STRB

        instructions.add(
            STR(
                armRegister,
                AddressingMode2(
                    AddrMode2.OFFSET,
                    ARMRegisterAllocator.curr()
                ),
                strMode
            )
        )

        ARMRegisterAllocator.free()
        ARMRegisterAllocator.free()

        return null
    }

    override fun visitArrayElemNode(node: ArrayElemNode): Void? {
        /* get the address of this array and store it in an available register */

        /* get the address of this array and store it in an available register */
        val addrReg: ARMRegister = ARMRegisterAllocator.allocate()
        val offset: Int = (currentSymbolTable!!.tableSize
                - currentSymbolTable!!.getStackOffset(
            node.name,
            node.symbol
        ))
        instructions.add(Add(addrReg, ARMRegister.SP, Operand2(offset)))

        checkAndAddRuntimeError(RuntimeErrorInstruction.CHECK_ARRAY_BOUND)
        checkAndAddRuntimeError(
            RuntimeErrorInstruction.THROW_RUNTIME_ERROR
        )

        var indexReg: ARMRegister
        for (i in 0 until node.indexDepth) {
            /* load the index at depth `i` to the next available register */
            val index: ExprNode = node.index[i]
            if (index !is IntNode) {
                visit(index)
                indexReg = ARMRegisterAllocator.curr()
                if (isExprLhs) {
                    instructions.add(LDR(
                        indexReg,
                        AddressingMode2(AddrMode2.OFFSET, indexReg)
                    ))
                }
            } else {
                indexReg = ARMRegisterAllocator.allocate()
                instructions
                    .add(LDR(indexReg,
                            ImmAddressing(index.value)))
            }

            /* check array bound */
            instructions.add(
                LDR(
                    addrReg,
                    AddressingMode2(AddrMode2.OFFSET, addrReg)
                )
            )
            instructions.add(Mov(ARMRegister.R0, Operand2(indexReg)))
            instructions.add(Mov(ARMRegister.R1, Operand2(addrReg)))
            instructions.add(BL(RuntimeErrorInstruction.CHECK_ARRAY_BOUND.toString()))
            instructions.add(Add(addrReg, addrReg, Operand2(POINTERSIZE)))
            val elemSize: Int = node.type!!.size() / 2
            instructions.add(
                Add(
                    addrReg,
                    addrReg,
                    Operand2(indexReg, Operand2Operator.LSL, elemSize)
                )
            )

            /* free indexReg to make it available for the indexing of the next depth */
            ARMRegisterAllocator.free()
        }

        /* if is not lhs, load the array content to `reg` */
        if (!isExprLhs) {
            instructions.add(
                LDR(
                    addrReg, AddressingMode2(AddrMode2.OFFSET, addrReg),
                    if (node.type!!.size() > 1) LdrMode.LDR else LdrMode.LDRSB
                )
            )
        }
        return null
    }

    override fun visitArrayNode(node: ArrayNode): Void? {
        /* get the total number of bytes needed to allocate enough space for the array */
        var size =
            if (node.type == null) 0 else node.getContentSize() * node.length
        /* add 4 bytes to `size` to include the size of the array as the first byte */
        size += POINTERSIZE

        /* load R0 with the number of bytes needed*/
        instructions.add(
            LDR(ARMRegister.R0, ImmAddressing(size))
        )

        // Malloc
        instructions.add(BL(SyscallInstruction.MALLOC.toString()))

        /* MOV the result pointer of the array to the next available register */
        val addrReg: ARMRegister = ARMRegisterAllocator.allocate()

        instructions.add(Mov(addrReg, Operand2(ARMRegister.R0)))

        /* Store array content into registers */
        /* Decide whether to store a byte or a word */
        val mode = if (node.getContentSize() > 1) STR.STRMode.STR else STR.STRMode.STRB

        for (i in 0 until node.length) {
            visit(node.content[i])
            val STRIndex: Int = i * node.getContentSize() + WORDSIZE
            instructions.add(
                STR(
                    ARMRegisterAllocator.curr(),
                    AddressingMode2(AddrMode2.OFFSET, addrReg, STRIndex),
                    mode
                )
            )
            ARMRegisterAllocator.free()
        }

        /* STR the size of the array in the first byte */
        val sizeReg: ARMRegister = ARMRegisterAllocator.allocate()
        instructions.add(
            LDR(
                sizeReg,
                ImmAddressing(node.length)
            )
        )

        instructions.add(
            STR(
                sizeReg,
                AddressingMode2(AddrMode2.OFFSET, addrReg)
            )
        )

        ARMRegisterAllocator.free()

        return null
    }

    override fun visitPairElemNode(node: PairElemNode): Void? {
        /* get pointer to the pair, store into next available register
         * reg is expected register where visit will put value in
         */

        val reg: ARMRegister = ARMRegisterAllocator.next()!!
        /* e.g. read fst a, (fst a) is lhs but (a) is rhs */
        val isLhsOutside: Boolean = isExprLhs
        isExprLhs = false
        visit(node.pair)
        isExprLhs = isLhsOutside

        /* move pair pointer to r0, prepare for null pointer check  */
        instructions.add(
            Mov(ARMRegister.R0, Operand2(reg))
        )

        // TODO: add check null pointer

        /* get the reg pointing to child
         * store snd in the same register, save register space
         */

        val addrMode: AddressingMode2 = if (node.isFirst()) {
            AddressingMode2(AddrMode2.OFFSET, reg)
        } else {
            AddressingMode2(AddrMode2.OFFSET, reg, POINTERSIZE)
        }

        if (isExprLhs) {
            instructions.add(LDR(reg, addrMode))
        } else {
            instructions.add(LDR(reg, addrMode))
            instructions.add(LDR(reg, AddressingMode2(AddrMode2.OFFSET, reg)))
        }

        return null
    }

    override fun visitPairNode(node: PairNode): Void? {
        /* null is also a pairNode */
        if (node.fst == null || node.snd == null) {
            instructions.add(
                LDR(
                    ARMRegisterAllocator.allocate(),
                    ImmAddressing(0)
                )
            )
            return null
        }

        /* 1 malloc pair */
        /* 1.1 move size of a pair in r0
         * a pair in heap is 2 pointers */
        instructions.add(
            LDR(
                ARMRegister.R0,
                ImmAddressing(2 * POINTERSIZE)
            )
        )

        /* 1.2 BL malloc and get pointer in general use register */
        instructions.add(
            BL(SyscallInstruction.MALLOC.toString())
        )
        val pairPointer: ARMRegister = ARMRegisterAllocator.allocate()

        instructions.add(Mov(pairPointer, Operand2(ARMRegister.R0)))

        /* 2 visit both child */
        visitPairChildExpr(
            node.fst!!,
            pairPointer,
            0
        )
        /* pair contains two pointers, each with size 4 */
        visitPairChildExpr(
            node.snd!!,
            pairPointer,
            WORDSIZE
        )

        return null
    }

    private fun visitPairChildExpr(
        child: ExprNode,
        pairPointer: ARMRegister,
        offset: Int
    ) {
        /* visit fst */
        val fstVal: ARMRegister? = ARMRegisterAllocator.next()
        visit(child)

        /* move size of fst in r0 */
        instructions.add(
            LDR(
                ARMRegister.R0,
                ImmAddressing(child.type!!.size())
            )
        )

        /* BL malloc */
        instructions.add(
            BL(SyscallInstruction.MALLOC.toString())
        )

        /* STR the fst value into reg[0] */
        val mode = if (child.type!!.size() > 1) STR.STRMode.STR else STR.STRMode.STRB
        instructions.add(
            STR(
                fstVal!!,
                AddressingMode2(AddrMode2.OFFSET, ARMRegister.R0),
                mode
            )
        )

        /* STR the snd value into reg[1] */
        instructions.add(
            STR(
                ARMRegister.R0,
                AddressingMode2(AddrMode2.OFFSET, pairPointer, offset)
            )
        )

        /* free register used for storing child's value */
        ARMRegisterAllocator.free()
    }
}