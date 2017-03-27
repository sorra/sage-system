package sage

import com.avaje.ebean.EbeanServer
import com.avaje.ebean.EbeanServerFactory
import com.avaje.ebean.config.PropertyMap
import com.avaje.ebean.config.ServerConfig
import httl.web.springmvc.HttlViewResolver
import org.apache.commons.lang3.builder.ToStringBuilder
import org.apache.commons.lang3.builder.ToStringStyle
import org.avaje.agentloader.AgentLoader
import org.springframework.beans.factory.annotation.Autowire
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.web.servlet.ErrorPage
import org.springframework.boot.web.servlet.ErrorPageRegistrar
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Scope
import org.springframework.http.HttpStatus
import org.springframework.web.multipart.commons.CommonsMultipartResolver
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter
import org.springframework.web.servlet.view.json.MappingJackson2JsonView
import sage.service.FilesService
import sage.util.Settings
import sage.web.context.VersionsMapper
import sage.web.filter.CurrentRequestFilter
import sage.web.filter.LoggingURLFilter
import sage.web.filter.StaticResourceRefreshFilter
import javax.servlet.ServletContext

@SpringBootApplication
@EnableWebMvc
class Application : WebMvcConfigurerAdapter() {

  @Bean(autowire = Autowire.BY_TYPE) @Scope(SCOPE_SINGLETON)
    fun getEbeanServer(): EbeanServer {
    val config = ServerConfig()
    config.name = "db"
    val ebeanProps = PropertyMap.defaultProperties()
    Settings.props.getProperty("pass")?.let { pass ->
      if (pass.isNotEmpty()) ebeanProps.setProperty("datasource.db.password", pass.map { it - 1 }.joinToString(""))
    }
    config.loadFromProperties(ebeanProps)
    config.isDefaultServer = true

    return EbeanServerFactory.create(config)
  }

  override fun configureViewResolvers(registry: ViewResolverRegistry) {
    registry.viewResolver(HttlViewResolver().apply { setContentType("text/html; charset=UTF-8") })
  }

  override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
    super.addResourceHandlers(registry)
    registry.addResourceHandler("/static/**")?.addResourceLocations("/static/")
    registry.addResourceHandler("/files/pic/**").addResourceLocations(toLocation(filesService.picDir()))
    registry.addResourceHandler("/files/avatar/**").addResourceLocations(toLocation(filesService.avatarDir()))
  }

  private fun toLocation(path: String): String {
    if (path.startsWith("/")) { return "file:$path/" }
    else { return "file:///$path/" }
  }

  @Bean
  fun json() = MappingJackson2JsonView()

  @Bean
  fun multipartResolver() = CommonsMultipartResolver().apply { setMaxUploadSize(5*1024*1024) }

  @Bean
  fun errorPages() = ErrorPageRegistrar { registry ->
    registry.addErrorPages(ErrorPage(HttpStatus.NOT_FOUND, "/not-found"))
  }

  @Bean
  fun loggingURLFilter() = LoggingURLFilter()
  @Bean
  fun currentRequestFilter() = CurrentRequestFilter()
  @Bean
  fun staticResourceRefreshFilter() = StaticResourceRefreshFilter()

  @Bean
  fun versionsMapper(servletContext: ServletContext) = VersionsMapper.setup(servletContext)

  @Autowired
  private lateinit var filesService: FilesService
}

fun main(args: Array<String>) {
  if (!AgentLoader.loadAgentFromClasspath("avaje-ebeanorm-agent", "debug=1;packages=sage.entity.**")) {
    System.err.println("avaje-ebeanorm-agent not found in classpath - not dynamically loaded")
  }
  ToStringBuilder.setDefaultStyle(ToStringStyle.SHORT_PREFIX_STYLE)
  SpringApplication.run(Application::class.java, *args)
}
