package type

import type.Type.Companion.BYTESIZE
import type.Type.Companion.POINTERSIZE
import type.Type.Companion.WORDSIZE

class BasicType(val typeEnum: BasicTypeEnum) : Type {

    override fun equals(other: Any?): Boolean {
        if (other == null) {
            return true
        }
        return if (other !is BasicType) {
            false
        } else typeEnum == other.typeEnum
    }

    override fun toString(): String {
        return typeEnum.toString()
    }

    override fun size(): Int {
        return when (typeEnum) {
            BasicTypeEnum.CHAR, BasicTypeEnum.BOOLEAN -> BYTESIZE
            BasicTypeEnum.INTEGER -> WORDSIZE
            BasicTypeEnum.STRING -> POINTERSIZE
        }
    }
}