package astrac.vimtegrate

import scala.util.Try

case class Position(file: String, line: Int) {
  val fixPath = s"$file:$line"
}

sealed trait Fix
case class Error(position: Position, msg: String) extends Fix
case class Warning(position: Position, msg: String) extends Fix

trait Quickfix {
  def write(fixes: List[Fix]): Try[Unit]

  def vimError(pos: Position, msg: String) = s"[error] ${pos.fixPath}: $msg"
  def vimWarning(pos: Position, msg: String) = s"[warn] ${pos.fixPath}: $msg"
}

class ServerQuickfix(serverName: String, vimClient: String, postQfCommands: String) extends Quickfix {
  import sys.process._
  import scala.language.postfixOps

  def command(vimCommand: String) =
    vimClient :: "--servername" :: serverName :: "--remote-send" :: s""""<C-\\><C-n>$vimCommand<CR>"""" :: Nil

  def addErrorCommand(pos: Position, msg: String) =
    command(s""":caddexpr '${vimError(pos, msg)}'""")

  def addWarningCommand(pos: Position, msg: String) =
    command(s""":caddexpr '${vimWarning(pos, msg)}'""")

  val cleanCommand = command(":call setqflist([])")

  def write(fixes: List[Fix]): Try[Unit] = Try {
    cleanCommand !

    fixes.map { fix =>
      fix match {
        case Error(p, m) => addErrorCommand(p, m)
        case Warning(p, m) => addWarningCommand(p, m)
      }
    } foreach { cmd => cmd ! }

    command(postQfCommands) !
  }
}
