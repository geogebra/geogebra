package org.geogebra.gradle.ruby

import org.geogebra.gradle.env.configureEnvironment
import org.geogebra.gradle.env.provideExecutable
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.Input
import org.gradle.kotlin.dsl.*
import org.gradle.process.ExecOperations
import org.gradle.process.ExecSpec
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.OutputStream
import javax.inject.Inject

abstract class RubyPlugin : Plugin<Project> {

    @get:Inject 
    protected abstract val execOps: ExecOperations


    companion object {
        const val RUBY_EXTENSION_NAME = "ruby"
    }

    override fun apply(target: Project) {
        with(target) {
            val ruby = extensions.create<RubyExtension>(RUBY_EXTENSION_NAME)

            with(ruby) {
                version.convention("2.7.6")
                rbenvHome.convention(project.layout.projectDirectory.dir("${System.getenv("HOME")}/.rbenv"))
                gemBinPath.convention(project.layout.buildDirectory.dir("gemBin"))
                gemPath.convention(project.layout.projectDirectory.dir("${System.getenv("HOME")}/.gems"))
            }

            with(tasks) {
                val downloadRbenv by registering {
                    onlyIf("rbenv is not already downloaded") {
                        !project.file(ruby.rbenvHome.get().file("bin/rbenv")).exists() || execOps.exec {
                            rbenv(ruby)
                            args("install", "-l")
                            isIgnoreExitValue = true
                            standardOutput = OutputStream.nullOutputStream()
                        }.exitValue != 0
                    }

                    doLast {
                        logger.info("Installing rbenv and ruby-build...")
                        ruby.rbenvHome.get().asFile.deleteRecursively()
                        execOps.exec {
                            executable("git")
                            args("clone", "https://github.com/rbenv/rbenv.git", ruby.rbenvHome.get())
                            isIgnoreExitValue = true
                            standardOutput = OutputStream.nullOutputStream()
                        }
                        execOps.exec {
                            executable("git")
                            args("clone", "https://github.com/rbenv/ruby-build.git")
                            args(ruby.rbenvHome.dir("plugins/ruby-build").get())
                            standardOutput = OutputStream.nullOutputStream()
                        }
                    }
                }

                val installRuby by registering(Rbenv::class) {
                    doFirst {
                        args("install", "--skip-existing", ruby.version.get())
                    }
                }

                val rubyBin = objects.directoryProperty()

                val configureRubyHome by registering(Rbenv::class) {
                    val output = ByteArrayOutputStream()
                    dependsOn(installRuby)
                    standardOutput = output
                    doFirst {
                        args("prefix", ruby.version.get())
                    }
                    doLast {
                        val rubyHome = output.toString().trim()
                        rubyBin.set(File(rubyHome).resolve("bin"))
                    }
                }

                withType<Rbenv> {
                    group = "rbenv"
                    dependsOn(downloadRbenv)
                    rbenv(ruby)
                }

                withType<RubyTool> {
                    dependsOn(configureRubyHome)
                    provideExecutable(rubyBin.file(tool))
                    configureEnvironment {
                        put("PATH+RUBY", rubyBin)
                    }
                }

                withType<InstallGem> {
                    doFirst {
                        mkdir(layout.buildDirectory)
                    }
                    tool = "gem"
                    args("install")
                    environment("GEM_HOME", "")
                    environment("GEM_PATH", "")
                    doFirst {
                        args(gem.get())
                        args("--version", version.get())
                        args("--bindir", ruby.gemBinPath.get())
                        args("--install-dir", ruby.gemPath.get())
                        args("--no-document")
                    }
                }

                withType<ExecuteGem> {
                    provideExecutable(ruby.gemBinPath.file(gem))
                    configureEnvironment {
                        put("PATH+RUBY", ruby.gemBinPath)
                        put("GEM_HOME", ruby.gemPath)
                        put("GEM_PATH", ruby.gemPath)
                    }
                }
            }
        }
    }
}

private fun ExecSpec.rbenv(extension: RubyExtension) {
    val rbenvHome = extension.rbenvHome.get()
    executable(rbenvHome.file("bin/rbenv"))
    environment("RBENV_ROOT", rbenvHome)
    environment("PATH", "${rbenvHome.dir("bin")}:${System.getenv("PATH")}")
}

private abstract class Rbenv : Exec()

/** Runs a tool provided by the Ruby installation */
abstract class RubyTool : Exec() {

    /** Ruby tool, e.g. "gem" or "bundle" */
    @get:Input
    abstract val tool: Property<String>
}

/** Installs a gem and adds a binary to [RubyExtension.gemBinPath] */
abstract class InstallGem : RubyTool() {

    @get:Input
    abstract val gem: Property<String>

    @get:Input
    abstract val version: Property<String>
}

/** Executes a given gem. */
abstract class ExecuteGem : Exec() {

    @get:Input
    abstract val gem: Property<String>
}