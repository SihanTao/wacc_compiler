package node.stat

import node.expr.ExprNode

/**
 * Represent a return statement
 */

class ReturnNode(val expr: ExprNode) : StatNode() {
    init {
        isReturned = true
    }
}