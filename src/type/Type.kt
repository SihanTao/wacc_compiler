package type

import node.Node

interface Type : Node {
    // Type
    fun size(): Int
    companion object {
        val POINTERSIZE = 4
    }
}
