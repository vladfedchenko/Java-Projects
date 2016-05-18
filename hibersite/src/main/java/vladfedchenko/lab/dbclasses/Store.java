package vladfedchenko.lab.dbclasses;


import java.util.Set;

/**
 * Created by vladfedchenko on 5/17/16.
 */
public class Store {

    private int id;
    private String name;
    private String adress;
    private Set albumAvailability;

    public Store() { }

    public Store(String name, String adress)
    {
        this.name = name;
        this.adress = adress;
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

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public Set getAlbumAvailability() {
        return albumAvailability;
    }

    public void setAlbumAvailability(Set albumAvailability) {
        this.albumAvailability = albumAvailability;
    }

}
