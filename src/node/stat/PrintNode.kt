package node.stat

import node.expr.ExprNode

/**
 * Represent a print statement
 */

class PrintNode(
    private val expr: ExprNode?
) : StatNode()