package backend

import backend.instructions.Label

class Data(private val messages: Map<Label, String>) : Directive {

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("\t.data\n\n")
        for ((key, value) in messages) {
            sb.append("\t$key\n").append("\t\t.word ${getLength(value)}\n")
                .append("\t\t.ascii $value\n")
        }
        return sb.append("\n").toString()
    }

    companion object {
        private val escapedChar =
            mutableSetOf('0', 'b', 't', 'n', 'f', 'r', '\"', '\'', '\\')
    }

    private fun getLength(s: String): Int {
        var isSlashAhead = false
        var count = 0
        for (i in 0 until s.length) {
            val c = s[i]
            if (isSlashAhead && escapedChar.contains(c)) {
                count--
                isSlashAhead = false
            } else if (isSlashAhead && !escapedChar.contains(c)) {
                isSlashAhead = false
            } else if (!isSlashAhead && c == '\\') {
                isSlashAhead = true
            }
            count++
        }

        /* minus the length of quotes */
        return count - 2
    }
}