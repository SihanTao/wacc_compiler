package instruction.addressing_mode

class Constant(val const: Int): AddressingMode {
    override fun toString(): String {
        return "=$const"
    }
}