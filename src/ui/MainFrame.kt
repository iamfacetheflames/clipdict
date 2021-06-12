package ui

import data.Word
import model.Configuration
import model.Database
import java.awt.Dimension
import java.awt.EventQueue
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.swing.JFrame
import javax.swing.JMenuBar
import javax.swing.JScrollPane
import javax.swing.JTextArea
import kotlin.system.exitProcess

class Presenter(
    private val config: Configuration,
    private val database: Database
) {

    fun getLastWordList(): List<Word> {
        return database.getSomeLastWords()
    }

    fun getPopularWordList(): List<Word> = database.getMostFrequentWords()

    fun deleteWord(word: Word) {
        database.deleteWord(word)
    }

    fun initUi(frame: JFrame) {
        config.swingComponentStateFromConfigItem(frame)
        frame.isVisible = true
    }

    fun closeApp(frame: JFrame) {
        config.setSwingComponentState(frame)
        config.save()
        exitProcess(0)
    }

    fun requestData(dataForShow: ((String) -> Unit)) {
        try {
            primaryClipboard { clipboard ->
                database.saveWord(clipboard)
                translate(clipboard) { translated ->
                    val dataForUi = translated + getPopularWordsInString()
                    dataForShow.invoke(dataForUi)
                }
            }
        } catch (e: Exception) {
            exitProcess(1)
        }
    }

    fun translate(input: String, output: ((String) -> Unit)) {
        val command = "sdcv $input"
        val proc = Runtime.getRuntime().exec(command)
        val reader = BufferedReader(InputStreamReader(proc.inputStream))
        val allText = reader.use(BufferedReader::readText)
        proc.waitFor()
        output.invoke(allText)
    }

    fun getPopularWordsInString(): String = getMostFrequentWordsInString(database)

    // apt-get install xclip sdcv
    private fun primaryClipboard(output: ((String) -> Unit)) {
        val command = "xclip -o"
        val proc = Runtime.getRuntime().exec(command)
        val reader = BufferedReader(InputStreamReader(proc.inputStream))
        val allText = reader.use(BufferedReader::readText)
        proc.waitFor()
        val result = allText.trim().split(" ").firstOrNull() ?: ""
        output.invoke(result.toUpperCase())
    }

    private fun getMostFrequentWordsInString(database: Database): String {
        val result = StringBuilder()
        for ((index, wordObject) in database.getMostFrequentWords().withIndex()) {
            wordObject.apply {
                val item = "#${index + 1}\t count=$numberSaves\t $word \n"
                result.append(item)
            }
        }
        return result.toString()
    }

}

class MainFrame(val presenter: Presenter) {

    private var frame: JFrame = JFrame()
    private lateinit var textArea: JTextArea

    init {
        createTextArea()
        frame.apply {
            pack()
            title = "clipdict"
            size = Dimension(800, 400)
            setLocationRelativeTo(null)
            addWindowListener(object : WindowAdapter() {
                override fun windowClosing(p0: WindowEvent?) {
                    presenter.closeApp(frame)
                }
            })
        }
    }

    fun showFrame() {
        presenter.requestData { text ->
            showText(text)
            createMenu()
        }
        presenter.initUi(frame)
    }

    private fun showText(text: String) {
        textArea.apply {
            selectAll()
            replaceSelection("")
            append(text)
            caretPosition = 0
        }
    }

    private fun createTextArea() {
        textArea = JTextArea()
        textArea.lineWrap = true
        textArea.wrapStyleWord = true
        val scroll = JScrollPane(
            textArea,
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        )
        frame.add(scroll)
    }

    private fun createMenu() {
        val menuBar = JMenuBar().apply {
            val deleteItems = mutableListOf<MenuItem>()
            presenter.getLastWordList().forEach { word ->
                deleteItems.add(
                    MenuItem(
                        word.word
                    ) {
                        presenter.deleteWord(word)
                        EventQueue.invokeLater(::createMenu)
                    }
                )
            }
            val translateItems = mutableListOf<MenuItem>()
            presenter.getPopularWordList().forEach { word ->
                translateItems.add(
                    MenuItem(
                        word.word
                    ) {
                        presenter.translate(word.word) { text ->
                            showText(text + presenter.getPopularWordsInString())
                        }
                    }
                )
            }
            addMenuGroup(MenuGroup(
                name = "Translate",
                items = translateItems
            ))
            addMenuGroup(MenuGroup(
                name = "Delete",
                items = deleteItems
            ))
        }
        frame.jMenuBar = menuBar
        frame.jMenuBar.revalidate()
        frame.jMenuBar.repaint()
    }

}