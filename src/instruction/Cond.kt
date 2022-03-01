package instruction

enum class Cond {
    EQ, NE, CS, CC, MI, PL, VS, VC, HI, LS, GE, LT, GT, LE, AL;

    override fun toString(): String {
        when (this) {
            AL -> return ""
            else -> return name
        }
    }
}