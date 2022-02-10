package node.stat

import node.expr.ExprNode

class IfNode(
    private val condition: ExprNode, private val ifBody: StatNode?, private val elseBody: StatNode?
) : StatNode() {
    init {
        isReturned = endValue
    }

    private val endValue: Boolean
        get() {
            return ifBody!!.isReturned && elseBody!!.isReturned
        }
}