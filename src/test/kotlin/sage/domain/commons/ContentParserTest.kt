package sage.domain.commons

import org.junit.Assert
import org.junit.Test
import sage.entity.User

class ContentParserTest {
  @Test
  fun testSingleLine() {
    runs("", "", emptySet())
    runs("http://a/#id #@Some @Nobody#\"Em\"#",
        "<a class=\"link\" href=\"http://a/#id\" target=\"_blank\" rel=\"noopener noreferrer\">http://a/#id</a> #<a class=\"mention\" uid=\"1\" href=\"/users/1\">@Some</a> @Nobody<strong>#\"Em\"#</strong>",
        setOf(1))
  }

  @Test
  fun testMultiLine() {
    runs("\n\n\n", "<br/>", emptySet())
    runs("http://a/#\nid #@Some @Nobody#\"Em\"#",
        "<a class=\"link\" href=\"http://a/#\" target=\"_blank\" rel=\"noopener noreferrer\">http://a/#</a><br/>id #<a class=\"mention\" uid=\"1\" href=\"/users/1\">@Some</a> @Nobody<strong>#\"Em\"#</strong>",
        setOf(1))
  }

  private fun runs(original: String, expected: String, expectedMentions: Set<Long>) {
    val (json, mentionedIds) = ContentParser.tweet(original, { _ -> User("a@a.com", "pwd").apply { id = 1 } })
    Assert.assertEquals(expected, json)
    Assert.assertEquals(expectedMentions, mentionedIds)
  }
}