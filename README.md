# Cypher Cove libraries for [LibGDX](https://github.com/libgdx/libgdx)
This repository contains libraries for use with the LibGDX framework.

## Libraries

### [FlexBatch](flexbatch)

A library for for batching many small custom objects optimally. It can be used as a flexible version of SpriteBatch, but where the sprites can have custom parameters, multi-texturing, bump-mapped lighting, etc.

### [Cove Tools](covetools)

A library containing utility classes, including AssignmentAssetManager, which allows assets to be specified, loaded, and referenced with very little code.

## Working with the sources
This project's Gradle files are based on those in the [gdx-lml project](https://github.com/czyzby/gdx-lml) by [czyzby](https://github.com/czyzby/). The below explanation is originally from that project.

Clone this repository. The whole setup is Gradle-based, with very similar structure to default LibGDX projects generated with `gdx-setup`. Note that Gradle wrapper is not included in the root project, so you should have Gradle installed locally.

To deploy the libs, the project requires some additional "secret" properties, used for archives signing and logging to Maven Central. While most likely you will not need these functionalities, Gradle still forces you to provide these properties. A default unfilled `gradle.properties` file is available in the root folder, so Gradle will not complain about missing properties. However, eventually you might want to fill these in your Gradle home folder:
```
        signing.keyId= 
        signing.password= 
        signing.secretKeyRingFile= 

       ossrhUsername= 
       ossrhPassword= 
```
Note that deploying to Maven Local does *not* require the signing task, so if you just keep the signing properties commented out - you should be fine. Try running `gradle install` to check if deploying to Maven Local works.

If making a pull request, make sure your code is formatted with the [LibGDX Eclipse Formatter](https://github.com/libgdx/libgdx/blob/master/eclipse-formatter.xml).

To use Eclipse IDE, do the following after repository cloning:

- Make sure `gradle` is installed globally. This repository has no Gradle wrappers in any project, so you cannot just use `./gradlew`.
- Run `gradle eclipse` to generate Eclipse projects for core libraries.
- Open Eclipse IDE. Import existing projects. Make sure to turn on searching for nested ones.
- Add `eclipse-formatter.xml` as Java code formatter. Make this the default formatter for the imported projects.

### Useful Gradle tasks
- `gradle eclipse` - generates Eclipse project structure.
- `gradle idea` - generates IntelliJ project structure.
- `gradle build install` - builds the libraries' archives and pushes them to Maven Local.
- `gradle uploadArchives` - pushes the archives to Maven Central. Requires proper `gradle.properties` with signing and logging data.
- `gradle clean` - removes built archives.
- `gradle distZip` - prepares a zip archive with all jars in `build/distributions` folder. Useful for releases.
- `gradle closeAndPromoteRepository` - closes and promotes Nexus repository. Run after `uploadArchives`.

To run a task on a specific library, proceed task name with its project ID. For example, `gradle flexbatch:build` will build archives of the FlexBatch library.
