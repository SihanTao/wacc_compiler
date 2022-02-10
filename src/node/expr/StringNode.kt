package node.expr

import type.BasicType
import type.BasicTypeEnum

class StringNode(string: String) : ExprNode() {

    private val length: Int

    init {
        length = string.length
        type = BasicType(BasicTypeEnum.STRING)
    }
}