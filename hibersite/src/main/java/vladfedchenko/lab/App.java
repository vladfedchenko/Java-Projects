package vladfedchenko.lab;

import vladfedchenko.lab.dao.GeneralDAO;
import vladfedchenko.lab.dbclasses.Artist;

import java.sql.Date;

public class App 
{
    public static void main( String[] args )
    {

        System.out.println( "Hello World!" );

        GeneralDAO dao = new GeneralDAO();

        //dao.addArtist("Frank Sinatra", Date.valueOf("1915-12-12"));
        Artist artist = dao.getArtist(new Integer(1));

        System.out.println(artist.getId() + " " + artist.getName() + " " + artist.getBirthDate());

        System.exit(0);
    }

}
