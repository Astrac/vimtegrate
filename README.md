# Vimtegrate

Vimtegrate is a plugin for sbt inspired by [sbt-quickfix](https://github.com/dscleaver/sbt-quickfix)
that aims to providing a basic integration layer between sbt and vim; its requirements are:

* vim has to be compiled with the `clientserver` option and running in server mode
* [vim-scala](https://github.com/derekwyatt/vim-scala) plugin should be installed to be able to correctly parse the quickfix

By default the plugin sends commands to a vim server having name `sbt`, but it can be configured
to use a different name. Whenever a project using this plugin is compiled the following will happen:

* The quickfix is cleaned up
* Errors are sent to the quickfix using the `caddexpr` command
* The quickfix window is shown
* The cursor is sent back to the last window

The plugin will report both compilation and test failures.

## Customization

From the `astrac.vimtegrate.Plugin` class:

    val serverName = SettingKey[String]("vim-server-name", "The name of the vim server where to send the quickfix")
    val vimCommand = SettingKey[String]("vim-command", "The command to be used to run the vim client")
    val postQuickfixCommands = SettingKey[String]("vim-post-quickfix-commands", "Whether or not to automatically show the quickfix window")

Set this options to change the behaviour of the plugin.

## Roadmap

At the moment the project is in the very early stages of development and is hardly tested at all. Any contribution in consolidating
what is implemented will be much appreciated. Some more things that I want to implement:

* Better test error source resolution (somewhat hacky at the moment)
* ctags generation
* Support quickfix output to file
* Stack-trace highlight throughout the sourcein case of error (more an idea to investigate than anything concrete at the moment)

