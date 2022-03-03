package type

import type.Type.Companion.POINTERSIZE

class PairType constructor(val fstType: Type? = null, val sndType: Type? = null) : Type {

    override fun equals(other: Any?): Boolean {
        if (other == null) {
            return true
        }
        if (other !is PairType) {
            return false
        }
        return (subTypeCoerce(fstType, other.fstType)
                && subTypeCoerce(sndType, other.sndType))
    }

    override fun size(): Int {
        return POINTERSIZE
    }

    private fun subTypeCoerce(thisType: Type?, thatType: Type?): Boolean {
        if (thisType == null || thatType == null) {
            return true
        } else if (thisType is PairType) {
            return thatType is PairType
        }
        return thisType == thatType
    }

    override fun toString(): String {
        return "Pair<$fstType, $sndType>"
    }

    override fun hashCode(): Int {
        var result = fstType?.hashCode() ?: 0
        result = 31 * result + (sndType?.hashCode() ?: 0)
        return result
    }
}