import node.FuncNode
import node.ProgramNode
import node.expr.*
import node.stat.*
import type.*

class WACCOptimiserVisitor(optimisationLevel: Int) {

	private val constantPropagationAnalysis: Boolean
	private val controlFlowAnalysis: Boolean

	var currSymbolTable: SymbolTable<ExprNode>? = null

	init {
		constantPropagationAnalysis = optimisationLevel >= 2
		controlFlowAnalysis = optimisationLevel >= 3
	}

    fun visitProgramNode(node: ProgramNode) {
		node.functions.forEach{ (_, func) ->
			visitFuncNode(func)
		}
		val newBody : StatNode? = visitStatNode(node.body)
        if (newBody != null) {
			node.body = newBody
		}
    }

	fun visitFuncNode(node: FuncNode) {
		if (node.functionBody != null) {
			val newFunctionBody = visitStatNode(node.functionBody!!)
			if (newFunctionBody != null) {
				node.functionBody = newFunctionBody
			}
		}
	}

	fun visitStatNode(node: StatNode): StatNode? {
		currSymbolTable = node.scope
        return when (node) {
            is AssignNode -> visitAssignNode(node)
            is DeclareStatNode -> visitDeclareStatNode(node)
            is ExitNode -> visitExitNode(node)
            is IfNode -> visitIfNode(node)
            is PrintlnNode -> visitPrintlnNode(node)
            is PrintNode -> visitPrintNode(node)
            is ReturnNode -> visitReturnNode(node)
            is ScopeNode -> visitScopeNode(node)
            is SequenceNode -> visitSequenceNode(node)
            is WhileNode -> visitWhileNode(node)
			else -> null
        }
    }

	fun visitExprNode(node: ExprNode): ExprNode? {
        return when (node) {
            is ArrayElemNode -> visitArrayElemNode(node)
            is ArrayNode -> visitArrayNode(node)
            is BinopNode -> visitBinopNode(node)
            is FunctionCallNode -> visitFunctionCallNode(node)
            is IdentNode -> visitIdentNode(node)
            is PairNode -> visitPairNode(node)
            is UnopNode -> visitUnopNode(node)
			else -> null
        }
    }

	/* =========================================================
    *                   Statement Visitors
    * =========================================================
    */

	fun visitAssignNode(node: AssignNode): StatNode? {
		val newrhs: ExprNode? = visitExprNode(node.rhs!!)
		if (newrhs != null) {
			node.rhs = newrhs
		}
		return null
	}

	fun visitDeclareStatNode(node: DeclareStatNode): StatNode? {
		val newrhs: ExprNode? = visitExprNode(node.rhs!!)
		if (newrhs != null) {
			node.rhs = newrhs
		}
		return null
	}

	fun visitExitNode(node: ExitNode): StatNode? {
		val newExitCode: ExprNode? = visitExprNode(node.exitCode)
		if (newExitCode != null) {
			node.exitCode = newExitCode
		}
		return null
	}

	fun visitIfNode(node: IfNode): StatNode? {
		val newCondition: ExprNode? = visitExprNode(node.condition)
		if (newCondition != null) {
			node.condition = newCondition
		}
		val newIfBody: StatNode? = visitStatNode(node.ifBody!!)
		val newElseBody: StatNode? = visitStatNode(node.elseBody!!)
		if (newIfBody != null) {
			node.ifBody = newIfBody
		}
		if (newElseBody != null) {
			node.elseBody = newElseBody
		}
		if (controlFlowAnalysis) {
			if (node.condition is BoolNode) {
				return if ((node.condition as BoolNode).`val`) {
					node.ifBody
				} else {
					node.elseBody
				}
			}
		}
		return null
	}

	fun visitPrintlnNode(node: PrintlnNode): StatNode? {
		if (node.expr != null) {
			val newExpr: ExprNode? = visitExprNode(node.expr!!)
			if (newExpr != null) {
				node.expr = newExpr
			}
		}
		return null
	}

	fun visitPrintNode(node: PrintNode): StatNode? {
		if (node.expr != null) {
			val newExpr: ExprNode? = visitExprNode(node.expr!!)
			if (newExpr != null) {
				node.expr = newExpr
			}
		}
		return null
	}

	fun visitReturnNode(node: ReturnNode): StatNode? {
		val newExpr: ExprNode? = visitExprNode(node.expr)
		if (newExpr != null) {
			node.expr = newExpr
		}
		return null
	}

	private fun optimiseListOfStatements(list: MutableList<StatNode>) {
		for (i in 0 until list.size) {
			val newStat: StatNode? = visitStatNode(list[i])
			if (newStat != null) {
				list[i] = newStat
			}
		}
	}

	fun visitScopeNode(node: ScopeNode): StatNode? {
		optimiseListOfStatements(node.body)
		return null
	}

	fun visitSequenceNode(node: SequenceNode): StatNode? {
		optimiseListOfStatements(node.body)
		return null
	}

