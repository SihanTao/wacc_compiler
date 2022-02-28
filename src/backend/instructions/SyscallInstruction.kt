package backend.instructions

enum class SyscallInstruction: Instruction {
    MALLOC, FREE,
    SCANF,
    EXIT,
    PUTS, PUTCHAR, PRINTF, FFLUSH;

    override fun toString(): String {
        return name.lowercase()
    }}