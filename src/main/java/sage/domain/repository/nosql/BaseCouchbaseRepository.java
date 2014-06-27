package sage.domain.repository.nosql;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import org.springframework.util.Assert;

import sage.domain.nosql.CouchbaseFactory;
import sage.entity.nosql.IdAble;
import sage.web.context.JsonUtil;

import com.couchbase.client.CouchbaseClient;

public abstract class BaseCouchbaseRepository<T extends IdAble> {
  
  protected CouchbaseClient client = CouchbaseFactory.createClient(entityClass().getSimpleName());

  protected abstract Class<T> entityClass();
  
  public T get(String key) {
    Assert.notNull(key);
    String json = (String) client.get(key);
    if (json ==null) {
      return null;
    }
    T result = JsonUtil.object(json, entityClass());
    result.setId(key);
    return result;
  }
  
  public Future<Boolean> add(String key, T value) {
    Assert.notNull(key);
    Assert.notNull(value);
    return client.add(key, JsonUtil.json(value));
  }

  public Future<Boolean> set(String key, T value) {
    Assert.notNull(key);
    Assert.notNull(value);
    return client.set(key, JsonUtil.json(value));
  }
  
  @PreDestroy
  protected void shutdown() {
    client.shutdown(10, TimeUnit.SECONDS);
  }
}
