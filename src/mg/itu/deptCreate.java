package mg.itu;

// Contrôleur normal → retourne du HTML
@Controller
public class deptCreate {

    @Url(value = "/dept/create", method = "GET")
    public String create() {
        return "<!DOCTYPE html><html><head><meta charset=\"UTF-8\"><title>Créer un département</title></head>"
                + "<body><h1>Enregistrer un employé</h1>"
                + "<form method=\"post\" action=\"\">"
                + "<label for=\"employer\">Employé :</label> "
                + "<input id=\"employer\" name=\"employer\" type=\"text\"> "
                + "<button type=\"submit\">Enregistrer</button></form></body></html>";
    }

    @Url(value = "/dept/create", method = "POST")
    public String save(String employer) {
        String value = employer == null ? "null" : "\"" + escapeHtml(employer) + "\"";
        return "<!DOCTYPE html><html><head><meta charset=\"UTF-8\"><title>Résultat</title></head>"
                + "<body><h1>Enregistrement reçu</h1><p>employer : " + value + "</p>"
                + "<a href=\"create\">Retour au formulaire</a></body></html>";
    }

    private String escapeHtml(String value) {
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
