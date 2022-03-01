package instruction.addrMode2

class StaticRef(val msg: String): AddrMode2 {
    override fun toString(): String {
        return "=$msg"
    }
}