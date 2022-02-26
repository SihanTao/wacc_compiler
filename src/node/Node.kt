package node

import backend.ASTVisitor

interface Node {
    fun <T> accept(astVisitor: ASTVisitor<T>): T? {
        return null
    }
}