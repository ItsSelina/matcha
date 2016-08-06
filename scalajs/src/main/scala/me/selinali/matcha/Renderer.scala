package me.selinali.matcha

import org.scalajs.dom.raw.Element
import org.scalajs.jquery._

import scala.scalajs.js
import js.annotation.JSExport

@JSExport
object Renderer extends CategoryView {

  val Presenter = new CategoryPresenter(this)

  @JSExport
  def main() = {
    Presenter.loadCategories()
    jQuery(".side-bar").on("click", "a", (e: JQueryEventObject) => onCategoryClick(e.target.asInstanceOf[Element].id))
  }

  def onCategoryClick(id: String) = {
    println("ButtonClicked, id is " + id)
    Presenter.itemClicked(id)
  }

  override def bindCategoriesToSideBar(namesHtml: String) = {
    jQuery(".side-bar").append(namesHtml)
  }

  override def bindIcons(iconsHtml: String) = {
    jQuery(".icon-container").empty().append(iconsHtml)
  }

  override def highlightCurrentCategory() = {

  }
}