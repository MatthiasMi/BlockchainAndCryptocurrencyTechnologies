# Bitcoin and Cryptocurrency Technologies
This repository solves the programming assignments of the "Bitcoin and Cryptocurrency Technologies" online [course](https://www.coursera.org/learn/cryptocurrency).

The Princeton University course, freely available on the Internet, allows to practice playing around with the technical details behind popular blockchain technologies and helps exploring their current limitations by solving challenges and obtaining performance results of automated grading scripts.


## Assignment #1: [ScroogeCoin](./ScroogeCoin/Assignment1.pdf)
This implements the class in

 - `ScroogeCoin/TxHandler.java` (pass, graded 95/95),
 - `ScroogeCoin/MaxFeeTxHandler.java` (pass, graded 5/5),

and test

 - `ScroogeCoin/MainPA1.java`,
 
to check the functionality for the fictitious cryptocurrency *ScroogeCoin*, where Scrooge - a trusted entity - issues each token and validates it by cryptographically signing it.


### Tests
For GNU/Linux the following should compile and run tests (fodr Windows possibly replace ':', by ';' when setting the classpath).
Compile with
`javac ScroogeCoin/*.java -Xlint:deprecation -cp ./bcprov-jdk15on-169.jar`
then run
`java -cp ./bcprov-jdk15on-169.jar:ScroogeCoin MainPA1`


## Assignment #2: [ConsensusFromTrust](./ConsensusFromTrust/Assignment2.pdf)
This implements an alternative method for a distributed ledger to resist [sybil attacks](https://en.wikipedia.org/wiki/Sybil_attack), yet achieving consensus among a majority participating nodes in the network. The approach does not rely on proof-of-work (PoW) techniques and realizes distributed consensus as needed for modern cryptocurrencies.

This implements the class in
 
- `ConsensusFromTrust/CompliantNode.java` (pass, graded 89/100),

and tests
 
- `ConsensusSimulationScript.sh`,
- `ConsensusFromTrust/MainPA2.java`,
 
to check the functionality for the fictitious network reaching consensus among a majority (reproducibly).

To ease offline testing 
`ConsensusFromTrust/MainPA2.java` provides the functionality to automatically test a specifiable amount of instantiations.


### Simulation
Compile

`javac -cp ConsensusFromTrust ConsensusFromTrust/Simulation.java`

then run the simulation, with the arguments in the specified ranges, e.g.,

`java -cp ConsensusFromTrust Simulation .1 .15 .05 10`

or

`java ConsensusFromTrust/MainPA2`

to run specifics of all 3x3x3x2 = 54 combinations or invoke the accompanying `bash` script to specify more using, e.g.,

`chmod +x ConsensusSimulationScript.sh && ./ConsensusSimulationScript.sh`

to run up to `t=1,2,...54` tests.


### Tests
Compile all with

`javac ConsensusFromTrust/*.java -cp ./bcprov-jdk15on-169.jar`

or MainPA2 individually

`javac -cp ./bcprov-jdk15on-169.jar:ConsensusFromTrust ConsensusFromTrust/MainPA2.java`

then for testing with (default parametrization) execute

`java -cp ./bcprov-jdk15on-169.jar:ConsensusFromTrust MainPA2`

or for (reproducible) test results specifying each parameter (including the randomness seed, e.g., 42) like this

`java -cp ./bcprov-jdk15on-169.jar:ConsensusFromTrust MainPA2 0.1 0.15 0.1 10 22 111 42`

to check the functionality for the fictitious cryptocurrency *ScroogeCoin*, where Scrooge - a trusted entity - issues each token and validates it by cryptographically signing it.


## Assignment #2: [ConsensusFromTrust](./ConsensusFromTrust/Assignment2.pdf)
This implements an alternative method for a distributed ledger to resist [sybil attacks](https://en.wikipedia.org/wiki/Sybil_attack), yet achieving consensus among a majority participating nodes in the network. The approach does not rely on proof-of-work (PoW) techniques and realizes distributed consensus as needed for modern cryptocurrencies.

This implements the class in

- `ConsensusFromTrust/CompliantNode.java` (pass, graded 89/100),

and tests
 
- `ConsensusSimulationScript.sh`,
- `ConsensusFromTrust/MainPA2.java`,
 
to check the functionality for the fictitious network reaching consensus among a majority (reproducibly).

To ease offline testing 
`ConsensusFromTrust/MainPA2.java` provides the functionality to automatically test a specifiable amount of instantiations.


### Simulation
Compile

`javac -cp ConsensusFromTrust ConsensusFromTrust/Simulation.java`

then run the simulation, with the arguments in the specified ranges, e.g.,

`java -cp ConsensusFromTrust Simulation .1 .15 .05 10`

or

`java ConsensusFromTrust/MainPA2`

to run specifics of all 3x3x3x2 = 54 combinations or invoke the accompanying `bash` script to specify more using, e.g.,

`chmod +x ConsensusSimulationScript.sh && ./ConsensusSimulationScript.sh`

to run up to `t=1,2,...54` tests.


### Tests
Compile all with

`javac ConsensusFromTrust/*.java -cp ./bcprov-jdk15on-169.jar`

or MainPA2 individually

`javac -cp ./bcprov-jdk15on-169.jar:ConsensusFromTrust ConsensusFromTrust/MainPA2.java`

then for testing with (default parametrization) execute

`java -cp ./bcprov-jdk15on-169.jar:ConsensusFromTrust MainPA2`

or for (reproducible) test results specifying each parameter (including the randomness seed, e.g., 42) like this

`java -cp ./bcprov-jdk15on-169.jar:ConsensusFromTrust MainPA2 0.1 0.15 0.1 10 22 111 42`