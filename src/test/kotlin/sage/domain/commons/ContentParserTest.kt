package sage.domain.commons

import org.junit.Assert
import org.junit.Test
import sage.entity.User

class ContentParserTest {
  @Test
  fun test() {
    runs("", "", emptySet())
    runs("http://a/#id #@Some @Nobody#\"Em\"#",
        "<a class=\"link\" href=\"http://a/#id\" target=\"_blank\">http://a/#id</a> #<a class=\"mention\" uid=\"1\" href=\"/users/1\">@Some</a> @Nobody<strong>#\"Em\"#</strong>",
        setOf(1))
  }

  private fun runs(original: String, expected: String, expectedMentions: Set<Long>) {
    val (json, mentionedIds) = ContentParser.tweet(original, { _ -> User("a@a.com", "pwd").apply { id = 1 } })
    Assert.assertEquals(expected, json)
    Assert.assertEquals(expectedMentions, mentionedIds)
  }
}