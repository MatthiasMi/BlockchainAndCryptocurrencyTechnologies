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
For GNU/Linux the following should compile and run tests (for Windows possibly replace ':', by ';' when setting the classpath).
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


#### Details
`ConsensusFromTrust/Simulation.java` defines the following API, and tests random graph instatiations of size `numNodes=100` with `numTx = 500` according to the parametrization;

pairwise connectivity probability `p_graph \in {.1, .2, .3}`,
probability of a node to act malicious or rather non-conformal `p_malicious \in {.15, .30, .45}`,
probability that each of the (initially assumed valid) transactions will be broadcast by a node `p_txDistribution \in {{.01, .05, .10}`, and 
number of rounds before necessarily reaching an agreement `numRounds \in {10, 20}`.


### Tests
Compile all with

`javac ConsensusFromTrust/*.java -cp ./bcprov-jdk15on-169.jar`

or MainPA2 individually

`javac -cp ./bcprov-jdk15on-169.jar:ConsensusFromTrust ConsensusFromTrust/MainPA2.java`

then for testing with (default parametrization) execute

`java -cp ./bcprov-jdk15on-169.jar:ConsensusFromTrust MainPA2`

or for (reproducible) test results specifying each parameter (including the randomness seed, e.g., 42) like this

`java -cp ./bcprov-jdk15on-169.jar:ConsensusFromTrust MainPA2 0.1 0.15 0.1 10 22 111 42`


#### Details
As the performance of the (online) test instances are measured based on several metrics the code takes into account (in this order):

1. Maximizing set of nodes to reach consensus (true iff these nodes output the same transaction list), 
2. Maximizing the set itself that consensus is reached on,
of transactions as large as possible.
3. Execution time (as grading script might time out).

A compliant node alone cannot know the network topology and best tries to work in an average case (ommiting low probability cases), adapting only slightly as for if to include transactions into the network of consensus.
For simplicitly, here we can assume that all `Transactions` the `CompliantNode` class sees are syntactically and semantically valid.
The `Simulation` class will only send valid transactions over the network (both initially and between rounds), and only this class has the ability to create valid transactions.



