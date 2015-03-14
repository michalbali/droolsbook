# Examples for the book - "Drools JBoss Rules 5.X Developer's Guide" #
More information about this book can be found at http://www.packtpub.com/jboss-rules-5-x-developers-guide/book

Examples are in archive `parent-1.1-examples.zip` that can be downloaded from [here](http://droolsbook.googlecode.com/files/parent-1.1-examples.zip).

## Contents ##
The following is a list of projects that this archive contains.
One chapter in the book corresponds to one or more projects.

  * chapter 3 validating - project validation
  * chapter 4 transforming data - project etl\_iBatis
  * chapter 5 creating human readable rules - projects dsl, decision\_tables, ruleflow
  * chapter 6 working with stateful session - project stateful
  * chapter 7 complex event processing - project cep
  * chapter 8 defining processes with jBPM - project jBPM
  * chapter 9 building sample application - project sampleApplication
  * chapter 10 testing - project testing
  * chapter 11 integrating - project integration
  * appendix custom operator - project cep
  * other projects:
    * project bankingcore - contains banking domain model, reporting model, some service interfaces, some utility classes

## Usage ##
  1. install Eclipse
  1. install maven
  1. create new workspace in Eclipse
  1. set M2\_REPO Eclipse variable to point to your local maven repository (for more info see http://maven.apache.org/plugins/maven-eclipse-plugin/usage.html#Maven%20repository)
  1. extract zip file into the Eclipse workspace (you can do this step outside of Eclipse)
  1. go to the droolsBookParent project and run `mvn clean install eclipse:eclipse -DdownloadSources=true`
  1. import all (14) projects through _File->Import..->General->Existing Projects into Workspace->select root directory->you should see bunch of projects that you can import_
  1. setup is complete, you can start playing with the examples

You can also build all examples and run all tests from command line. Go to the droolsBookParent project and execute:
`mvn clean install`

The examples were developed and tested with JDK 1.6.0\_27, Maven 3.0.4

**Note**: Project page for previous revision of this book (Drools JBoss Rules 5.0 Developer's Guide) can be found here [DroolsDevelopersGuide50](DroolsDevelopersGuide50.md)