package astrac.vimtegrate

import sbt._
import sbt.testing._

class TestsListener(quickfix: Quickfix, src: => Seq[File]) extends sbt.TestsListener {
  var fixes: List[Fix] = Nil

  def doComplete(finalResult: TestResult.Value): Unit = {
    quickfix.write(fixes)
  }

  def doInit(): Unit = ()
  def endGroup(name: String, result: TestResult.Value): Unit = ()
  def endGroup(name: String, t: Throwable): Unit = ()
  def startGroup(name: String): Unit = ()

  def testEvent(event: TestEvent): Unit = {
    event.detail.flatMap(evt => fixFromEvent(evt)).foreach { fix => fixes = fix :: fixes }
  }

  def filePath(fileName: String): Option[String] = src.find(_.name == fileName).map(_.absolutePath)

  def fixFromEvent(evt: Event): Option[Fix] =
    if (evt.status == Status.Failure) {
      val throwable = evt.throwable.get
      val stackTrace = throwable.getStackTrace
      val fqn = evt.fullyQualifiedName
      val stackElements = stackTrace.filter(_.getClassName.startsWith(fqn)).lastOption

      for {
        stackElem <- stackElements
        absoluteFilePath <- filePath(stackElem.getFileName)
      } yield Error(Position(absoluteFilePath, stackElem.getLineNumber), throwable.getMessage)

    } else None
}
