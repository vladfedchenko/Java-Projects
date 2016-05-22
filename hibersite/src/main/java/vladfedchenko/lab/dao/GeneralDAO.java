package vladfedchenko.lab.dao;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import vladfedchenko.lab.dbclasses.*;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

/**
 * Created by vladfedchenko on 5/17/16.
 */
public class GeneralDAO
{
    public GeneralDAO() {}

    public Integer addSong(String name, Time length, Album album)
    {
        Session session = SessionFactorySingleton.getInstance().openSession();
        Transaction tx = null;
        Integer id = null;
        try{
            tx = session.beginTransaction();
            Song obj = new Song(name, length, album);
            id = (Integer) session.save(obj);
            tx.commit();
        }catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }finally {
            session.close();
        }
        return id;
    }

    public Song getSong(Integer id)
    {
        Session session = SessionFactorySingleton.getInstance().openSession();
        Song toRet = null;
        Transaction tx = null;
        try{
            tx = session.beginTransaction();
            toRet = (Song)session.get(Song.class, id);
            tx.commit();
        }catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
            toRet = null;
        }finally {
            session.close();
        }
        return toRet;
    }

    public Integer addAlbum(String name, int songCount, Artist artist)
    {
        Session session = SessionFactorySingleton.getInstance().openSession();
        Transaction tx = null;
        Integer id = null;
        try{
            tx = session.beginTransaction();
            Album obj = new Album(name, songCount, artist);
            id = (Integer) session.save(obj);
            tx.commit();
        }catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }finally {
            session.close();
        }
        return id;
    }

    public Album getAlbum(Integer id)
    {
        Session session = SessionFactorySingleton.getInstance().openSession();
        Album toRet = null;
        Transaction tx = null;
        try{
            tx = session.beginTransaction();
            toRet = (Album)session.get(Album.class, id);
            tx.commit();
        }catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
            toRet = null;
        }finally {
            session.close();
        }
        return toRet;
    }

    public Integer addArtist(String name, Date birthDate)
    {
        Session session = SessionFactorySingleton.getInstance().openSession();
        Transaction tx = null;
        Integer id = null;
        try{
            tx = session.beginTransaction();
            Artist obj = new Artist(name, birthDate);
            id = (Integer) session.save(obj);
            tx.commit();
        }catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }finally {
            session.close();
        }
        return id;
    }

    public Artist getArtist(Integer id)
    {
        Session session = SessionFactorySingleton.getInstance().openSession();
        Artist toRet = null;
        Transaction tx = null;
        try{
            tx = session.beginTransaction();
            toRet = (Artist)session.get(Artist.class, id);
            tx.commit();
        }catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
            toRet = null;
        }finally {
            session.close();
        }
        return toRet;
    }

    public Integer addStore(String name, String adress)
    {
        Session session = SessionFactorySingleton.getInstance().openSession();
        Transaction tx = null;
        Integer id = null;
        try{
            tx = session.beginTransaction();
            Store obj = new Store(name, adress);
            id = (Integer) session.save(obj);
            tx.commit();
        }catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }finally {
            session.close();
        }
        return id;
    }

    public Store getStore(Integer id)
    {
        Session session = SessionFactorySingleton.getInstance().openSession();
        Store toRet = null;
        Transaction tx = null;
        try{
            tx = session.beginTransaction();
            toRet = (Store)session.get(Store.class, id);
            tx.commit();
        }catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
            toRet = null;
        }finally {
            session.close();
        }
        return toRet;
    }

    public Integer addAlbumAvailability(Album album, Store store, int count)
    {
        Session session = SessionFactorySingleton.getInstance().openSession();
        Transaction tx = null;
        Integer id = null;
        try{
            tx = session.beginTransaction();
            AlbumsAvailability obj = new AlbumsAvailability(album, store, count);
            id = (Integer) session.save(obj);
            tx.commit();
        }catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }finally {
            session.close();
        }
        return id;
    }

    public void deleteAlbumAvailability(Integer id){
        Session session = SessionFactorySingleton.getInstance().openSession();
        Transaction tx = null;
        try{
            tx = session.beginTransaction();
            AlbumsAvailability aa =
                    (AlbumsAvailability)session.get(AlbumsAvailability.class, id);
            session.delete(aa);
            tx.commit();
        }catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }finally {
            session.close();
        }
    }

    public List getAllArtists()
    {
        Session session = SessionFactorySingleton.getInstance().openSession();
        List toRet = null;
        Transaction tx = null;
        try{
            tx = session.beginTransaction();
            toRet = session.createCriteria(Artist.class).list();
            tx.commit();
        }catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
            toRet = null;
        }finally {
            session.close();
        }
        return toRet;
    }
}
