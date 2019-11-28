### Starting up the app
Use <code>/gradlew bootRun</code> from the **sample-shop** directory. Gradle installation is not needed.

After the app starts, it is reachable on <code>localhost:8080</code>

### Database
H2 embedded database is used. After the app starts a folder **database** is created and inside is the database.

A sample script(<code>src/main/resources/data.sql</code>) is used to populate the database with sample data on first run.

### Trash files
Application, more specifically - database driver, creates a **.h2.server.properties** file when connecting to a console to 
save configurations. This file can be safely deleted.
