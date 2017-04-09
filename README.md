Groestlcoin Lite
====

Groestlcoin Lite is a wallet based on the groestlcoinj.

Getting Started
---------------

You'll need to download and compile supporting libraries and Groestlcoin Lite in order to run Groestlcoin Lite.

You'll need the following installed:

* Git
* Java 8 SDK
* Apache Maven

Then use Git to clone the repositories for, and Maven to install:

* https://github.com/groestlcoin/groestlcoinj

To install the libraries, run "mvn install -DskipTests" within the cloned
repository. In both cases the master (default) branch must be used, as Groestlcoin Lite requires
the latest code.

Once that is done, clone the Groestlcoin Lite repository and run "mvn package" to build it.
This will produce an JAR file "target/groestlcoin-lite-XXXXXXXXX.jar", double click
it to run.
