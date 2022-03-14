package node.stat

import node.expr.ExprNode

/**
 * Represent an if statment with ifBody and elseBody
 * store the result in endValue 
 */

class IfNode(
    var condition: ExprNode, val ifBody: StatNode?, val elseBody: StatNode?
) : StatNode() {
    init {
        isReturned = endValue
    }

    private val endValue: Boolean
        get() {
            return ifBody!!.isReturned && elseBody!!.isReturned
        }
}
