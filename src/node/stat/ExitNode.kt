package node.stat

import node.expr.ExprNode

/**
 * Represent an exit statement 
 * set return to true and exit
 */

class ExitNode(var exitCode: ExprNode) : StatNode() {
    init {
        isReturned = true
    }
}
