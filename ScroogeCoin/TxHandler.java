import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TxHandler {
    private UTXOPool utxoPool;

    /**
     * Creates a public ledger whose current UTXOPool (collection of unspent
     * transaction outputs) is {@code utxoPool}. This should make a copy of utxoPool
     * by using the UTXOPool(UTXOPool uPool) constructor.
     */
    public TxHandler(UTXOPool utxoPool) {
        this.utxoPool = new UTXOPool(utxoPool);
    }

    /**
     * @return true if: (1) all outputs claimed by {@code tx} are in the current
     *         UTXO pool, (2) the signatures on each input of {@code tx} are valid,
     *         (3) no UTXO is claimed multiple times by {@code tx}, (4) all of
     *         {@code tx}s output values are non-negative, and (5) the sum of
     *         {@code tx}s input values is greater than or equal to the sum of its
     *         output values; and false otherwise.
     */
    public boolean isValidTx(Transaction tx) {
        boolean isValid = true;
        Set<UTXO> claimedUTXO = new HashSet<UTXO>();
        double fee = 0;
        List<Transaction.Input> inputs = tx.getInputs();

        for (int index = 0; index < inputs.size(); index++) {
            Transaction.Input input = inputs.get(index);

            isValid = isValid && outputInUTXO(input);
            isValid = isValid && validSignature(tx, index, input);
            isValid = isValid && !doubleSpendAttempt(claimedUTXO, input);

            UTXO utxo = new UTXO(input.prevTxHash, input.outputIndex);
            Transaction.Output txOut = utxoPool.getTxOutput(utxo);
            if (txOut != null) {
                fee += txOut.value;
            }
        }

        List<Transaction.Output> outputs = tx.getOutputs();
        for (int index = 0; index < outputs.size(); index++) {
            Transaction.Output output = outputs.get(index);
            isValid = isValid && !negativeOutput(output);
            fee -= output.value;
        }
        isValid = isValid && !negativeTxFee(fee);

        return isValid;
    }

    // Clause 5 / 5
    private boolean negativeTxFee(double fee) {
        return (fee < 0);
    }

    // Clause 4 / 5
    private boolean negativeOutput(Transaction.Output output) {
        return (output.value < 0);
    }

    // Clause 3 / 5
    private boolean doubleSpendAttempt(Set<UTXO> claimedUTXO, Transaction.Input input) {
        return !claimedUTXO.add(new UTXO(input.prevTxHash, input.outputIndex));
    }

    // Clause 2 / 5
    private boolean validSignature(Transaction tx, int index, Transaction.Input input) {
        UTXO utxo = new UTXO(input.prevTxHash, input.outputIndex);
        Transaction.Output txOut = utxoPool.getTxOutput(utxo);
        PublicKey pk = txOut.address;
        return Crypto.verifySignature(pk, tx.getRawDataToSign(index), input.signature);
    }

    // Clause 1 / 5
    private boolean outputInUTXO(Transaction.Input input) {
        return utxoPool.contains(new UTXO(input.prevTxHash, input.outputIndex));
    }

    /**
     * Handles each epoch by receiving an unordered array of proposed transactions,
     * checking each transaction for correctness, returning a mutually valid array
     * of accepted transactions, and updating the current UTXO pool as appropriate.
     */
    public Transaction[] handleTxs(Transaction[] possibleTxs) {
        List<Transaction> validTxs = new ArrayList<Transaction>();
        for (Transaction tx : possibleTxs) {
            if (isValidTx(tx)) {
                validTxs.add(tx);
                removeTxFromUTXO(tx);
                addTxToUTXO(tx);
            }
        }

        Transaction[] mutuallyValidTxs = new Transaction[validTxs.size()];
        validTxs.toArray(mutuallyValidTxs);
        return mutuallyValidTxs;
    }

    private void addTxToUTXO(Transaction tx) {
        List<Transaction.Output> outputs = tx.getOutputs();
        for (int index = 0; index < outputs.size(); index++) {
            Transaction.Output txOut = outputs.get(index);
            byte[] txHash = tx.getHash();
            UTXO utxo = new UTXO(txHash, index);
            utxoPool.addUTXO(utxo, txOut);
        }
    }

    private void removeTxFromUTXO(Transaction tx) {
        List<Transaction.Input> inputs = tx.getInputs();
        for (int index = 0; index < inputs.size(); index++) {
            Transaction.Input input = inputs.get(index);
            byte[] txHash = input.prevTxHash;
            int outIndex = input.outputIndex;
            UTXO utxo = new UTXO(txHash, outIndex);
            utxoPool.removeUTXO(utxo);
        }
    }
}