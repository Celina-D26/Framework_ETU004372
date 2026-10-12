import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Parameter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mg.itu.Controller;
import mg.itu.RestAPI;
import mg.itu.Url;
import mg.itu.UrlKey;

public class FrontController extends HttpServlet {

    // Map qui associe (URL + méthode HTTP) → méthode Java
    private Map<UrlKey, Method> urlMapping = new HashMap<>();

    @Override
    public void init() {
        try {
            // On scanne les classes @Controller (HTML)
            List<Class<?>> controllers = ClassScanner.scanClasses("mg.itu", Controller.class);

            // On scanne aussi les classes @RestAPI (JSON)
            List<Class<?>> restControllers = ClassScanner.scanClasses("mg.itu", RestAPI.class);

            // On regroupe les deux listes
            controllers.addAll(restControllers);

            // Pour chaque classe trouvée
            for (Class<?> controllerClass : controllers) {
                for (Method method : controllerClass.getDeclaredMethods()) {
                    if (method.isAnnotationPresent(Url.class)) {

                        Url urlAnnotation = method.getAnnotation(Url.class);
                        String urlValue = urlAnnotation.value();
                        String httpMethod = urlAnnotation.method();

                        UrlKey key = new UrlKey(urlValue, httpMethod);
                        urlMapping.put(key, method);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        handle(request, response, "GET");
    }



    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        handle(request, response, "POST");
    }

    private void handle(HttpServletRequest request, HttpServletResponse response,
                        String httpMethod) throws IOException {

        String urlTapee = request.getPathInfo();
        UrlKey key = new UrlKey(urlTapee, httpMethod);

        try {
            if (!urlMapping.containsKey(key)) {
                throw new Exception("URL inconnue : " + urlTapee + " [" + httpMethod + "]");
            }

            Method method = urlMapping.get(key);
            Object controller = method.getDeclaringClass().getDeclaredConstructor().newInstance();
            Object[] parameters = new Object[method.getParameterCount()];
            Parameter[] methodParameters = method.getParameters();

            for (int i = 0; i < methodParameters.length; i++) {
                if (methodParameters[i].getType() != String.class) {
                    throw new IllegalArgumentException("Seuls les paramètres String sont pris en charge");
                }

                String value = request.getParameter(methodParameters[i].getName());
                parameters[i] = value == null || value.trim().isEmpty() ? null : value;
            }

            method.invoke(controller, parameters);

            // La classe qui contient cette méthode a-t-elle @RestAPI ?
            boolean isRestAPI = method.getDeclaringClass()
                                      .isAnnotationPresent(RestAPI.class);

            if (isRestAPI) {
                // ─── Réponse JSON ───────────────────────────────────
                response.setContentType("application/json;charset=UTF-8");
                PrintWriter out = response.getWriter();

                out.println("{");
                out.println("  \"url\": \"" + urlTapee + "\",");
                out.println("  \"httpMethod\": \"" + httpMethod + "\",");
                out.println("  \"method\": \"" + method.getName() + "\"");
                out.println("}");

            } else {
                // ─── Réponse HTML ────────────────────────────────────
                response.setContentType("text/html;charset=UTF-8");
                PrintWriter out = response.getWriter();

                if ("GET".equals(httpMethod) && "/dept/create".equals(urlTapee)) {
                    out.println("<!DOCTYPE html>");
                    out.println("<html><head><meta charset=\"UTF-8\"><title>Créer un département</title></head><body>");
                    out.println("<h1>Créer un département</h1>");
                    out.println("<form action=\"" + request.getContextPath() + "/dept/save\" method=\"post\">");
                    out.println("<label for=\"employer\">Employeur</label>");
                    out.println("<input id=\"employer\" name=\"employer\" type=\"text\">");
                    out.println("<button type=\"submit\">Enregistrer</button></form>");
                    out.println("</body></html>");
                } else {
                    out.println("<!DOCTYPE html><html><head><meta charset=\"UTF-8\"><title>Résultat</title></head><body>");
                    out.println("<p>URL : " + urlTapee + "</p>");
                    out.println("<p>Méthode HTTP : " + httpMethod + "</p>");
                    out.println("<p>Méthode Java appelée : " + method.getName() + "</p>");
                    for (int i = 0; i < methodParameters.length; i++) {
                        out.println("<p>" + methodParameters[i].getName() + " : " + parameters[i] + "</p>");
                    }
                    out.println("</body></html>");
                }
            }

        } catch (Exception e) {
            // Erreur → on retourne toujours du HTML avec la liste des URLs
            response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();

            out.println("<!DOCTYPE html>");
            out.println("<html><body>");
            out.println("<h1>Erreur : " + e.getMessage() + "</h1>");
            out.println("<p>Voici les URLs connues :</p>");
            out.println("<ul>");

            for (UrlKey k : urlMapping.keySet()) {
                out.println("<li>" + k.toString() + "</li>");
            }

            out.println("</ul>");
            out.println("</body></html>");
        }
    }
}
