package node.expr

import type.BasicType
import type.BasicTypeEnum

class CharNode(private val ch: Char) : ExprNode() {
    init {
        type = BasicType(BasicTypeEnum.CHAR)
    }
}