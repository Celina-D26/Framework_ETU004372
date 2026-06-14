package itu.controller;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class Framework extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        isPost(req, res);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        isGet(req, res);
    }

    public void isGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("text/html;charset=UTF-8");
        PrintWriter out = res.getWriter();
        
        // 🎯 C'est ici qu'on extrait et qu'on connaît l'URL tapée !
        String urlComplete = req.getRequestURI(); // Exemple: "/sprint_0/lister"
        String contextPath = req.getContextPath(); // Exemple: "/sprint_0"
        
        // On garde uniquement ce qui vient après le nom du projet
        String urlTraquee = urlComplete.substring(contextPath.length());

        out.println("<html><body>");
        out.println("<h2>[Framework] Requête reçue en GET</h2>");
        out.println("<p>L'URL que tu as écrite est : <strong style='color:blue;'>" + urlTraquee + "</strong></p>");
        out.println("</body></html>");
    }

    public void isPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("text/html;charset=UTF-8");
        PrintWriter out = res.getWriter();
        
        String urlComplete = req.getRequestURI();
        String contextPath = req.getContextPath();
        String urlTraquee = urlComplete.substring(contextPath.length());

        out.println("<html><body>");
        out.println("<h2>[Framework] Requête reçue en POST</h2>");
        out.println("<p>L'URL soumise est : <strong style='color:red;'>" + urlTraquee + "</strong></p>");
        out.println("</body></html>");
    }
}