package instruction.addrMode2

enum class Sign{
    PLUS, MINUS;

    override fun toString(): String {
        return when (this) {
            PLUS -> "+"
            MINUS -> "-"
        }
    }
}