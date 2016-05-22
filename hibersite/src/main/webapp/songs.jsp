<%@ page import="org.springframework.context.ApplicationContext" %>
<%@ page import="org.springframework.context.support.ClassPathXmlApplicationContext" %>
<%@ page import="vladfedchenko.lab.dao.GeneralDAO" %>
<%@ page import="vladfedchenko.lab.dbclasses.Album" %>
<%@ page import="vladfedchenko.lab.dbclasses.Song" %>
<%@ page import="java.util.*" %>
<html>
    <body>
        <%{
            ApplicationContext context = new ClassPathXmlApplicationContext("Beans.xml");
            GeneralDAO dao = (GeneralDAO) context.getBean("generalDao");
            int albumId = Integer.parseInt(request.getParameter("album"));
            Album album = dao.getAlbum(albumId);
            List<Song> songs = new ArrayList<Song>(album.getSongs());
            Collections.sort(songs, new Comparator<Song>() {
                public int compare(Song song, Song t1) {
                    return song.getId() > t1.getId() ? 1 : (song.getId() == t1.getId() ? 0 : -1);
                }
            });

            String backAdr = "albums.jsp?artist=" + album.getArtist().getId();
        %>
        <h3><%=album.getName()%></h3>
        <br/>

        <table border="1">
            <tr>
                <th>ID</th>
                <th>Name</th>
                <th>Length</th>
            </tr>

            <%for(Song song : songs){
            %>

            <tr>
                <td><%=song.getId()%></td>
                <td><%=song.getName()%> </td>
                <td><%=song.getLength()%></td>
            </tr>

            <%}%>

        </table>

        <br/>
        <a href="<%=backAdr%>">Back to albums</a>

        <%}%>
    </body>
</html>
