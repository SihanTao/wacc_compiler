package node.expr

import type.PairType

class PairNode : ExprNode {

    /**
     * Represent a pair of expression nodes
     * Examples: pair(<pair-elem-type>, <pair-elem-type>)
     *           pair(1, 2)
     */

    var fst: ExprNode?
    var snd: ExprNode?

    constructor() {
        fst = null
        snd = null
        type = PairType()
    }

    constructor(fst: ExprNode, snd: ExprNode) {
        this.fst = fst
        this.snd = snd
        type = PairType(fst.type, snd.type)
    }
}