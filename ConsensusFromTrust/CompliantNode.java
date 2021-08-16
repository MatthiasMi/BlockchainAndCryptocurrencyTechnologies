import java.util.Set;

/* CompliantNode refers to a node that follows the rules (not malicious). */
public class CompliantNode implements Node {
    private final double p_gra, p_mal, p_dis;  // unused, TODO adapt strategy to network specifics
    private final int rounds;  // unused, TODO adapt strategy to (last) protocol round(s)
    private boolean[] followees;  // unused, TODO adapt strategy to non-compliant nodes

    private Set<Transaction> pendingTxs;

    public CompliantNode(double p_graph, double p_malicious, double p_txDistribution, int numRounds) {
        this.p_gra = p_graph;
        this.p_mal = p_malicious;
        this.p_dis = p_txDistribution;
        this.rounds = numRounds;
    }

    public void setFollowees(boolean[] followees) {
        this.followees = followees;
    }

    public void setPendingTransaction(Set<Transaction> pendingTransactions) {
        this.pendingTxs = pendingTransactions;
    }

    public Set<Transaction> sendToFollowers() {
        return this.pendingTxs;
    }

    public void receiveFromFollowees(Set<Candidate> candidates) {
        receiveFromFolloweesFor(candidates);
    }
    private void receiveFromFolloweesFor(Set<Candidate> candidates) {
        for (Candidate c : candidates) {
            if (!this.pendingTxs.contains(c.tx)) {
                this.pendingTxs.add(c.tx);
            }
        }
    }
}