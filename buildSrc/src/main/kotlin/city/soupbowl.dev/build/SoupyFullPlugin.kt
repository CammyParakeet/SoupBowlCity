package city.soupbowl.dev.build

import org.gradle.api.Plugin
import org.gradle.api.Project

open class SoupyFullPlugin(private val usePaperweight: Boolean = false) : Plugin<Project> {
    override fun apply(target: Project) {
        target.pluginManager.apply(SoupyPlugin::class.java)

        target.extensions.configure(SoupyExtension::class.java) {
            this.useLombok = true
            this.useShadow = true
            this@configure.usePaperweight = this@SoupyFullPlugin.usePaperweight
            this.usePaper = !usePaperweight
        }
    }
}

class SoupyFullPaperPlugin : SoupyFullPlugin(usePaperweight = false)
class SoupyFullPaperweightPlugin : SoupyFullPlugin(usePaperweight = true)