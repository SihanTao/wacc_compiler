package node.stat

import node.expr.ExprNode

/**
 * repersents a while statement with a body and a condition
 */

class WhileNode(
        var cond: ExprNode, var body: StatNode
) : StatNode()