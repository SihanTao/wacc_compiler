package backend.directive

class Text : Directive {
    @Override
    override fun toString(): String {
        return "\t.text\n\n"
    }
}