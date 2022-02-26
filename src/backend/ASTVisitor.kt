package backend

import node.Node
import node.ProgramNode
import node.stat.ExitNode
import node.stat.ScopeNode
import node.stat.SkipNode

interface ASTVisitor<T> {
    fun visit(node: Node): T? {
        node.accept(this)
        return null
    }

    fun visitProgramNode(node: ProgramNode): T?
    fun visitSkipNode(node: SkipNode): T?
    fun visitExitNode(node: ExitNode): T?
    fun visitScopeNode(node: ScopeNode): T?
}