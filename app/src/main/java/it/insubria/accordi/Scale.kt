package it.insubria.accordi

data class Scale(val notes: List<Note>, val scale: String) {

    override fun toString(): String {
        return "Scale(notes=$notes, scale='$scale')"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Scale

        if (notes != other.notes) return false
        if (scale != other.scale) return false

        return true
    }

    override fun hashCode(): Int {
        var result = notes.hashCode()
        result = 31 * result + scale.hashCode()
        return result
    }
}
