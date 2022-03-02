package instruction.addressing_mode

enum class Sign{
    PLUS, MINUS;

    override fun toString(): String {
        return when (this) {
            PLUS -> "+"
            MINUS -> "-"
        }
    }
}