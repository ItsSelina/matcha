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

import me.selinali.matcha.IconApi.{Category, Icon}

import scala.collection.SortedMap
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

trait CategoryView {
  def renderSideBar(namesHtml: String)

  def renderIcons(iconsHtml: String)
}

class CategoryPresenter(view: CategoryView) {

  private val All = "All"
  private var Categories: SortedMap[String, List[Icon]] = SortedMap()
  private val formatter = (s: String, n: String) =>
    s"$s\n<i class='material-icons icon-rounded-corners md-36 md-dark'>$n</i>"

  def loadCategories() = {
    IconApi.fetchCategories().onComplete {
      case Success(cs) =>
        Categories = Category.toMap(cs)
        bindCategoriesToSideBar(Categories.keySet.toList)
        itemClicked(All)
      case Failure(e) => println(e.getCause)
    }
  }

  def itemClicked(id: String) = id match {
    case All => bindAll()
    case i if Categories.contains(i) => bind(Categories(i))
  }

  def performSearch(input: String, currentCategory: String) = {
    if (currentCategory.equals(All)) {
      view.renderIcons(Categories.filter(_._2.exists(_.name.contains(input.toLowerCase()))).map {
        case (name, icons) => s"<p class='category-header'>${name.capitalize}</p>${
          icons.filter(_.name.contains(input.toLowerCase)).map(formatIcon).mkString}"}.mkString
      )
    } else if (Categories.contains(currentCategory)) {
      view.renderIcons(Categories(currentCategory)
          .filter(_.name.contains(input.toLowerCase()))
          .map(_.name.replace(' ', '_')).foldLeft("")(formatter))
    }
  }

  private def bindAll() = view.renderIcons(Categories.map { case (name, icons) =>
    s"<p class='category-header'>${name.capitalize}</p>${icons.map(formatIcon).mkString}" }.mkString
  )

  private def bind(icons: List[Icon]) = {
    view.renderIcons(icons.map(_.name.replace(' ', '_')).foldLeft("")(formatter))
  }

  private def formatIcon(icon: Icon): String = {
    "<i class=\"material-icons icon-rounded-corners md-36 md-dark\">" + icon.name.replace(' ', '_') + "</i>\n"
  }

  private def bindCategoriesToSideBar(names: List[String]) = {
    val formatter = (s: String, n: String) => s"$s\n<li><a id='$n' href='#'>${n.capitalize}</a></li>"
    val items = names.foldLeft("")(formatter)
    view.renderSideBar(s"<ul>\n<li><a id='All' class='sidebar-item-selected' href='#'>All</a></li>\n$items</ul>")
  }
}