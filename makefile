
build: Interface.class

Interface.class: Interface.java
	javac Interface.java

run: Interface.class
	java -cp .:mssql-jdbc-11.2.0.jre11.jar Interface

clean:
	rm Interface.class
