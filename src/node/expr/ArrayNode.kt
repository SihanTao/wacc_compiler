package node.expr

import type.ArrayType
import type.Type

/**
 * Represent an array with a set function for single digit and set all elements
 * Examples: [a, b, c, d, e]
 */

class ArrayNode(
    private val contentType: Type?,
    val content: MutableList<ExprNode>,
    var length: Int
) : ExprNode() {

    constructor(contentType: Type?, length: Int) : this(
        contentType,
        mutableListOf(),
        length
    )

    init {
        type = ArrayType(contentType)
    }

    fun getContentSize(): Int {
        return if (contentType == null || content.isEmpty()) 0 else content[0].type!!.size()
    }

}