package node.stat

import node.expr.ExprNode

/**
 * Represent an assignment node with left-hand-side and right-hand-side
 * both left-hand-side and right-hand-side are expression nodes
 * Examples:  false 
 *            /   \
 *           1     2
 */

class AssignNode(

    private val lhs: ExprNode?, private val rhs: ExprNode?
) : StatNode()