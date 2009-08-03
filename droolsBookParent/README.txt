= Examples for the book - "Drools JBoss Rules 5.0 Developer's Guide" = 
More information about this book can be found at http://www.packtpub.com/drools-jboss-rules-5-0-developers-guide/book

== Contents ==
The following is a list of projects that this archive contains.
One chapter in the book corresponds to one or more projects. 

 * chapter 3 validation - project validation
 * chapter 4 data transformation - project etl_iBatis
 * chapter 5 human readable rules - projects dsl, decision_tables, ruleflow
 * chapter 6 stateful session - project stateful
 * chapter 7 complex event processing - project cep
 * chapter 8 drools flow - project droolsflow
 * chapter 9 sample application - project sampleApplication
 * chapter 10 testing - project testing
 * chapter 11 integration - project integration
 * appendix custom operator - project cep
 * other projects:
   * project bankingcore - contains banking domain model, reporting model, some service interfaces, some utility classes

== Usage ==
 # install Eclipse
 # install maven
 # create new workspace in Eclipse
 # set M2_REPO Eclipse variable to point to your local maven repository (for more info see http://maven.apache.org/plugins/maven-eclipse-plugin/usage.html#Maven%20repository)
 # extract zip file into the Eclipse workspace (you can do this step outside of Eclipse)
 # go to the droolsBookParent project and run `mvn eclipse:eclipse -DdownloadSources=true`
 # import all (14) projects through _File->Import..->General->Existing Projects into Workspace->select root directory->you should see bunch of projects that you can import_
 # setup is complete, you can start playing with the examples

You can also build all examples and run all tests from command line. Go to the droolsBookParent project and execute:
`mvn clean install` 

The examples were developed and tested with JDK 1.6.0_14, Maven 2.0.9  