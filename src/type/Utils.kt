package type

import org.antlr.v4.runtime.ParserRuleContext

class Utils {


    enum class Unop {
        NOT, MINUS, LEN, ORD, CHR
    }

    enum class Binop {
        PLUS, MINUS, MUL, DIV, MOD, GREATER, GREATER_EQUAL, LESS, LESS_EQUAL, EQUAL, INEQUAL, AND, OR
    }

    companion object {
        // If program has error, return true
        @JvmStatic
        fun typeCheck(ctx: ParserRuleContext?, expected: Set<Type>, actual: Type): Boolean {
            if (expected.stream().noneMatch(actual::equals)) {
                ErrorHandler.typeMismatch(ctx!!, expected, actual)
                return true
            }
            return false
        }

        @JvmStatic
        fun typeCheck(ctx: ParserRuleContext?, expected: Type?, actual: Type): Boolean {
            try {
                if (actual != expected) {
                    ErrorHandler.typeMismatch(ctx!!, expected!!, actual)
                    return true
                }
            } catch (e: NullPointerException) {
                // This is the special case when expected is Array<null> when declared
                return false
            }
            return false
        }

        @JvmStatic
        fun typeCheck(
            ctx: ParserRuleContext?, varName: String?, expected: Type?,
            actual: Type
        ): Boolean {
            try {
                if (actual != expected) {
                    ErrorHandler.typeMismatch(ctx!!, varName!!, expected!!, actual)
                    return true
                }
            } catch (e: NullPointerException) {
                // This is the special case when expected is Array<null> when declared
                return false
            }
            return false
        }

        /* Basic Types */
        val INT_T: Type = BasicType(BasicTypeEnum.INTEGER)
        val BOOL_T: Type = BasicType(BasicTypeEnum.BOOLEAN)
        val CHAR_T: Type = BasicType(BasicTypeEnum.CHAR)
        val STRING_T: Type = BasicType(BasicTypeEnum.STRING)
        val ARRAY_T: Type = ArrayType()

        val PAIR_T: Type = PairType()

        @JvmStatic
        val unopEnumMapping: Map<String, Unop> = mapOf(
            "-" to Unop.MINUS,
            "chr" to Unop.CHR,
            "!" to Unop.NOT,
            "len" to Unop.LEN,
            "ord" to Unop.ORD
        )

        @JvmStatic
        val unopTypeMapping: Map<String, Type> = mapOf(
            "-" to INT_T,
            "chr" to INT_T,
            "!" to BOOL_T,
            "len" to ARRAY_T,
            "ord" to CHAR_T
        )

        @JvmStatic
        val binopEnumMapping: Map<String, Binop> = mapOf(
            "+" to Binop.PLUS,
            "-" to Binop.MINUS,
            "*" to Binop.MUL,
            "/" to Binop.DIV,
            "%" to Binop.MOD
        )

        @JvmStatic
        val EqEnumMapping: Map<String, Binop> = mapOf("==" to Binop.EQUAL, "!=" to Binop.INEQUAL)

        @JvmStatic
        val LogicOpEnumMapping: Map<String, Binop> = mapOf("&&" to Binop.AND, "||" to Binop.OR)

        @JvmStatic
        val CmpEnumMapping: Map<String, Binop> = mapOf(
            ">" to Binop.GREATER,
            ">=" to Binop.GREATER_EQUAL,
            "<" to Binop.LESS,
            "<=" to Binop.LESS_EQUAL
        )

        /* a list of allowed types in read, free, cmp statement */
        @JvmStatic
        val readStatAllowedTypes: Set<Type> = setOf(STRING_T, INT_T, CHAR_T)

        @JvmStatic
        val freeStatAllowedTypes: Set<Type> = setOf(ARRAY_T, PAIR_T)

        @JvmStatic
        val compareStatAllowedTypes: Set<Type> = setOf(STRING_T, INT_T, CHAR_T)

        @JvmStatic
        val notPrintable: Set<Type> = setOf(ArrayType(CHAR_T))


    }

}
