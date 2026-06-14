mkdir -p bin

javac -cp "lib/*" \
-d bin \
src/main/java/itu/controller/*.java
jar cvf framework.jar -C bin .
echo "Framework compilé avec succès en framework.jar !"