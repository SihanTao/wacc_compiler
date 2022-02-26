package backend.instructionGenerator

import node.Node
import node.ProgramNode
import node.stat.SkipNode

interface ASTVisitor<T> {
    fun visit(node: Node): T? {
        node.accept(this)
        return null
    }

    fun visitProgramNode(node: ProgramNode?): T
    fun visitSkipNode(node: SkipNode?): T
}