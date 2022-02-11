package node.expr

import type.BasicType
import type.BasicTypeEnum

class StringNode(string: String) : ExprNode() {

    /**
     * Represent a string
     * Examples: "string"
     */

    private val length: Int

    init {
        length = string.length
        type = BasicType(BasicTypeEnum.STRING)
    }
}