package sage.domain.commons;

import java.util.function.Supplier;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Declared in spring starting config
 */
public class Tx {

  public static <R> R apply(Supplier<R> f) {
    return instance.doApply(f);
  }

  public static void apply(Runnable f) {
    instance.doApply(() -> {f.run(); return null;});
  }

  public static <R> R applyNew(Supplier<R> f) {
    return instance.doApplyNew(f);
  }

  public static void applyNew(Runnable f) {
    instance.doApplyNew(() -> {f.run(); return null;});
  }

  @Transactional
  <R> R doApply(Supplier<R> f) {
    return f.get();
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  <R> R doApplyNew(Supplier<R> f) {
    return f.get();
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
