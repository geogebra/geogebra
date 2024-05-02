plugins {
    java
}

sourceSets {
    val nonfree by creating
    val gpl by creating
    main {
        val sourceSet = if (project.hasProperty("usegpl")) gpl else nonfree
        resources.srcDirs(sourceSet.resources.srcDirs())
    }
}
