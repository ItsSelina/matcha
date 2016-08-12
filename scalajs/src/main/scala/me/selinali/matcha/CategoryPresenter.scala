package me.selinali.matcha

import me.selinali.matcha.IconApi.{Category, Icon}

import scala.collection.SortedMap
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

trait CategoryView {
  def renderSideBar(namesHtml: String)
  def renderIcons(iconsHtml: String)
  def highlightCurrentCategory()
}

class CategoryPresenter(view: CategoryView) {

  private val All = "All"
  private var Categories: SortedMap[String, List[Icon]] = SortedMap()

  def loadCategories() = {
    IconApi.fetchCategories().onComplete {
      case Success(cs) =>
        Categories = Category.toMap(cs)
        bindCategoryNames(Categories.keySet.toList)
        itemClicked(All)
      case Failure(e) => println(e.getCause)
    }
  }

  def itemClicked(id: String) = id match {
    case All => bindAll()
    case i if Categories.contains(i) => bind(Categories(i))
  }

  private def bindAll() = view.renderIcons(Categories.map { case (name, icons) =>
    s"<p class='category-header'>${name.capitalize}</p>${icons.map(formatIcon).mkString}" }.mkString
  )

  private def bind(icons: List[Icon]) = {
    val formatter = (s: String, n: String) => s"$s\n<i class='material-icons md-36 md-dark'>$n</i>"
    view.renderIcons(icons.map(_.name.replace(' ', '_')).foldLeft("")(formatter))
  }

  private def formatIcon(icon: Icon): String = {
    "<i class=\"material-icons md-36 md-dark\">" + icon.name.replace(' ', '_') + "</i>\n"
  }

  private def bindCategoryNames(names: List[String]) = {
    val formatter = (s: String, n: String) => s"$s\n<li><a id='$n' href='#'>${n.capitalize}</a></li>"
    val items = (All :: names).foldLeft("")(formatter)
    view.renderSideBar(s"<ul>\n$items</ul>")
  }
}