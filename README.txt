Ausf�hren ohne Eclipse
=============================
Um das Programm ohne Eclipse laufen zu lassen besteht die m�glichkeit es �ber die Konsole zu tun. Erstens mit javac, zweitens mit java.
Im Downloadbereich gibt es die notwendigen Jar Dateien vom MySQL Treiber Log4J und junit. 

Nach einem clone auf das Repo hat man einen Ordner "HtwMapsBackend". Ich gehe folglich davon aus dass man die genannten .jar dateien in den Ordner HtwMapsBackend/bib/ kopiert.

Man erstellt nun einen Ordner "bin", in den alle compilierten .class dateien beim compilieren geschrieben werden.

Kompilieren:
cd HtwMapsBackend
javac -d .\bin -classpath .\bib\junit.jar;.\bib\log4j-1.2.16.jar;.\bib\mysql-connector-java-5.0.6-bin.jar ".\src\de\htwmaps\algorithm\tests\*.java" ".\src\de\htwmaps\algorithm\*.java" ".\src\de\htwmaps\database\*.java" ".\src\de\htwmaps\util\*.java"

Ausf�hren:
cd HtwMapsBackend
java -classpath .\bib\log4j-1.2.16.jar;.\bib\mysql-connector-java-5.0.6-bin.jar;.\bin de.htwmaps.algorithm.tests.DijkstraStarterTest

Kompilieren und Ausf�hren k�nnen in endg�ltiger Programmauslieferung
so in ein batch-, shell-, oder ant-Skript kopiert werden und in Eclipse 
gibt es ein sehr bequemes Export-Jar-Tool.

 
JUnit funktioniert hier so nicht.
java.exe sucht nach einer "main" methode. M�glicherweise kann man java.exe f�r JUnit konfigurieren.
deswegen hab ich die junit.jar aus dem classpath weggelassen.

beim git add sollte man nun bib weglassen sonst gibts fehler.
man sollte immer git add src/ machen. nicht git add .

