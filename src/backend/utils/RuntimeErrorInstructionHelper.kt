package backend.utils

import backend.instructions.*
import backend.register.ARMRegister
import backend.instructions.LDR.LdrMode
import backend.instructions.addressing.AddressingMode2
import backend.instructions.addressing.AddressingMode2.AddrMode2
import backend.instructions.addressing.LabelAddressing
import java.util.*

enum class RuntimeErrorInstructionHelper: Instruction {
    CHECK_DIVIDE_BY_ZERO, THROW_RUNTIME_ERROR, CHECK_ARRAY_BOUND, CHECK_NULL_POINTER,
    THROW_OVERFLOW_ERROR, FREE_PAIR;

    override fun toString(): String {
        return "p_${name.lowercase(Locale.getDefault())}"
    }

    companion object {
        private const val PRINT_ARRAY_NEG_INDEX_MSG =
            "\"ArrayIndexOutOfBoundsError: negative index\\n\\0\""
        private const val PRINT_ARRAY_INDEX_TOO_LARGE_MSG =
            "\"ArrayIndexOutOfBoundsError: index too large\\n\\0\""
        private const val PRINT_OVERFLOW_MSG =
            "\"OverflowError: the result is too small/large to store in a 4-byte signed-integer.\\n\\0\""
        private const val PRINT_DIV_ZERO_MSG =
            "\"DivideByZeroError: divide or modulo by zero\\n\\0\""
        private const val PRINT_NULL_REF_MSG =
            "\"NullReferenceError: dereference a null reference\\n\\0\""

        fun addCheckDivByZero(
            labelGenerator: LabelGenerator,
            data: MutableMap<Label, String>
        ): List<Instruction> {
            val label = labelGenerator.getLabel()
            data[label] = PRINT_DIV_ZERO_MSG

            return listOf(
                Label("$CHECK_DIVIDE_BY_ZERO"),
                Push(ARMRegister.LR),
                Cmp(ARMRegister.R1, 0),
                LDR(ARMRegister.R0, LabelAddressing(label), LdrMode.LDREQ),
                BL(Cond.EQ, "$THROW_RUNTIME_ERROR"),
                Pop(ARMRegister.PC)
            )
        }

        fun addCheckNullPointer(
            labelGenerator: LabelGenerator,
            data: MutableMap<Label, String>
        ): List<Instruction> {
            val msgLabel = labelGenerator.getLabel()
            data[msgLabel] = PRINT_NULL_REF_MSG

            return listOf(
                Label("$CHECK_NULL_POINTER"),
                Push(ARMRegister.LR),
                Cmp(ARMRegister.R0, Operand2(0)),
                LDR(ARMRegister.R0, LabelAddressing(msgLabel), LdrMode.LDREQ),
                BL(Cond.EQ, "$THROW_RUNTIME_ERROR"),
                Pop(ARMRegister.PC)
            )

        }

        fun addFree(
            labelGenerator: LabelGenerator,
            data: MutableMap<Label, String>
        ): List<Instruction> {

            val msg: Label = addMsg(
                PRINT_NULL_REF_MSG,
                data,
                labelGenerator
            )

            return listOf(
                Label("$FREE_PAIR"),
                Push(ARMRegister.LR),
                Cmp(ARMRegister.R0, Operand2(0)),
                LDR(ARMRegister.R0, LabelAddressing(msg), LdrMode.LDREQ),
                B(Cond.EQ, "$THROW_RUNTIME_ERROR"),
                Push(ARMRegister.R0),
                LDR(
                    ARMRegister.R0,
                    AddressingMode2(ARMRegister.R0)
                ),
                BL("${SyscallInstruction.FREE}"),
                LDR(
                    ARMRegister.R0, AddressingMode2(ARMRegister.SP)
                ),
                LDR(
                    ARMRegister.R0,
                    AddressingMode2(AddrMode2.OFFSET, ARMRegister.R0, 4)
                ),
                BL("${SyscallInstruction.FREE}"),
                Pop(ARMRegister.R0),
                BL("${SyscallInstruction.FREE}"),
                Pop(ARMRegister.PC)
            )

        }

        fun addThrowRuntimeError(): List<Instruction> {
            return listOf(
                Label("$THROW_RUNTIME_ERROR"),
                BL("${IOInstructionHelper.PRINT_STRING}"),
                Mov(ARMRegister.R0, Operand2(-1)),
                BL("${SyscallInstruction.EXIT}")
            )
        }

        fun addThrowOverflowError(
            labelGenerator: LabelGenerator,
            data: MutableMap<Label, String>
        ): List<Instruction> {
            val label = labelGenerator.getLabel()
            data[label] = PRINT_OVERFLOW_MSG

            return listOf(
                Label("$THROW_OVERFLOW_ERROR"),
                LDR(ARMRegister.R0, LabelAddressing(label)),
                BL("$THROW_RUNTIME_ERROR")
            )
        }

        fun addCheckArrayBound(
            labelGenerator: LabelGenerator,
            data: MutableMap<Label, String>
        ): List<Instruction> {
            val negativeIndexLabel = labelGenerator.getLabel()
            data[negativeIndexLabel] = PRINT_ARRAY_NEG_INDEX_MSG
            val indexOutOfBoundLabel = labelGenerator.getLabel()
            data[indexOutOfBoundLabel] = PRINT_ARRAY_INDEX_TOO_LARGE_MSG

            return mutableListOf(
                Label("$CHECK_ARRAY_BOUND"),
                Push(ARMRegister.LR),
                Cmp(ARMRegister.R0, Operand2(0)),
                LDR(
                    ARMRegister.R0,
                    LabelAddressing(negativeIndexLabel),
                    LdrMode.LDRLT
                ),
                BL(Cond.LT, "$THROW_RUNTIME_ERROR"),
                LDR(
                    ARMRegister.R1,
                    AddressingMode2(ARMRegister.R1)
                ),
                Cmp(ARMRegister.R0, Operand2(ARMRegister.R1)),
                LDR(
                    ARMRegister.R0,
                    LabelAddressing(indexOutOfBoundLabel),
                    LdrMode.LDRCS
                ),
                BL(Cond.CS, "$THROW_RUNTIME_ERROR"),
                Pop(ARMRegister.PC)
            )
        }

        private fun addMsg(
            msg: String,
            data: MutableMap<Label, String>,
            labelGenerator: LabelGenerator
        ): Label {
            val msgLabel = labelGenerator.getLabel()
            data[msgLabel] = msg
            return msgLabel
        }
    }


}