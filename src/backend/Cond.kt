package backend

enum class Cond {
    NONE, EQ, NE, LT, VS, S, CS;

    override fun toString(): String {
        return when (this) {
            NONE -> ""
            else -> super.toString()
        }
    }
}
