

import blockchain.Block;
import blockchain.Transaction;
import blockchain.Util;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import dev.morphia.Datastore;
import dev.morphia.Morphia;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;



class TransactionUploader {
    final Morphia morphia = new Morphia();
    ServerAddress addr = new ServerAddress("127.0.0.1", 27017);
    String databaseName = "blockchain";
    MongoClient mongoClient = new MongoClient(addr);
    Datastore datastore = morphia.createDatastore(mongoClient, databaseName);
    private ArrayList<Block> mainBlockArray = new ArrayList<>();
    private PublicKey publicKey = null;
    private PrivateKey privateKey = null;
    private HashMap<String, String[]> accountDB = new HashMap<>();
    private boolean endtrload = false;


    public static void main(String[] args) throws Exception {
        TransactionUploader transactionUploader = new TransactionUploader();
        transactionUploader.run();

    }


    private void run() throws Exception {
        mainBlockArray.add(new Block(0));
        loadAccounts("tempdb.txt");
        Transaction transaction = null;
        Scanner scanner = new Scanner(new File("transactions.txt"));
        while (true){
            if (endtrload)
                break;
            String sender = null;
            String reciever = null;
            String parcelTN = null;
            byte code = 0;
            switch(scanner.next()){
                case "add":
                    sender = scanner.next();
                    reciever = scanner.next();
                    parcelTN = scanner.next();
                    code = Byte.parseByte(scanner.next());
                    break;
                case "end":
                    endtrload = true;
                    break;
            }
            if (parcelTN!=null && sender!=null && reciever!=null)
                transaction = generateSignedTransaction(sender,reciever,parcelTN,code);
            if (transaction!=null)
                addTransaction(transaction);
            else{
                System.err.println("Seems like you did something wrong...");
                return;
            }




        }
        for (Block block:mainBlockArray
             ) {
            datastore.save(block);
        }

    }


    Transaction generateSignedTransaction(String sender, String reciever, String parcelTN, byte code) throws Exception {
        login(sender);
        Signature ecdsa = Signature.getInstance("SHA256withECDSA");
        if (accountDB.get(sender)[1].equals(Util.keyToString(privateKey)))
            ecdsa.initSign(privateKey);
        else
            return null;
        ecdsa.update((sender+reciever+parcelTN+code).getBytes(StandardCharsets.UTF_8));
        byte[] signature = ecdsa.sign();
        return new Transaction(parcelTN, accountDB.get(sender)[0],accountDB.get(reciever)[0], (System.currentTimeMillis()) / 1000L, signature, code);


    }

    void loadAccounts(String pathname) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(pathname));
        while (true) {
            switch (scanner.next()) {
                case "add":
                    String name = scanner.next();
                    String publicKey = scanner.next();
                    String privateKey = scanner.next();
                    accountDB.put(name, new String[]{publicKey, privateKey});
                    break;
                case "end":
                    return;
            }
        }
    }

    void login(String login) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(Util.hex2bytes(accountDB.get(login)[0],false));
        EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(Util.hex2bytes(accountDB.get(login)[1],false));
        publicKey = keyFactory.generatePublic(publicKeySpec);
        privateKey = keyFactory.generatePrivate(privateKeySpec);

    }

    void addTransaction(Transaction transaction) throws Exception {
        Block lastBlock = mainBlockArray.get(mainBlockArray.size() - 1);
        if (lastBlock.isFull()) {
            lastBlock.pack(new KeyPair(publicKey,privateKey), mainBlockArray.size() == 1 ? null : mainBlockArray.get(mainBlockArray.size() - 2));
            lastBlock = new Block(lastBlock.getId() + 1);
            mainBlockArray.add(lastBlock);
        }
        lastBlock.addTransaction(transaction);
    }
}