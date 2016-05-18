package vladfedchenko.lab.dbclasses;

import java.sql.*;

/**
 * Created by vladfedchenko on 5/17/16.
 */
public class Song {

    private int id;
    private String name;
    private Time length;
    private Album album;

    public Song() { }

    public Song(String name, Time length, Album album)
    {
        this.name = name;
        this.length = length;
        this.album = album;
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

    public Time getLength() {
        return length;
    }

    public void setLength(Time length) {
        this.length = length;
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

}
