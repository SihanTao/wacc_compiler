package node.stat

class SequenceNode (stat1: StatNode?, stat2: StatNode?) : StatNode() {

    private val body: MutableList<StatNode> = ArrayList()

    private fun mergeScope(s: StatNode?) {
        if (s is SequenceNode) {
            body.addAll(s.body)
        } else if (s !is SkipNode) {
            if (s != null) {
                body.add(s)
            }
        }
    }

    /* This will help to determine whether there is a return statement at the end of a sequence */
    private val endValue: Boolean
        get() = !body.isEmpty() && body[body.size - 1].isReturned

    init {
        mergeScope(stat1)
        mergeScope(stat2)
        isReturned = endValue
    }
}