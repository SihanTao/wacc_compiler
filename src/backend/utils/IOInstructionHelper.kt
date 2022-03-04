package backend.utils

import backend.instructions.*
import backend.register.ARMRegister.*
import backend.instructions.addressing.AddressingMode2
import backend.instructions.addressing.LabelAddressing
import backend.instructions.unopAndBinop.Add
import java.util.*

enum class IOInstructionHelper: Instruction {
    // Print
    PRINT_INT, PRINT_BOOL, PRINT_CHAR, PRINT_STRING, PRINT_LN, PRINT_REFERENCE,

    // Read
    READ_INT, READ_CHAR;

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

        // The main print or read function
        fun addPrintOrRead(
            ioInstructionHelper: IOInstructionHelper,
            labelGenerator: LabelGenerator,
            dataSegment: MutableMap<Label, String>
        ): List<Instruction> {
            return when (ioInstructionHelper) {
                PRINT_STRING -> addPrintString(dataSegment, labelGenerator)
                PRINT_LN -> addPrintln(dataSegment, labelGenerator)
                PRINT_INT -> addPrintInt(dataSegment, labelGenerator)
                PRINT_BOOL -> addPrintBool(dataSegment, labelGenerator)
                PRINT_CHAR -> return emptyList()
                PRINT_REFERENCE -> addPrintRef(dataSegment, labelGenerator)
                READ_INT -> addRead(READ_INT, dataSegment, labelGenerator)
                READ_CHAR -> addRead(READ_CHAR, dataSegment, labelGenerator)
            }
        }

        private fun addPrintInt(
            dataSegment: MutableMap<Label, String>,
            labelGenerator: LabelGenerator
        ): List<Instruction> {
            val printIntLabel =
                addMsg(PRINT_INT_MSG, dataSegment, labelGenerator)
            val instructions: MutableList<Instruction> = ArrayList(
                listOf(
                    /* add the helper function label */
                    Label(PRINT_INT.toString()),
                    Push(LR),  /* put the content in r0 int o r1 as the snd arg of printf */
                    Mov(R1, Operand2(R0)),  /* fst arg of printf is the format */
                    LDR(R0, LabelAddressing(printIntLabel))
                )
            )
            instructions.addAll(addCommonPrint())

            return instructions
        }

        private fun addPrintRef(
            dataSegment: MutableMap<Label, String>,
            labelGenerator: LabelGenerator
        ): List<Instruction> {
            println("In addPrintRef")
            val label = labelGenerator.getLabel()
            dataSegment[label] = PRINT_REF_MSG
            val printIntLabel =
                addMsg(PRINT_REF_MSG, dataSegment, labelGenerator)
            val instructions: MutableList<Instruction> = ArrayList(
                listOf(
                    /* add the helper function label */
                    Label(PRINT_REFERENCE.toString()),
                    Push(LR),  /* put the content in r0 int o r1 as the snd arg of printf */
                    Mov(R1, Operand2(R0)),  /* fst arg of printf is the format */
                    LDR(R0, LabelAddressing(printIntLabel))
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
                Push(LR),
                LDR(R0, LabelAddressing(printlnLabel)),  /* skip the first 4 byte of the msg which is the length of it */
                Add(R0, R0, Operand2(4)),
                BL(SyscallInstruction.PUTS.toString()),  /* refresh the r0 and buffer */
                Mov(R0, Operand2(0)),
                BL(SyscallInstruction.FFLUSH.toString()),
                Pop(PC)
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
                    Push(LR),  /* put the string length into r1 as snd arg */
                    LDR(R1, AddressingMode2(R0)),
                    /* skip the fst 4 bytes which is the length of the string */
                    Add(R2, R0, Operand2(4)),
                    LDR(R0, LabelAddressing(msg))
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
                    Push(LR),
                    /* cmp the content in r0 with 0 */
                    Cmp(R0, Operand2(0)),  /* if not equal to 0 LDR true */
                    LDR(R0, LabelAddressing(msgTrue), LDR.LdrMode.LDRNE),
                    LDR(R0, LabelAddressing(msgFalse), LDR.LdrMode.LDREQ)
                )
            )
            instructions.addAll(addCommonPrint())
            return instructions
        }

        private fun addCommonPrint(): MutableList<Instruction> {
            return mutableListOf( /* skip the first 4 byte of the msg which is the length of it */
                Add(R0, R0, Operand2(4)),
                BL(SyscallInstruction.PRINTF.toString()),  /* refresh the r0 and buffer */
                Mov(R0, Operand2(0)),
                BL(SyscallInstruction.FFLUSH.toString()),
                Pop(PC)
            )
        }

        private fun addRead(
            readInstruction: IOInstructionHelper,
            dataSegment: MutableMap<Label, String>,
            labelGenerator: LabelGenerator
        ): List<Instruction> {
            /* add the helper function label */
            val readLabel = Label(readInstruction.toString())

            /* add the format into the data list */
            val asciiMsg: String =
                if (readInstruction === READ_INT) PRINT_INT_MSG else PRINT_CHAR_MSG
            val msgLabel = labelGenerator.getLabel()
            dataSegment[msgLabel] = asciiMsg

            return listOf(
                readLabel,
                Push(LR),
                Mov(R1, Operand2(R0)),
                LDR(R0, LabelAddressing(msgLabel)),
                Add(R0, R0, Operand2(4)), BL("${SyscallInstruction.SCANF}"),
                Pop(PC)
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

