package astrac.vimtegrate

import sbt._
import scala.util.{ Success, Failure }

class CompilationLogger(quickfix: Quickfix) extends BasicLogger {

  private var fixes: List[Fix] = Nil
  private val positionRegex = """^(/[^\:]+):(\d+): (.*)$""".r

  private def isStart(message: String): Boolean = message.startsWith("Initial source changes:")

  private def isFinish(message: String): Boolean =
    message.startsWith("All newly invalidated sources") ||
    message.startsWith("All initially invalidated sources") ||
    message.endsWith("Compilation failed")

  private def parsePosition(message: String): Option[(Position, String)] =
    message match {
      case positionRegex(file, line, message) => Option(Position(file, line.toInt), message)
      case _ => None
    }

  private def parseFix(level: Level.Value, message: => String): Option[Fix] =
    level match {
      case Level.Error => parsePosition(message).map { case (p, m) => Error(p, m) }
      case Level.Warn => parsePosition(message).map { case (p, m) => Warning(p, m) }
      case _ => None
    }

  private def send(): Unit = {
    // TODO: Transform println to debug logs
    quickfix.write(fixes) match {
      case Success(()) => // TODO: Ignore
      case Failure(reason) => println(s"FAILURE: $reason")
    }
  }

  override def log(level: Level.Value, rawMessage: => String) : Unit = {
    val message = rawMessage.replace("\n", " ")

    if (isStart(message)) fixes = Nil
    else if (isFinish(message)) send()
    else parseFix(level, message) foreach { fix => fixes = fix :: fixes }
  }

  /* Unused implementations */
  override def control(event: ControlEvent.Value, message: => String): Unit = ()
  override def logAll(events: Seq[LogEvent]): Unit = ()
  override def success(message: => String): Unit = ()
  override def trace(t: => Throwable): Unit = ()
}
