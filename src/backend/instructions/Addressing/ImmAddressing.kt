package backend.instructions.Addressing

class ImmAddressing(private val imm: Int) : Addressing {
    @Override
    override fun toString(): String {
        return "=$imm"
    }
}