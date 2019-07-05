package example

import org.scalactic.TypeCheckedTripleEquals
import org.scalatest._

class HelloSpec extends FlatSpec with Matchers with TypeCheckedTripleEquals {
  "The Hello object" should "say Hello" in {
    Hello.greeting should ===("Hello")
  }
}
