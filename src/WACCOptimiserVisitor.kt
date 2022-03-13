import node.FuncNode
import node.ProgramNode
import node.expr.*
import node.stat.*
import type.*

class WACCOptimiserVisitor() {

    fun visitProgramNode(node: ProgramNode) {
		node.functions.forEach{ident, func ->
			visitFuncNode(func)
		}
        visitStatNode(node.body)
    }

	fun visitFuncNode(node: FuncNode) {
		if (node.functionBody != null) {
			visitScopeNode(ScopeNode(node.functionBody!!))	
		}
	}

	fun visitStatNode(node: StatNode): StatNode? {
        return when (node) {
            is AssignNode -> visitAssignNode(node)
            is DeclareStatNode -> visitDeclareStatNode(node)
            is ExitNode -> visitExitNode(node)
            is FreeNode -> visitFreeNode(node)
            is IfNode -> visitIfNode(node)
            is PrintlnNode -> visitPrintlnNode(node)
            is PrintNode -> visitPrintNode(node)
            is ReadNode -> visitReadNode(node)
            is ReturnNode -> visitReturnNode(node)
            is ScopeNode -> visitScopeNode(node)
            is SequenceNode -> visitSequenceNode(node)
            is SkipNode -> visitSkipNode(node)
            is WhileNode -> visitWhileNode(node)
			else -> null
        }
    }

	fun visitExprNode(node: ExprNode): ExprNode? {
        return when (node) {
            is ArrayElemNode -> visitArrayElemNode(node)
            is ArrayNode -> visitArrayNode(node)
            is BinopNode -> visitBinopNode(node)
            is BoolNode -> visitBoolNode(node)
            is CharNode -> visitCharNode(node)
            is FunctionCallNode -> visitFunctionCallNode(node)
            is IdentNode -> visitIdentNode(node)
            is IntNode -> visitIntNode(node)
            is PairElemNode -> visitPairElemNode(node)
            is PairNode -> visitPairNode(node)
            is StringNode -> visitStringNode(node)
            is UnopNode -> visitUnopNode(node)
			else -> null
        }
    }

	/* =========================================================
    *                   Statement Visitors
    * =========================================================
    */

	fun visitAssignNode(node: AssignNode): StatNode? {
		 visitExprNode(node.rhs!!)
		 return null
	}

	fun visitDeclareStatNode(node: DeclareStatNode): StatNode? {
		visitExprNode(node.rhs!!)
		return null
	}

	fun visitFreeNode(node: FreeNode): StatNode? {
		visitExprNode(node.expr)
		return null
	}

	fun visitExitNode(node: ExitNode): StatNode? {
		return null
	}

	fun visitIfNode(node: IfNode): StatNode? {
		visitExprNode(node.condition)
		visitStatNode(node.ifBody!!)
		visitStatNode(node.elseBody!!)
		return null
	}

	fun visitPrintlnNode(node: PrintlnNode): StatNode? {
		visitPrintNode(PrintNode(node.expr))
		return null
	}

	fun visitPrintNode(node: PrintNode): StatNode? {
		visitExprNode(node.expr!!)
		return null
	}

	fun visitReadNode(node: ReadNode): StatNode? {
		return null
	}

	fun visitReturnNode(node: ReturnNode): StatNode? {
		visitExprNode(node.expr)
		return null
	}

	fun visitScopeNode(node: ScopeNode): StatNode? {
		node.body.forEach{stat -> visitStatNode(stat)}
		return null
	}

	fun visitSequenceNode(node: SequenceNode): StatNode? {
		node.body.forEach{stat -> visitStatNode(stat)}
		return null
	}

	fun visitSkipNode(node: SkipNode): StatNode? {
		return null
	}

	fun visitWhileNode(node: WhileNode): StatNode? {
		visitStatNode(node.body)
		visitExprNode(node.cond)
		return null
	}

	/* =======================================================
     *                  Expression Visitors
     * =======================================================
     */

	/* The expression visitors are where a lot of our expressions will be
	optimised. If the visitor returns null, it means that there is no
	optimisation possible, so there's no need to create a new node.
	This prevents unneeded creation of new nodes when there are no changes */

	fun visitArrayNode(node: ArrayNode): ExprNode? {
		return null
	}

	fun visitArrayElemNode(node: ArrayElemNode): ExprNode? {

		val newindex: MutableList<ExprNode> = java.util.ArrayList()		
		/* variable to check if any of the expressions have been optimised */
		var isChanged: Boolean = false

		for (expr in node.index) {
			val newexpr: ExprNode? = visitExprNode(expr)
			if (newexpr != null) {
				newindex.add(newexpr)
				isChanged = true
			} else {
				newindex.add(expr)
			}
		}

		if (!isChanged) {
			return ArrayElemNode(node.arrayIdent, node.array, newindex, node.type)
		}
		return null
	}

