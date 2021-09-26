import java.util.ArrayList;

public class BlockChain {
    public static final int CUT_OFF_AGE = 10;
    private TransactionPool txPool;
    private Block root;
    private ArrayList<Block> blockChain;

    /**
     * create an empty block chain with just a genesis block. Assume {@code genesisBlock} is a valid
     * block
     */
    public BlockChain(Block genesisBlock) {
        root = genesisBlock;
        blockChain = new ArrayList<>();
        txPool = new TransactionPool();
    }

    /**
     * Get the maximum height block
     */
    public Block getMaxHeightBlock() {
        int lastIndex = blockChain.size() - 1;
        if ( lastIndex < 0) return root;
        return blockChain.get(lastIndex);
    }

    /**
     * Get the UTXOPool for mining a new block on top of max height block
     */
    public UTXOPool getMaxHeightUTXOPool() {
        UTXOPool utxoPool = new UTXOPool();
        Block head = getMaxHeightBlock();
        ArrayList<Transaction> txs = head.getTransactions();
        txs.add(head.getCoinbase());
        txs.forEach(tx -> { for (int i = 0; i < tx.getOutputs().size(); i++)
        	utxoPool.addUTXO(new UTXO(tx.getHash(), i), tx.getOutput(i)); });
        return utxoPool;
    }

    /**
     * Get the transaction pool to mine a new block
     */
    public TransactionPool getTransactionPool() {
        return txPool;
    }

    /**
     * Add {@code block} to the block chain if it is valid. For validity, all transactions should be
     * valid and block should be at {@code height > (maxHeight - CUT_OFF_AGE)}.
     * <p>
     * <p>
     * For example, you can try creating a new block over the genesis block (block height 2) if the
     * block chain height is {@code <=
     * CUT_OFF_AGE + 1}. As soon as {@code height > CUT_OFF_AGE + 1}, you cannot create a new block
     * at height 2.
     *
     * @return true if block is successfully added
     */
    public boolean addBlock(Block block) {

        byte[] prevBlockHash = block.getPrevBlockHash();
        if (prevBlockHash == null) return false;

        // Check if all txs are valid
        TxHandler th = new TxHandler(getMaxHeightUTXOPool());
        ArrayList<Transaction> txs = block.getTransactions();
        if (!txs.stream().allMatch(th::isValidTx)) return false;

        boolean check = root.getHash() == prevBlockHash;
		check|= blockChain.stream().anyMatch(b -> b.getHash() == prevBlockHash);
        if ( check && blockChain.size() < CUT_OFF_AGE ) {
            blockChain.add(block);
            
                    UTXOPool utxoPool = getMaxHeightUTXOPool();
                    Transaction txc = block.getCoinbase();
                    //update utxo transaction coinbase
        for (int i = 0; i < txc.numOutputs(); i++) 
            utxoPool.addUTXO(new UTXO(txc.getHash(),i),txc.getOutput(i));
        

        //remove trans pool
        for (Transaction tx: txs)
            txPool.removeTransaction(tx.getHash());
        
            return true;
        }
        return false;
    }

    /**
     * Add a transaction to the transaction pool
     */
    public void addTransaction(Transaction tx) {
        txPool.addTransaction(tx);
    }
}
