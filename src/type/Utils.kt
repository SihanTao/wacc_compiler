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
            for (expectedType in expected) {
                if (expectedType != actual) {
                    ErrorHandler.typeMismatch(ctx!!, expected, actual)
                    return true
                }
            }
            return false
        }

        @JvmStatic
        fun typeCheck(ctx: ParserRuleContext?, expected: Type?, actual: Type): Boolean {
            if (actual != expected) {
                ErrorHandler.typeMismatch(ctx!!, expected!!, actual)
                return true
            }
            return false
        }

        @JvmStatic
        fun typeCheck(
            ctx: ParserRuleContext?, varName: String?, expected: Type?,
            actual: Type
        ): Boolean {
            if (actual != expected) {
                ErrorHandler.typeMismatch(ctx!!, varName!!, expected!!, actual)
                return true
            }
            return false
        }

        @JvmStatic
        lateinit var unopEnumMapping: Map<String, Unop>

        @JvmStatic
        lateinit var unopTypeMapping: Map<String, Type>

        @JvmStatic
        lateinit var binopEnumMapping: Map<String, Binop>

        @JvmStatic
        lateinit var EqEnumMapping: Map<String, Binop>

        @JvmStatic
        lateinit var LogicOpEnumMapping: Map<String, Binop>

        @JvmStatic
        lateinit var CmpEnumMapping: Map<String, Binop>

        /* a list of allowed types in read, free, cmp statement */
        @JvmStatic
        lateinit var readStatAllowedTypes: Set<Type>

        @JvmStatic
        lateinit var freeStatAllowedTypes: Set<Type>

        @JvmStatic
        lateinit var compareStatAllowedTypes: Set<Type>

        @JvmStatic
        lateinit var notPrintable: Set<Type>

        /* Basic Types */
        val INT_T: Type = BasicType(BasicTypeEnum.INTEGER)
        val BOOL_T: Type = BasicType(BasicTypeEnum.BOOLEAN)
        val CHAR_T: Type = BasicType(BasicTypeEnum.CHAR)
        val STRING_T: Type = BasicType(BasicTypeEnum.STRING)
        val ARRAY_T: Type = ArrayType()

        val PAIR_T: Type = PairType()
    }

    init {
        unopEnumMapping = mapOf(
            "-" to Unop.MINUS,
            "chr" to Unop.CHR,
            "!" to Unop.NOT,
            "len" to Unop.LEN,
            "ord" to Unop.ORD
        )
        unopTypeMapping = mapOf(
            "-" to INT_T,
            "chr" to INT_T,
            "!" to BOOL_T,
            "len" to ARRAY_T,
            "ord" to CHAR_T
        )

        binopEnumMapping = mapOf(
            "+" to Binop.PLUS,
            "-" to Binop.MINUS,
            "*" to Binop.MUL,
            "/" to Binop.DIV,
            "%" to Binop.MOD
        )

        EqEnumMapping = mapOf("==" to Binop.EQUAL, "!=" to Binop.INEQUAL)

        LogicOpEnumMapping = mapOf("&&" to Binop.AND, "||" to Binop.OR)

        CmpEnumMapping = mapOf(
            ">" to Binop.GREATER,
            ">=" to Binop.GREATER_EQUAL,
            "<" to Binop.LESS,
            "<=" to Binop.LESS_EQUAL
        )

        readStatAllowedTypes = setOf(STRING_T, INT_T, CHAR_T)
        freeStatAllowedTypes = setOf(ARRAY_T, PAIR_T)
        compareStatAllowedTypes = setOf(STRING_T, INT_T, CHAR_T)
        notPrintable = setOf(ArrayType(CHAR_T))

    }


}
