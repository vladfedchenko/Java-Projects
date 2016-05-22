<%@ page import="org.springframework.context.ApplicationContext" %>
<%@ page import="org.springframework.context.support.ClassPathXmlApplicationContext" %>
<%@ page import="vladfedchenko.lab.dao.GeneralDAO" %>
<%@ page import="vladfedchenko.lab.dbclasses.Artist" %>
<%@ page import="vladfedchenko.lab.dbclasses.Album" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Collections" %>
<%@ page import="java.util.Comparator" %>
<html>
    <body>
        <%{
            ApplicationContext context = new ClassPathXmlApplicationContext("Beans.xml");
            GeneralDAO dao = (GeneralDAO) context.getBean("generalDao");
            int artistId = Integer.parseInt(request.getParameter("artist"));
            Artist artist = dao.getArtist(artistId);
            List<Album> albums = new ArrayList<Album>(artist.getAlbums());
            Collections.sort(albums, new Comparator<Album>() {
                public int compare(Album al1, Album al2) {
                    return al1.getId() > al2.getId() ? 1 : (al1.getId() == al2.getId() ? 0 : -1);
                }
            });
            //Hibernate.initialize(albums);
        %>
        <h3><%=artist.getName()%></h3>
        <br/>

        <table border="1">
            <tr>
                <th>ID</th>
                <th>Name</th>
                <th>Song count</th>
            </tr>

            <%for(Album album : albums){
                String refAdr = "songs.jsp?album=" + album.getId();
                String refToBuy = "albums_availability.jsp?album=" + album.getId();
            %>

            <tr>
                <td><%=album.getId()%></td>
                <td><a href="<%=refToBuy%>"><%=album.getName()%></a></td>
                <td><a href="<%=refAdr%>"><%=album.getSongCount()%></a></td>
            </tr>

            <%}%>

        </table>
        <br/>

        <a href="index.jsp">Back to artists</a>
        <%}%>
    </body>
</html>
