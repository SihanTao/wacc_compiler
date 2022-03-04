package backend.utils

class Immediate(val value: Int, private val isChar: Boolean) {
    constructor(value: Int) : this(value, false)

    override fun toString(): String {
        if (isChar) {
            return "#'${value.toChar()}'"
        }
        return "#$value"
    }
}
