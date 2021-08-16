import java.util.Arrays;
import java.util.Set;
import static java.util.stream.Collectors.toSet;

/* CompliantNode refers to a node that follows the rules (not malicious). */
public class CompliantNode implements Node {
  private final double p_gra, p_mal, p_dis; // unused, TODO adapt strategy to network specifics
  /*
   * Def.: A valid candidate transaction (`c.tx`) is an ID listed in simulator's
   * validTxs.
   * 
   * Since an instance of the compliant node class cannot know exactly its
   * content, the following heuristic decides if `c.tx` is valid:
   * (1) more than one node ('validators') proposed it.
   * 
   * Background: At least 2 validators, the "committee", are required to attest
   * the validity. This simple Proof of Stake (PoS) protocol is a consensus
   * mechanism for ScroogeCoin's ledger.
   */

  // The grader ran out of memory, hence trying optimizing the approach.
  private boolean[] followees; // used to implement strategy to untrust non-compliant nodes
  private boolean[] untrusted;

  private int round; // used to adapt strategy to (last) protocol round
  private Set<Transaction> consentTxs;

  private Set<Transaction> pendingTxs;

  public CompliantNode(double p_graph, double p_malicious, double p_txDistribution, int numRounds) {
    this.p_gra = p_graph;
    this.p_mal = p_malicious;
    this.p_dis = p_txDistribution;
    this.round = numRounds;
  }

  public void setFollowees(boolean[] followees) {
    this.followees = followees;
    untrusted = new boolean[followees.length];
    Arrays.fill(untrusted, Boolean.FALSE);
  }

  public void setPendingTransaction(Set<Transaction> pendingTransactions) {
    pendingTxs = pendingTransactions;
    consentTxs = pendingTransactions;
  }

  public Set<Transaction> sendToFollowers() {
    if (round < 1)
      return consentTxs;
    else
      return pendingTxs;
  }

  public void receiveFromFollowees(Set<Candidate> candidates) {
    if (round-- < 1)  // skip last round
      return;

    // (1) followees[i] is untrusted if it is the only node proposing it
    Set<Integer> senders = candidates.stream().map(c -> c.sender).collect(toSet());
    for (int i = 0; i < followees.length; i++) {
      if (followees[i] && !senders.contains(i))
        untrusted[i] = true;
    }

    // Handle (temporarily) trusted txs
    pendingTxs.clear();
    for (Candidate c : candidates) {
      if (!untrusted[c.sender] && !consentTxs.contains(c.tx)) {
        pendingTxs.add(c.tx);
        consentTxs.contains(c.tx);
      }
    }
  }
}