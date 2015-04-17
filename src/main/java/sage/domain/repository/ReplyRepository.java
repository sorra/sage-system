package sage.domain.repository;

import org.springframework.stereotype.Repository;
import sage.entity.Reply;

@Repository
public class ReplyRepository extends BaseRepository<Reply> {
  @Override
  protected Class<Reply> entityClass() {
    return Reply.class;
  }
}
