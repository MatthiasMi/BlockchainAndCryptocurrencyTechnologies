#! /bin/bash

# ConsensusSimulationScript.sh
# tests `compliantNode` class by running 3x3x3x2 = 54 instantiations of
# `Simulation` class that expects four command line arguments in these ranges:
# `$p={graph} \in {.1, .2, .3}, p={malicious} \in {.15, .30, .45},
# p={txDistribution} \in {.01, .05, .10}$`, and `$\tt{numRounds} \in {10, 20}$`.
#
# The user can set maximum number of tests to run (defaults to `t =11`).

if [ $# -gt 2 ]; then
    echo "Usage: ./ConsensusSimulationScript.sh [numNodes = 100] [numTxs = 500]";
    exit 1
elif [ $# -gt 1 ]; then numTxs=$2;  numNodes=$1
elif [ $# -gt 0 ]; then numTxs=500; numNodes=$1;
else                    numTxs=500; numNodes=100;
fi

CMD="java -cp ./bcprov-jdk15on-169.jar:ConsensusFromTrust/src/ Simulation"

echo Enter number of tests to run, t \in {1, 2, ..., 54}, within 3 [sec]. t =
read -t 3 t

if [ -n ${t} -a "${t}" -gt 0 -a "${t}" -lt 55 ]; then
    echo "t = ${t} is an integer \in {1, 2, ..., 54}."
else
    echo "Invalid input: Running t=11 simulation..."
    t=11
fi

c=0
for i in .1 .2 .3; do
    for j in .15 .3 .45; do
        for k in 0.01 .04 .1; do
            for r in 10 20; do
                ((c++))
                if [ "${c}" -gt "${t}" ]; then exit 1; fi
                echo ======================================================
                echo Run [${c} / ${t}]: Simulation_{N=${numNodes}, T=${numTxs}} ${i} ${j} ${k} ${r}
                echo ======================================================
                ${CMD} ${i} ${j} ${k} ${r}
            done
        done
    done
done