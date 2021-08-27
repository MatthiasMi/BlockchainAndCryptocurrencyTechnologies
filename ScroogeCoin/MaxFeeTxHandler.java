import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class MaxFeeTxHandler extends TxHandler {

  private HashMap<Transaction, Double> validTxToFee = new HashMap<Transaction, Double>();

	public MaxFeeTxHandler(UTXOPool utxoPool) {
		super(utxoPool);
	}

    /**
     * @return `true` iff:
     * (1) all outputs claimed by {@code tx} are in the current UTXO pool,
     * (2) the signatures on each input of {@code tx} are valid,
     * (3) no UTXO is claimed multiple times by {@code tx},
     * (4) all of {@code tx}s output values are non-negative, and
     * (5) the sum of {@code tx}s input values not less than the sum of its output values;
     * and early aborts with `false` otherwise.
     */
    public boolean isValidTx(Transaction tx) {
        // Using stateless, non-interfering stream operations (that do not modify source)
        List<Transaction.Input> inputs = tx.getInputs();
        List<Transaction.Output> outputs = tx.getOutputs();
        int numInputs = inputs.size();

        // (1) returns iff not all claimed utxo are in the current utxoPool
        if (!inputs.stream().map(input -> new UTXO(input.prevTxHash, input.outputIndex))
                .allMatch(utxo -> super.utxoPool.contains(utxo))) return false;

        // (2) returns iff the signature of 1 input is invalid
        boolean validInputIndexSignature;
        for (int index = 0; index < numInputs; index++) {
            validInputIndexSignature = super.validSignature(tx, index, inputs.get(index));
            if (!validInputIndexSignature) return validInputIndexSignature;
        }

        // (3) returns iff an UTXO is claimed multiple times
        if (inputs.stream()
            .map(input -> new UTXO(input.prevTxHash, input.outputIndex))
            .map(utxo -> utxo.hashCode())
            .collect(Collectors.toSet()).size() != numInputs) return false;

        // (4) returns iff an output value is negative
        if (outputs.stream().anyMatch(output -> output.value < 0))
            return false;

        // (5) returns iff input value sum is less than the output value sum
        double fee = inputs.stream()
        .mapToDouble(input -> super.utxoPool.getTxOutput(new UTXO(input.prevTxHash, input.outputIndex)).value)
        .sum();
        fee -= outputs.stream().mapToDouble(output ->
                {if (output == null) return 0.0; //else
                  return output.value;
                }).sum();
        
        if ( fee < 0 )
          return false;
        else
          validTxToFee.put(tx, fee);

        // tx is valid otherwise
        return true;
    }

    /**
     * Handles each epoch by receiving an unordered array of proposed transactions,
     * checking each transaction for correctness, returning a mutually valid array
     * of accepted transactions, and updating the current UTXO pool as appropriate.
     */
    public Transaction[] handleTxs(Transaction[] possibleTxs) {
      // Filters validTxs and stores their fees, and updates UTXO
      Arrays.stream(possibleTxs).filter(tx -> isValidTx(tx))
      .peek(tx -> {super.removeTxFromUTXO(tx); super.addTxToUTXO(tx);});
      //System.out.println("|validTxToFee| = " + validTxToFee.size());

      // Compares fees of txs (stored in validTxToFee) and sorts in decreasing order
      List<Transaction> txsSortedByFee = new ArrayList<Transaction>( validTxToFee.keySet() );
      txsSortedByFee.sort(new Metric( txsSortedByFee ).reversed());
      return txsSortedByFee.toArray(Transaction[]::new);
    }

    private class Metric implements Comparator<Transaction> {
      private List<Transaction> txsSortedByComparisonMetric;

      public Metric(List<Transaction> txsSortedByFee) {
          this.txsSortedByComparisonMetric = txsSortedByFee;
      }

      public int compare(Transaction tx1, Transaction tx2) {
          return validTxToFee.get(tx1).compareTo(validTxToFee.get(tx2));
      }
    }
}