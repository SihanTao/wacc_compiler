package backend

import node.Node
import node.ProgramNode
import node.expr.*
import node.stat.*

interface ASTVisitor<T> {
    fun visit(node: Node): T? {
        node.accept(this)
        return null
    }

    fun visitProgramNode(node: ProgramNode): T?
    fun visitSkipNode(node: SkipNode): T?
    fun visitExitNode(node: ExitNode): T?
    fun visitSequenceNode(node: SequenceNode): T?
    fun visitIntNode(node: IntNode): T?
    fun visitBoolNode(node: BoolNode): T?
    fun visitCharNode(node: CharNode): T?
    fun visitStringNode(node: StringNode): T?
    fun visitArrayNode(node: ArrayNode): T?
    fun visitArrayElemNode(node: ArrayElemNode): T?
    fun visitPairElemNode(node: PairElemNode): T?
    fun visitPairNode(node: PairNode): T?
    fun visitReadNode(node: ReadNode): T?
    fun visitPrintNode(node: PrintNode): T?
    fun visitPrintlnNode(node: PrintlnNode): T?
    fun visitIfNode(node: IfNode): T?
    fun visitWhileNode(node: WhileNode): T?
    fun visitDeclareStatNode(node: DeclareStatNode): T?
    fun visitIdentNode(node: IdentNode): T?
    fun visitAssignNode(node: AssignNode): T?
    fun visitBinopNode(node: BinopNode): T?
    fun visitUnopNode(node: UnopNode): T?
}