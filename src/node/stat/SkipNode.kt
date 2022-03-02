package node.stat

import SymbolTable
import backend.ASTVisitor

class SkipNode(symbolTable: SymbolTable) : StatNode() {
    init {
        scope = symbolTable
    }

    override fun <T> accept(astVisitor: ASTVisitor<T>): T? {
        return astVisitor.visitSkipNode(this)
    }
}
