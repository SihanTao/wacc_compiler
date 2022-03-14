package node.stat

import node.expr.ExprNode

/**
 * Represent a printline statement
 */

class PrintlnNode(
        var expr: ExprNode?
) : StatNode()
