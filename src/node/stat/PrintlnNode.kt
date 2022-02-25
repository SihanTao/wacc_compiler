package node.stat

import node.expr.ExprNode

/**
 * Represent a printline statement
 */

class PrintlnNode(
    val expr: ExprNode?
) : StatNode()
