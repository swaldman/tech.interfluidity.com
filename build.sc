
import mill._
import mill.scalalib._

// huge thanks to @lolgab onn the Scala discord!
import $file.buildCompilationSettings

import $ivy.`com.mchange::untemplate-mill:0.0.4`
import untemplate.mill._

val UnstaticVersion = "0.0.5-SNAPSHOT"

object Dependency {
  val Unstatic = ivy"com.mchange::unstatic:${UnstaticVersion}"
  val UnstaticZTapir = ivy"com.mchange::unstatic-ztapir:${UnstaticVersion}"
}

object tech extends UntemplateModule {
  override def scalaVersion = "3.2.1"

  // supports Scala 3.2.1
  override def ammoniteVersion = "2.5.6"

  // we'll build an index!
  override def untemplateIndexNameFullyQualified : Option[String] = Some("com.interfluidity.tech.IndexedUntemplates")

  override def untemplateSelectCustomizer: untemplate.Customizer.Selector = { key =>
    var out = untemplate.Customizer.empty

    out = out.copy(extraImports=Seq("com.interfluidity.tech.*","com.interfluidity.tech.TechSite.MainBlog","unstatic.*"))

    // to customize, examine key and modify the customer
    // with out = out.copy=...
    //
    // e.g. out = out.copy(extraImports=Seq("cominterfluiditytech.*"))

    out
  }

  override def ivyDeps = T {
    super.ivyDeps() ++
      Agg (
        Dependency.Unstatic,
        Dependency.UnstaticZTapir,
      ) // Agg
  }
}


