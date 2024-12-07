
build: Populate.class

Populate.class: Populate.java
	javac Populate.java

run: Populate.class
	java -cp .:mssql-jdbc-11.2.0.jre11.jar Populate

clean:
	rm Populate.class
