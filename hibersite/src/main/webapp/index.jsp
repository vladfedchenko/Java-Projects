<%@ page import="org.springframework.context.ApplicationContext" %>
<%@ page import="org.springframework.context.support.ClassPathXmlApplicationContext" %>
<%@ page import="vladfedchenko.lab.dao.GeneralDAO" %>
<%@ page import="vladfedchenko.lab.dbclasses.Artist" %>
<%@ page import="java.util.List" %>
<%@ page import="org.hibernate.Hibernate" %>
<html>
    <body>
        <%{
            ApplicationContext context = new ClassPathXmlApplicationContext("Beans.xml");
            GeneralDAO dao = (GeneralDAO) context.getBean("generalDao");
            List<Artist> artists = dao.getAllArtists();
        %>
        <table border="1">
            <tr>
                <th>ID</th>
                <th>Name</th>
                <th>Birth date</th>
            </tr>

            <%for(Artist artist : artists){
                String refAdr = "albums.jsp?artist=" + artist.getId();
            %>

            <tr>
                <td><%=artist.getId()%></td>
                <td><a href="<%=refAdr%>"><%=artist.getName()%></a> </td>
                <td><%=artist.getBirthDate()%></td>
            </tr>

            <%}%>

        </table>



        <%}%>
    </body>
</html>
