package node.expr

import type.BasicType
import type.BasicTypeEnum

class StringNode(val string: String) : ExprNode() {

    /**
     * Represent a string
     * Examples: "string"
     */

    val length: Int

    init {
        length = string.length - string.count{ "\\".contains(it) }
        type = BasicType(BasicTypeEnum.STRING)
    }
}
