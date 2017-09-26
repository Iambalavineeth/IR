package service

import akka.actor.ActorSystem
import com.google.inject.{ImplementedBy, Inject, Singleton}

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[FileProcessorImpl])
trait FileProcessor {
  def uniword(arg: String): Future[Option[String]]
  def biword(arg: String): Future[Option[String]]
  def uniwordPos(arg: String): Future[Option[String]]
}

@Singleton
class FileProcessorImpl @Inject() (actorSystem: ActorSystem) extends FileProcessor {
  val myExecutionContext: ExecutionContext = actorSystem.dispatchers.lookup("my-context")

  override def uniword(arg: String): Future[Option[String]] = {
    Future {
      scala.io.Source.fromFile("/home/bala/Desktop/uniword.txt").getLines().find { line =>
        line.trim.split("\\s+").headOption match {
          case Some(value) => value == arg
          case None => false
        }
      }
    }(myExecutionContext)
  }

  override def biword(arg: String): Future[Option[String]] = {
    Future {
      scala.io.Source.fromFile("/home/bala/Desktop/biword.txt").getLines().find { line =>
        val Array(a, b, _ @ _*) = line.trim.split("\\s+")
        s"$a $b" == arg
      }
    }(myExecutionContext)
  }

  override def uniwordPos(arg: String): Future[Option[String]] = {
    Future {
      scala.io.Source.fromFile("/home/bala/Desktop/uniwordpos.txt").getLines().find { line =>
        val Array(a, _ @ _*) = line.trim.split("\\s+")
        s"$a" == arg
      }
    }(myExecutionContext)
  }
}
