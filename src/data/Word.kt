package data

import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable
import java.util.*

@DatabaseTable(tableName = "words")
data class Word (
    @DatabaseField(columnName = "word", canBeNull = false, unique = true)
    var word: String = "",
    @DatabaseField(columnName = "date_created", canBeNull = false)
    var dateCreated: Long = Date().time,
    @DatabaseField(columnName = "last_repetition", canBeNull = false)
    var lastRepetition: Long = 0,
    @DatabaseField(columnName = "number_repetition", canBeNull = false)
    var numberRepetitions: Int = 0,
    @DatabaseField(columnName = "number_saves", canBeNull = false)
    var numberSaves: Int = 1
) {
    @DatabaseField(columnName = "id", generatedId = true)
    var id: Long = 0
}