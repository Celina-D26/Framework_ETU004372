#!/bin/bash
set -euo pipefail

# Script de compilation et déploiement sprint6
# TOMCAT_HOME peut être défini pour utiliser une autre installation.
SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
TOMCAT_HOME="${TOMCAT_HOME:-$HOME/Documents/tomcat/tomcat}"
SERVLET_API="$TOMCAT_HOME/lib/servlet-api.jar"

if [[ ! -x "$TOMCAT_HOME/bin/startup.sh" || ! -x "$TOMCAT_HOME/bin/shutdown.sh" || ! -d "$TOMCAT_HOME/webapps" ]]; then
	printf 'Tomcat introuvable ou incomplet : %s\n' "$TOMCAT_HOME" >&2
	exit 1
fi

if [[ ! -f "$SERVLET_API" ]]; then
	printf 'servlet-api.jar introuvable : %s\n' "$SERVLET_API" >&2
	exit 1
fi

cd "$SCRIPT_DIR"

mkdir -p out

# 1. Annotations et UrlKey
javac -d out src/mg/itu/Controller.java src/mg/itu/RestAPI.java src/mg/itu/Url.java src/mg/itu/UrlKey.java

# 2. Contrôleurs
javac -parameters -cp out -d out src/mg/itu/deptCreate.java src/mg/itu/deptApi.java

# 3. ClassScanner et FrontController
javac -cp "out:$SERVLET_API" -d out src/ClassScanner.java src/FrontController.java

# 4. Créer le jar
jar cf spring6.jar -C out .

# 5. Copier dans app-test
cp spring6.jar app-test/WEB-INF/lib/

# 6. Redéployer dans Tomcat
"$TOMCAT_HOME/bin/shutdown.sh" || true
sleep 3
rm -rf -- "$TOMCAT_HOME/webapps/app-test"
cp -r app-test "$TOMCAT_HOME/webapps/"
"$TOMCAT_HOME/bin/startup.sh"

echo "Déploiement terminé !"
echo "Test HTML : http://localhost:8081/app-test/dept/create"
echo "Test JSON : http://localhost:8081/app-test/api/dept/list"
