# Extension for Flyway database migrations to support branching

This project extends the standard functionality of flyway db migrations (http://flywaydb.org)
for support in migrating the database from different branches.

**Main advantages:**

-  Not limited to linear migrations on feature branch only
-  Adds 'release' term to existing version for migration artifacts
-  Well suited for software product development with typical support of migrating between different productive customer versions

## How to use it

*  Add the following dependency in your pom:

```xml

    <dependency>
      <groupId>info.novatec</groupId>
      <artifactId>flyway-branching-extension</artifactId>
      <version>{currentVersion}</version>
    </dependency>
```

*  You also need to add the following repository to your pom:

```xml

    <repository>
       <id>Novatec</id>
       <name>Novatec Repository</name>
       <url>http://repository.novatec-gmbh.de/content/repositories/novatec</url>
    </repository>
```

# Please be aware of the fact that it is not yet released to the repository above. So in the meantime you have to add the jar manually to your local maven repository !!

