package model

import com.j256.ormlite.dao.DaoManager
import com.j256.ormlite.support.ConnectionSource
import com.j256.ormlite.table.TableUtils
import data.Word

class Database(var source: ConnectionSource) {

    init {
        TableUtils.createTableIfNotExists(source, Word::class.java)
    }

    fun saveWord(word: String) {
        val dao = DaoManager.createDao(source, Word::class.java)
        val request = "SELECT * FROM words WHERE word = '$word' LIMIT 1;"
        val queryResult = dao.queryRaw(request, dao.rawRowMapper)
        val lastSave = queryResult.firstOrNull()
        val item = lastSave?.apply {
            numberSaves += 1
        } ?: Word(word)
        dao.createOrUpdate(item)
    }

    fun getMostFrequentWords(): List<Word> {
        val dao = DaoManager.createDao(source, Word::class.java)
        val request = "SELECT * FROM words ORDER BY number_saves DESC LIMIT 15;"
        val queryResult = dao.queryRaw(request, dao.rawRowMapper)
        return queryResult.results
    }

    fun getSomeLastWords(): List<Word> {
        val dao = DaoManager.createDao(source, Word::class.java)
        val request = "SELECT * FROM words ORDER BY date_created DESC LIMIT 15;"
        val queryResult = dao.queryRaw(request, dao.rawRowMapper)
        return queryResult.results
    }

    fun deleteWord(word: Word) {
        val dao = DaoManager.createDao(source, Word::class.java)
        dao.delete(word)
    }

}