package backend.instructionGenerator

import node.Node
import node.stat.SkipNode

interface ASTVisitor<T> {
    fun visit(node: Node): T? {
        node.accept(this)
        return null
    }

    fun visitSkipNode(node: SkipNode?): T
}