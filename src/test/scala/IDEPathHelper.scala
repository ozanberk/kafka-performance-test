import java.nio.file.{Path, Paths}

object IDEPathHelper {
  private val projectRootDir = Paths.get(getClass.getClassLoader.getResource("gatling.conf").toURI).getParent.getParent.getParent
  private val mavenTargetDirectory = projectRootDir.resolve("target")
  private val mavenSrcTestDirectory = projectRootDir.resolve("src").resolve("test")

  val mavenSourcesDirectory: Path = mavenSrcTestDirectory.resolve("scala")
  val mavenResourcesDirectory: Path = mavenSrcTestDirectory.resolve("resources")
  val mavenBinariesDirectory: Path = mavenTargetDirectory.resolve("test-classes")
  val resultsDirectory: Path = mavenTargetDirectory.resolve("gatling")
  val recorderConfigFile: Path = mavenResourcesDirectory.resolve("recorder.conf")
}