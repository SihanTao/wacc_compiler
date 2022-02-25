package node.expr

import type.BasicType
import type.BasicTypeEnum

class CharNode(val ch: Char) : ExprNode() {
    /**
     * Represent a char node
     * Examples: 'a', 'b'
     */

    init {
        type = BasicType(BasicTypeEnum.CHAR)
    }
}