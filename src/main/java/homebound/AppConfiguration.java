package homebound;

import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import homebound.common.HibernateUtil;
import homebound.hibernate.*;

@Configuration
@EnableWebMvc
public class AppConfiguration {
	@Bean(name="restTemplate")
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}
	
	@Bean(name = "sessionFactory")
    public SessionFactory getSessionFactory() {
        return HibernateUtil.createSessionFactory(
                PersistentItem.class,
                PersistentCategories.class,
                PersistentHistory.class,
                PersistentUser.class);
    }


}
