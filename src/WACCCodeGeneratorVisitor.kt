import node.FuncNode
import node.ParamNode
import node.ProgramNode
import node.expr.*
import node.stat.*

class WACCCodeGeneratorVisitor {
    fun visitProgramNode(node: ProgramNode) {}

    private fun visitFuncNode(node: FuncNode) {}

    private fun visitParamNode(node: ParamNode) {}

    private fun visitStatNode(node: StatNode) {
        when (node) {
            is AssignNode -> visitAssignNode(node)
            is DeclareStatNode -> visitDeclareStatNode(node)
            is ExitNode -> visitExitNode(node)
            is FreeNode -> visitFreeNode(node)
            is IfNode -> visitIfNode(node)
            is PrintlnNode -> visitPrintlnNode(node)
            is PrintNode -> visitPrintNode(node)
            is ReadNode -> visitReadNode(node)
            is ReturnNode -> visitReturnNode(node)
            is ScopeNode -> visitScopeNode(node)
            is SequenceNode -> visitSequenceNode(node)
            is SkipNode -> visitSkipNode(node)
            is StatNode -> visitStatNode(node)
            is WhileNode -> visitWhileNode(node)
        }
    }

    private fun visitExprNode(node: ExprNode) {
        when (node) {
            is ArrayElemNode -> visitArrayElemNode(node)
            is ArrayNode -> visitArrayNode(node)
            is BinopNode -> visitBinopNode(node)
            is BoolNode -> visitBoolNode(node)
            is CharNode -> visitCharNode(node)
            is ExprNode -> visitExprNode(node)
            is FunctionCallNode -> visitFunctionCallNode(node)
            is IdentNode -> visitIdentNode(node)
            is IntNode -> visitIntNode(node)
            is PairElemNode -> visitPairElemNode(node)
            is PairNode -> visitPairNode(node)
            is StringNode -> visitStringNode(node)
            is UnopNode -> visitUnopNode(node)
        }
    }


    private fun visitAssignNode(node: AssignNode) {}
    private fun visitDeclareStatNode(node: DeclareStatNode) {}
    private fun visitExitNode(node: ExitNode) {}
    private fun visitFreeNode(node: FreeNode) {}
    private fun visitIfNode(node: IfNode) {}
    private fun visitPrintlnNode(node: PrintlnNode) {}
    private fun visitPrintNode(node: PrintNode) {}
    private fun visitReadNode(node: ReadNode) {}
    private fun visitReturnNode(node: ReturnNode) {}
    private fun visitScopeNode(node: ScopeNode) {}
    private fun visitSequenceNode(node: SequenceNode) {}
    private fun visitSkipNode(node: SkipNode) {}
    private fun visitWhileNode(node: WhileNode) {}

    private fun visitArrayElemNode(node: ArrayElemNode) {}
    private fun visitArrayNode(node: ArrayNode) {}
    private fun visitBinopNode(node: BinopNode) {}
    private fun visitBoolNode(node: BoolNode) {}
    private fun visitCharNode(node: CharNode) {}
    private fun visitFunctionCallNode(node: FunctionCallNode) {}
    private fun visitIdentNode(node: IdentNode) {}
    private fun visitIntNode(node: IntNode) {}
    private fun visitPairElemNode(node: PairElemNode) {}
    private fun visitPairNode(node: PairNode) {}
    private fun visitStringNode(node: StringNode) {}
    private fun visitUnopNode(node: UnopNode) {}
}