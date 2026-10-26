package test.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpSession;
import src.FileUpload;
import src.ModelAndView;
import src.annotation.Controller;
import src.annotation.GetMapping;
import src.annotation.Param;
import src.annotation.ParamObject;
import src.annotation.PostMapping;
import src.annotation.SessionAttribute;
import src.annotation.UrlApi;
import test.model.Etudiant;

@Controller
public class TestController {

    // ==========================================
    // Sprint 9 : Gestion de Session & @SessionAttribute
    // ==========================================

    @GetMapping("/connexion")
    public String connexion(HttpSession session, @Param(name = "utilisateur") String utilisateur) {
        String nom = (utilisateur != null && !utilisateur.isBlank()) ? utilisateur : "Celina";
        session.setAttribute("utilisateur", nom);
        return "Session initialisée avec succès ! Utilisateur connecté : " + nom;
    }

    @GetMapping("/profil")
    public String profil(@SessionAttribute(name = "utilisateur") String utilisateur) {
        return "Bienvenue sur votre profil, " + utilisateur + " ! (Injecté via @SessionAttribute)";
    }

    @GetMapping("/deconnexion")
    public String deconnexion(HttpSession session) {
        session.invalidate();
        return "Session fermée avec succès. Vous êtes déconnecté(e).";
    }

    // ==========================================
    // Sprint 7 : Paramètres simples (@Param)
    // ==========================================

    @GetMapping("/bind-simple")
    public String bindSimple(
            @Param(name = "nom") String nom,
            @Param(name = "age") int age,
            @Param(name = "note") double note,
            @Param(name = "valide") boolean valide) {
        return "Paramètres reçus et convertis :"
                + "\n- nom : " + nom
                + "\n- age : " + age
                + "\n- note : " + note
                + "\n- valide : " + valide;
    }

    // ==========================================
    // Sprint 7b : Binding Objet & Validation
    // ==========================================

    @PostMapping("/bind-objet")
    public String bindObjet(@ParamObject(name = "etudiant") Etudiant etudiant) {
        return "Étudiant validé avec succès :"
                + "\n- Nom : " + etudiant.getNom()
                + "\n- Âge : " + etudiant.getAge()
                + "\n- Email : " + etudiant.getEmail();
    }

    // ==========================================
    // Sprint 7 : ModelAndView & Vue JSP
    // ==========================================

    @GetMapping("/vue")
    public ModelAndView viewDemo(@Param(name = "nom") String nom) {
        ModelAndView mv = new ModelAndView("bienvenue");
        mv.addAttribute("nom", (nom != null && !nom.isBlank()) ? nom : "Celina");
        mv.addAttribute("message", "Le Framework Sprint 9 fonctionne parfaitement sur Tomcat 10 !");
        mv.addAttribute("date", new Date());
        return mv;
    }

    // ==========================================
    // Sprint 6 : Retour API JSON
    // ==========================================

    @UrlApi
    @GetMapping("/api/data")
    public Map<String, Object> apiDemo() {
        Map<String, Object> map = new HashMap<>();
        map.put("framework", "Sprint 9 MVC Framework");
        map.put("statut", "OK");
        map.put("message", "Sérialisation JSON automatique réussie !");
        map.put("timestamp", System.currentTimeMillis());
        return map;
    }

    // ==========================================
    // Sprint 8 : Upload de fichier multipart
    // ==========================================

    @PostMapping("/upload")
    public String uploadDemo(FileUpload fichier) {
        if (fichier == null || fichier.getContent() == null) {
            return "Aucun fichier reçu.";
        }
        return "Fichier reçu avec succès !"
                + "\n- Nom du fichier : " + fichier.getFileName()
                + "\n- Taille : " + fichier.getSize() + " octets"
                + "\n- Type MIME : " + fichier.getContentType();
    }
}
