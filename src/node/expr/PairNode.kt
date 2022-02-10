package node.expr

import type.PairType

class PairNode : ExprNode {

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