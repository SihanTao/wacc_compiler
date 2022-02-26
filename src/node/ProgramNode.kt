package node

import backend.ASTVisitor
import node.stat.StatNode

class ProgramNode(
    val functions: MutableMap<String, FuncNode>, val body: StatNode
) : Node {

    override fun <T> accept(astVisiter: ASTVisitor<T>): T? {
        return astVisiter.visitProgramNode(this)
    }
}