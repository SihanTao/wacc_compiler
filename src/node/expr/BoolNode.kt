package node.expr

import type.BasicTypeEnum
import type.BasicType

class BoolNode(

    /**
     * Represent a boolean
     * Examples: true, false
     */
    private val `val`: Boolean
) : ExprNode() {
    init {
        type = BasicType(BasicTypeEnum.BOOLEAN)
    }
}