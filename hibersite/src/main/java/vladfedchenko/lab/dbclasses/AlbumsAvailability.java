package vladfedchenko.lab.dbclasses;

/**
 * Created by vladfedchenko on 5/17/16.
 */
public class AlbumsAvailability {

    private int id;
    private Album album;
    private Store store;
    private int count;

    public AlbumsAvailability() { }

    public AlbumsAvailability(Album album, Store store, int count)
    {
        this.album = album;
        this.store = store;
        this.count = count;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

}
