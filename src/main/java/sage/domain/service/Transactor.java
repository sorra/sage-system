package sage.domain.service;

import java.util.function.Supplier;
import javax.annotation.PostConstruct;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class Transactor {
  public static Transactor get() {
    return instance;
  }

  public <R> R apply(Supplier<R> f) {
    return f.get();
  }

  public void run(Runnable f) {
    f.run();
  }

  @Autowired
  private ApplicationContext applicationContext;
  @PostConstruct
  void setup() {
    instance = applicationContext.getBean(Transactor.class);
  }
  private static Transactor instance;
}
