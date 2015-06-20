package sage.domain.commons;

import java.util.function.Supplier;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class Tx {

  public static <R> R apply(Supplier<R> f) {
    return instance.doApply(f);
  }

  public static void apply(Runnable f) {
    instance.doApply(f);
  }

  <R> R doApply(Supplier<R> f) {
    return f.get();
  }

  void doApply(Runnable f) {
    f.run();
  }

  @Autowired
  private ApplicationContext applicationContext;

  @PostConstruct
  void setup() {
    instance = applicationContext.getBean(Tx.class);
  }

  @PreDestroy
  void shutdown() {
    instance = null;
  }

  private static Tx instance;
}
