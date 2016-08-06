package me.selinali.matcha


import me.selinali.matcha.IconApi.Category

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
    IconApi.fetchCategoryNames().onComplete {
      case Success(names) => bindCategories(names)
      case Failure(e) => println(e.getCause)
    }

    IconApi.fetchCategories().onComplete {
      case Success(categories) => bindIcons(categories.head)
      case Failure(e) => println(e.getCause)
    }
  }

  def bindCategories(names: List[String]) = {
    val items = ("All" :: names).map("<li><a href=\"#\">" + _.capitalize + "</a></li>\n").mkString
    jQuery(".side-bar").append(s"<ul>\n$items</ul>")
  }

  def bindIcons(category: Category) = {
    val items = category.icons
        .map(icon => "<i class=\"material-icons md-36 md-dark\">" + icon.name.replace(' ', '_') + "</i>\n")
        .mkString
    jQuery(".icon-container").append(items)
  }

  def listFiles(path: String): Seq[String] = {
    fs.readdirSync(path).asInstanceOf[js.Array[String]]
  }
}