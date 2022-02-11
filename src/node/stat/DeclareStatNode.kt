package node.stat

import node.expr.ExprNode

class DeclareStatNode(
    private val identifier: String, private val rhs: ExprNode?
) : StatNode()