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
    PRINT_INT, PRINT_BOOL, PRINT_CHAR, PRINT_STRING, PRINT_LN, PRINT_REFERENCE;

    override fun toString(): String {
        if (this == PRINT_CHAR) {
            return SyscallInstruction.PUTCHAR.toString()
        }

        return "p_${name.lowercase(Locale.getDefault())}"
    }

    companion object {
        private const val PRINT_BOOL_TRUE = "\"true\\0\""
        private const val PRINT_BOOL_FALSE = "\"false\\0\""
        private const val PRINT_INT_MSG = "\"%d\\0\""
        private const val PRINT_STRING_MSG = "\"%.*s\\0\""
        private const val PRINT_LN_MSG = "\"\\0\""
        private const val PRINT_REF_MSG = "\"%p\\0\""
        private const val PRINT_CHAR_MSG = "\" %c\\0\""

        // The main print function
        fun addPrint(
            ioInstruction: IOInstruction,
            labelGenerator: LabelGenerator,
            dataSegment: MutableMap<Label, String>
        ): List<Instruction> {
            return when (ioInstruction) {
                PRINT_STRING -> addPrintString(dataSegment, labelGenerator)
                PRINT_LN -> addPrintln(dataSegment, labelGenerator)
                PRINT_INT -> addPrintInt(dataSegment, labelGenerator)
                PRINT_BOOL -> addPrintBool(dataSegment, labelGenerator)
                PRINT_CHAR -> TODO("Print char not implemented")
                PRINT_REFERENCE -> TODO("Print ref not implemented.")
                else -> TODO("NOT IMPLEMENTED")
            }
        }

        private fun addPrintInt(
            dataSegment: MutableMap<Label, String>,
            labelGenerator: LabelGenerator
        ): List<Instruction> {
            val printIntLabel = addMsg(PRINT_INT_MSG, dataSegment, labelGenerator)
            val instructions: MutableList<Instruction> = ArrayList(
                listOf(
                    /* add the helper function label */
                    Label(PRINT_INT.toString()),
                    Push(ARMRegister.LR),  /* put the content in r0 int o r1 as the snd arg of printf */
                    Mov(ARMRegister.R1, Operand2(ARMRegister.R0)),  /* fst arg of printf is the format */
                    LDR(ARMRegister.R0, LabelAddressing(printIntLabel))
                )
            )
            instructions.addAll(addCommonPrint())

            return instructions
        }

        private fun addPrintln(
            dataSegment: MutableMap<Label, String>,
            labelGenerator: LabelGenerator
        ): List<Instruction> {
            val printlnLabel = labelGenerator.getLabel()
            dataSegment[printlnLabel] = PRINT_LN_MSG

            return listOf(
                Label(PRINT_LN.toString()),
                Push(ARMRegister.LR),
                LDR(
                    ARMRegister.R0,
                    LabelAddressing(printlnLabel)
                ),  /* skip the first 4 byte of the msg which is the length of it */
                Add(ARMRegister.R0, ARMRegister.R0, Operand2(4)),
                BL(SyscallInstruction.PUTS.toString()),  /* refresh the r0 and buffer */
                Mov(ARMRegister.R0, Operand2(0)),
                BL(SyscallInstruction.FFLUSH.toString()),
                Pop(ARMRegister.PC)
            )

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

        /* print bool */
        private fun addPrintBool(
            dataSegment: MutableMap<Label, String>,
            labelGenerator: LabelGenerator
        ): List<Instruction> {
            /* add the msgTrue into the data list */
            val msgTrue: Label = addMsg(
                PRINT_BOOL_TRUE,
                dataSegment,
                labelGenerator
            )

            /* add the msgFalse into the data list */
            val msgFalse: Label = addMsg(
                PRINT_BOOL_FALSE,
                dataSegment,
                labelGenerator
            )

            val instructions: MutableList<Instruction> = ArrayList(
                listOf(
                    /* add the helper function label */
                    Label(PRINT_BOOL.toString()),
                    Push(ARMRegister.LR),
                    /* cmp the content in r0 with 0 */
                    Cmp(ARMRegister.R0, Operand2(0)),  /* if not equal to 0 LDR true */
                    LDR(ARMRegister.R0, LabelAddressing(msgTrue), LDR.LdrMode.LDRNE),
                    LDR(ARMRegister.R0, LabelAddressing(msgFalse), LDR.LdrMode.LDREQ)
                )
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
