package node.stat

import node.expr.ExprNode

class PrintlnNode(
    private val expr: ExprNode?
) : StatNode()
