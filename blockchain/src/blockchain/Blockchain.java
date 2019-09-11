package blockchain;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import dev.morphia.query.Query;

import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.security.Signature;

import static blockchain.TransactionUploader.keyPair;

public class Blockchain {

    final Morphia morphia = new Morphia();
    ServerAddress addr;
    String databaseName;
    MongoClient mongoClient;
    Datastore datastore;
    private Block currPackingBlock;
    private Block prevBlock;

    Blockchain(){
        addr = new ServerAddress("127.0.0.1", 27017);
        databaseName = "blockchain";
        mongoClient = new MongoClient(addr);
        datastore = morphia.createDatastore(mongoClient, databaseName);
        try {
            prevBlock = datastore.find(Block.class).order("-id").get();
        }
        catch(NullPointerException ignored){}

        currPackingBlock = prevBlock == null ? new Block(0) : new Block(prevBlock.getId()+1);
        morphia.map(Block.class);
        morphia.map(Transaction.class);
        datastore.ensureIndexes();
    }

    void addTransaction(PublicKey senderPublicKey, String reciever, String parcelTN, byte code) throws Exception {
        String recieverPubKey = retrievePubKey(reciever);
        Signature ecdsa = Signature.getInstance("SHA256withECDSA");
        ecdsa.initSign(keyPair.getPrivate());
        ecdsa.update((Util.k2str(senderPublicKey) + reciever + parcelTN + code).getBytes(StandardCharsets.UTF_8));
        byte[] signature = ecdsa.sign();
        Transaction transaction = new Transaction(parcelTN, Util.k2str(keyPair.getPublic()), recieverPubKey, (System.currentTimeMillis()) / 1000L, signature, code);
        addToBlock(transaction);

    }

    void addTransaction(PublicKey senderPublicKey, PublicKey recieverPubKey, String parcelTN, byte code) throws Exception {
        Signature ecdsa = Signature.getInstance("SHA256withECDSA");
        ecdsa.initSign(keyPair.getPrivate());
        ecdsa.update((Util.k2str(senderPublicKey) + Util.k2str(recieverPubKey) + parcelTN + code).getBytes(StandardCharsets.UTF_8));
        byte[] signature = ecdsa.sign();
        addToBlock(new Transaction(parcelTN, Util.k2str(keyPair.getPublic()), Util.k2str(recieverPubKey), (System.currentTimeMillis()) / 1000L, signature, code));
    }

    void addToBlock(Transaction transaction) throws Exception {
        datastore.save(transaction);
        if (currPackingBlock.isFull()) {
            currPackingBlock.pack(keyPair, prevBlock);
            prevBlock = currPackingBlock;
            currPackingBlock = new Block(currPackingBlock.getId() + 1);
            datastore.save(prevBlock);
        }
        currPackingBlock.addTransaction(transaction);
    }

    boolean verifyLogin(String login, String publicKey){
        Transaction transaction = datastore.createQuery(Transaction.class).field("data").equal(login).get();
        return transaction.getSenderPublicKey().equals(publicKey);
    }

    String retrievePubKey(String reciever){
        Transaction transaction = datastore.createQuery(Transaction.class).field("data").equal(reciever).get();
        return transaction.getSenderPublicKey();
    }
}
