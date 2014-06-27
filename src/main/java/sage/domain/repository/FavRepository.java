package sage.domain.repository;

import java.util.Collection;

import org.springframework.stereotype.Repository;

import sage.entity.Fav;

@Repository
public class FavRepository extends BaseRepository<Fav> {

  public Collection<Fav> favs(long ownerId) {
    return session().createQuery("from Fav where owner.id = :ownerId")
        .setLong("ownerId", ownerId).list();
  }

  @Override
  protected Class<Fav> entityClass() {
    return Fav.class;
  }
}
