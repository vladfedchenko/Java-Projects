package vladfedchenko.lab;

import vladfedchenko.lab.dao.GeneralDAO;
import vladfedchenko.lab.dbclasses.Artist;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.sql.Date;

public class App 
{
    public static void main( String[] args )
    {
        ApplicationContext context =
                new ClassPathXmlApplicationContext("Beans.xml");

        GeneralDAO dao = (GeneralDAO) context.getBean("generalDao");

        //dao.addArtist("Frank Sinatra", Date.valueOf("1915-12-12"));
        Artist artist = dao.getArtist(new Integer(1));

        System.out.println(artist.getId() + " " + artist.getName() + " " + artist.getBirthDate());

        System.exit(0);
    }

}
