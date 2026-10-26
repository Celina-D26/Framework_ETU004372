package test.model;

import src.annotation.Numeric;
import src.annotation.Range;
import src.annotation.Required;

public class Etudiant {

    @Required(message = "Le nom est obligatoire")
    private String nom;

    @Numeric(message = "L'âge doit être numérique")
    @Range(min = 16, max = 100, message = "L'âge doit être entre 16 et 100")
    private int age;

    @Required(message = "L'email est obligatoire")
    private String email;

    public Etudiant() {
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
