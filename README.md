## Working with the sources
Clone this repository. The whole setup is Gradle-based, with very similar structure to default LibGDX projects generated with `gdx-setup`. Note that Gradle wrapper is not included in the root project, so you should have Gradle installed locally.

To deploy the libs, the project requires some additional "secret" properties, used for archives signing and logging to Maven Central. While most likely you will not need these functionalities, Gradle still forces you to provide these properties. A default unfilled `gradle.properties` file is available in the root folder, so Gradle will not complain about missing properties. However, eventually you might want to fill these in your Gradle home folder:
```
        signing.keyId= 
        signing.password= 
        signing.secretKeyRingFile= 

        ossrhUsername= 
        ossrhPassword= 
```
Note that deploying to Maven Local does *not* require the signing task, so if you just keep the signing properties commented out - you should be fine. Try running `gradle installAll` to check if deploying to Maven Local works.

Before pulling any requests, make sure your code is formatted with `eclipse-formatter.xml` (or its equivalent for other IDE). Note that this is *not* the official LibGDX code formatter, as I'm not really a huge fan of its setup.

Assuming you want to use Eclipse IDE (which is IMHO much better for managing multiple projects than IntelliJ thanks to its workspaces, working sets and whatnot), this is a simple list of stuff to do after repository cloning:

- Make sure `gradle` is installed globally. This repository has no Gradle wrappers in any project, so you cannot just use `./gradlew`.
- Run `gradle eclipse` to generate Eclipse projects for core libraries.
- *Optional*: `cd examples` and run `gradle eclipseAll` to generate Eclipse projects for all examples. Note that some tasks will fail to run; this is expected, as some examples have an Android project and require you to add `local.properties` file with `sdk.dir` property.
- Open Eclipse IDE. Import existing projects. Make sure to turn on searching for nested ones.
- Add `eclipse-formatter.xml` as Java code formatter. Make this the default formatter for the imported projects.

### Useful Gradle tasks
- `gradle eclipse` - generates Eclipse project structure.
- `gradle idea` - generates IntelliJ project structure.
- `gradle build install` - builds the libraries' archives and pushes them to Maven Local.
- `gradle installAll` - same as the previous one, but the tasks are always invoked in the correct order. Use when changing libraries' versions to avoid missing artifacts errors.
- `gradle uploadArchives` - pushes the archives to Maven Central. Requires proper `gradle.properties` with signing and logging data.
- `gradle clean` - removes built archives.
- `gradle distZip` - prepares a zip archive with all jars in `build/distributions` folder. Useful for releases.
- `gradle closeAndPromoteRepository` - closes and promotes Nexus repository. Run after `uploadArchives`.

Additionally, in `examples` directory you can find a utility Gradle project. This is *not* the root project of example applications: they are all autonomous and can be copied outside the repository (and should still work!). Still, it contains some utility tasks that modify or test example projects en masse:

- `gradle updateVersion` - copies `gradle.properties` from `examples` to all projects directories.
- `gradle eclipseAll` - generates Eclipse project meta-data for all examples.
- `gradle runAll` - starts each desktop application, one by one. Useful for quick testing. Some applications (web socket tests) might fail to run if their corresponding server application is not turned on - this is expected.
- `gradle cleanAll` - cleans build directories of example projects.

To run a task on a specific library, proceed task name with its project ID. For example, `gradle kiwi:build` will build archives of Kiwi library.
