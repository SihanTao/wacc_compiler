package node.stat

import node.expr.ExprNode

class ReturnNode(private val expr: ExprNode) : StatNode() {
    init {
        returned()
    }
}