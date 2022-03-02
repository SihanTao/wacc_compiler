package backend.instructions

import backend.ARMRegister
import backend.Cond
import backend.instructionGenerator.LabelGenerator
import backend.instructions.LDR.LdrMode
import backend.instructions.addressing.AddressingMode2
import backend.instructions.addressing.AddressingMode2.AddrMode2
import backend.instructions.addressing.LabelAddressing
import backend.instructions.operand.Operand2
import java.util.*

enum class RuntimeErrorInstruction : Instruction {
    CHECK_DIVIDE_BY_ZERO, THROW_RUNTIME_ERROR, CHECK_ARRAY_BOUND, CHECK_NULL_POINTER,
    THROW_OVERFLOW_ERROR, FREE_ARRAY, FREE_PAIR;

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

        fun addThrowRuntimeError(): List<Instruction> {
            return listOf(
                Label(THROW_RUNTIME_ERROR.toString()),
                BL(IOInstruction.PRINT_STRING.toString()),
                Mov(ARMRegister.R0, Operand2(-1)),
                BL(SyscallInstruction.EXIT.toString())
            )
        }

        fun addThrowOverflowError(
            labelGenerator: LabelGenerator,
            data: MutableMap<Label, String>
        ): List<Instruction> {
            val label = labelGenerator.getLabel()
            data[label] = PRINT_OVERFLOW_MSG

            return listOf(
                Label(THROW_OVERFLOW_ERROR.toString()),
                LDR(ARMRegister.R0, LabelAddressing(label)),
                BL(THROW_RUNTIME_ERROR.toString())
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
                Label(CHECK_ARRAY_BOUND.toString()),
                Push(ARMRegister.LR),
                Cmp(ARMRegister.R0, Operand2(0)),
                LDR(
                    ARMRegister.R0,
                    LabelAddressing(negativeIndexLabel),
                    LdrMode.LDRLT
                ),
                BL(Cond.LT, THROW_RUNTIME_ERROR.toString()),
                LDR(
                    ARMRegister.R1,
                    AddressingMode2(AddrMode2.OFFSET, ARMRegister.R1)
                ),
                Cmp(ARMRegister.R0, Operand2(ARMRegister.R1)),
                LDR(
                    ARMRegister.R0,
                    LabelAddressing(indexOutOfBoundLabel),
                    LdrMode.LDRCS
                ),
                BL(Cond.CS, THROW_RUNTIME_ERROR.toString()),
                Pop(ARMRegister.PC)
            )
        }
    }


}