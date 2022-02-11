package node.stat

import node.expr.ExprNode

/**
 * repersents a while statement with a body and a condition
 */

class WhileNode(
    private val cond: ExprNode, private val body: StatNode
) : StatNode()