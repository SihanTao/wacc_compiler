package node.stat

import node.expr.ExprNode

class ExitNode(exitCode: ExprNode) : StatNode() {
    init {
        isReturned = true
    }
}
