package backend.instructions.addressing

class ImmAddressing(private val imm: Int) : Addressing {
    @Override
    override fun toString(): String {
        return "=$imm"
    }
}