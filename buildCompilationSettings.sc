// huge thanks to @lolgab on the Scala discord!

interp.configureCompiler { c =>
  val settings = c.settings
  settings.YtastyReader.value = true
}


