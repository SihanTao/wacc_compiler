package node.expr

import type.ArrayType
import type.Type



class ArrayNode(
    /**
     * Represent an array with a set function for single digit and set all elements
     * Examples: [a, b, c, d, e]
     */

    val contentType: Type?, var content: MutableList<ExprNode>, var length: Int
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