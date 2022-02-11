package node.stat

import node.expr.ExprNode

/**
 * Represent a printline statement
 */

class PrintlnNode(
    private val expr: ExprNode?
) : StatNode()
