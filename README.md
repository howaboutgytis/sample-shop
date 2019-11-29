### Starting up the app
Use <code>/gradlew bootRun</code> from the **sample-shop** directory. Gradle installation is not needed.

After the app starts, it is reachable on <code>localhost:8080</code>

### Database
H2 embedded database is used. After the app starts a folder **database** is created and inside is the database.

A sample script(<code>src/main/resources/data.sql</code>) is used to populate the database with sample data on first run.

The database is persisted, but if a fresh clean one is required it can be achieved by changing <code>src/main/resources/application.properties</code> and setting
<code>spring.jpa.hibernate.ddl-auto</code> property to <code>create</code> (default is <code>update</code>)

### Documentation
Documentation is made with Spring Rest Docs and Asciidoctor.
A gradle task <code>asciidoctor</code> generates a file <code>build/asciidoc/html5/docs.html</code>.
I have just copied this file as index file.
It is generated by using <code>src/docs/asciidoc/docs.adoc</code> file.

### Trash files
Application, more specifically - database driver, creates a **.h2.server.properties** file when connecting to a console to 
save configurations. This file can be safely deleted.

### Authentication
There is no authentication, but if I would make one it would be API key based one.
* A user needs to register and only then gets an API key (a generated string)
* Every API request should be made with that string

This would add few degrees of security:
* Unregistered users can't make requests, preventing from overflowing server with requests from anyone.
* If something bad happens, the API key can be traced down to the user that uses it.
* It is possible to place API usage limits on the user - either allowing only certain HTTP methods or limiting number of calls

P.S. I am strongly in favor of OAuth, but didn't explore nor had any experience with it, so I skipped that option.

### How can the service be made redundant?
I would consider open sourcing the service, making it a package/library you could use in your projects, without needing to create yet another ecommerce system.
