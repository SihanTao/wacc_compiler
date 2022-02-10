package type

class TypeConst {
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
}