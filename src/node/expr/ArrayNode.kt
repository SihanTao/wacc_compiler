package node.expr

import type.ArrayType
import type.Type

class ArrayNode(
    contentType: Type?, private var content: MutableList<ExprNode>, var length: Int
) : ExprNode() {

    constructor(contentType: Type?, length: Int) : this(contentType, mutableListOf(), length)

    init {
        type = ArrayType(contentType)
    }

    fun setElem(index: Int, value: ExprNode) {
        content[index] = value
    }

    fun setAllElem(content: MutableList<ExprNode>) {
        this.content = content
        length = content.size
    }
}