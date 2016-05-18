package vladfedchenko.lab.dao;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Created by vladfedchenko on 5/17/16.
 */
public class SessionFactorySingleton {

    private  static SessionFactory factory = null;

    public static SessionFactory getInstance()
    {
        if (SessionFactorySingleton.factory == null)
        {
            SessionFactorySingleton.factory = new Configuration().configure().buildSessionFactory();
        }
        return factory;
    }
}
