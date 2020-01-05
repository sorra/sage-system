package sage.util

object SiteMetadata {

  @JvmStatic fun siteName() = Settings.getProperty("site.name")
  @JvmStatic fun siteDescription() = Settings.getProperty("site.description")
  @JvmStatic fun siteDomain() = Settings.getProperty("site.domain")
  @JvmStatic fun siteUrl() = Settings.getProperty("site.url")
  @JvmStatic fun siteEmail() = Settings.getProperty("site.email")
}
