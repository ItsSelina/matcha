package me.selinali.iconic

import me.selinali.iconic.MaterialIcons.fetchCategories
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Seconds, Span}

class MaterialIconsTest extends FunSuite with Matchers with ScalaFutures {

  private implicit val Patience = PatienceConfig(timeout = Span(5, Seconds), interval = Span(500, Millis))
  private val ExpectedCategories = List("action", "alert", "av", "communication", "content", "device", "editor", "file",
    "hardware", "image", "maps", "navigation", "notification", "places", "social", "toggle")

  test("Someshit") {
    whenReady(fetchCategories()) { result =>
      val names = result.map(_.name)
      names should contain only ExpectedCategories
    }
    //assert(false)
  }
}
