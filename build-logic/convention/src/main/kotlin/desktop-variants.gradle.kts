object Variants {

    private fun DependencyHandlerScope.classifierVariant(dependencyProvider: Provider<MinimalExternalModuleDependency>, classifier: String): Provider<MinimalExternalModuleDependency> {
        return variantOf(dependencyProvider) { classifier(classifier) }
    }

    fun DependencyHandlerScope.nativesLinuxAmd64(dependencyProvider: Provider<MinimalExternalModuleDependency>): Provider<MinimalExternalModuleDependency> {
        return classifierVariant(dependencyProvider, "natives-linux-amd64")
    }

    fun DependencyHandlerScope.nativesWindowsAmd64(dependencyProvider: Provider<MinimalExternalModuleDependency>): Provider<MinimalExternalModuleDependency> {
        return classifierVariant(dependencyProvider, "natives-windows-amd64")
    }

    fun DependencyHandlerScope.nativesMacOSXUniversal(dependencyProvider: Provider<MinimalExternalModuleDependency>): Provider<MinimalExternalModuleDependency> {
        return classifierVariant(dependencyProvider, "natives-macosx-universal")
    }
}