package node

import node.stat.StatNode

class ProgramNode(
    val functions: MutableMap<String, FuncNode>, val body: StatNode
) : Node