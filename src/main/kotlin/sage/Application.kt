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
import org.springframework.web.servlet.config.annotation.*
import org.springframework.web.servlet.view.json.MappingJackson2JsonView
import sage.service.FileService
import sage.service.TagService
import sage.service.UserService
import sage.util.Settings
import sage.web.context.PageDefaultModelInterceptor
import sage.web.context.VersionsMapper
import sage.web.filter.CurrentRequestFilter
import sage.web.filter.AccessLoggingFilter
import sage.web.filter.StaticResourceRefreshFilter
import java.sql.Timestamp
import javax.servlet.ServletContext

@SpringBootApplication
class Application : WebMvcConfigurerAdapter() {

  @Bean(autowire = Autowire.BY_TYPE) @Scope(SCOPE_SINGLETON)
    fun getEbeanServer(): EbeanServer {
    val config = ServerConfig()
    config.name = "db"
    val ebeanProps = PropertyMap.defaultProperties()
    Settings.getProperty("dbpass")?.let { dbpass ->
      ebeanProps.setProperty("datasource.db.password", dbpass)
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
    registry.addResourceHandler("/files/pic/**").addResourceLocations(toLocation(fileService.picDir()))
    registry.addResourceHandler("/files/avatar/**").addResourceLocations(toLocation(fileService.avatarDir()))
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
    registry.addErrorPages(ErrorPage(HttpStatus.NOT_FOUND, "/errors/not-found"))
  }

  @Bean
  fun accessLoggingFilter() = AccessLoggingFilter()
  @Bean
  fun currentRequestFilter() = CurrentRequestFilter()
  @Bean
  fun staticResourceRefreshFilter() = StaticResourceRefreshFilter()

  @Bean
  fun versionsMapper(servletContext: ServletContext) = VersionsMapper.setup(servletContext)

  override fun addInterceptors(registry: InterceptorRegistry) {
    registry.addInterceptor(PageDefaultModelInterceptor(userService, tagService))
  }

  @Autowired
  private lateinit var fileService: FileService
  @Autowired
  private lateinit var userService: UserService
  @Autowired
  private lateinit var tagService: TagService
}

fun main(args: Array<String>) {
  if (!AgentLoader.loadAgentFromClasspath("avaje-ebeanorm-agent", "debug=1;packages=sage.entity.**")) {
    System.err.println("avaje-ebeanorm-agent not found in classpath - not dynamically loaded")
  }

  ToStringBuilder.setDefaultStyle(ToStringStyle.SHORT_PREFIX_STYLE)
  SpringApplication.run(Application::class.java, *args)

  // Show a success message in console, because production logs are not printed in console
  System.out.println("[${Timestamp(System.currentTimeMillis())}] Server is started.")
  System.out.flush()
}
