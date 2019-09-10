package example

import zio._
import zio.console._

object Hello extends App {
  val program = for {
    n <- putStrLn("Hello! What is your name?") *>
      getStrLn
    _ <- putStrLn("Hello , " + n)
  } yield ()

  def run(args: List[String]) =
    program.fold(_ => 1, _ => 0) //.untraced
}
