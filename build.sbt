import java.nio.file.{Files, StandardCopyOption}
import Dependencies._

lazy val zioVersion = "1.0.0-RC12-1"

lazy val `graal-zio-hello` = (project in file("."))
  .enablePlugins(GraalVMNativeImagePlugin, DockerPlugin, JavaServerAppPackaging)
  .settings(
    inThisBuild(Seq(
      scalaVersion := "2.12.9",
      version := "0.1.0-SNAPSHOT",
      organization := "io.github.DmytroOrlov",
    )),
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio" % zioVersion,
      scalaTest % Test,
    ),
    graalVMNativeImageOptions += "--static",
  )
  .settings(
    dockerGraalvmNative := {
      val log = streams.value.log

      val stageDir = target.value

      IO.write(
        file((stageDir / "Dockerfile").getAbsolutePath),
        s"""FROM alpine:3.10.2
          |COPY graalvm-native-image/${name.value} /opt/docker/out
          |CMD ["/opt/docker/out"]
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
