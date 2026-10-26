#! /bin/bash
echo "=================================================="
echo " Deploy Framework - JDK 17+ requis, sortie Java 17"
echo "=================================================="

# --- 1. Chercher un javac compatible ---
JAVAC=""
JARTOOL=""

if [ -n "$JAVA_HOME" ] && [ -x "$JAVA_HOME/bin/javac" ]; then
    JAVAC="$JAVA_HOME/bin/javac"
    JARTOOL="$JAVA_HOME/bin/jar"
fi

# Chemins connus sous Linux
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

echo "JDK utilise : $JAVAC"
"$JAVAC" -version
if [ $? -ne 0 ]; then
    echo ""
    echo "[ERREUR] Aucun javac fonctionnel trouve."
    echo "Installe un JDK 17/21/24 et definis JAVA_HOME."
    exit 1
fi

# --- 2. Verifier version minimale 17 ---
if "$JAVAC" -version 2>&1 | grep -q "1\.8"; then
    echo ""
    echo "[ERREUR] Le javac detecte est Java 8 : $JAVAC"
    echo "Le framework utilise pattern-matching -> JDK 17 minimum requis."
    echo "Exemple : export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64"
    exit 1
fi

echo "nettoyage..."
rm -rf build
mkdir -p build/classes

echo "compilation..."
# --release 17 : bytecode v61 (lisible par JDK 17 : compil du test + Tomcat).
# -encoding UTF-8 : accents, -parameters : noms des parametres gardes (binding).
"$JAVAC" --release 17 -encoding UTF-8 -parameters -cp "lib/*" -d build/classes $(find src -name "*.java")

if [ $? -ne 0 ]; then
    echo "Compilation failed."
    exit 1
fi

echo "creation du jar..."
"$JARTOOL" cf Framework.jar -C build/classes .

echo "termine : Framework.jar genere (bytecode Java 17)."
