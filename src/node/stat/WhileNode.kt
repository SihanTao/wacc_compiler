package node.stat

import node.expr.ExprNode

class WhileNode(
    private val cond: ExprNode, private val body: StatNode
) : StatNode()