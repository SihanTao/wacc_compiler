package instruction.addressing_mode

class Constant(val const: Int) {
    override fun toString(): String {
        return "=$const"
    }
}