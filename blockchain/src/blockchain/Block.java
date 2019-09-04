package blockchain;

import com.google.common.hash.HashCode;
import dev.morphia.annotations.*;
import org.bson.types.ObjectId;

import java.math.BigInteger;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;

@Indexes(
        @Index(fields = @Field("hashCode"))
)

public class Block {
    //TODO: determine how to make size dynamic
    private final int BLOCK_SIZE = 2;


    @Id
    private ObjectId objectId;
    private String hashCode;
    private int id;
    private Transaction[] transactions;
    private String publicKey;
    private int nextEmpty;
    private byte[] signature;
    @Reference
    private Block prevBlock;

    public Block(int id) {
        transactions = new Transaction[BLOCK_SIZE];
        this.id = id;
        nextEmpty = 0;
        prevBlock = null;
    }

    public void addTransaction(Transaction transaction) {
        transactions[nextEmpty] = transaction;
        nextEmpty++;

    }

    public int getId() {
        return id;
    }

    public boolean isFull() {
        return transactions[BLOCK_SIZE - 1] != null;
    }

    public Transaction[] getTransactions() {
        return transactions;
    }

//    public String getInfo() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
//        return "Block #" + id + "\t Hash: " + hashCode + "\t Prevhash: " + prevBlock + "\t Sig: " + (signature == null ? "null" : Util.bytesToHex(signature)) + "\tisValid: " + (signature == null ? "null" : String.valueOf(isValid()));
//    }
//
//    public String getTruncInfo() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
//        return "Block #" + id + "\t Hash: " + (hashCode == null ? "null" : truncate(hashCode.toString())) + "\t Prevhash: " + (prevBlock == null ? "null" : truncate(prevBlock.toString())) + "\t Sig: " + (signature == null ? "null" : truncate(Util.bytesToHex(signature))) + "\tisValid: " + (signature == null ? "null" : String.valueOf(isValid()));
//    }

    public String truncate(String string) {
        if (string == null)
            return null;
        return string.substring(0, 5) + "..." + string.substring(string.length() - 6, string.length() - 1);
    }


    public void pack(KeyPair keyPair, Block prevBlock) throws Exception {
        hashCode = createMercleRoot();
        Signature ecdsa = Signature.getInstance("SHA256withECDSA");
        ecdsa.initSign(keyPair.getPrivate());
        ecdsa.update(hashCode.getBytes());
        signature = ecdsa.sign();
        this.prevBlock = prevBlock;
        publicKey = Util.keyToString(keyPair.getPublic());
    }

    private String createMercleRoot() throws NoSuchAlgorithmException {
        if (transactions.length == 0)
            return completeShaString("");
        ArrayList<String> shas = new ArrayList<>(transactions.length);
        for (Transaction transaction : transactions) {
            shas.add(transaction.getHashCode().toString());
        }
        int count = transactions.length;
        int offset = 0;
        int newCount;
        boolean last;
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        while (count != 1) {
            newCount = 0;
            last = (count & 1) == 1;
            if (last)
                count--;
            for (int i = offset; i < count + offset; i += 2) {
                BigInteger dblSha = new BigInteger(shas.get(i) + shas.get(i + 1), 16);
                shas.add(completeShaString(new BigInteger(1, digest.digest(digest.digest(dblSha.toByteArray()))).toString(16)));
                newCount++;
            }
            if (last) {
                BigInteger dblSha = new BigInteger(shas.get(count + offset) + shas.get(count + offset), 16);
                shas.add(completeShaString(new BigInteger(1, digest.digest(digest.digest(dblSha.toByteArray()))).toString(16)));
                newCount++;
                count++;
            }

            offset += count;
            count = newCount;
        }
        return shas.get(shas.size() - 1);
    }

    private static String completeShaString(String sha) {
        return completeStringWith(sha, 64);
    }

    public String getHashCode() {
        return hashCode;
    }

    private static String completeStringWith(String string, int len) {
        int need = len - string.length();
        if (need == 0)
            return string;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < need; i++) {
            builder.append("0");
        }
        return builder.append(string).toString();
    }

    boolean isValid() throws InvalidKeyException, SignatureException, NoSuchAlgorithmException, InvalidKeySpecException {
        Signature ecdsaVerify = Signature.getInstance("SHA256withECDSA");
        ecdsaVerify.initVerify((PublicKey)Util.stringToPubKey(publicKey));
        ecdsaVerify.update(hashCode.getBytes());
        return ecdsaVerify.verify(signature);
    }

    public boolean contains(String parcelTN){
        for (Transaction transaction:transactions) {
            if (transaction == null)
                continue;
            if (transaction.getParcelTN().equals(parcelTN))
                return true;
        }
        return false;
    }
}
