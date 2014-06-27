package sage;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.junit.Before;
import org.junit.Test;

public class HibernateTest {

    @Before
    public void setUp() throws Exception {
    }

//    @Test
    public void test() {
        Configuration cfg = new Configuration().configure();
        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().applySettings(cfg.getProperties());
        SessionFactory sf = cfg.buildSessionFactory(builder.build());
        Session session = sf.openSession();
        Transaction tx = session.beginTransaction();
        //
        tx.commit();
        session.close();
    }

}
