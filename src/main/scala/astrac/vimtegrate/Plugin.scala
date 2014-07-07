package astrac.vimtegrate

import sbt._
import Keys._

object VimtegrateKeys {
  val serverName = SettingKey[String]("vim-server-name", "The name of the vim server where to send the quickfix")
  val vimCommand = SettingKey[String]("vim-command", "The command to be used to run the vim client")
  val postQuickfixCommands = SettingKey[String]("vim-post-quickfix-commands", "Commands to execute after compilation errors are sent (by default :cwindow<CR><C-w><C-p>)")
}

object VimtegratePlugin extends Plugin {
  import VimtegrateKeys._

  override val projectSettings = Seq(
    serverName := "sbt",
    vimCommand := "vim",
    postQuickfixCommands := ":cwindow<CR><C-w><C-p>",

    extraLoggers <<= (extraLoggers, serverName, vimCommand, postQuickfixCommands) apply { (currentFunction, srvName, vimCmd, postQfCommands) =>
      (key: ScopedKey[_]) => {
        val task = key.scope.task.toOption
        val loggers = currentFunction(key)
        task.map(_.label).fold(loggers) { t =>
          if (t == "compile") new CompilationLogger(new ServerQuickfix(srvName, vimCmd, postQfCommands)) +: loggers
          else loggers
        }
      }
    },

    testListeners <+= (serverName, vimCommand, postQuickfixCommands, sources in Test) map { (srvName, vimCmd, postQfCommands, sources) =>
      new TestsListener(new ServerQuickfix(srvName, vimCmd, postQfCommands), sources)
    }
  )
}
