Pectin is built using the [Gradle](http://gradle.org) build system.  It's built using version 0.8 for which you can find the [user guide here](http://www.gradle.org/0.8/docs/userguide/userguide.html).

**Please Note:** _You don't need to install gradle_ to build the project. The project uses a Gradle provided wrapper that automatically downloads the correct version for you.

# Project directory structure #
The project contains three subprojects, the library itself (contained in the gwt-pectin directory), the demo and the GWT contacts example updated to use Pectin.  Building each project is performed from the subdirectories.

Gradle requires a number of build files (this requirement is being relaxed later versions) to operate.  In the root directory, a `build.gradle` and `settings.gradle` file provide configuration for the whole project (such as maven repositories, plugins and custom functions) while a `build.gradle` in each of the sub directories provides the main build code.  The following illustrates:

```
<root>
  - build.gradle          <- project wide configuration
  - settings.gradle       <- bulid system settings and config
  - contacts-mvp-example
    - build.gradle        <- mvp example build file 
  - demo
    - build.gradle        <- demo build file
  - gwt-pectin
    - build.gradle        <- main build file
```



# Source layout #
The source layout for the project follows the standard Maven layout with the addition of a `gwtTest` leaf for test cases that extend GWTTestCase.
```
<root>
  - gwt-pectin
    - src
      - gwtTest
        - java        <- GWTTestCase test sources
      - main
        - java        <- main pectin sources
        - resources
      - test
        - java        <- TestNG test sources
        - resources

```



# Building the jar #
To build the main jar you need to change to the gwt-pectin sub directory.

From `root/gwt-pectin`:
```
   ./gradlew build  
```

**Note:**
All the examples here are based on unix.  If you're using Windows then substitute `gradlew.bat` for `./gradlew`.


The will jar path will be `root/gwt-pectin/build/libs/gwt-pectin-0.x-SNAPSHOT.jar`

If you use the release target then jar will be built without the SNAPSHOT sufix and the build number will be incremented for the next build.

```
   ./gradlew release 
```


In this case will jar path will be `root/gwt-pectin/build/libs/gwt-pectin-0.x.jar`


# Running the demo #
To run the demo you need to be in the demo sub directory.

From `root/demo`:
```
   ./gradlew demo
```

If you would like the build the full demo instead of running the shell you can do the following:

From `root/demo`:
```
   ./gradlew dist
```

The compiled output can be found in `root/demo/build/web`.


# Running the MVP Example #
To run the demo you need to be in the demo sub directory.

From `root/contacts-mvp-example`:
```
   ./gradlew run
```