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

import io.atom.electron.BrowserWindow

import scala.scalajs.js.JSApp
import scala.scalajs.js
import js.Dynamic.{global => g}
import io.atom.electron._

object Main extends JSApp {

  def main(): Unit = {

    val electron = g.require("electron")
    val app = electron.app.asInstanceOf[App] // Module to control application life.

    // Keep a global reference of the window object, if you don't, the window will
    // be closed automatically when the JavaScript object is GCed.
    var mainWindow: BrowserWindow = null

    // Quit when all windows are closed.
    app.on("window-all-closed", { () =>
      // On OS X it is common for applications and their menu bar
      // to stay active until the user quits explicitly with Cmd + Q
      if (Process.platform != "darwin") {
        app.quit()
      }
    })

    // This method will be called when Electron has finished
    // initialization and is ready to create browser windows.
    app.on("ready", () => {
      // Create the browser window.
      mainWindow = BrowserWindow(width = 680, height = 500, titleBarStyle = "hidden")

      // and load the index.html of the app.
      mainWindow.loadURL("file://" + g.__dirname + "/index.html")

      // Open the devtools.
      mainWindow.openDevTools()

      // Emitted when the window is closed.
      val _ = mainWindow.on("closed", () =>
        // Dereference the window object, usually you would store windows
        // in an array if your app supports multi windows, this is the time
        // when you should delete the corresponding element.
        mainWindow = null
      )

    })

  }
}
