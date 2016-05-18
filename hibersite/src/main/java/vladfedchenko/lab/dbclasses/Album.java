package vladfedchenko.lab.dbclasses;

import java.util.Set;

/**
 * Created by vladfedchenko on 5/17/16.
 */
public class Album {

    private int id;
    private String name;
    private int songCount;
    private Artist artist;
    private Set songs;
    private Set albumAvailability;

    public Album() { }

    public Album (String name, int songCount, Artist artist)
    {
        this.name = name;
        this.songCount = songCount;
        this.artist = artist;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
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

    public int getSongCount() {
        return songCount;
    }

    public void setSongCount(int songCount) {
        this.songCount = songCount;
    }

    public Set getSongs() {
        return songs;
    }

    public void setSongs(Set songs) {
        this.songs = songs;
    }

    public Set getAlbumAvailability() {
        return albumAvailability;
    }

    public void setAlbumAvailability(Set albumAvailability) {
        this.albumAvailability = albumAvailability;
    }

}
