import com.j256.ormlite.jdbc.JdbcConnectionSource
import com.j256.ormlite.support.ConnectionSource
import model.Configuration
import model.Database
import ui.MainFrame
import ui.Presenter
import java.awt.*
import java.io.File

private fun getAppPath(): String = File(".").canonicalPath

private fun getDatabaseSource(): ConnectionSource {
    val url = "jdbc:sqlite:${getAppPath()}/database.sqlite3"
    return JdbcConnectionSource(url, "", "")
}

private fun getConfig(): Configuration {
    val configPath = "${getAppPath()}/config.json"
    val config = Configuration(configPath)
    config.load()
    return config
}

private fun showMainFrame() {
    val config = getConfig()
    val database = Database(
        getDatabaseSource()
    )
    val presenter = Presenter(config, database)
    val mainFrame = MainFrame(presenter)
    mainFrame.showFrame()
}

fun main() {
    try {
        EventQueue.invokeLater(::showMainFrame)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}