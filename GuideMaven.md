# Maven Repository #
(Note: only available from 0.8 releases onwards)

```
<repository>
   <id>maven.pietschy.com</id>
   <name>Andrew Pietsch's Maven Repository</name>
   <url>http://maven.pietschy.com/repository</url>
</repository>
```

```
<dependency>
   <groupId>com.pietschy.gwt</groupId>
   <artifactId>gwt-pectin</artifactId>
   <version>0.8</version>
</dependency>
```

If you're after snapshots you can use:
```
<repository>
   <id>snapshots.maven.pietschy.com</id>
   <name>Andrew Pietsch's Maven Snapshots</name>
   <url>http://maven.pietschy.com/snapshots</url>
   <releases>
      <enabled>false</enabled>
   </releases>
   <snapshots>
      <enabled>true</enabled>
   </snapshots>
</repository>
```
```
<dependency>
   <groupId>com.pietschy.gwt</groupId>
   <artifactId>gwt-pectin</artifactId>
   <version>0.8-SNAPSHOT</version>
</dependency>
```