import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
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
                Object controller = method.getDeclaringClass().getDeclaredConstructor().newInstance();
                Object result;
                Class<?>[] parameterTypes = method.getParameterTypes();

                if (parameterTypes.length == 0) {
                    result = method.invoke(controller);
                } else if (parameterTypes.length == 1 && parameterTypes[0] == String.class) {
                    String employer = request.getParameter("employer");
                    if (employer != null && employer.trim().isEmpty()) {
                        employer = null;
                    }
                    result = method.invoke(controller, employer);
                } else {
                    throw new IllegalArgumentException("Paramètres non pris en charge pour " + method.getName());
                }

                out.print(result == null ? "Action exécutée." : result);
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
