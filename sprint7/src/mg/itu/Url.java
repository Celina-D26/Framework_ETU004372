package mg.itu;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Url {

    // L'URL ex: "/dept/create"
    String value();

    // La méthode HTTP — GET par défaut
    String method() default "GET";
}
