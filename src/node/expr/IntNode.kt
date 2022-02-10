package node.expr

import type.BasicType
import type.BasicTypeEnum

class IntNode(
    private val value: Int
) : ExprNode() {
    init {
        type = BasicType(BasicTypeEnum.INTEGER)
    }
}