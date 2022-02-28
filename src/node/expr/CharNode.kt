package node.expr

import backend.ASTVisitor
import type.BasicType
import type.BasicTypeEnum

class CharNode(private val liter: String) : ExprNode() {
    /**
     * Represent a char node
     * Examples: 'a', 'b'
     */
    val char: Char

    init {
        type = BasicType(BasicTypeEnum.CHAR)
        if (liter[1] == '\\') {
            char = when (liter[2]) {
                    '0' -> 0.toChar()
                    'b' -> '\b'
                    't' -> '\t'
                    'n' -> '\n'
                    'f' -> 12.toChar()
                    '"' -> '\"'
                    '\'' -> '\''
                    '\\' -> '\\'
                    else -> error("invalid escaped character")
                }
        } else {
            char = liter[1]
        }
    }

    override fun <T> accept(astVisitor: ASTVisitor<T>): T? {
        return astVisitor.visitCharNode(this)
    }

}