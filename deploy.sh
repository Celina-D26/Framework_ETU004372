mkdir -p bin

find src -name "*.java" > sources.txt
javac -cp "lib/*" -d bin @sources.txt
rm sources.txt
jar cvf framework.jar -C bin .
echo "Framework compilé avec succès en framework.jar !"