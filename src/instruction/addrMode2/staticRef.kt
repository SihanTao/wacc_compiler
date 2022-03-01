package instruction.addrMode2

class staticRef(val msg: String): AddrMode2 {
    override fun toString(): String {
        return "=$msg"
    }
}