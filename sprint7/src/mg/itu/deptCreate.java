package mg.itu;

// Contrôleur normal → retourne du HTML
@Controller
public class deptCreate {

    @Url(value = "/dept/create", method = "GET")
    public void create() {
    }

    @Url(value = "/dept/save", method = "POST")
    public void save(String employer) {
        System.out.println("Employer reçu : " + employer);
    }
}
