package me.selinali.iconic

import org.scalajs.dom.ext.Ajax

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.scalajs.js

object MaterialIcons {

  case class Icon(name: String, svg: String)

  case class Category(name: String, icons: List[Icon])

  private val BASE_URL = "https://api.github.com/repos/google/material-design-icons"

  private def treeEndpoint(sha1: String) = s"$BASE_URL/git/trees/$sha1"

  private def contentsEndpoint(categoryName: String) = s"$BASE_URL/contents/$categoryName/svg/production"

  private val RequiredSubfolders = Set(
    "drawable-hdpi",
    "drawable-mdpi",
    "drawable-xdpi",
    "drawable-xxdpi",
    "drawable-xxxdpi",
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
//    val root = js.JSON.parse(jsonString)
//    val tree = root.tree.asInstanceOf[js.Array[js.Dynamic]]
//    val jjj = tree.filter(_.`type`.toString.equals("tree"))
//        .map(e => {
//          Ajax.get(e.url.toString)
//              .map(xhr => {
//                js.JSON.parse(xhr.responseText).tree.asInstanceOf[js.Array[js.Dynamic]]
//              })
//              .collect { case a =>
//                if (RequiredSubfolders subsetOf subfolders.map(_.path.toString).toSet) e
//              }
//        })
//    j
//    ???
    val root = js.JSON.parse(jsonString)
    val tree = root.tree.asInstanceOf[js.Array[js.Dynamic]]
    Future.sequence(tree.filter(_.`type`.toString.equals("tree"))
        .map(item => Ajax.get(item.url.toString)
            .map(xhr => js.JSON.parse(xhr.responseText).tree.asInstanceOf[js.Array[js.Dynamic]])
            .map(innerTree => {
              if (RequiredSubfolders subsetOf innerTree.map(_.path.toString).toSet) {
                Some(item)
              } else {
                None
              }
            })).toList).map(a => a.flatten.map(a => a.path.toString))
  }

  /*
  Ajax.get(elem.url.toString).map(xhr => {
          val subfolders = js.JSON.parse(xhr.responseText).tree.asInstanceOf[js.Array[js.Dynamic]]
          RequiredSubfolders subsetOf subfolders.map(_.path.toString).toSet
        }
   */

  private def fetchIcons(categoryName: String): Future[List[Icon]] = {
    Ajax.get(contentsEndpoint(categoryName)).flatMap(xhr => {
      val root = js.JSON.parse(xhr.responseText)
      val items = root.asInstanceOf[js.Array[js.Dynamic]]
      Future.sequence(items.filter(elem => elem.name.toString.contains("48"))
          .map(elem => Ajax.get(elem.download_url.toString).map(xhr => Icon(elem.name.toString, xhr.responseText)))
          .toList)
    })
  }
}
