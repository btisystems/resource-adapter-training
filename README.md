Resource Adapter Training
=========================

Training module for users of the Resource Adapter.  


Dependencies
----------

This training assumes you have configured your machines with the following software

* [Java 7](http://www.oracle.com/technetwork/java/javase/downloads/index.html) - Make sure this is Java 7!
* [Maven](http://maven.apache.org/download.cgi) 
* IDE of Choice: 
    * [Eclipse download](https://www.eclipse.org/downloads/)
    * [Intellij download](http://www.jetbrains.com/idea/download/)
    * [Netbeans download](https://netbeans.org/downloads/) 


Background
----------

This maven build uses Nikita Volkov's fantastic [install-to-project-repo](https://github.com/nikita-volkov/install-to-project-repo) to install libraries to an in-project Maven repository.

Nikita's version requires that the jar file names be in a specific (eclipse) format, most of our jars to not conform to this,
fortunately a fork was made that ignores the naming constraint, the fork can be found [here](https://github.com/carchrae/install-to-project-repo/blob/master/install-to-project-repo.py). 


Initial Setup
---------------

Checkout this module, transition through the training is done with Git Branches, explained later.  


Download the version of the Resource Adapter your using from your PSM Server from the following URL 
       
       https://Your Servers Address:9998/client/resource-adapter/


Unzip this file and copy the jar files from the following directories into the maven directory [${basedir}/lib](./lib)

       resource-adapter-X.x/target/
       resource-adapter-X.x/target/lib/


Run this [install-to-project-repo.py](./install-to-project-repo.py) file, this will take the jars and install in a local directory repository and provide 
the maven dependencies to include in your pom.xml.  
These get written to a file here [${basedir}/repo/dependencies.txt](./repo/dependencies.txt) for your convenience. 

Copy the dependencies into your pom.xml in the dependencies section.  And confirm that everything builds ok by running 
the following command

       mvn clean install



Start Coding
---------------

With your IDE of choice, import this maven project.

