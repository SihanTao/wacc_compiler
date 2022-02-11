package node

import node.stat.StatNode

class ProgramNode(
    private val functions: MutableMap<String, FuncNode>, private val body: StatNode
) : Node