package sage.domain.repository;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import sage.entity.User;

@Repository
public class UserRepository extends BaseRepository<User> {

  public User findByEmail(String email) {
    Query query = session().createQuery("from User u where u.email=:email")
        .setString("email", email);
    return (User) query.uniqueResult();
  }

  public User findByName(String name) {
    Query query = session().createQuery("from User u where u.name=:name")
        .setString("name", name);
    return (User) query.uniqueResult();
  }

  @Override
  protected Class<User> entityClass() {
    return User.class;
  }
}
