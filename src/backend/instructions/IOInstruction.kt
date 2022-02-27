package backend.instructions

import java.util.*

enum class IOInstruction {
    // Print
    PRINT_INT, PRINT_BOOL, PRINT_CHAR, PRINT_STRING, PRINT_LN;

    override fun toString(): String {
        if (this == PRINT_CHAR) {
            return "putchar"
        }

        return "p_${name.lowercase(Locale.getDefault())}"
    }}
