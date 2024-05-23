plugins {
    java
    id("org.docstr.gwt")
}

interface GwtDistExtension {
    /** Name of the distribution */
    val distributionName: Property<String>

    /** Name of the folder to create the distribution */
    val distributionFolder: DirectoryProperty
}

val extension = project.extensions.create<GwtDistExtension>("gwtDistribution")
extension.distributionFolder.convention(layout.projectDirectory.dir("dist"))

val cleanDist by tasks.registering(Delete::class) {
    delete(extension.distributionFolder)
}

val dist by tasks.registering(Copy::class) {
    dependsOn(cleanDist)
    from(tasks.compileGwt)
    into("dist")
    doLast {
        val name = extension.distributionName.get()
        val folder = extension.distributionFolder.get()
        val jsFile = file("$folder/$name/$name.nocache.js")
        val newFile = file("$folder/$name/$name.js")
        jsFile.renameTo(newFile)

        // remove unneeded artifacts
        val clearCacheGif = file("$folder/$name/clear.cache.gif")
        clearCacheGif.delete()
        val webInfFolder = file("$folder/WEB-INF")
        deleteRecursively(webInfFolder)
    }
}

val zipDist by tasks.registering(Zip::class) {
    dependsOn(dist)
    from("${extension.distributionFolder.get()}/$name")
    destinationDirectory = extension.distributionFolder
}

fun deleteRecursively(file: File): Boolean {
    if (file.isDirectory()) {
        val children = file.list()
        for (child in children!!) {
            val success = deleteRecursively(File(file, child))
            if (!success) {
                return false
            }
        }
    }
    return file.delete()
}