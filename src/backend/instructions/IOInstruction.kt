package backend.instructions

import backend.ARMRegister
import backend.instructionGenerator.LabelGenerator
import backend.instructions.addressing.AddressingMode2
import backend.instructions.addressing.LabelAddressing
import backend.instructions.arithmeticLogic.Add
import backend.instructions.operand.Operand2
import java.util.*

enum class IOInstruction {
    // Print
    PRINT_INT, PRINT_BOOL, PRINT_CHAR, PRINT_STRING, PRINT_LN;

    override fun toString(): String {
        if (this == PRINT_CHAR) {
            return SyscallInstruction.PUTCHAR.toString()
        }

        return "p_${name.lowercase(Locale.getDefault())}"
    }

    companion object {
        private const val PRINT_STRING_MSG = "\"%.*s\\0\""

        // The main print function
        fun addPrint(
            ioInstruction: IOInstruction,
            labelGenerator: LabelGenerator,
            dataSegment: MutableMap<Label, String>
        ) : List<Instruction> {
            val label = labelGenerator.getLabel()
            return when(ioInstruction) {
                PRINT_STRING -> addPrintString(dataSegment, labelGenerator)
                else -> TODO("NOT IMPLEMENTED")
            }
        }

        private fun addPrintString(
            dataSegment: MutableMap<Label, String>,
            labelGenerator: LabelGenerator
        ): List<Instruction> {
            /* add the format into the data list */
            val msg: Label = addMsg(
                PRINT_STRING_MSG,
                dataSegment,
                labelGenerator
            )

            val instructions: MutableList<Instruction> =
                mutableListOf( /* add the helper function label */
                    Label(PRINT_STRING.toString()),
                    Push(ARMRegister.LR),  /* put the string length into r1 as snd arg */
                    LDR(
                        ARMRegister.R1,
                        AddressingMode2(
                            AddressingMode2.AddrMode2.OFFSET,
                            ARMRegister.R0
                        )
                    ),
                    /* skip the fst 4 bytes which is the length of the string */
                    Add(ARMRegister.R2, ARMRegister.R0, Operand2(4)),
                    LDR(ARMRegister.R0, LabelAddressing(msg))

                )

            instructions.addAll(addCommonPrint())
            return instructions
        }

        private fun addCommonPrint(): MutableList<Instruction> {
            return mutableListOf( /* skip the first 4 byte of the msg which is the length of it */
                Add(ARMRegister.R0, ARMRegister.R0, Operand2(4)),
                BL(SyscallInstruction.PRINTF.toString()),  /* refresh the r0 and buffer */
                Mov(ARMRegister.R0, Operand2(0)),
                BL(SyscallInstruction.FFLUSH.toString()),
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
