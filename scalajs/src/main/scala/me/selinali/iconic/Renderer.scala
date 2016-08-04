package me.selinali.iconic


import scala.scalajs.js
import org.scalajs.jquery.jQuery

import js.Dynamic.{global => g}
import js.annotation.JSExport
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

@JSExport
object Renderer {

  val fs = g.require("fs")

  @JSExport
  def main(): Unit = {
    MaterialIcons.fetchCategoryNames().onComplete {
      case Success(names) => bindCategories(names)
      case Failure(e) => println(e.getCause)
    }
  }

  def bindCategories(names: List[String]) = {
    val items = names.map("<li><a href=\"#\">" + _.capitalize + "</a></li>\n").mkString
    jQuery(".side-bar").append(s"<ul>\n$items</ul>")
  }

  def listFiles(path: String): Seq[String] = {
    fs.readdirSync(path).asInstanceOf[js.Array[String]]
  }
}