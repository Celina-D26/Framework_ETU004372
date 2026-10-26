#!/bin/bash
set -e

echo "=================================================="
echo " Compilation & Déploiement du Test - Sprint 9"
echo "=================================================="

# --- 1. Détection du JDK 21 / 17 ---
JAVAC=""
JARTOOL=""

if [ -n "$JAVA_HOME" ] && [ -x "$JAVA_HOME/bin/javac" ]; then
    JAVAC="$JAVA_HOME/bin/javac"
    JARTOOL="$JAVA_HOME/bin/jar"
fi

if [ -z "$JAVAC" ]; then
    for JVM_DIR in \
        "/usr/lib/jvm/java-21-openjdk-amd64" \
        "/usr/lib/jvm/default-java" \
        "/usr/lib/jvm/java-17-openjdk-amd64" \
        "/usr/lib/jvm/java-21-openjdk" \
        "/usr/lib/jvm/java-17-openjdk"
    do
        if [ -x "$JVM_DIR/bin/javac" ]; then
            export JAVA_HOME="$JVM_DIR"
            JAVAC="$JVM_DIR/bin/javac"
            JARTOOL="$JVM_DIR/bin/jar"
            break
        fi
    done
fi

if [ -z "$JAVAC" ]; then
    JAVAC="javac"
    JARTOOL="jar"
fi

echo "JDK détecté : $JAVAC"
"$JAVAC" -version

# --- 2. Détection de Tomcat ---
TOMCAT_HOME="${TOMCAT_HOME:-$HOME/Documents/tomcat/tomcat}"

if [ ! -d "$TOMCAT_HOME" ]; then
    echo "[ERREUR] Répertoire Tomcat introuvable à : $TOMCAT_HOME"
    exit 1
fi

SERVLET_API="$TOMCAT_HOME/lib/servlet-api.jar"
if [ ! -f "$SERVLET_API" ]; then
    SERVLET_API="lib/jakarta.servlet-api-6.0.0.jar"
fi

# Configurer Tomcat pour tourner sous Java 21
mkdir -p "$TOMCAT_HOME/bin"
cat << 'EOF' > "$TOMCAT_HOME/bin/setenv.sh"
#!/bin/bash
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
export JRE_HOME=/usr/lib/jvm/java-21-openjdk-amd64
EOF
chmod +x "$TOMCAT_HOME/bin/setenv.sh"

# --- 3. Compilation du Framework ---
echo "--> 1. Compilation du Framework..."
rm -rf build
mkdir -p build/classes
"$JAVAC" --release 17 -encoding UTF-8 -parameters -cp "lib/*" -d build/classes $(find src -name "*.java")
"$JARTOOL" cf Framework.jar -C build/classes .
echo "Framework.jar généré avec succès."

# --- 4. Préparation de app-test ---
echo "--> 2. Préparation de app-test..."
mkdir -p app-test/WEB-INF/lib
mkdir -p app-test/WEB-INF/classes

cp Framework.jar app-test/WEB-INF/lib/

# --- 5. Compilation des contrôleurs de test ---
echo "--> 3. Compilation des contrôleurs de test..."
"$JAVAC" --release 17 -encoding UTF-8 -parameters \
    -cp "Framework.jar:$SERVLET_API" \
    -d app-test/WEB-INF/classes \
    $(find app-test/src -name "*.java")

# --- 6. Déploiement dans Tomcat ---
echo "--> 4. Déploiement vers Tomcat ($TOMCAT_HOME/webapps/app-test)..."
rm -rf "$TOMCAT_HOME/webapps/app-test"
mkdir -p "$TOMCAT_HOME/webapps/app-test"

# Copie des fichiers WEB-INF, index.html, etc.
cp -r app-test/WEB-INF "$TOMCAT_HOME/webapps/app-test/"
cp app-test/index.html "$TOMCAT_HOME/webapps/app-test/"

# --- 7. Redémarrage de Tomcat ---
echo "--> 5. Redémarrage de Tomcat..."
"$TOMCAT_HOME/bin/shutdown.sh" 2>/dev/null || true
pkill -f "catalina" 2>/dev/null || true
sleep 1

"$TOMCAT_HOME/bin/startup.sh"
sleep 2

echo "=================================================="
echo "  DÉPLOIEMENT RÉUSSI !"
echo "=================================================="
echo "Pour voir l'affichage, ouvrez votre navigateur :"
echo ""
echo "👉 Dashboard interactif : http://localhost:8082/app-test/index.html"
echo "👉 Liste brute des routes: http://localhost:8082/app-test/"
echo "👉 Test direct Session    : http://localhost:8082/app-test/connexion?utilisateur=Celina"
echo "👉 Test direct Profil     : http://localhost:8082/app-test/profil"
echo "👉 Test direct Vue JSP    : http://localhost:8082/app-test/vue?nom=Celina"
echo "👉 Test direct API JSON   : http://localhost:8082/app-test/api/data"
echo "=================================================="
