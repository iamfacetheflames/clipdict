package ui

import javax.swing.JMenu
import javax.swing.JMenuBar
import javax.swing.JMenuItem
import javax.swing.KeyStroke

fun JMenuBar.addMenuGroup(menuGroup: MenuGroup) {
    val swingMenuParent = JMenu(menuGroup.name)
    for (menu in menuGroup.items) {
        when(menu) {
            is MenuGroup -> {}
            is MenuItem -> {
                val swingMenuItem = JMenuItem(menu.name)
                swingMenuItem.addActionListener {
                    menu.onClick()
                }
                if (menu.hotkey != null) {
                    swingMenuItem.accelerator = menu.hotkey
                }
                swingMenuParent.add(swingMenuItem)
            }
        }
    }
    this.add(swingMenuParent)
}

open class Menu(val name: String)

class MenuItem(
    name: String,
    val hotkey: KeyStroke? = null,
    val onClick: (() -> Unit)
): Menu(name)

class MenuGroup(
    name: String,
    val items: List<Menu>
): Menu(name)