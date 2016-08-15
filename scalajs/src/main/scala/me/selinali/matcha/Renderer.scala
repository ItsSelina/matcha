/*
 * Copyright 2016 Selina Li
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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