import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class TxHandler {

  protected UTXOPool utxoPool;

	/**
	 * Creates a public ledger whose current UTXOPool (collection of unspent
	 * transaction outputs) is {@code utxoPool}. This should make a copy of utxoPool
	 * by using the UTXOPool(UTXOPool uPool) constructor.
	 */
	public TxHandler(UTXOPool utxoPool) {
		this.utxoPool = new UTXOPool(utxoPool);
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
				.allMatch(utxo -> utxoPool.contains(utxo))) return false;

		// (2) returns iff the signature of 1 input is invalid
		boolean validInputIndexSignature;
		for (int index = 0; index < numInputs; index++) {
			validInputIndexSignature = validSignature(tx, index, inputs.get(index));
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
		if (inputs.stream()
			.mapToDouble(input -> utxoPool.getTxOutput(new UTXO(input.prevTxHash, input.outputIndex)).value)
			.sum() < outputs.stream().mapToDouble(output -> output.value).sum()) return false;
		
		// tx is valid otherwise
		return true;
	}

	// Clause 2 / 5
	protected boolean validSignature(Transaction tx, int index, Transaction.Input input) {
		UTXO utxo = new UTXO(input.prevTxHash, input.outputIndex);
		Transaction.Output txOut = utxoPool.getTxOutput(utxo);
		return Crypto.verifySignature(txOut.address, tx.getRawDataToSign(index), input.signature);
	}

	/**
	 * Handles each epoch by receiving an unordered array of proposed transactions,
	 * checking each transaction for correctness, returning a mutually valid array
	 * of accepted transactions, and updating the current UTXO pool as appropriate.
	 */
	public Transaction[] handleTxs(Transaction[] possibleTxs) {
		return Arrays.stream(possibleTxs).filter(tx -> isValidTx(tx))
		.peek(tx -> {removeTxFromUTXO(tx); addTxToUTXO(tx);}).toArray(Transaction[]::new);
	}

	protected void addTxToUTXO(Transaction tx) {
		List<Transaction.Output> outputs = tx.getOutputs();
		for (int index = 0; index < outputs.size(); index++) {
			Transaction.Output txOut = outputs.get(index);
			byte[] txHash = tx.getHash();
			UTXO utxo = new UTXO(txHash, index);
			utxoPool.addUTXO(utxo, txOut);
		}
	}

	protected void removeTxFromUTXO(Transaction tx) {
		List<Transaction.Input> inputs = tx.getInputs();
		for (int index = 0; index < inputs.size(); index++) {
			Transaction.Input input = inputs.get(index);
			byte[] txHash = input.prevTxHash;
			int outIndex = input.outputIndex;
			UTXO utxo = new UTXO(txHash, outIndex);
			utxoPool.removeUTXO(utxo);
		}
	}
	
	public UTXOPool getUTXOPool() {
        return utxoPool;
    }
}