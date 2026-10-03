package mg.itu;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Marque une classe comme contrôleur qui retourne du JSON
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RestAPI {
}
