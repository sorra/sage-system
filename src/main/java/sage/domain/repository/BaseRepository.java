package sage.domain.repository;

import java.util.Optional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import sage.domain.commons.DomainRuntimeException;

public abstract class BaseRepository<T> {

  @Autowired
  private SessionFactory sessionFactory;

  protected abstract Class<T> entityClass();
  
  protected Session session() {
    return sessionFactory.getCurrentSession();
  }

  public T load(long id) {
    return (T) session().load(entityClass(), id);
  }

  public T get(long id) {
    Object o = session().get(entityClass(), id);
    if (o == null) {
      throw new DomainRuntimeException("%s[id: %d] does not exist!", entityClass().getSimpleName(), id);
    }
    return (T) o;
  }

  public T nullable(long id) {
    return (T) session().get(entityClass(), id);
  }

  public Optional<T> optional(long id) {
    return Optional.ofNullable(nullable(id));
  }

  public T save(T entity) {
    session().save(entity);
    return entity;
  }

  public void update(T entity) {
    session().update(entity);
  }

  public void merge(T entity) {
    session().saveOrUpdate(entity);
  }

  public void delete(T entity) {
    session().delete(entity);
  }
}
