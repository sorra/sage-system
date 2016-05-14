package sage

import com.avaje.ebean.EbeanServer
import com.avaje.ebean.EbeanServerFactory
import com.avaje.ebean.config.ServerConfig
import com.fasterxml.jackson.databind.ObjectMapper
import httl.web.springmvc.HttlViewResolver
import org.avaje.agentloader.AgentLoader
import org.springframework.beans.factory.annotation.Autowire
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ImportResource
import org.springframework.context.annotation.Scope
import org.springframework.web.multipart.commons.CommonsMultipartResolver
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport
import org.springframework.web.servlet.view.json.MappingJackson2JsonView
import sage.domain.service.FilesService

@SpringBootApplication
@ImportResource("root-context.xml")
open class Application : WebMvcConfigurationSupport() {
//  @Bean(autowire = Autowire.BY_TYPE)
//  @Scope(SCOPE_SINGLETON)
//  open fun getEbeanServer(): EbeanServer {
//    val config = ServerConfig()
//    config.name = "db"
//    config.loadFromProperties()
//    config.isDefaultServer = true
//
//    return EbeanServerFactory.create(config)
//  }

  override fun configureViewResolvers(registry: ViewResolverRegistry) {
    registry.viewResolver(HttlViewResolver().apply { setContentType("text/html; charset=UTF-8") })
  }

  override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
    registry.addResourceHandler("/static/**")?.addResourceLocations("/static/")
    registry.addResourceHandler("/files/pic/**").addResourceLocations(toLocation(filesService!!.picDir()))
    registry.addResourceHandler("/files/avatar/**").addResourceLocations(toLocation(filesService!!.avatarDir()))
  }

  private fun toLocation(path: String): String {
    if (path.startsWith("/")) { return "file:$path/" }
    else { return "file:///$path/" }
  }

  @Bean @Scope(SCOPE_SINGLETON)
  open fun json() = MappingJackson2JsonView()

  @Bean @Scope(SCOPE_SINGLETON)
  open fun multipartResolver() = CommonsMultipartResolver().apply { setMaxUploadSize(10000000) }

  @Autowired
  private val filesService: FilesService? = null
}

fun main(args: Array<String>) {
//  if (!AgentLoader.loadAgentFromClasspath("avaje-ebeanorm-agent", "debug=1;packages=com.iostate.**")) {
//    System.err.println("avaje-ebeanorm-agent not found in classpath - not dynamically loaded")
//  }
  SpringApplication.run(Application::class.java, *args)
}
