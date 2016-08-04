package me.selinali.matcha

import me.selinali.matcha.IconApi._
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Seconds, Span}

class IconApiTest extends FunSuite with Matchers with ScalaFutures {

  private implicit val Patience = PatienceConfig(timeout = Span(5, Seconds), interval = Span(500, Millis))

  private val ExpectedCategories = List(
    "action",
    "alert",
    "av",
    "communication",
    "content",
    "device",
    "editor",
    "file",
    "hardware",
    "image",
    "maps",
    "navigation",
    "notification",
    "places",
    "social",
    "toggle"
  )

  test("Fetch category names") {
    whenReady(fetchCategoryNames()) {
      result => {
        info(result.toString())
        result should contain only ExpectedCategories
      }
    }
  }
}
