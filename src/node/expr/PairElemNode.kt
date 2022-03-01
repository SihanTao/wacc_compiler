package node.expr

import backend.ASTVisitor
import type.Type

class PairElemNode(private val pair: ExprNode, type: Type?) : ExprNode() {
    /**
     * Represent a pair of elem_node 
     * functions fst snd can get the two elements
     * function isFirst can check if the element is the first 
     * Examples: fst<expr>, snd<expr> 
     *           fst true, snd false
     */

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

    override fun <T> accept(astVisitor: ASTVisitor<T>): T? {
        return astVisitor.visitPairElemNode(this)
    }
}