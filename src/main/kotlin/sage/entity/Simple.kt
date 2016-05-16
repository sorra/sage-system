package sage.entity

import java.util.HashSet
import javax.persistence.Entity
import javax.persistence.ManyToMany

import org.avaje.agentloader.AgentLoader
import javax.persistence.FetchType

@Entity
class Simple {
  @ManyToMany(fetch = FetchType.EAGER)
  var tags: MutableSet<Tag> = HashSet()

  constructor(tags: Set<Tag>) {
    this.tags.addAll(tags)
  }
}
fun main(args: Array<String>) {
  if (!AgentLoader.loadAgentFromClasspath("avaje-ebeanorm-agent", "debug=1;packages=sage.entity.**")) {
    System.err.println("avaje-ebeanorm-agent not found in classpath - not dynamically loaded")
  }
  Simple(HashSet<Tag>())
}

