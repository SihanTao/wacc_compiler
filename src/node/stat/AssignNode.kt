package node.stat

import node.expr.ExprNode

class AssignNode(
    private val lhs: ExprNode?, private val rhs: ExprNode?
) : StatNode()