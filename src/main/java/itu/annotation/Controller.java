package itu.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE) // Cible les classes
@Retention(RetentionPolicy.RUNTIME) // 🔥 ULTRA IMPORTANT : Permet au framework de la voir au runtime
public @interface Controller {
}