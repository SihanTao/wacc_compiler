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

	// reduces the rhs expression of an assignment statement
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

	// reduces the argument to the exit node
	fun visitExitNode(node: ExitNode): StatNode? {
		val newExitCode: ExprNode? = visitExprNode(node.exitCode)
		if (newExitCode != null) {
			node.exitCode = newExitCode
		}
		return null
	}

	// first reduces the condition of the if node, and then
	// replaces with the if-body if the condition is always true,
	// and replaces with the else-body if the condition is always false
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

	// reduces the argument to println
	fun visitPrintlnNode(node: PrintlnNode): StatNode? {
		if (node.expr != null) {
			val newExpr: ExprNode? = visitExprNode(node.expr!!)
			if (newExpr != null) {
				node.expr = newExpr
			}
		}
		return null
	}

	// reduces the argument to the print
	fun visitPrintNode(node: PrintNode): StatNode? {
		if (node.expr != null) {
			val newExpr: ExprNode? = visitExprNode(node.expr!!)
			if (newExpr != null) {
				node.expr = newExpr
			}
		}
		return null
	}

	// reduces the argument to the return
	fun visitReturnNode(node: ReturnNode): StatNode? {
		val newExpr: ExprNode? = visitExprNode(node.expr)
		if (newExpr != null) {
			node.expr = newExpr
		}
		return null
	}

	// iterates through a list of statements and visits all of them
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

	// reduces the condition of the while node, and then
	// removes the entire while block if the condition is always false
	fun visitWhileNode(node: WhileNode): StatNode? {
		val newCond: ExprNode? = visitExprNode(node.cond)
		/* if (newCond != null) {
			node.cond = newCond
		} */
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

	/* Attempts to optimise expressions in a list of expressions */
	private fun optimiseListOfExpressions(list: MutableList<ExprNode>) {
		for (i in 0 until list.size) {
			val newExpr: ExprNode? = visitExprNode(list[i])
			if (newExpr != null) {
				list[i] = newExpr
			}
		}
	}

	// first reduces the arguments to array access (the indices),
	// and then tries to replace with the direct value of the array access
	fun visitArrayElemNode(node: ArrayElemNode): ExprNode? {
		if (constantPropagationAnalysis) {
			var currArray = (node.array as ArrayNode)
			var returnElem: ExprNode? = null
			for (index in node.index) {
				val indexOptimised = visitExprNode(index) ?: index
				if (indexOptimised is IntNode) {
					if (indexOptimised.value < currArray.length && indexOptimised.value >= 0) {
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

	// reduces the list of expressions in an array literal
	fun visitArrayNode(node: ArrayNode): ExprNode? {
		/* can't be optimised if empty list */
		if (node.length == 0) {
			return null
		}

		optimiseListOfExpressions(node.content)
		return null
	}

	// returns if an integer is between -2^31 and 2^31 - 1,
	// i.e. if it is a valid integer
	fun checkIntValid(value: Long): Boolean {
		return value >= -2147483648 && value <= 2147483647
	}

	// Reduces the arguments to the binary operation, and then
	// attempts to reduce the binary operation
	fun visitBinopNode(node: BinopNode): ExprNode? {

		val optimisedExpr1 = visitExprNode(node.expr1)
		val optimisedExpr2 = visitExprNode(node.expr2)

		if (optimisedExpr1 != null) {
			node.expr1 = optimisedExpr1
		}
		if (optimisedExpr2 != null) {
			node.expr2 = optimisedExpr2
		}

		val lhs = node.expr1
		val rhs = node.expr2

		/* Binop expressions can only be reduced if both sides
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
					val value = lhs.value.toLong() + rhs.value
					return if (checkIntValid(value)) {
						IntNode(value.toInt())
					} else {
						null
					}
				}
				Utils.Binop.MINUS -> {
					val value = lhs.value.toLong() - rhs.value
					return if (checkIntValid(value)) {
						IntNode(value.toInt())
					} else {
						null
					}
				}
				Utils.Binop.MUL -> {
					val value = lhs.value.toLong() * rhs.value
					return if (checkIntValid(value)) {
						IntNode(value.toInt())
					} else {
						null
					}
				}
				Utils.Binop.DIV -> {
					if (rhs.value == 0) {
						null
					} else {
						IntNode(lhs.value / rhs.value)
					}
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

	// reduces the argument to the unary operation,
	// and then attempts to reduce to an int/bool/char
	fun visitUnopNode(node: UnopNode): ExprNode? {

		node.expr = visitExprNode(node.expr) ?: node.expr

		if (node.expr is IntNode || node.expr is BoolNode ||
				node.expr is CharNode || node.expr is ArrayNode) {
			return when (node.operator) {
				Utils.Unop.MINUS -> {
					val value = -((node.expr as IntNode).value.toLong())
					return if (checkIntValid(value)) {
						IntNode(value.toInt())
					} else {
						null
					}
				}
				Utils.Unop.CHR -> {
					CharNode("'" + (node.expr as IntNode).value.toChar().toString() + "'")
				}
				Utils.Unop.NOT -> {
					BoolNode(!(node.expr as BoolNode).`val`)
				}
				Utils.Unop.ORD -> {
					IntNode((node.expr as CharNode).char.code)
				}
				Utils.Unop.LEN -> {
					if (constantPropagationAnalysis) {
						IntNode((node.expr as ArrayNode).length)
					} else {
						null
					}
				}
			}
		}
		return null
	}

	// reduces arguments to pair
	fun visitPairNode(node: PairNode): ExprNode? {

		val newfst: ExprNode? = if (node.fst != null) visitExprNode(node.fst!!) else null
		val newsnd: ExprNode? = if (node.snd != null) visitExprNode(node.snd!!) else null

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

	// will simply reduce the arguments to the function call
	fun visitFunctionCallNode(node: FunctionCallNode): ExprNode? {
		optimiseListOfExpressions(node.params)
		return null
	}

	// replaces identifiers with the corresponding expression node,
	// which it tries to reduce
	fun visitIdentNode(node: IdentNode): ExprNode? {
		if (constantPropagationAnalysis) {
			val newExpr = currSymbolTable!!.lookupAll(node.name)
			if (newExpr != null) {
				return visitExprNode(newExpr) ?: newExpr
			}
		}
		return null
	}

}	
