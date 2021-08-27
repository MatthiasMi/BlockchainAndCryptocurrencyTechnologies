import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;

import java.util.stream.Collectors;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class MainPA1 {

  private static String info = new StringBuilder()
  .append("MainPA1 demonstrates functionality of TxHandler (and MaxFeeTxHandler)")
  .append("by running some tests.\n")
  .append("Instantiating cryptographic primitives for [Java](en.wikipedia.org/wiki/Bouncy_Castle_(cryptography)),\n")
  .append("for simplicity, using RSA provided by [Bouncy Castle's JAR](bouncycastle.org/download/bcprov-jdk15on-169.jar).\n")
  .append("Caution, compare hashes before executing any .jar, running `sha256sum bcprov-jdk15on-169.jar` yields:\n")
  .append("e469bd39f936999f256002631003ff022a22951da9d5bd9789c7abfa9763a292  bcprov-jdk15on-169.jar\n")
  .append("For deterministic reproducibility of the probabilistic algorithms a fixed seed is used.").toString();

  private static final String keyAlg = "RSA"; // "EC";
  private static final String sigAlg = "SHA256withRSA"; // "SHA256withPLAIN-ECDSA";
  private static final double ICO = 99.9;

  private static KeyPairGenerator keyGen;
  private static PublicKey pk_Scrooge;
  private static PrivateKey sk_Scrooge;
  
  private static byte[] currentHead;
  private static Transaction rootTx;
  private static TxHandler txHandler;
  private static UTXOPool utxoPool;

  public static void main(String[] args)
      throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException {

    System.out.printf(info);
    
    System.out.println("\nInitializing crypto primitives and generating Scrooge's signature keys.");
    initializeCryptoPrimitives();

    info = String.format("Initial tokens worth %.1f [S] (signed by Scrooge) start this blockchain.", ICO);
    System.out.println(info);

    byte[] genesisBlock = ScroogeCoinGenesis(ICO);
    currentHead = genesisBlock;

    System.out.println("Adding users to the network, e.g., Alice identified via (A_pk, A_sk),");
    KeyPair pair = keyGen.generateKeyPair();
    PrivateKey sk_Alice = pair.getPrivate();
    PublicKey pk_Alice = pair.getPublic();

    System.out.println("\tand Bob identified via (B_pk, B_sk).");
    pair = keyGen.generateKeyPair();
    PrivateKey sk_Bob = pair.getPrivate();
    PublicKey pk_Bob = pair.getPublic();

    System.out.println("Testing tx validity (one is valid, and one invalid):");
    currentHead = genesisBlock;
    Transaction tx1 = transferAmount(66.6, sk_Scrooge, pk_Alice, genesisBlock);
    printTxs(tx1);
    currentHead = tx1.getInputs().get(0).prevTxHash;
    Transaction tx2 = transferAmount(44.7, sk_Alice, pk_Bob, currentHead);
    printTxs(tx2);
    System.out.println(txHandler.isValidTx(tx1));
    System.out.println(txHandler.isValidTx(tx2) == false);

    Transaction[] txs = new Transaction[] {tx1, tx2};
    System.out.println(String.format("Valid transactions: %d", txHandler.handleTxs(txs).length));
  }

  private static byte[] ScroogeCoinGenesis(double amount) {
    // Creates root transaction to put in the UTXOPool passed to the TxHandler
    rootTx = new Transaction();
    Transaction tx = rootTx;
    tx.addOutput(amount, pk_Scrooge);

    int initialIndex = 0;
    BigInteger initial = BigInteger.valueOf(1234566654321L);
    byte[] initialHash = initial.toByteArray();
    tx.addInput(initialHash, initialIndex);
    // initialMsg will access tx.inputs.get(0).prevTxHash;;
    byte[] initialMsg = tx.getRawDataToSign(initialIndex);

    byte[] sig = new byte[32];
    try {
      sig = signByScrooge(initialMsg);
    } catch (Exception e) {
    }

    tx.addSignature(sig, initialIndex);
    tx.finalize();
    // Genesis

    // Register output of the root tx as unspent
    utxoPool = new UTXOPool();
    UTXO utxo = new UTXO(tx.getHash(), initialIndex);
    utxoPool.addUTXO(utxo, tx.getOutput(initialIndex));

    txHandler = new TxHandler(utxoPool);
    txHandler = new MaxFeeTxHandler(utxoPool);

    return tx.getHash();
  }
  
  private static void initializeCryptoPrimitives() throws NoSuchAlgorithmException, NoSuchProviderException {
    final long seed = 42; // typically use: SecureRandom.generateSeed()
    final int bitLength = 1024;//4096; // 1024; 2048; 4096;

    SecureRandom random = SecureRandom.getInstanceStrong(); // else: .getInstance("NativePRNG");
    random.setSeed(seed); // alternatively, for self-seeding use random.nextBytes(randomBytes);

    keyGen = KeyPairGenerator.getInstance(keyAlg, new BouncyCastleProvider());
    keyGen.initialize(bitLength, random);

    // Generating key pairs for Scrooge (S_pk, S_sk)
    KeyPair pair = keyGen.generateKeyPair();
    // Assigning static, class member variable
    pk_Scrooge = pair.getPublic();
    sk_Scrooge = pair.getPrivate();
  }

  private static byte[] messageSignature(PrivateKey sk, byte[] msg)
      throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException {
    // Signs specified message msg with provided private key sk
    Signature sig = null;
    try {
      sig = Signature.getInstance(sigAlg, new BouncyCastleProvider());
      sig.initSign(sk);
      sig.update(msg);
    } catch (NoSuchAlgorithmException | InvalidKeyException e) {
      throw new RuntimeException(e);
    }
    return sig.sign();
  }

  private static byte[] signByScrooge(byte[] msg)
      throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException {
    // Signs specified message msg with Scrooge's private key sk
    return messageSignature(sk_Scrooge, msg);
  }

  private static Transaction transferAmount(double amount, PrivateKey fromSK, PublicKey toPK, byte[] blockchainHead)
      throws NoSuchAlgorithmException, NoSuchProviderException, SignatureException, InvalidKeyException {
    // Transfers specified amount as a single output transaction
    int zero = 0;
    Transaction tx = new Transaction();
    tx.addInput(blockchainHead, zero);
    tx.addOutput(amount, toPK);
    tx.addSignature(messageSignature(fromSK, tx.getRawDataToSign(zero)), zero);
    tx.finalize();
    return tx;
  }

  private static void printTxs(Transaction tx) {
    // Prints Txs in- and outputs
    tx.getInputs().stream().forEach(input -> {
      System.out.println(String.format("Input : %s", input.signature));
    });
    tx.getOutputs().stream().forEach(output -> {
      System.out.println(String.format("Output : %s", output.value));
    });
  }
}