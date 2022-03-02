package instruction.addressing_mode

class StaticRef(val msg: String): AddressingMode {
    override fun toString(): String {
        return "=$msg"
    }
}