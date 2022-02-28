package backend.instructions.operand

class Immediate(val value: Int) {
    override fun toString(): String {
        return "#$value"
    }
}
