package backend.instructions

class Label(val label: String) : Instruction() {

    @Override
    override fun toString(): String {
        return "$label:"
    }
}