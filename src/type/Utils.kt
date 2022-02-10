package type

class Utils {
    val INT: Type = BasicType(BasicTypeEnum.INTEGER)
    val BOOL: Type = BasicType(BasicTypeEnum.BOOLEAN)
    val CHAR: Type = BasicType(BasicTypeEnum.CHAR)
    val STRING: Type = BasicType(BasicTypeEnum.STRING)
    val ARRAY_TYPE: Type = ArrayType()
    val PAIR_TYPE: Type = PairType()

    /* a list of allowed types in read, free, cmp statement */
    val readStatAllowedTypes: Set<Type> = setOf(STRING, INT, CHAR)
    val freeStatAllowedTypes: Set<Type> = setOf(ARRAY_TYPE, PAIR_TYPE)
    val compareStatAllowedTypes: Set<Type> = setOf(STRING, INT, CHAR)
    val notPrintable: Set<Type> = setOf(ArrayType(CHAR))

    enum class Unop {
        NOT, MINUS, LEN, ORD, CHR
    }

    companion object {
        @JvmStatic lateinit var unopEnumMapping: Map<String, Unop>
        @JvmStatic lateinit var unopTypeMapping: Map<String, Type>
        @JvmStatic lateinit var binopEnumMapping: Map<String, Binop>
        @JvmStatic lateinit var EqEnumMapping: Map<String, Binop>
        @JvmStatic lateinit var LogicOpEnumMapping: Map<String, Binop>
        @JvmStatic lateinit var CmpEnumMapping: Map<String, Binop>
    }
    /* mapping from string literals to internal representations of UnopEnum and Type */

    init {
        unopEnumMapping = mapOf(
            "-" to Unop.MINUS,
            "chr" to Unop.CHR,
            "!" to Unop.NOT,
            "len" to Unop.LEN,
            "ord" to Unop.ORD
        )

        unopTypeMapping = mapOf(
            "-" to INT,
            "chr" to INT,
            "!" to BOOL,
            "len" to ARRAY_TYPE,
            "ord" to CHAR
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
    }


    enum class Binop {
        PLUS, MINUS, MUL, DIV, MOD, GREATER, GREATER_EQUAL, LESS, LESS_EQUAL, EQUAL, INEQUAL, AND, OR
    }

}