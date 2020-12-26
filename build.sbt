val V = new {
  val zio = "1.0.1"
  val graal = "20.3.0-java11"
}

val Deps = new {
  val scalaTest = "org.scalatest" %% "scalatest" % "3.0.8"
}

lazy val `graal-zio-hello` = (project in file("."))
  .enablePlugins(GraalVMNativeImagePlugin, DockerPlugin, JavaServerAppPackaging)
  .settings(
    inThisBuild(Seq(
      scalaVersion := "2.12.11",
      version := "0.1.0-SNAPSHOT",
      organization := "io.github.DmytroOrlov",
    )),
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio" % V.zio,
      Deps.scalaTest % Test,
    ),
    graalVMNativeImageOptions += "--static",
    graalVMNativeImageOptions += "--no-fallback",
    // graalVMNativeImageOptions += "--report-unsupported-elements-at-runtime",
    graalVMNativeImageGraalVersion := Some(V.graal),
  )
  .settings(
    dockerGraalvmNative := {
      val log = streams.value.log

      val stageDir = target.value

      IO.write(
        file((stageDir / "Dockerfile").getAbsolutePath),
        s"""FROM oracle/graalvm-ce:${V.graal}
           |COPY graalvm-native-image/${name.value} /opt/docker/out
           |ENTRYPOINT ["/opt/docker/out"]
           |""".stripMargin.getBytes("UTF-8"))
      val buildContainerCommand = Seq(
        "docker",
        "build",
        "-t",
        name.value,
        "-f",
        (stageDir / "Dockerfile")
          .getAbsolutePath,
        stageDir.absolutePath
      )

      log.info("Building the container with the generated native image")
      log.info(s"Running: ${buildContainerCommand.mkString(" ")}")

      sys.process.Process(buildContainerCommand, stageDir) ! streams.value.log match {
        case 0 => stageDir / "graalvm-native-image"
        case r => sys.error(s"Failed to run docker, exit status: " + r)
      }
    }
  )

val dockerGraalvmNative = taskKey[Unit](
  "Create a docker image containing a binary build with GraalVM's native-image."
)
