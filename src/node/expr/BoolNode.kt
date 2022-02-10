package node.expr

import type.BasicTypeEnum
import type.BasicType

class BoolNode(
    private val `val`: Boolean
) : ExprNode() {
    init {
        type = BasicType(BasicTypeEnum.BOOLEAN)
    }
}