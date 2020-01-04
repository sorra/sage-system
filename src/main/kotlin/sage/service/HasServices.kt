package sage.service

import org.springframework.beans.factory.annotation.Autowired

abstract class HasServices {
  @Autowired
  protected lateinit var blogService: BlogService

  @Autowired
  protected lateinit var favService: FavService

  @Autowired
  protected lateinit var fileService: FileService

  @Autowired
  protected lateinit var heedService: HeedService

  @Autowired
  protected lateinit var listService: ListService

  @Autowired
  protected lateinit var messageService: MessageService

  @Autowired
  protected lateinit var notificationService: NotificationService

  @Autowired
  protected lateinit var relationService: RelationService

  @Autowired
  protected lateinit var searchService: SearchService

  @Autowired
  protected lateinit var streamService: StreamService

  @Autowired
  protected lateinit var tagChangeService: TagChangeService

  @Autowired
  protected lateinit var tagService: TagService

  @Autowired
  protected lateinit var transferService: TransferService

  @Autowired
  protected lateinit var tweetPostService: TweetPostService

  @Autowired
  protected lateinit var tweetReadService: TweetReadService

  @Autowired
  protected lateinit var userService: UserService

  @Autowired
  protected lateinit var recommendationService: RecommendationService
}