package node.expr

import type.Type

class PairElemNode(private val pair: ExprNode, type: Type?) : ExprNode() {
    private var isFirst: Boolean

    init {
        this.type = type
        this.isFirst = false
    }

    fun fst(): PairElemNode {
        this.isFirst = true
        return this
    }

    fun snd(): PairElemNode {
        this.isFirst = false
        return this
    }
}