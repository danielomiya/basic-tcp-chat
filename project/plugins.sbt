val sbtAssembly = "com.eed3si9n" % "sbt-assembly" % "1.1.0"
val ideSettings = "org.jetbrains" % "sbt-ide-settings" % "1.1.0"
 
addSbtPlugin(ideSettings)
addSbtPlugin(sbtAssembly)
