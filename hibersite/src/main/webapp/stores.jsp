<%@ page import="org.springframework.context.ApplicationContext" %>
<%@ page import="org.springframework.context.support.ClassPathXmlApplicationContext" %>
<%@ page import="vladfedchenko.lab.dao.GeneralDAO" %>
<%@ page import="vladfedchenko.lab.dbclasses.Album" %>
<%@ page import="vladfedchenko.lab.dbclasses.Store" %>
<html>
    <body>
        <%{
            ApplicationContext context = new ClassPathXmlApplicationContext("Beans.xml");
            GeneralDAO dao = (GeneralDAO) context.getBean("generalDao");
            int albumId = Integer.parseInt(request.getParameter("album"));
            Album album = dao.getAlbum(albumId);

            int storeId = Integer.parseInt(request.getParameter("store"));
            Store store = dao.getStore(storeId);

            String backAdr = "albums_availability.jsp?album=" + album.getId();
        %>
        <h2><%=store.getName()%></h2>
        <br/>
        <h3><%=store.getAdress()%></h3>
        <br/>
        <a href="<%=backAdr%>">Back to stock</a>

        <%}%>
    </body>
</html>
