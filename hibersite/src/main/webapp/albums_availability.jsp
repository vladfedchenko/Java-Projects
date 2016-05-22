<%@ page import="org.springframework.context.ApplicationContext" %>
<%@ page import="org.springframework.context.support.ClassPathXmlApplicationContext" %>
<%@ page import="vladfedchenko.lab.dao.GeneralDAO" %>
<%@ page import="vladfedchenko.lab.dbclasses.Album" %>
<%@ page import="vladfedchenko.lab.dbclasses.Song" %>
<%@ page import="java.util.*" %>
<%@ page import="vladfedchenko.lab.dbclasses.AlbumsAvailability" %>
<html>
    <body>
        <%{
            ApplicationContext context = new ClassPathXmlApplicationContext("Beans.xml");
            GeneralDAO dao = (GeneralDAO) context.getBean("generalDao");
            int albumId = Integer.parseInt(request.getParameter("album"));
            Album album = dao.getAlbum(albumId);
            List<AlbumsAvailability> albums_stores = new ArrayList<AlbumsAvailability>(album.getAlbumAvailability());
            Collections.sort(albums_stores, new Comparator<AlbumsAvailability>() {
                public int compare(AlbumsAvailability av1, AlbumsAvailability av2) {
                    return av1.getId() > av2.getId() ? 1 : (av1.getId() == av2.getId() ? 0 : -1);
                }
            });

            String backAdr = "albums.jsp?artist=" + album.getArtist().getId();
        %>
        <h3><%=album.getName()%></h3>
        <br/>

        <table border="1">
            <tr>
                <th>ID</th>
                <th>Store</th>
                <th>Count</th>
            </tr>

            <%for(AlbumsAvailability tmp : albums_stores){
                String refAdr = "stores.jsp?store=" + tmp.getStore().getId() + "&album=" + album.getId();
            %>

            <tr>
                <td><%=tmp.getId()%></td>
                <td><a href="<%=refAdr%>"><%=tmp.getStore().getName()%><a/></td>
                <td><%=tmp.getCount()%></td>
            </tr>

            <%}%>

        </table>

        <br/>
        <a href="<%=backAdr%>">Back to albums</a>

        <%}%>
    </body>
</html>
