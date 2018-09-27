package homebound.common;

import java.util.Arrays;
import java.util.Collection;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
	public static final String RESOURCE = "hibernate.cfg.xml";
	
    private static SessionFactory createSessionFactory(Collection<Class<?>> annotatedClasses) {
        Configuration hibernateConfig = new org.hibernate.cfg.Configuration()
                .configure(RESOURCE);

        for (Class<?> clazz : annotatedClasses) {
            hibernateConfig.addAnnotatedClass(clazz);
        }

        return hibernateConfig.buildSessionFactory();
    }

    public static SessionFactory createSessionFactory(Class<?>... annotatedClasss) {
        return createSessionFactory(Arrays.asList(annotatedClasss));
    }
}
