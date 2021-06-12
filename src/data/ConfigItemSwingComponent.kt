package data

open class ConfigItemSwingComponent (
    var name: String? = "",
    var x: Int = 0,
    var y: Int = 0,
    var width: Int = 0,
    var height: Int = 0
) {
    var child: MutableList<ConfigItemSwingComponent> = mutableListOf()
    var splitPosition: Int = 0
}