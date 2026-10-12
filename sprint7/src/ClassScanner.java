import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassScanner {

    public static List<Class<?>> scanClasses(String packageName, Class annotation)
            throws Exception {

        List<Class<?>> result = new ArrayList<>();
        String packagePath = packageName.replace(".", "/");

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL resource = classLoader.getResource(packagePath);

        String protocol = resource.getProtocol();

        if (protocol.equals("file")) {
            // Dossier normal sur le disque
            File folder = new File(resource.toURI());

            for (File file : folder.listFiles()) {
                if (file.getName().endsWith(".class") && !file.getName().contains("$")) {
                    String className = file.getName().replace(".class", "");
                    Class<?> clazz = Class.forName(packageName + "." + className);

                    if (clazz.isAnnotationPresent(annotation)) {
                        result.add(clazz);
                    }
                }
            }

        } else if (protocol.equals("jar")) {
            // Dans un jar déployé dans Tomcat
            String jarPath = resource.getPath()
                                     .substring(5)
                                     .split("!")[0];

            JarFile jarFile = new JarFile(jarPath);
            Enumeration<JarEntry> entries = jarFile.entries();

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String entryName = entry.getName();

                if (entryName.startsWith(packagePath)
                        && entryName.endsWith(".class")
                        && !entryName.contains("$")) {

                    String className = entryName
                                        .replace("/", ".")
                                        .replace(".class", "");

                    Class<?> clazz = Class.forName(className);

                    if (clazz.isAnnotationPresent(annotation)) {
                        result.add(clazz);
                    }
                }
            }

            jarFile.close();
        }

        return result;
    }
}
