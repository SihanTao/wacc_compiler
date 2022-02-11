package node.stat

import node.expr.ExprNode

class PrintNode(
    private val expr: ExprNode?
) : StatNode()