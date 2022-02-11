package node.expr

import type.BasicType
import type.BasicTypeEnum

class IntNode(
    /**
     * Represent and integer node
     * Examples: 1, 2, 3
     */

    private val value: Int
) : ExprNode() {
    init {
        type = BasicType(BasicTypeEnum.INTEGER)
    }
}