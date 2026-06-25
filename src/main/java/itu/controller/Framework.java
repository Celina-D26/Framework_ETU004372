package itu.controller;

import itu.annotation.Url;
import itu.utils.Utils; // 🎯 Utilise le bon package ici

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class Framework extends HttpServlet {
    
    private List<Class<?>> listeClassController;
    private Map<Class<?>, List<Method>> listeMethode;

    @Override
    public void init() throws ServletException {
        super.init();
        initialiserFramework();
    }

    private void initialiserFramework() {
        System.out.println("Initialisation du framework...");
        
        // 🎯 Scan des contrôleurs via le bon package itu.utils
        listeClassController = Utils.scanControllers();
        System.out.println("Nombre de controllers trouvés : " + listeClassController.size());
        
        // 🎯 Scan des méthodes via le bon package itu.utils
        listeMethode = Utils.scanMethodes(listeClassController);
        System.out.println("Scan des méthodes terminé.");
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
        String contextPath = request.getContextPath();
        String urlTraquee = url.substring(contextPath.length());

        out.println("<html>");
        out.println("<head><style>");
        out.println("body { font-family: Arial, sans-serif; margin: 30px; background-color: #f8f9fa; }");
        out.println(".card { background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); margin-bottom: 20px; }");
        out.println(".methode-item { background: #e9ecef; padding: 10px; margin: 5px 0; border-radius: 4px; font-family: monospace; }");
        out.println(".annotation { color: #d63384; font-weight: bold; }");
        out.println(".nom-fonction { color: #0d6efd; font-weight: bold; }");
        out.println("</style></head>");
        out.println("<body>");
        
        out.println("<h2>[Framework] Tableau de bord</h2>");
        out.println("<p>URL demandée : <strong style='color:blue;'>" + urlTraquee + "</strong></p>");

        out.println("<h2>🎯 Liste des Classes et leurs Fonctions annotées :</h2>");

        if (listeMethode == null || listeMethode.isEmpty()) {
            out.println("<p style='color:red;'><em>Aucune méthode annotée trouvée. Vérifie le scan ou les packages.</em></p>");
        } else {
            for (Map.Entry<Class<?>, List<Method>> entry : listeMethode.entrySet()) {
                Class<?> classe = entry.getKey();
                List<Method> fonctions = entry.getValue();

                out.println("<div class='card'>");
                out.println("  <h3>Classe : <span style='color:#198754;'>" + classe.getName() + "</span></h3>");
                out.println("  <h4>Fonctions détectées avec @Url :</h4>");

                if (fonctions == null || fonctions.isEmpty()) {
                    out.println("    <p style='color:orange;'>⚠️ Aucune fonction avec l'annotation @Url dans cette classe.</p>");
                } else {
                    out.println("  <ul>");
                    for (Method f : fonctions) {
                        // Récupération de la valeur de l'annotation au-dessus de la fonction
                        String annotationValue = f.getAnnotation(Url.class).value();
                        
                        out.println("    <li class='methode-item'>");
                        out.println("      <span class='annotation'>@Url(\"" + annotationValue + "\")</span><br/>");
                        out.println("      public void <span class='nom-fonction'>" + f.getName() + "()</span>");
                        out.println("    </li>");
                    }
                    out.println("  </ul>");
                }
                out.println("</div>");
            }
        }
        
        out.println("</body></html>");
    }
}