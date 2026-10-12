package mg.itu;

// Contrôleur REST → retourne du JSON
@RestAPI
public class deptApi {

    @Url(value = "/api/dept/list", method = "GET")
    public void listDept() {
    }

    @Url(value = "/api/dept/create", method = "POST")
    public void createDept() {
    }
}
