package itu.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class Framework extends HttpServlet {
    
    private List<Class<?>> listeClassController;

      @Override
    public void init() throws ServletException {
        super.init();
        initialiserFramework();
    }

    private void initialiserFramework() {
        System.out.println("Initialisation du framework...");
        listeClassController = Utils.scanControllers();
        System.out.println("Nombre de controllers trouvés : " + listeClassController.size());
        System.out.println("Framework initialisé !");
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String url = request.getRequestURI();
        System.out.println("[FrontController] URL --> " + url);

        for (Class<?> classe : listeClassController) {
            System.out.println("Controller dispo : " + classe.getName());
        }

        String contextPath = request.getContextPath();
        String urlTraquee = url.substring(contextPath.length());

        out.println("<html><body>");
        out.println("<h2>[Framework] Requête reçue</h2>");
        out.println("<p>L'URL que tu as écrite est : <strong style='color:blue;'>" + urlTraquee + "</strong></p>");

        out.println("<h3>Controllers détectés</h3>");
        if (listeClassController == null || listeClassController.isEmpty()) {
            out.println("<p>Aucun controller trouvé.</p>");
        } else {
            out.println("<ul>");
            for (Class<?> c : listeClassController) {
                out.println("<li>" + c.getName() + "</li>");
            }
            out.println("</ul>");
        }

        out.println("</body></html>");
    }
}