package it.insubria.accordi

data class Note(val note: String, val octave: String, val frequency: Double) {
    constructor(note: String, octave: String) : this(note, octave, 0.0)
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Note

        if (note != other.note) return false
        if (octave != other.octave) return false
        if (frequency != other.frequency) return false

        return true
    }

    override fun hashCode(): Int {
        var result = note.hashCode()
        result = 31 * result + octave.hashCode()
        result = 31 * result + frequency.hashCode()
        return result
    }

    override fun toString(): String {
        return note + octave
    }


}
