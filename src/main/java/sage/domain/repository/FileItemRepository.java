package sage.domain.repository;

import org.springframework.stereotype.Repository;
import sage.entity.FileItem;

@Repository
public class FileItemRepository extends BaseRepository<FileItem> {

  @Override
  protected Class<FileItem> entityClass() {
    return FileItem.class;
  }
}
