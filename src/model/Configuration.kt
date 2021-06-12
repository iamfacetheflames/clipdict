package model

import data.ConfigData
import data.ConfigItemSwingComponent
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.awt.Component
import java.awt.Container
import java.io.File
import java.io.FileNotFoundException
import javax.swing.JSplitPane

class Configuration(val configPath: String) {

    lateinit var configData: ConfigData

    fun load() {
        try {
            val raw = readFile(configPath)
            configData = Gson().fromJson(raw, ConfigData::class.java)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            configData = ConfigData()
        }
    }

    fun save() {
        val gson = GsonBuilder().setPrettyPrinting().create()
        val json = gson.toJson(configData)
        val file = File(configPath)
        file.createNewFile()
        file.writeText(json)
    }

    fun swingComponentToConfigItem(c: Component, index: Int = 0): ConfigItemSwingComponent {
        val name = c.getSwingComponentName(index)
        val item = ConfigItemSwingComponent(name, c.getX(), c.getY(), c.getWidth(), c.getHeight())
        when (c) {
            is JSplitPane -> {
                item.splitPosition = c.dividerLocation
            }
        }
        if (c is Container) {
            for ((index, child) in c.getComponents().withIndex()) {
                item.child.add(swingComponentToConfigItem(child, index))
            }
        }
        return item
    }

    fun swingComponentStateFromConfigItem(c: Component) {
        restoreComponentFromSwingComponent(c, configData.windowSizeAndPosition ?: return)
    }

    private fun Component.getSwingComponentName(index: Int): String {
        val c = this
        return c.name ?: "${c.javaClass}.$index"
    }

    private fun restoreComponentFromSwingComponent(
        c: Component,
        ci: ConfigItemSwingComponent,
        index: Int = 0
    ) {
        c.setBounds(ci.x, ci.y, ci.width, ci.height)
        if (c is JSplitPane) {
            c.dividerLocation = ci.splitPosition
        }
        if (c is Container) {
            for ((index, child) in c.components.withIndex()) {
                val childName = child.getSwingComponentName(index)
                if (child is JSplitPane) {
                    println()
                }
                ci.child.find { it.name == childName }?.let { configItem ->
                    restoreComponentFromSwingComponent(child, configItem, index)
                }
            }
        }
    }

    fun setSwingComponentState(c: Component) {
        configData.windowSizeAndPosition = swingComponentToConfigItem(c)
    }

    private fun readFile(path: String): String {
        val file = File(configPath)
        return file.readText()
    }

}