import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class MainPA2 {

  public static void main(String[] args) {
    // Run: java -cp ./bcprov-jdk15on-169.jar:ConsensusFromTrust MainPA2 0.1 0.15 0.1 10 22 111 42

    // There are four required command line arguments: p_graph (.1, .2, .3),
    // p_malicious (.15, .30, .45), p_txDistribution (.01, .05, .10),
    // and numRounds (10, 20). You should try to test your CompliantNode
    // code for all 3x3x3x2 = 54 combinations.
    double p_graph, p_malicious, p_txDistribution;
    int numRounds, numNodes, numTx, randomSeed;

    // Default parameter set
    randomSeed = 42;
    numTx = 555;
    numNodes = 55;
    numRounds = 10;
    p_txDistribution = .1;
    p_malicious = .15;
    p_graph = .1;

    // Parsing, else fall-back to defaults
    int numArgs = args.length;
    try {
      if (numArgs == 7) {
        p_graph = Double.parseDouble(args[0]); // parameter for random graph: prob. that an edge will exist
        p_malicious = Double.parseDouble(args[1]); // prob. that a node will be set to be malicious
        p_txDistribution = Double.parseDouble(args[2]); // prob. of assigning an initial transaction to each node
        numRounds = Integer.parseInt(args[3]); // number of simulation rounds your nodes will run for
        numNodes = Integer.parseInt(args[4]);
        numTx = Integer.parseInt(args[5]);
        randomSeed = Integer.parseInt(args[6]);
      } else if (numArgs == 6) {
        p_graph = Double.parseDouble(args[0]); // parameter for random graph: prob. that an edge will exist
        p_malicious = Double.parseDouble(args[1]); // prob. that a node will be set to be malicious
        p_txDistribution = Double.parseDouble(args[2]); // prob. of assigning an initial transaction to each node
        numRounds = Integer.parseInt(args[3]); // number of simulation rounds your nodes will run for
        numNodes = Integer.parseInt(args[4]);
        numTx = Integer.parseInt(args[5]);
      } else if (numArgs == 5) {
        p_graph = Double.parseDouble(args[0]); // parameter for random graph: prob. that an edge will exist
        p_malicious = Double.parseDouble(args[1]); // prob. that a node will be set to be malicious
        p_txDistribution = Double.parseDouble(args[2]); // prob. of assigning an initial transaction to each node
        numRounds = Integer.parseInt(args[3]); // number of simulation rounds your nodes will run for
        numNodes = Integer.parseInt(args[4]);
      } else if (numArgs == 4) {
        p_graph = Double.parseDouble(args[0]); // parameter for random graph: prob. that an edge will exist
        p_malicious = Double.parseDouble(args[1]); // prob. that a node will be set to be malicious
        p_txDistribution = Double.parseDouble(args[2]); // prob. of assigning an initial transaction to each node
        numRounds = Integer.parseInt(args[3]);
      } else {} // Use default set
    } catch (Exception e) {
      System.out.println("Exception => " + e.getMessage() + "\nusing defaults...");
    }

    // Input Validation
    if (p_graph != .1) if (p_graph != .2 && p_graph != .3) p_graph = .1;
    if (p_malicious != .15) if (p_malicious != .30 && p_malicious != .45) p_malicious = .15;
    if (p_txDistribution != .1) if (p_txDistribution != .01 && p_malicious != .05) p_txDistribution = .1;
    if (numRounds != 10) if (numRounds != 20) numRounds = 10;
    if (numTx < 2 || numTx > 999) numTx = 555;
    if (numNodes < 2 || numNodes > 111) numNodes = 55;

    // Preparing required randomn numbers at once (fixing seed for reproducibility)
    Random random = new Random();
    random.setSeed(randomSeed);

    // default is false for each boolean
    boolean[] rand_p_malicious = new boolean[numNodes];
    boolean[] rand_p_dist = new boolean[numNodes * numTx];
    boolean[] rand_p_graph = new boolean[numNodes * numNodes];
    Integer[] validTxIds = new Integer[numTx];

    for (int i = 0; i < numTx; i++) validTxIds[i] = random.nextInt();

    for (int i = 0; i < numNodes; i++) {
      if (random.nextDouble() < p_malicious) rand_p_malicious[i] = true;

      // initialize a set of valid Transactions with random ids
      for (int j = 0; j < numTx; j++) if (random.nextDouble() < p_txDistribution)
        rand_p_dist[i * numTx + j] = true;

      for (int k = 0; k < numNodes; k++) if (random.nextDouble() < p_graph)
      rand_p_graph[i * numNodes + k] = true;
    }

    System.out.printf("\nStarting Simulation with parameters (p_graph, p_malicious, p_txDistribution, numRounds, numNodes, numTx, randomSeed = ");
    System.out.printf(String.format("%d) : %.1f %.2f %.2f %2d %d %d", randomSeed, p_graph, p_malicious, p_txDistribution, numRounds, numNodes, numTx));
    System.out.printf(String.format("According to the parametrization, the random graph G(V, E) has |V| = %d nodes and |E| = %d edges.",numNodes, numNodes * numTx));

    // pick which nodes are malicious and which are compliant
    Node[] nodes = new Node[numNodes];
    for (int i = 0; i < numNodes; i++) {
      if (rand_p_malicious[i])
      // When you are ready to try testing with malicious nodes, replace the
      // instantiation below with an instantiation of a MaliciousNode
        nodes[i] = new CompliantNode(p_graph,p_malicious,p_txDistribution,numRounds);
      else
        nodes[i] = new CompliantNode(p_graph, p_malicious, p_txDistribution, numRounds);
    }

    // initialize random follow graph
    boolean[][] followees = new boolean[numNodes][numNodes]; // followees[i][j] is true iff i follows j
    for (int i = 0; i < numNodes; i++) {
      for (int j = 0; j < numNodes; j++) {
        if (i == j) continue;
        if (rand_p_graph[i * numNodes + j]) { // p_graph is .1, .2, or .3
          followees[i][j] = true;
        }
      }
    }

    // notify all nodes of their followees
    for (int i = 0; i < numNodes; i++) nodes[i].setFollowees(followees[i]);

    // distribute the Transactions throughout the nodes, to initialize
    // the starting state of Transactions each node has heard. The distribution
    // is random with probability p_txDistribution for each Transaction-Node pair.
    for (int i = 0; i < numNodes; i++) {
      HashSet<Transaction> pendingTransactions = new HashSet<Transaction>();
      for (int j = 0; j < numTx; j++) {
        if (rand_p_dist[i * numTx + j]) { // p_txDistribution is .01, .05, or .10.
          pendingTransactions.add(new Transaction(validTxIds[j]));
        }
      }
      nodes[i].setPendingTransaction(pendingTransactions);
    }

    // Simulate for numRounds times
    for (int round = 0; round < numRounds; round++) { // numRounds is either 10 or 20
      // gather all the proposals into a map. The key is the index of the node receiving
      // proposals. The value is an ArrayList containing 1x2 Integer arrays. The first
      // element of each array is the id of the transaction being proposed and the second
      // element is the index # of the node proposing the transaction.
      HashMap<Integer, Set<Candidate>> allProposals = new HashMap<>();
      Set<Integer> validTxIdSet = Arrays.stream(validTxIds).collect(Collectors.toSet());

      for (int i = 0; i < numNodes; i++) {
        Set<Transaction> proposals = nodes[i].sendToFollowers();
        for (Transaction tx : proposals) {
          if (!validTxIdSet.contains(tx.id)) continue; // ensure that each tx is actually valid

          for (int j = 0; j < numNodes; j++) {
            if (!followees[j][i]) continue; // tx only matters if j follows i

            if (!allProposals.containsKey(j)) {
              Set<Candidate> candidates = new HashSet<>();
              allProposals.put(j, candidates);
            }
            Candidate candidate = new Candidate(tx, i);
            allProposals.get(j).add(candidate);
          }
        }
      }

      // Distribute the Proposals to their intended recipients as Candidates
      for (int i = 0; i < numNodes; i++) {
        if (allProposals.containsKey(i)) nodes[i].receiveFromFollowees(allProposals.get(i));
      }
    }

    // print results
    System.out.printf("\n\nNumber of transaction ids that nodes believe achieving consensus on:\n");
    String st = "";
    Set<Integer> ids;
    Set<Transaction> transactions = nodes[0].sendToFollowers(); // can assume Node 0 exists
    Set<Integer> consensusIds = new HashSet<Integer>(); // cannot have more elements than Node 0
    for (Transaction tx : transactions) consensusIds.add(tx.id);

    for (int i = 1; i < numNodes; i++) {
      ids = new HashSet<Integer>();
      transactions = nodes[i].sendToFollowers();
      st += "Node " + i + ": " + transactions.size() + ", ";
      for (Transaction tx : transactions) ids.add(tx.id);
      consensusIds.retainAll(ids);
    }
    System.out.printf(st.substring(0, st.length() - 2));
    System.out.printf("\n\nSimulation finished, consensus set of size ");
    System.out.printf(consensusIds.size() +" reached across all " + nodes.length +" nodes:\n" +consensusIds+"\n\n");
  }
}