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

	private fun visitFuncNode(node: FuncNode) {
		if (node.functionBody != null) {
			visitScopeNode(ScopeNode(node.functionBody!!))	
		}
	}

	private fun visitStatNode(node: StatNode): StatNode? {
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

	private fun visitExprNode(node: ExprNode): ExprNode? {
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

	private fun visitAssignNode(node: AssignNode): StatNode? {
		 visitExprNode(node.rhs!!)
		 return null
	}

	private fun visitDeclareStatNode(node: DeclareStatNode): StatNode? {
		visitExprNode(node.rhs!!)
		return null
	}

	private fun visitFreeNode(node: FreeNode): StatNode? {
		visitExprNode(node.expr)
		return null
	}

	private fun visitExitNode(node: ExitNode): StatNode? {
		return null
	}

	private fun visitIfNode(node: IfNode): StatNode? {
		visitExprNode(node.condition)
		visitStatNode(node.ifBody!!)
		visitStatNode(node.elseBody!!)
		return null
	}

	private fun visitPrintlnNode(node: PrintlnNode): StatNode? {
		visitPrintNode(PrintNode(node.expr))
		return null
	}

	private fun visitPrintNode(node: PrintNode): StatNode? {
		visitExprNode(node.expr!!)
		return null
	}

	private fun visitReadNode(node: ReadNode): StatNode? {
		return null
	}

	private fun visitReturnNode(node: ReturnNode): StatNode? {
		visitExprNode(node.expr)
		return null
	}

	private fun visitScopeNode(node: ScopeNode): StatNode? {
		node.body.forEach{stat -> visitStatNode(stat)}
		return null
	}

	private fun visitSequenceNode(node: SequenceNode): StatNode? {
		node.body.forEach{stat -> visitStatNode(stat)}
		return null
	}

	private fun visitSkipNode(node: SkipNode): StatNode? {
		return null
	}

	private fun visitWhileNode(node: WhileNode): StatNode? {
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

	private fun visitArrayNode(node: ArrayNode): ExprNode? {
		return null
	}

	private fun visitArrayElemNode(node: ArrayElemNode): ExprNode? {

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

	private fun VisitArrayNode(node: ArrayNode): ExprNode? {

		/* can't be optimised if empty list */
		if (node.length == 0) {
			return null
		}

		var newcontent: MutableList<ExprNode> = java.util.ArrayList()
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

	private fun visitBinopNode(node: BinopNode): ExprNode? {

		/*
		val newexpr1: ExprNode? = visitExprNode(node.expr1)
		val newexpr2: ExprNode? = visitExprNode(node.expr2)

		if (newexpr1 == null && newexpr2 == null) {
			return null
		}

		if (newexpr1 == null) {
			val lhs: ExprNode = node.expr1
		} else {
			val lhs: ExprNode = newexpr1
		}

		if (newexpr2 == null) {
			val rhs: ExprNode = node.expr2
		} else {
			val rhsL ExprNode = newexpr2
		}

		// TODO

		/* TODO: Equality of any statements */

		/* TODO: x + 5 < x + 10 regardless of x */

		/* Other Binop expressions can only be reduced if both sides
		are BasicType */

		if (lhs.type!! is BasicType && rhs.type!! is BasicType) {

			/* There's no need to check rhs_type, as we know the program is
			semantically valid at this stage, so types must match */

			if (lhs is IntNode) {

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

			if (lhs.type.typeEnum is BasicTypeEnum.BOOLEAN) {
				
				when (node.operator) {

					Utils.Binop.AND -> {
						return BoolNode(lhs.`val` && rhs.`val`);
					}
					Utils.Binop.OR -> {
						return BoolNode(lhs.`val` || rhs.`val`);
					}
			}

			if (lhs_type.typeEnum is BasicTypeEnum.INTEGER) {

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

		} else {
			return BinopNode(lhs, rhs, node.operator)
		}
		*/
		return null
	}

	private fun visitUnopNode(node: UnopNode): ExprNode? {

		val newexpr: ExprNode? = visitExprNode(node.expr)

		if (newexpr == null) {
			return null
		}

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

	private fun visitPairElemNode(node: PairElemNode): ExprNode? {

		val newpair: ExprNode? = visitExprNode(node.pair!!)

		if (newpair == null) {
			return null
		}

        val pairType = newpair.type
        val pairElemType: Type? = (pairType as PairType).fstType

        return PairElemNode(newpair, pairElemType).fst()
	}

	private fun visitPairNode(node: PairNode): ExprNode? {
		val newfst: ExprNode? = visitExprNode(node.fst!!)
		val newsnd: ExprNode? = visitExprNode(node.snd!!)

		if (newfst != null && newsnd != null) {
        	return PairNode(
				if (newfst == null) node.fst!! else newfst!!,
				if (newsnd == null) node.snd!! else newsnd!!)
		} 
		return null
	}

	private fun visitFunctionCallNode(node: FunctionCallNode): ExprNode? {

		val params: MutableList<ExprNode> = java.util.ArrayList()
		node.params.forEach { param ->
			visitExprNode(param)
		}
		return node
	}


	private fun visitIdentNode(node: IdentNode): ExprNode? {
		return node
	}


	/****************************************/
	/* Basic Types never need to be changed */
	/****************************************/

	private fun visitBoolNode(node: BoolNode): ExprNode? {
		return null
	}

	private fun visitCharNode(node: CharNode): ExprNode? {
		return null
	}

	private fun visitIntNode(node: IntNode): ExprNode? {
		return null
	}

	private fun visitStringNode(node: StringNode): ExprNode? {
		return null
	}

}	