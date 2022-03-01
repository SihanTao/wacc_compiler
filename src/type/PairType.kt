package type

import type.Type.Companion.POINTERSIZE

class PairType constructor(val fstType: Type? = null, val sndType: Type? = null) : Type {

    fun asPairType(): PairType {
        return this
    }

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

    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun size(): Int {
        return POINTERSIZE
    }

    private fun subTypeCoerce(thisType: Type?, thatType: Type?): Boolean {
        if (thisType == null || thatType == null) {
            /* if either thisType or thatType is null, then we can coerce them
       * see comments in PairNode class for more information */
            return true
        } else if (thisType is PairType) {
            /*  */
            return thatType is PairType
        }
        return thisType == thatType
    }

    override fun toString(): String {
        return "Pair<$fstType, $sndType>"
    }
}