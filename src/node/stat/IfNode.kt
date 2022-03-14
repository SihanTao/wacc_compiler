package node.stat

import node.expr.ExprNode

/**
 * Represent an if statment with ifBody and elseBody
 * store the result in endValue 
 */

class IfNode(
        var condition: ExprNode, var ifBody: StatNode?, var elseBody: StatNode?
) : StatNode() {
    init {
        isReturned = endValue
    }

    private val endValue: Boolean
        get() {
            return ifBody!!.isReturned && elseBody!!.isReturned
        }
}
