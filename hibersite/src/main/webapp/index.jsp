<%@ page import="org.springframework.context.ApplicationContext" %>
<%@ page import="org.springframework.context.support.ClassPathXmlApplicationContext" %>
<%@ page import="vladfedchenko.lab.dao.GeneralDAO" %>
<%@ page import="vladfedchenko.lab.dbclasses.Artist" %>
<html>
    <body>
        <%{
            ApplicationContext context = new ClassPathXmlApplicationContext("Beans.xml");
            GeneralDAO dao = (GeneralDAO) context.getBean("generalDao");

            Artist artist = dao.getArtist(new Integer(1));
            %>

            <h1><%=artist.getId()%> <%=artist.getName()%> <%=artist.getBirthDate()%></h1>

        <%}%>
    </body>
</html>
