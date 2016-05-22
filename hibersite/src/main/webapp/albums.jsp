<%@ page import="org.springframework.context.ApplicationContext" %>
<%@ page import="org.springframework.context.support.ClassPathXmlApplicationContext" %>
<%@ page import="vladfedchenko.lab.dao.GeneralDAO" %>
<%@ page import="vladfedchenko.lab.dbclasses.Artist" %>
<%@ page import="vladfedchenko.lab.dbclasses.Album" %>
<%@ page import="java.util.Set" %>
<%@ page import="org.hibernate.Hibernate" %>
<html>
    <body>
        <%{
            ApplicationContext context = new ClassPathXmlApplicationContext("Beans.xml");
            GeneralDAO dao = (GeneralDAO) context.getBean("generalDao");
            int artistId = Integer.parseInt(request.getParameter("artist"));
            Artist artist = dao.getArtist(artistId);
            Set<Album> albums = artist.getAlbums();
            Hibernate.initialize(albums);
        %>
        <table border="1">
            <tr>
                <th>ID</th>
                <th>Name</th>
                <th>Artist</th>
                <th>Song count</th>
            </tr>

            <%for(Album album : albums){
                String refAdr = "sonsg.jsp?album=" + album.getId();
            %>

            <tr>
                <td><%=album.getId()%></td>
                <td><%=album.getName()%> </td>
                <td><%=artist.getName()%></td>
                <td><%=album.getSongCount()%></td>
            </tr>

            <%}%>

        </table>



        <%}%>
    </body>
</html>
