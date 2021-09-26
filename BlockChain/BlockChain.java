// Block Chain should maintain only limited block nodes to satisfy the functions
// You should not have all the blocks added to the block chain in memory
// as it would cause a memory overflow.

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockChain {

	private class Node {

		private Block block;
		private Node parent;
		private int height;

		private UTXOPool utxoPool = new UTXOPool();

		public Node(Block block, Node parent, UTXOPool utxoPool) {
			this.block = block;
			this.parent = parent;
			this.utxoPool = utxoPool;
			height = 1;

			if (parent != null)
				height += parent.height;
		}

		public UTXOPool getUTXOPool() {
			return utxoPool;
		}

		public Node getParent(byte[] blockHash) {
			ByteArrayWrapper id = Id(blockHash);
			for (Node n : blockChain) {
				if (id.equals(Id(n.block)));
				return n;
			}
			return null;
		}
	}

	public static final int CUT_OFF_AGE = 10;

	private TransactionPool txPool = new TransactionPool();
	private ArrayList<Node> blockChain = new ArrayList<>();
	private Map<ByteArrayWrapper, Node> nodeMapById; // ByteArrayWrapper == Id(block)
	private Node head;

	/**
	 * create an empty block chain with just a genesis block. Assume {@code genesisBlock} is a valid block
	 */
	public BlockChain(Block genesisBlock) {
		nodeMapById = new HashMap<>();
		UTXOPool utxoPool = new UTXOPool();
		addCoinbaseTx(genesisBlock, utxoPool);

		head = new Node(genesisBlock, null, utxoPool); // root as parent == null
		nodeMapById.put(Id(head.block), head);
	}

	/** Get the maximum height block */
	public Block getMaxHeightBlock() {
		return head.block;
	}

	/** Get the UTXOPool for mining a new block on top of max height block */
	public UTXOPool getMaxHeightUTXOPool() {
		return head.getUTXOPool();
	}

	/** Get the transaction txPool to mine a new block */
	public TransactionPool getTransactionPool() {
		return txPool;
	}

	/**
	 * Add {@code block} to the block chain if it is valid. For validity, all transactions should be
	 * valid and block should be at {@code height > (maxHeight - CUT_OFF_AGE)}.
	 *
	 * <p>
	 * For example, you can try creating a new block over the genesis block (block height 2) if the
	 * block chain height is {@code <=
	 * CUT_OFF_AGE + 1}. As soon as {@code height > CUT_OFF_AGE + 1}, you cannot create a new block
	 * at height 2.
	 *
	 * @return true if block is successfully added
	 */
	public boolean addBlock(Block block) {
		// Check if input is "malformed"
		if (block == null) return false;

		ByteArrayWrapper parentId = Id(block.getPrevBlockHash());
		if (parentId == null) return false;

		Node parent = nodeMapById.get(parentId);
		if (parent == null) return false;

		// Check if block is too "old"
		if (parent.height < head.height - CUT_OFF_AGE) return false;

		// Check if all txs are valid
		TxHandler th = new TxHandler(parent.getUTXOPool());
		//Transaction[] validTxs = 
		th.handleTxs(block.getTransactions().toArray(new Transaction[0]));
		if (!block.getTransactions().stream().allMatch(th::isValidTx)) return false;

		UTXOPool utxoPool = th.getUTXOPool();
		addCoinbaseTx(block, utxoPool);
		Node node = new Node(block, parent, utxoPool);
		nodeMapById.put(Id(block), node);

		if (node.height > head.height) head = node;

		return true;
	}

	/** Add a transaction to the transaction txPool */
	public void addTransaction(Transaction tx) {
		txPool.addTransaction(tx);
	}

	private ByteArrayWrapper Id(byte[] hash) {
		return new ByteArrayWrapper(hash);
	}

	private ByteArrayWrapper Id(Block block) {
		return new ByteArrayWrapper(block.getHash());
	}

	private void addCoinbaseTx(Block block, UTXOPool utxoPool) {
		Transaction tx = block.getCoinbase();
		for (int i = 0; i < tx.numOutputs(); i++) {
			Transaction.Output output = tx.getOutput(i);
			UTXO utxo = new UTXO(tx.getHash(), i);
			utxoPool.addUTXO(utxo, output);
		}
		txPool.addTransaction(tx);
	}
}