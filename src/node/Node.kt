package node

import backend.instructionGenerator.ASTVisitor

interface Node {
    fun <T> accept(astVisiter: ASTVisitor<T>) {

    }
}