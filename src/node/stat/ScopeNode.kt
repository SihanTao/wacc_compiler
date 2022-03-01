package node.stat

import backend.ASTVisitor

/**
 * Represent a scope statement 
 * has a function mergeScope to go through all the statement nodes in the list to form the body
 */

class ScopeNode(node: StatNode) : StatNode() {

    val body: MutableList<StatNode> = ArrayList()

    private fun mergeScope(s: StatNode) {
        if (s is ScopeNode) {
            body.addAll(s.body)
        } else if (s !is SkipNode) {
            body.add(s)
        }
    }

    /* This will help to determine whether there is a return statement at the end of a sequence */
    private val endValue: Boolean = body.isNotEmpty() && body[body.size - 1].isReturned

    init {
        mergeScope(node)
        isReturned = endValue
    }

    override fun <T> accept(astVisitor: ASTVisitor<T>): T? {
        return astVisitor.visitScopeNode(this)
    }

    fun size(): Int {
        return scope!!.tableSize
    }
}