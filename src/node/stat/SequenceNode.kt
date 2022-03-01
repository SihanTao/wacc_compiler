package node.stat

import backend.ASTVisitor

class SequenceNode : StatNode {
    /**
     * Represent BEGIN <stat> END scope statement, as well as
     * <stat> ; <stat> sequential statement
    </stat></stat></stat> */
    private val body: MutableList<StatNode> = ArrayList<StatNode>()
    private var isFuncBody = false
    private var isBeginEnd = false

    constructor(node: StatNode) {
        if (node is SequenceNode) {
            body.addAll(node.body)
        } else {
            body.add(node)
        }
        isReturned = endValue
        isBeginEnd = true
        scope = node.scope
    }

    /* Handle the sequential statement */
    constructor(before: StatNode, after: StatNode) {
        mergeScope(before)
        mergeScope(after)
        isReturned = (endValue)
    }

    private fun mergeScope(s: StatNode) {
        if (s is SequenceNode && !s.isBeginEnd) {
            body.addAll(s.body)
        } else if (s !is SkipNode) {
            body.add(s)
        }
    }

    /* This will help to determine whether there is a return statement at the end of a sequence */
    private val endValue: Boolean
        get() = !body.isEmpty() && body[body.size - 1].isReturned


    override fun <T> accept(astVisitor: ASTVisitor<T>): T? {
        return astVisitor.visitSequenceNode(this)
    }

    fun size(): Int {
        return scope!!.tableSize
    }
}