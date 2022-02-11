package node.stat

import node.expr.ExprNode

/**
 * Represent a declaration statement
 * has its own identifier and also a right-hand-side note which is an expression node
 * Examples: int i = 1
 */

class DeclareStatNode(
    private val identifier: String, private val rhs: ExprNode?
) : StatNode()