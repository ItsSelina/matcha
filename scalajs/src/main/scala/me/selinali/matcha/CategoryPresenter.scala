package me.selinali.matcha

import me.selinali.matcha.IconApi.{Category, Icon}

import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global

trait CategoryView {
  def bindCategoriesToSideBar(namesHtml: String)
  def bindIcons(iconsHtml: String)
  def highlightCurrentCategory()
}

class CategoryPresenter(view: CategoryView) {

  var currentCategoryName = ""
  var storedCategories = List.empty[IconApi.Category]

  def loadCategories() = {
    IconApi.fetchCategories().onComplete {
      case Success(categories) =>
        storedCategories = categories
        bindCategories(categories.map(_.name))
        setCurrentCategory(categories.head)
      case Failure(e) => println(e.getCause)
    }
  }

  def itemClicked(id: String) = {
    println("Id is " + id)
    setCurrentCategory(storedCategories.filter(category => category.name.equals(id)).head)
    println("Changed page")
  }

  private def setCurrentCategory(category: Category) = {
    bindIcons(category.icons)
//    Renderer.highlightCurrentCategory()
  }

  private def bindCategories(names: List[String]) = {
    def formatCategory(s: String): String = {
      s"<li><a id='$s' href='#'>${s.capitalize}</a></li>\n"
    }
    val items = ("All" :: names).map(formatCategory).mkString
    view.bindCategoriesToSideBar(s"<ul>\n$items</ul>")
  }

  private def bindIcons(icons: List[Icon]) = {
    val items = icons.map(icon => "<i class=\"material-icons md-36 md-dark\">" + icon.name.replace(' ', '_') + "</i>\n").mkString
    view.bindIcons(items)
  }
}