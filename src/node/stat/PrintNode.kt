package node.stat

import node.expr.ExprNode

/**
 * Represent a print statement
 */

class PrintNode(
        var expr: ExprNode?
) : StatNode()