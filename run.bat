git clone https://github.com/idoshuan/enigma-maven.git ./enigma

cd enigma

call mvn clean install

cd enigma-app\target

java -jar enigma-machine-server-ex3.jar
