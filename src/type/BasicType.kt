package type

class BasicType(val typeEnum: BasicTypeEnum) : Type {

    override fun equals(other: Any?): Boolean {
        if (other == null) {
            return true
        }
        return if (other !is BasicType) {
            false
        } else typeEnum == other.typeEnum
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun toString(): String {
        return typeEnum.toString()
    }
}