package backend

import node.Node
import node.ProgramNode
import node.expr.BoolNode
import node.expr.IntNode
import node.expr.StringNode
import node.stat.*

interface ASTVisitor<T> {
    fun visit(node: Node): T? {
        node.accept(this)
        return null
    }

    fun visitProgramNode(node: ProgramNode): T?
    fun visitSkipNode(node: SkipNode): T?
    fun visitExitNode(node: ExitNode): T?
    fun visitScopeNode(node: ScopeNode): T?
    fun visitSequenceNode(node: SequenceNode): T?
    fun visitIntNode(node: IntNode): T?
    fun visitBoolNode(node: BoolNode): T?
    fun visitPrintNode(node: PrintNode): T?
    fun visitPrintlnNode(node: PrintlnNode): T?
    fun visitStringNode(node: StringNode): T?
}