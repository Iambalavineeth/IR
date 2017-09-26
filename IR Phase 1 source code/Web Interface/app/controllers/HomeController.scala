package controllers


import com.google.inject.{Inject, Singleton}
import play.api.data.Form
import play.api.mvc._
import play.api.data.Forms._
import service.FileProcessor

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class HomeController @Inject()(fileProcessor: FileProcessor) extends Controller {

  val argForm = Form {
    single("arg" -> text)
      .verifying("arg must not be empty.", _.nonEmpty)
  }

  def index = Action {
    Ok(views.html.index(argForm))
  }

  def arg = Action.async(parse.anyContent) { implicit req =>
    argForm.bindFromRequest().fold(
      hasErrors => Future.successful(BadRequest(views.html.index(hasErrors))),
      arg => {
        arg.trim.split("\\s+").length match {
          case 1 =>
            fileProcessor.uniword(arg).map {
              case Some(line) =>
                val Array(arg, files) = line.split("\\s+")
                val fileList = files.split(";").toList.dropRight(1).map(_.takeWhile(_ != '@'))
                Ok(s"Occurrences of $arg in:${fileList.mkString("\n", "\n", "\n")}")
              case None =>
                Ok(s"No occurrences of $arg found.")
            }.recover { case th =>
              Ok(s"error occurred reason: ${th.getMessage}")
            }
          case 2 =>
            fileProcessor.biword(arg).map {
              case Some(line) =>
                val Array(_, _, files) = line.split("\\s+")
                val fileList = files.split(";").toList.dropRight(1).map(_.takeWhile(_ != '@'))
                Ok(s"Occurrences of $arg in:${fileList.mkString("\n", "\n", "\n")}")
              case None =>
                Ok(s"No occurrences of $arg found.")
            }.recover { case th =>
              Ok(s"error occurred reason: ${th.getMessage}")
            }
        }
      }
    )
  }

  def pos = Action {
    Ok(views.html.pos(argForm))
  }

  def posSubmit = Action.async(parse.anyContent) { implicit req =>
    argForm.bindFromRequest().fold(
      hasErrors => Future.successful(BadRequest(views.html.index(hasErrors))),
      arg =>
        fileProcessor.uniwordPos(arg).map {
          case Some(line) =>
            val Array(arg, files) = line.split("\\s+")
            val fileList = files.split(";").toList.dropRight(1)
            Ok(s"Occurrences of $arg in:${fileList.mkString("\n", "\n", "\n")}")
          case None =>
            Ok(s"No occurrences of $arg found.")
        }.recover { case th =>
          Ok(s"error occurred reason: ${th.getMessage}")
        }
    )
  }

}
