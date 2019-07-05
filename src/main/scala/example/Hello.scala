package example

import zio.App
import zio.console._

object Hello extends Greeting with App {
  val program =
    for {
      _ <- putStrLn("Hello! What is your name?")
      n <- getStrLn
      _ <- putStrLn(greeting + ", " + n)
    } yield ()

  val app = program.fold(_ => 1, _ => 0)

  def run(args: List[String]) = app
}

trait Greeting {
  def greeting: String = "Hello"
}
