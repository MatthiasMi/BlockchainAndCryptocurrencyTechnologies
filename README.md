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


#### Grader Output
Tests for this assignment involve your submitted miner competing with a number of different types of malicious miners:

	Running test with parameters: numNodes = 100, p_graph = 0.1, p_malicious = 0.3, p_txDistribution = 0.01, numRounds = 10
	On average 65 out of 72 of nodes reach consensus
	
	Running test with parameters: numNodes = 100, p_graph = 0.1, p_malicious = 0.3, p_txDistribution = 0.05, numRounds = 10
	On average 72 out of 72 of nodes reach consensus
	
	Running test with parameters: numNodes = 100, p_graph = 0.1, p_malicious = 0.45, p_txDistribution = 0.01, numRounds = 10
	On average 47 out of 58 of nodes reach consensus
	
	Running test with parameters: numNodes = 100, p_graph = 0.1, p_malicious = 0.45, p_txDistribution = 0.05, numRounds = 10
	On average 52 out of 58 of nodes reach consensus
	
	Running test with parameters: numNodes = 100, p_graph = 0.2, p_malicious = 0.3, p_txDistribution = 0.01, numRounds = 10
	On average 63 out of 76 of nodes reach consensus
	
	Running test with parameters: numNodes = 100, p_graph = 0.2, p_malicious = 0.3, p_txDistribution = 0.05, numRounds = 10
	On average 71 out of 76 of nodes reach consensus
	
	Running test with parameters: numNodes = 100, p_graph = 0.2, p_malicious = 0.45, p_txDistribution = 0.01, numRounds = 10
	On average 44 out of 54 of nodes reach consensus
	
	Running test with parameters: numNodes = 100, p_graph = 0.2, p_malicious = 0.45, p_txDistribution = 0.05, numRounds = 10
	On average 48 out of 54 of nodes reach consensus
	
	


## Assignment #3: [BlockChain](./BlockChain/Assignment3.pdf)
This builds on top of Assignment #1 as it adds valid nodes to a blockchain achieving distributed consensus by handling incoming transactions and maintaining an updated data structure trusted by participating parties.

This implements the class in

- `BlockChain/BlockChain.java` (pass, graded 81/100),

and test
 
- `BlockChain/MainPA3.java`,
 
to check the functionality for the fictitious network reaching consensus among a majority (reproducibly).

To ease offline testing 
`BlockChain/MainPA3.java` provides the functionality to automatically test a specifiable amount of instantiations.


### Tests
Compile all with

`javac BlockChain/*.java -cp ./bcprov-jdk15on-169.jar`

or MainPA3 individually

`javac -cp ./bcprov-jdk15on-169.jar:BlockChain ConsensusFromTrust/MainPA3.java`

then for testing with (default parametrization) execute

`java -cp ./bcprov-jdk15on-169.jar:BlockChain MainPA3`