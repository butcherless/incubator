import sbt.Keys.*
import sbt.*
import java.nio.file.{Files, Paths}
import scala.jdk.CollectionConverters.*
import scala.util.matching.Regex

object PrintModulesTask
    extends AutoPlugin {

  object autoImport {
    val printModules = taskKey[String]("Prints the project module list")
  }

  import autoImport.*

  private val fileExtension      = ".scala"
  private val moduleRegex: Regex = raw"^\.{1,2}/(.*)/src/.*".r

  override def projectSettings: Seq[Def.Setting[?]] =
    Seq(
      printModules := Def.uncached {
        val path = Paths.get(".")

        val modules =
          Files.list(path).iterator().asScala
            .filter(p => Files.isDirectory(p) && !Files.isHidden(p))
            .flatMap { dir =>
              Files.walk(dir).iterator().asScala
                .map(_.toString)
                .find(path => path.endsWith(fileExtension))
                .collect { case moduleRegex(module) => module }
            }.toSet

        println(modules.mkString("\n"))

        ""
      }
    )
}
