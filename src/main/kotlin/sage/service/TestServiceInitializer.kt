package sage.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import sage.domain.constraints.Authority
import sage.entity.Tag
import sage.entity.User
import java.util.Arrays.asList
import java.util.Collections.emptyList

@Component
class TestServiceInitializer @Autowired constructor(
    private val tagService: TagService,
    private val userService: UserService,
    private val relationService: RelationService,
    private val tweetPostService: TweetPostService,
    private val blogService: BlogService,
    private val favService: FavService
) {

  @Volatile
  private var needInitialize = true

  private val root = Tag.ROOT_ID
  private var life: Long = 0
  private var society: Long = 0
  private var culture: Long = 0
  private var economy: Long = 0
  private var tech: Long = 0
  private var view: Long = 0
  private var art: Long = 0
  private var painting: Long = 0
  private var music: Long = 0
  private var prog: Long = 0
  private var digital: Long = 0

  private var admin: Long = 0
  private var bethia: Long = 0
  private var centos: Long = 0

  fun init() {
    if (!needInitialize) {
      throw IllegalStateException("Already initialized!")
    }
    tag()
    needInitialize = false
    user()
    relation()
    post()
    fav()
  }

  private fun tag() {
    life = createTag("生活", root)
    society = createTag("社会", root)
    culture = createTag("文化", root)
    economy = createTag("经济", root)
    tech = createTag("科技", root)

    view = createTag("时事", society)
    art = createTag("艺术", culture)
    painting = createTag("绘画", art)
    music = createTag("音乐", art)
    prog = createTag("编程", tech)
    digital = createTag("数码", tech)
  }

  private fun createTag(name: String, parentId: Long): Long {
    return tagService.create(1, name, parentId, true, "").id
  }

  private fun user() {
    admin = userService.register(
        User("admin@a.com", "123", "Admin", "伟大的Admin", "/static/img/1.jpg"))
    userService.grantAuthority(admin, Authority.SITE_ADMIN)
    bethia = userService.register(
        User("bethia@b.com", "123", "Bethia", "Elegant user", "/static/img/2.jpg"))
    centos = userService.register(
        User("centkuma@c.com", "123", "CentKuma", "CentOS Fans Kuma", "/static/img/3.jpg"))
  }

  private fun relation() {
    relationService.follow(admin, bethia, asList(society, culture))
    relationService.follow(admin, centos, asList(society, culture))

    relationService.follow(bethia, admin, asList(music, view))
    relationService.follow(bethia, centos, listOf(music))

    relationService.follow(centos, admin, asList(tech, view))
    relationService.follow(centos, bethia, listOf(art))
  }

  private fun post() {
    blogService.post(admin, "浅谈面向对象语言的类型运算",
        "像C#或者Haskell这样的先进的语言都有一个跟语法分不开的最核心的库。"
            + "譬如说C#的int，是mscorlib.dll里面的System.SInt32，Haskell的(x:xs)"
            + "则定义在了prelude里面。Vczh Library++ 3.0的ManagedX语言也有一个"
            + "类似mscorlib.dll的东西。之前的NativeX提供了一个核心的函数库叫"
            + "System.CoreNative (syscrnat.assembly)，因此ManagedX的就命名为"
            + "System.CoreManaged (syscrman.assembly)。System.CoreManaged里面"
            + "的预定义对象都是一些基本的、不可缺少的类型，例如System.SInt32、"
            + "System.IEnumerable<T>或者System.Reflection.Type。昨天晚上我的"
            + "未完成的语义分析器的完成程度已经足以完全分析System.CoreManaged里面的"
            + "托管代码了，因此符号表里面的类型系统也基本上是一个完整的类型系统。"
            + "在开发的过程中得到的心得体会便是写着一篇文章的来源。如今，"
            + "先进的面向对象语言的类型都离不开下面的几个特征：对象类型、函数类型和接口类型。"
            + "修饰类型的工具则有泛型和延迟绑定等等。譬如说C#，对象类型便是object，"
            + "函数类型则有.net framework支持的很好，但是不是核心类型的Func和Action，"
            + "接口类型则类似IEnumerable。泛型大家都很熟悉，延迟绑定则类似于dynamic关键字。"
            + "var关键字是编译期绑定的，因此不计算在内。Java的int是魔法类型，其设计的错误已经"
            + "严重影响到了类库的优美程度，其使用“类型擦除”的泛型系统也为今后的发展留下了一些祸根，"
            + "因此这些旁门左道本文章就不去详细讨论了。这篇文章讲针对重要的那三个类型和两个修饰"
            + "进行讨论，并解释他们之间互相换算的方法。",
        setOf(tech), MARKDOWN)

    blogService.post(bethia, "潜行吧！奈亚子",
        "点击:296,371 收藏:2,245 关注人数:2,672 "
            + "同义词:這いよる! ニャルアニ！潜行吧！"
            + "奈亚子W奈亚子W潜行吧!奈亚子W《潜行吧！奈亚子》"
            + "是由逢空万太创作的轻小说，自2009年4月开始连载。"
            + "作品在GA文库刊行之后，获得了极大好评，曾荣获"
            + "第1回GA文库大赏优秀奖。由XEBEC制作的动画版，"
            + "与此前的FLASH动画不同，本作将回归主线。",
        setOf(culture), MARKDOWN)

    blogService.post(centos, "群体对人的影响",
        "群体对人的影响，主要是极化，即群体愚蠢或群体智慧。"
            + "理论上，有一个共同的目标，分工互补，就可形成紧密的群。"
            + "《乌合之众》里讲到，群体对于个体有着极端放大和缩小的能力，"
            + "可以没有责任的暴虐成为暴民，也可以用高尚情感使之舍生赴死"
            + "（领袖们打动群众需要言之凿凿，信誓旦旦的重复和强大意志的感染）。"
            + "情绪化、简单化和跟随成本大大降低，是群对个人行为带来的影响。"
            + "新浪微博的运营，就是典型的时尚流行话题带动，名人引导，最后完成"
            + "群体讨论和活跃的，另一方面，群体是可以产生超越个体的群体智能的，"
            + "《失控》里称之为“涌现”（整体才具有，孤立部分及其总合不具有的性质）。"
            + "这个在人类社会尚无很好的范例，亚马逊的相关商品精准推荐，可能算群体预测吧，"
            + "但在自然界涌现有很多范例，比如水分子朝一个方向运动会形成漩涡，"
            + "白蚁可以构筑相当于人类数千层楼高的蚁巢，且通风卫生情况良好。",
        setOf(society), MARKDOWN)

    val a1 = tweetPostService.post(admin, "Post at root.", emptyList(), listOf(root)).id
    val a2 = tweetPostService.post(admin, "HUUSF View age.", emptyList(), listOf(view)).id
    val b1 = tweetPostService.post(bethia, "Music better!", emptyList(), listOf(music)).id
    val c1 = tweetPostService.post(centos, "Tech status", emptyList(), listOf(tech)).id

    tweetPostService.forward(admin, "forward", a1, emptyList())
    tweetPostService.forward(bethia, "OK, good", a2, emptyList())
    tweetPostService.forward(admin, "Oh, yeah", b1, emptyList())
    tweetPostService.forward(admin, "See it!", c1, emptyList())
  }

  private fun fav() {
    favService.create(admin, "https://github.com/sorra")
    favService.create(admin, "http://segmentfault.com/u/sorra")
    favService.create(admin, "tweet:1")
  }

  companion object {
    private const val MARKDOWN = "markdown"
  }
}