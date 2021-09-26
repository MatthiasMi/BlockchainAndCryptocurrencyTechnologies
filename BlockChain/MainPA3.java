import org.bouncycastle.jce.provider.BouncyCastleProvider;
import java.security.*;


public class MainPA3 {

  public static void main(String[] args)   throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {


    test1();
	test2();
    /*
    https://d28rh4a8wq0iu5.cloudfront.net/bitcointech/readings/princeton_bitcoin_book.pdf
        System.out.println("Start mining...");
    fix blockchain..., upload solution, write&fix readme
    // @SuppressWarnings( "deprecation" ) -> Block.java, tx.java
    format, only valid exceptions
    overhaul main+tests
        System.out.println("Process a block with a single valid transaction");
        add time!
    */
  }

  public static void test1()    throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {


    Security.addProvider(new BouncyCastleProvider());
    KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
    SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
    keyGen.initialize(1024, random);
    KeyPair pair = keyGen.generateKeyPair();

	PublicKey pk_Scrooge = pair.getPublic();
    Block genesisBlock = new Block(null, pk_Scrooge);
    genesisBlock.finalize();

    BlockChain blockChain = new BlockChain(genesisBlock);
    BlockHandler blockHandler = new BlockHandler(blockChain);

    KeyPair pair1 = keyGen.generateKeyPair();
    PublicKey pk_Alice = pair1.getPublic();
    Block block1 = new Block(genesisBlock.getHash(), pk_Alice);

	// spendCoinbaseTx
    Transaction tx1 = new Transaction();
    tx1.addInput(genesisBlock.getCoinbase().getHash(), 0);
    tx1.addOutput(10, pk_Alice);

    Signature signature = Signature.getInstance("SHA256withRSA");
    signature.initSign(pair.getPrivate());
    signature.update(tx1.getRawDataToSign(0));
    tx1.addSignature(signature.sign(), 0);

    tx1.finalize();
    block1.addTransaction(tx1);

    Transaction tx2 = new Transaction();
    tx2.addInput(genesisBlock.getCoinbase().getHash(), 0);
    tx2.addOutput(5, pk_Alice);

    Signature signature2 = Signature.getInstance("SHA256withRSA");
    signature2.initSign(pair.getPrivate());
    signature2.update(tx2.getRawDataToSign(0));
    tx2.addSignature(signature2.sign(), 0);

    tx2.finalize();
    block1.addTransaction(tx2);

    block1.finalize();

    Boolean validBlock =  blockHandler.processBlock(block1);
    StringBuilder info = new StringBuilder()
  .append(String.format("\nProcessing valid block: %5s, #txs(block1): %d", validBlock, block1.getTransactions().size()));

    KeyPair pair2 = keyGen.generateKeyPair();
    Block block2 = new Block(genesisBlock.getHash(), pair2.getPublic());

    Transaction spendCoinbaseTx3 = new Transaction();
    spendCoinbaseTx3.addInput(genesisBlock.getCoinbase().getHash(), 0);
    spendCoinbaseTx3.addOutput(5, pair1.getPublic());

    Signature signature3 = Signature.getInstance("SHA256withRSA");
    signature3.initSign(pair.getPrivate());
    signature3.update(spendCoinbaseTx3.getRawDataToSign(0));
    spendCoinbaseTx3.addSignature(signature3.sign(), 0);

    spendCoinbaseTx3.finalize();
    block2.addTransaction(spendCoinbaseTx3);

    block2.finalize();

    validBlock =  blockHandler.processBlock(block2);
	info.append(String.format("\nProcessing valid block: %5s, #txs(block2): %d", validBlock, block2.getTransactions().size()));

    info.append(String.format("\n\nBlockChain Stats: #txs: %d, #UTXOs: %d", blockChain.getTransactionPool().getTransactions().size(), blockChain.getMaxHeightUTXOPool().getAllUTXO().size()));
    info.append(String.format("\nBlockChain genesisBlock: %s", block1.getHash()));
    info.append(String.format("\nBlockChain maxHeightBlock: %s", blockChain.getMaxHeightBlock().getHash()));
    System.out.println(info.toString());
  }

  public static void test2()    throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
    Security.addProvider(new BouncyCastleProvider());
    KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
    SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
    keyGen.initialize(1024, random);

    KeyPair pair = keyGen.generateKeyPair();
    Block genesisBlock = new Block(null, pair.getPublic());
    genesisBlock.finalize();

    BlockChain bc = new BlockChain(genesisBlock);
    BlockHandler blockHandler = new BlockHandler(bc);

    System.out.println(String.format("BlockChain UTXOs count : %s", bc.getMaxHeightUTXOPool().getAllUTXO().size()));

    boolean passes = true;
    Transaction spendCoinbaseTx;
    Block prevBlock = genesisBlock;
    Signature signature = Signature.getInstance("SHA256withRSA");
    pair = keyGen.generateKeyPair();

    for (int i = 0; i < 1; i++) {
      spendCoinbaseTx = new Transaction();
      spendCoinbaseTx.addInput(prevBlock.getCoinbase().getHash(), 0);
      spendCoinbaseTx.addOutput(Block.COINBASE, pair.getPublic());

      signature.initSign(pair.getPrivate());
      signature.update(spendCoinbaseTx.getRawDataToSign(0));

      spendCoinbaseTx.addSignature(signature.sign(), 0);
      spendCoinbaseTx.finalize();
      blockHandler.processTx(spendCoinbaseTx);

      Block createdBlock = blockHandler.createBlock(pair.getPublic());

      passes = passes && createdBlock != null && createdBlock.getPrevBlockHash().equals(prevBlock.getHash()) && createdBlock.getTransactions().size() == 1 && createdBlock.getTransaction(0).equals(spendCoinbaseTx);
      prevBlock = createdBlock;
    }

    System.out.println(String.format("Passes : %s", passes));
  }
}
