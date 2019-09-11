package blockchain;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import dev.morphia.query.Query;

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
    ServerAddress addr;
    String databaseName;
    MongoClient mongoClient;
    Datastore datastore;

    Blockchain blockchain;

    private ArrayList<Block> mainBlockArray = new ArrayList<>();
    public static KeyPair keyPair = null;
    private HashMap<String, String[]> accountDB = new HashMap<>();
    private boolean endtrload = false;
    private String uploadPassword = "05474e60e202a0f583243eb78861844b9294a69755f84caa91fdbcbda7b5d8b9";


    public static void main(String[] args) throws Exception {
        TransactionUploader transactionUploader = new TransactionUploader();
        transactionUploader.run();
    }

    TransactionUploader() {
        addr = new ServerAddress("127.0.0.1", 27017);
        databaseName = "blockchain";
        mongoClient = new MongoClient(addr);
        datastore = morphia.createDatastore(mongoClient, databaseName);
        blockchain = new Blockchain();
    }


    private void run() throws Exception {
        System.out.println("Blockchain parcel tracking system");
        System.out.println("Type \"help\" for all commands");
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("> ");
            switch (scanner.next()) {
                case "login":
                    String login = scanner.next();
                    System.out.println("Enter password: ");
                    String password = new String(System.console().readPassword());
                    System.out.println(login(login, password,true) ? "Logged in as " + login : "Login failed :\\");
                    break;
                case "register":
                    login = scanner.next();
                    System.out.println("Enter password: ");
                    password = new String(System.console().readPassword());
                    System.out.println(register(login, password) ? "Registration complete! \nLogged in as " + login : "Registration failed :\\");
                case "listall":
                    Query<Block> query = datastore.createQuery(Block.class);
                    query.fetch().forEach((block -> {
                        try {
                            block.printTruncInfo();
                        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException | InvalidKeySpecException e) {
                            e.printStackTrace();
                        }
                    }));
//                    while(query.fetch().hasNext())
//                        query.fetch().next().printTruncInfo();
                    break;
                case "upload":
//                    password = scanner.next();
//                    if (Util.hash(password).equals(uploadPassword))
                        uploadTransactions("transactions.txt");
//                    else
//                        System.out.println("Oooops, wrong password mate :\\");


            }

        }
//        uploadTransactions("transactions.txt");
    }


    void uploadTransactions(String string) throws Exception {
        Transaction transaction = null;
        Scanner scanner = new Scanner(new File(string));
        String[] logins = scanner.nextLine().split(" ");
        for (String login : logins) {
            System.out.println(register(login, login) ? "Registration complete! \n Logged in as " + login : "Registration failed :\\");
        }
        while (true) {
            if (endtrload)
                break;
            String sender = null;
            String reciever = null;
            String parcelTN = null;
            byte code = 0;
            switch (scanner.next()) {
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

            if (parcelTN != null && sender != null && reciever != null) {
                login(sender, sender,true);
                blockchain.addTransaction(keyPair.getPublic(), reciever, parcelTN, code);
            }
        }


    }


    boolean register(String login, String password) throws Exception {
        if (login(login, password,false)) {
            blockchain.addTransaction(keyPair.getPublic(), keyPair.getPublic(), login, (byte) 99);
            return true;
        }
        return false;
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


    boolean login(String login, String password, boolean verify) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
        keyGen.initialize(new ECGenParameterSpec("secp256r1"), new SecureRandom((login + password).getBytes()));
        keyPair = keyGen.generateKeyPair();
        if (verify) {
            if (!blockchain.verifyLogin(login, Util.k2str(keyPair.getPublic())))
                keyPair = null;
        }
        return keyPair != null;

    }


//    @Indexes(
//            @Index(value = "login", fields = @Field("login")),
//            @Index(value = "publicKey", fields = @Field("publicKey"))
//
//    )
//    class UserInfo{
//        private String login;
//        private String publicKey;
//
//        public UserInfo(String login, String publicKey) {
//            this.login = login;
//            this.publicKey = publicKey;
//        }
//
//        public String getLogin() {
//            return login;
//        }
//
//        public String getPublicKey() {
//            return publicKey;
//        }
//    }
}