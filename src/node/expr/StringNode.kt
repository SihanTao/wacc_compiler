package node.expr

import type.BasicType
import type.BasicTypeEnum

class StringNode(str: String) : ExprNode() {
    val string: String
    val length: Int

    init {
        string = str.substring(1, str.length - 1)
        length = string.length - string.count{ "//".contains(it) }
        type = BasicType(BasicTypeEnum.STRING)
    }

}