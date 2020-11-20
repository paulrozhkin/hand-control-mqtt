package emulator.util

class StringGenerator {
    companion object {
        public fun generate(stringLength: Int) : String {
            return ('A'..'z').map { it }.shuffled().subList(0, stringLength).joinToString("")
        }
    }
}