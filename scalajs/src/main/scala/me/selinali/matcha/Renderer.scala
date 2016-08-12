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
    jQuery(".side-bar").on("click", "a", (e: JQueryEventObject) => {
      onCategoryClick(e.target.asInstanceOf[Element])
    })
  }

  def onCategoryClick(element: Element) = {
    val selectedItemClass = "sidebar-item-selected"
    jQuery(s".side-bar>ul>li>a.$selectedItemClass").removeClass(selectedItemClass)
    jQuery(element).addClass(selectedItemClass)
    Presenter.itemClicked(element.id)
  }

  override def renderSideBar(namesHtml: String) = {
    jQuery(".side-bar").append(namesHtml)
  }

  override def renderIcons(iconsHtml: String) = {
    jQuery(".icon-container").empty().append(iconsHtml)
  }
}