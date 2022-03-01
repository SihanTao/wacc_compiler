package type

import type.Type.Companion.POINTERSIZE

class ArrayType constructor(private val type: Type? = null) : Type {
    val depth: Int

    /*
        init calculate dimension recursively
        int[][] --> dimension = 2
     */
    init {
        var subType = type
        var depth = 1
        while (subType is ArrayType) {
            subType = subType.getContentType()
            depth++
        }
        this.depth = depth
    }

    override fun size(): Int {
        return POINTERSIZE
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) {
            return true
        }
        return if (other !is ArrayType) {
            false
        } else type!! == other.getContentType()
    }

    fun getContentType(): Type {
        return type!!
    }

    override fun toString(): String {
        return "Array<$type>"
    }

    override fun hashCode(): Int {
        var result = type?.hashCode() ?: 0
        result = 31 * result + depth
        return result
    }
}