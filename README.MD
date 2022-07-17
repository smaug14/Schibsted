Repository for Schibsted search-engine task.

--- Compilation:

In order to compile this project you need to import it as SBT project. After that
you should be able to build uber jar by opening 'sbt shell' and running command 'assembly'. 
JAR should be available under 'target/scala-2.13/schibsted-search.jar' path.
Example JAR should be available in repository in case of problems with compiling it on your side.

--- Running:

To run application you need to use JAR generate in previous step.
Example command:

' java -jar schibsted-search.jar testFolder/ '

Main class should be detected automatically.

You can also use some kind of IDE (fe. IntelliJ IDEA) to run application without JAR compilation. Just run 'boot.Main' class.