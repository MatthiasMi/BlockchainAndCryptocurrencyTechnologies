# Bitcoin and Cryptocurrency Technologies
This repository solves the programming assignments of the "Bitcoin and Cryptocurrency Technologies" online [course](https://www.coursera.org/learn/cryptocurrency).

The Princeton University course, freely available on the Internet, allows to practice playing around with the technical details behind popular blockchain technologies and helps exploring their current limitations by solving challenges and obtaining performance results of automated grading scripts.


## Assignment #1: [ScroogeCoin](./ScroogeCoin/Assignment1.pdf)
This implements the class in

 - `ScroogeCoin/TxHandler.java` (pass, graded 95/95),

to check the functionality for the fictitious cryptocurrency *ScroogeCoin*, where Scrooge - a trusted entity - issues each token and validates it by cryptographically signing it.


## Assignment #2: [ConsensusFromTrust](./ConsensusFromTrust/Assignment2.pdf)
This implements an alternative method for a distributed ledger to resist [sybil attacks](https://en.wikipedia.org/wiki/Sybil_attack), yet achieving consensus among a majority participating nodes in the network. The approach does not rely on proof-of-work (PoW) techniques and realizes distributed consensus as needed for modern cryptocurrencies.

This implements the class in
 
- `ConsensusFromTrust/CompliantNode.java` (pass, graded 89/100),

and tests
 
- `ConsensusSimulationScript.sh`,
 
to check the functionality for the fictitious network reaching consensus among a majority.