	fun VisitArrayNode(node: ArrayNode): ExprNode? {

		/* can't be optimised if empty list */
		if (node.length == 0) {
			return null
		}

		val newcontent: MutableList<ExprNode> = java.util.ArrayList()
		/* variable to check if any of the expressions have been optimised */
		var isChanged: Boolean = false

		for (expr in node.content) {
			val newexpr: ExprNode? = visitExprNode(expr)
			if (newexpr != null) {
				newcontent.add(newexpr)
				isChanged = true
			} else {
				newcontent.add(expr)
			}
		}

		if (isChanged) {
			return ArrayNode(node.contentType, newcontent, node.length)
		}
		return null
	}

	fun visitBinopNode(node: BinopNode): ExprNode? {

		val newexpr1: ExprNode? = visitExprNode(node.expr1)
		val newexpr2: ExprNode? = visitExprNode(node.expr2)

		val lhs = newexpr1 ?: node.expr1
		val rhs = newexpr2 ?: node.expr2

		// TODO: Equality of any statements

		// TODO: x + 5 < x + 10 regardless of x

		/* Other Binop expressions can only be reduced if both sides
		are BasicType */

		if (lhs is CharNode && rhs is CharNode) {

			when (node.operator) {

				Utils.Binop.GREATER -> {
					return BoolNode(lhs.char > rhs.char)
				}
				Utils.Binop.GREATER_EQUAL -> {
					return BoolNode(lhs.char >= rhs.char)
				}
				Utils.Binop.LESS -> {
					return BoolNode(lhs.char < rhs.char)
				}
				Utils.Binop.LESS_EQUAL -> {
					return BoolNode(lhs.char <= rhs.char)
				}
			}
		}

		else if (lhs is BoolNode && rhs is BoolNode) {

			when (node.operator) {

				Utils.Binop.AND -> {
					return BoolNode(lhs.`val` && rhs.`val`);
				}
				Utils.Binop.OR -> {
					return BoolNode(lhs.`val` || rhs.`val`);
				}
			}
		}

		else if (lhs is IntNode && rhs is IntNode) {

			when (node.operator) {

				Utils.Binop.PLUS -> {
					return IntNode(lhs.value + rhs.value)
				}
				Utils.Binop.MINUS -> {
					return IntNode(lhs.value - rhs.value)
				}
				Utils.Binop.MUL -> {
					return IntNode(lhs.value * rhs.value)
				}
				Utils.Binop.DIV -> {
					return IntNode(lhs.value / rhs.value)
				}
				Utils.Binop.MOD -> {
					return IntNode(lhs.value % rhs.value)
				}
				Utils.Binop.GREATER -> {
					return BoolNode(lhs.value > rhs.value)
				}
				Utils.Binop.GREATER_EQUAL -> {
					return BoolNode(lhs.value >= rhs.value)
				}
				Utils.Binop.LESS -> {
					return BoolNode(lhs.value < rhs.value)
				}
				Utils.Binop.LESS_EQUAL -> {
					return BoolNode(lhs.value <= rhs.value)
				}
			}
		}
		return null
	}

	fun visitUnopNode(node: UnopNode): ExprNode? {

		val newexpr: ExprNode = visitExprNode(node.expr) ?: return null

		/* TODO: len simplification */

		return when (val type = newexpr.type!!) {
			is BasicType -> when (newexpr) {
				is IntNode -> {
					if (node.operator == Utils.Unop.MINUS) {
						IntNode(-newexpr.value)
					} else {
						CharNode(newexpr.value.toChar().toString())
					}
				}
				is BoolNode -> {
					BoolNode(!newexpr.`val`)
				}
				is CharNode -> {
					IntNode(newexpr.char.toInt())
				}
				else -> null
			}
			else -> null
		}
	}

	fun visitPairElemNode(node: PairElemNode): ExprNode? {

		val newpair: ExprNode = visitExprNode(node.pair) ?: return null

		val pairType = newpair.type
        val pairElemType: Type? = (pairType as PairType).fstType

        return PairElemNode(newpair, pairElemType).fst()
	}

	fun visitPairNode(node: PairNode): ExprNode? {
		val newfst: ExprNode? = visitExprNode(node.fst!!)
		val newsnd: ExprNode? = visitExprNode(node.snd!!)

		if (newfst != null || newsnd != null) {
        	return PairNode(
					newfst ?: node.fst!!,
					newsnd ?: node.snd!!)
		} 
		return null
	}

	fun visitFunctionCallNode(node: FunctionCallNode): ExprNode? {

		val params: MutableList<ExprNode> = java.util.ArrayList()
		node.params.forEach { param ->
			visitExprNode(param)
		}
		return node
	}


	fun visitIdentNode(node: IdentNode): ExprNode? {
		return node
	}


	/****************************************/
	/* Basic Types never need to be changed */
	/****************************************/

	fun visitBoolNode(node: BoolNode): ExprNode? {
		return null
	}

	fun visitCharNode(node: CharNode): ExprNode? {
		return null
	}

	fun visitIntNode(node: IntNode): ExprNode? {
		return null
	}

	fun visitStringNode(node: StringNode): ExprNode? {
		return null
	}

}	
