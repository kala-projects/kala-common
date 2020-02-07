# Kala common

## Adding Kala common to your build

First, add the Glavo Maven repository to your project configuration file:

Maven:
```xml
<project>
...
  <repositories>
    <repository>
      <id>glavo</id>
      <name>Glavo maven repo</name>
      <url>https://dl.bintray.com/glavo/maven</url>
    </repository>
  </repositories>
...
</project>
```

Gradle:
```groovy
repositories {
    maven { 
        url 'https://dl.bintray.com/glavo/maven' 
    }
}
```

Then add dependencies:

Maven:
```xml
<dependency>
  <groupId>asia.kala</groupId>
  <artifactId>kala-base</artifactId>
  <version>0.1.0</version>
  <type>pom</type>
</dependency>
```

Gradle:
```groovy
implementation group: 'asia.kala', name: 'kala-base', version: '0.1.0'
```