	fun visitWhileNode(node: WhileNode): StatNode? {
		val newCond: ExprNode? = visitExprNode(node.cond)
		if (newCond != null) {
			node.cond = newCond
		}
		val newBody: StatNode? = visitStatNode(node.body)
		if (newBody != null) {
			node.body = newBody
		}
		if (controlFlowAnalysis) {
			if (node.cond is BoolNode) {
				if (!(node.cond as BoolNode).`val`) {
					return SkipNode()
				}
			}
		}
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

	/* Attempts to optimise expressions in a list of expressions,
	returns false if no optimisation was possible
	 */
	private fun optimiseListOfExpressions(list: MutableList<ExprNode>) {
		for (i in 0 until list.size) {
			val newExpr: ExprNode? = visitExprNode(list[i])
			if (newExpr != null) {
				list[i] = newExpr
			}
		}
	}

	fun visitArrayElemNode(node: ArrayElemNode): ExprNode? {
		if (constantPropagationAnalysis) {
			var currArray = (node.array as ArrayNode)
			var returnElem: ExprNode? = null
			for (index in node.index) {
				val indexOptimised = visitExprNode(index) ?: index
				if (indexOptimised is IntNode) {
					if (indexOptimised.value < currArray.length) {
						returnElem = currArray.content[indexOptimised.value]
						if (returnElem is ArrayNode) {
							currArray = returnElem
						}
					}
				} else {
					return null
				}
			}
			return returnElem
		} else {
			optimiseListOfExpressions(node.index)
			return null
		}
	}

	fun visitArrayNode(node: ArrayNode): ExprNode? {
		/* can't be optimised if empty list */
		if (node.length == 0) {
			return null
		}

		optimiseListOfExpressions(node.content)
		return null
	}

	fun visitBinopNode(node: BinopNode): ExprNode? {

		val lhs = visitExprNode(node.expr1) ?: node.expr1
		val rhs = visitExprNode(node.expr2) ?: node.expr2

		// TODO: Equality of any statements

		// TODO: x + 5 < x + 10 regardless of x

		/* Other Binop expressions can only be reduced if both sides
		are BasicType */

		if (lhs is CharNode && rhs is CharNode) {

			return when (node.operator) {

				Utils.Binop.GREATER -> {
					BoolNode(lhs.char > rhs.char)
				}
				Utils.Binop.GREATER_EQUAL -> {
					BoolNode(lhs.char >= rhs.char)
				}
				Utils.Binop.LESS -> {
					BoolNode(lhs.char < rhs.char)
				}
				Utils.Binop.LESS_EQUAL -> {
					BoolNode(lhs.char <= rhs.char)
				}
				Utils.Binop.EQUAL -> {
					BoolNode(lhs.char == rhs.char)
				}
				Utils.Binop.INEQUAL -> {
					BoolNode(lhs.char != rhs.char)
				}
				else -> null
			}
		}

		else if (lhs is BoolNode && rhs is BoolNode) {

			return when (node.operator) {

				Utils.Binop.AND -> {
					BoolNode(lhs.`val` && rhs.`val`)
				}
				Utils.Binop.OR -> {
					BoolNode(lhs.`val` || rhs.`val`)
				}
				Utils.Binop.EQUAL -> {
					BoolNode(lhs.`val` == rhs.`val`)
				}
				Utils.Binop.INEQUAL -> {
					BoolNode(lhs.`val` != rhs.`val`)
				}
				else -> null
			}
		}

		else if (lhs is IntNode && rhs is IntNode) {

			return when (node.operator) {

				Utils.Binop.PLUS -> {
					IntNode(lhs.value + rhs.value)
				}
				Utils.Binop.MINUS -> {
					IntNode(lhs.value - rhs.value)
				}
				Utils.Binop.MUL -> {
					IntNode(lhs.value * rhs.value)
				}
				Utils.Binop.DIV -> {
					IntNode(lhs.value / rhs.value)
				}
				Utils.Binop.MOD -> {
					IntNode(lhs.value % rhs.value)
				}
				Utils.Binop.GREATER -> {
					BoolNode(lhs.value > rhs.value)
				}
				Utils.Binop.GREATER_EQUAL -> {
					BoolNode(lhs.value >= rhs.value)
				}
				Utils.Binop.LESS -> {
					BoolNode(lhs.value < rhs.value)
				}
				Utils.Binop.LESS_EQUAL -> {
					BoolNode(lhs.value <= rhs.value)
				}
				Utils.Binop.EQUAL -> {
					BoolNode(lhs.value == rhs.value)
				}
				Utils.Binop.INEQUAL -> {
					BoolNode(lhs.value != rhs.value)
				}
				else -> null
			}
		}
		return null
	}

	fun visitUnopNode(node: UnopNode): ExprNode? {

		val newexpr: ExprNode = visitExprNode(node.expr) ?: node.expr

		return when (node.operator) {
			Utils.Unop.MINUS -> {
				IntNode(-(newexpr as IntNode).value)
			}
			Utils.Unop.CHR -> {
				CharNode("'" + (newexpr as IntNode).value.toChar().toString() + "'")
			}
			Utils.Unop.NOT -> {
				BoolNode(!(newexpr as BoolNode).`val`)
			}
			Utils.Unop.ORD -> {
				IntNode((newexpr as CharNode).char.code)
			}
			Utils.Unop.LEN -> {
				if (constantPropagationAnalysis) {
					IntNode((newexpr as ArrayNode).length)
				} else {
					null
				}
			}
		}
	}

	fun visitPairNode(node: PairNode): ExprNode? {
		val newfst: ExprNode? = visitExprNode(node.fst!!)
		val newsnd: ExprNode? = visitExprNode(node.snd!!)

		if (newfst != null || newsnd != null) {
			if (newfst != null) {
				node.fst = newfst
			}
			if (newsnd != null) {
				node.snd = newsnd
			}
		} 
		return null
	}

	fun visitFunctionCallNode(node: FunctionCallNode): ExprNode? {
		optimiseListOfExpressions(node.params)
		return null
	}

	fun visitIdentNode(node: IdentNode): ExprNode? {
		if (constantPropagationAnalysis) {
			val newExpr = currSymbolTable!!.lookupAll(node.name)
			if (newExpr != null) {
				val optimisedExpr = visitExprNode(newExpr)
				 if (optimisedExpr != null) {
					 return optimisedExpr
				 } else {
					 return newExpr
				 }
			}
		}
		return null
	}

}	
