package vladfedchenko.lab.dbclasses;

import java.sql.Date;
import java.util.Set;

/**
 * Created by vladfedchenko on 5/17/16.
 */
public class Artist {

    private int id;
    private String name;
    private Date birthDate;
    private Set albums;

    public Artist() { }

    public Artist (String name, Date birthDate)
    {
        this.name = name;
        this.birthDate = birthDate;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set getAlbums() {
        return albums;
    }

    public void setAlbums(Set albums) {
        this.albums = albums;
    }

}
