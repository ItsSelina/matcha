package me.selinali.matcha

import org.scalajs.dom.ext.Ajax

import scala.collection.SortedMap
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js

object IconApi {

  case class Icon(name: String, svg: String)
  case class Category(name: String, icons: List[Icon])

  object Category {
    def toMap(cs: List[Category]): SortedMap[String, List[Icon]] = cs.map(c => (c.name, c.icons))(collection.breakOut)
  }

  private val BASE_URL = "https://api.github.com/repos/google/material-design-icons"
  private val ICONS_LIST_ENDPOINT = "https://raw.githubusercontent.com/google/material-design-icons/master/iconfont/codepoints"

  private def treeEndpoint(sha1: String) = s"$BASE_URL/git/trees/$sha1"

  private def contentsEndpoint(categoryName: String) = s"$BASE_URL/contents/$categoryName/svg/production"

  private val RequiredSubfolders = Set(
    "drawable-hdpi",
    "drawable-mdpi",
    "drawable-xhdpi",
    "drawable-xxhdpi",
    "drawable-xxxhdpi",
    "svg"
  )

  def fetchCategories(): Future[List[Category]] = {
    fetchCategoryNames().flatMap(names => Future.sequence(names.map(fetchCategory)))
  }

  def fetchCategory(name: String): Future[Category] = fetchIcons(name).map(Category(name, _))

  private def fetchCategoryNames(): Future[List[String]] = {
    Ajax.get(treeEndpoint("master")).map(xhr => xhr.responseText).flatMap(parseCategories)
  }

  private def parseCategories(jsonString: String): Future[List[String]] = {
    val root = js.JSON.parse(jsonString)
    val tree = root.tree.asInstanceOf[js.Array[js.Dynamic]]
    Future.sequence(tree
        .filter(_.`type`.toString.equals("tree"))
        .map(item => {
          Ajax.get(item.url.toString)
              .map(xhr => {
                val subfolders = js.JSON.parse(xhr.responseText).tree.asInstanceOf[js.Array[js.Dynamic]].map(_.path.toString).toSet
                if (RequiredSubfolders subsetOf subfolders) {
                  Some(item)
                } else {
                  None
                }
              })
        }).toList).map(a => a.flatten.map(a => a.path.toString))
  }

  private def fetchIcons(categoryName: String): Future[List[Icon]] = {
    Ajax.get(contentsEndpoint(categoryName)).flatMap(xhr => {
      val root = js.JSON.parse(xhr.responseText)
      val items = root.asInstanceOf[js.Array[js.Dynamic]]
      Future.sequence(items.filter(elem => elem.name.toString.contains("48"))
          .map(elem => Ajax.get(elem.download_url.toString).map(xhr => Icon(extractName(elem.name.toString), xhr.responseText)))
          .toList).zip(fetchIconList()).map { case (fetchedIcons, actualIcons) =>
          fetchedIcons.filter(icon => actualIcons.contains(icon.name))
      }
    })
  }

  def fetchIconList(): Future[Set[String]] = {
    Ajax.get(ICONS_LIST_ENDPOINT).map(xhr => {
      xhr.responseText.split('\n').toList.map(_.split(' ').head.replace('_', ' ')).toSet
    })
  }

  private def extractName(rawName: String): String = {
    rawName.split('_').filter(elem => !elem.equals("ic") && !elem.contains("px")).mkString(" ")
  }
}
