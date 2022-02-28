package backend.instructions

enum class SyscallInstruction: Instruction {
    // TODO: NOT COMPLETED
    PUTCHAR, PRINTF, FFLUSH;

    override fun toString(): String {
        return name.lowercase()
    }}