
# DJar

djar is a Java library that avoids the need for fat executable jar files. Upon
invoking the executable jar, DJar will download the jar files dependencies if
and only if they cannot already be found locally.


Project Status:  Early conception/Pre-Build


## Motivation and Context

Java Jar files may be executed with the following command:

    java -jar helloworld.jar


Which will work if the jar file has an entry in its manifest stating which
class to invoke main upon and all of the dependencies for the class can be found
on the class path.


    Manifest-Version: 1.0
    Created-By: 1.7.0_06 (Oracle Corporation)

    Main-Class: com.world.HelloMain


If a jar file was compiled to use other libraries there has traditionally
been three options when it comes to packaging and distributing the program.

* Declare the jars manually on the class path and ship them all together
* Copy the jars into a lib directory and declare within the Manifest
* Merge all of the jars into a single uber jar

(nb converting the jar to an exe is a worthy mention but we will focus on jar files here)


The first approach is too error prone and annoying, the second approach is very
similar to the first but it does avoid having to manually create the class path.
The last approach can be the most convinient as tools such as Maven support it
out of the box and distribution is as simple as copying only a single file.  However
merging the jars does not always work, sometimes there are file clashes such as a config
file with the same name in two of the jar files.  It can also slow the build and
can turn a 50kb jar into a 50mb file.


This project offers a forth option that is motivated by the Maven approach
of downloading dependencies from repositories into a local directory that
may be shared between builds.  A djar file has a list of all of its dependencies
declared within the jar files manifest.  Upon invocation of the jar, before the
applications main is invoked;  DJar will scan the local repository
under the users home directory (usually ~/.m2) and downloads any artifacts that
are missing.  DJar will then construct a new class loader that makes use of those
artifacts before using it to run the application.


## Prerequisites

The jar files manifest must be modified to include the location of dependencies and
the jar file must have the djar classes copied into it.

DJar has no dependencies of its own and effort has been made to keep the size of the djar library small.


## Example DJar Manifest

    Manifest-Version: 1.0
    Created-By: 1.7.0_06 (Oracle Corporation)

    Main-Class: com.mosaic.djar.M
    Application-Main: com.world.HelloMain

    Dependencies: org.apache.commons:commons-lang:2.6 org.apache.commons:commons-io:2.6
    Resolved-Artifacts: http://repo1.maven.org/maven2/commons-lang/commons-lang/2.6/commons-lang-2.6.jar
        http://repo1.maven.org/maven2/commons-io/commons-io/2.6/commons-io-2.6.jar

* com.world.HelloMain has been moved to Application-Main
* Main-Class has been set to a djar class called com.mosaic.djar.M

com.mosaic.djar.M will load the manifest file, download any missing artifacts, configure a class loader
and then use the class loader to load and invoke com.world.HelloMain#main.

Note that this manifest lists both the maven encoded runes for the dependencies AND
the http urls of where to download the artifacts from.  This has two advantages, firstly
it keeps the size of the DJar code smaller as it does not have to understand Maven POM files; which
means that no xml parsing is needed, no dependency graph management and resolution or redirection.
All of that happens at compilation time and only the results have to be shared. It also means
that the location and order of the artifacts are fixed, which means that each  invocation
of the jar is going to behave the same.  People who have been caught out by class loader
ordering fun with J2EE containers will appreciate this behaviour.


# Invoking a DJar

By default the common case invocation is unchaged.

    java -jar helloworld.jar


