package type

import node.Node

interface Type : Node {
    // Type
    fun size(): Int
    companion object {
        const val POINTERSIZE = 4
        const val BYTESIZE = 1
        const val WORDSIZE = 4
    }
}
