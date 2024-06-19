package com.interfluidity.tech

import scala.collection.*

import unstatic.*
import unstatic.ztapir.*
import unstatic.ztapir.simple.*

import unstatic.*, UrlPath.*

import java.nio.file.Path as JPath

import java.time.ZoneId

import untemplate.Untemplate.AnyUntemplate
import java.time.Instant

import SimpleBlog.SyntheticUpdateAnnouncementSpec

object TechSite extends ZTSite.SingleStaticRootComposite( JPath.of("static") ):

  // edit this to where your site will actually be served!
  override val serverUrl : Abs    = Abs("https://tech.interfluidity.com/")
  override val basePath  : Rooted = Rooted.root

  case class MainLayoutInput( renderLocation : SiteLocation, mainContentHtml : String, sourceUntemplates : immutable.Seq[AnyUntemplate] = immutable.Seq.empty )

  object MainBlog extends SimpleBlog:
    override type Site = TechSite.type
    override val site = TechSite.this
    override lazy val rssFeed = site.location( "/feed/index.rss" )
    override val feedTitle = "tech \u2014 interfluidity" // \u2014 is unicode &mdash;, works the same if I just include '—' char
    override val frontPage = site.location("/index.html")
    override val frontPageIdentifiers = super.frontPageIdentifiers ++ immutable.Set("home","home-page") // since we are using the blog as home
    override val maxFrontPageEntries = Some(10)
    override val timeZone = ZoneId.of("America/New_York")
    override def entryUntemplates =
      IndexFilter.fromIndex( IndexedUntemplates )
        .inOrBeneathPackage("com.interfluidity.tech.blog")
        .withNameLike( _.startsWith("entry_") )
        .untemplates
        .map( _.asInstanceOf[EntryUntemplate] )
    override def mediaPathPermalink( ut : AnyUntemplate ) : MediaPathPermalink =
      import MediaPathPermalink.*
        overridable( yearMonthDayNameDir(timeZone), ut )

    override def defaultAuthors : immutable.Seq[String] = List("Steve Randy Waldman")

    override val revisionBinder : Option[RevisionBinder] = Some( RevisionBinder.GitByCommit(TechSite, JPath.of("."), siteRooted => Rel("public/").embedRoot(siteRooted)) )

    override val diffBinder : Option[DiffBinder] = Some( DiffBinder.JavaDiffUtils(TechSite) )

    // DON'T use Instant.MIN when we do this for real!
    //override val syntheticUpdateAnnouncementSpec : Option[SyntheticUpdateAnnouncementSpec] = Some( SyntheticUpdateAnnouncementSpec( "Update-o-Bot", Instant.MIN ) )

    override def layoutEntry(input: Layout.Input.Entry) : String = blog.layout_entry_html(input).text

    // overriding a def, but it's just a constant, so we override with val
    override val entrySeparator : String = """<div class="entry-separator"></div>""" // for now

    // here the blog shares the sites main overall layout
    override def layoutPage(input: Layout.Input.Page): String =
      val mainLayoutInput = MainLayoutInput( input.renderLocation, input.mainContentHtml, input.sourceEntries.map( _.entryUntemplate ) )
      layout_main_html(mainLayoutInput).text

    object Archive:
      val location = site.location("/archive.html")
      case class Input( renderLocation : SiteLocation, entryUntemplatesResolved : immutable.SortedSet[EntryResolved] )

      val task = zio.ZIO.attempt {
         val contentsHtml = blog.layout_archive_html( Input( location, entriesResolved.filterNot( _.entryUntemplate.UntemplateSynthetic ) ) ).text
         layout_main_html( MainLayoutInput( location, contentsHtml, Nil ) ).text
      }
      val endpointBinding = ZTEndpointBinding.publicReadOnlyHtml( location, task, None, immutable.Set("archive") )
    end Archive

    object Subscribe:
      val location = site.location("/subscribe.html")

      val task = zio.ZIO.attempt {
         val contentsHtml = blog.subscribe_page_html().text
         layout_main_html( MainLayoutInput( location, contentsHtml, Nil ) ).text
      }
      val endpointBinding = publicReadOnlyHtml( location, task, None, immutable.Set("subscribe"), resolveHashSpecials = true, memoize = true )
    end Subscribe

    override def endpointBindings : immutable.Seq[ZTEndpointBinding] = super.endpointBindings :+ Archive.endpointBinding :+ Subscribe.endpointBinding

  end MainBlog

  // avoid conflicts, but early items in the lists take precedence over later items
  override val endpointBindingSources : immutable.Seq[ZTEndpointBinding.Source] = immutable.Seq( MainBlog )

object TechSiteGenerator extends ZTMain(TechSite, "tech-site")

