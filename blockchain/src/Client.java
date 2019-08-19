import blockchain.Block;
import blockchain.ParcelDataTransaction;
import blockchain.Transaction;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.Scanner;

public class Client {

    private static KeyPair keyPair;
    private static ArrayList<Block> mainBlockArray;
    private boolean demoMode;
    private String divider = "---------------------------------------------------------------------------------------------------------------------------------------------------------";


    public Client(String publicKey, String privateKey) throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
        byte[] seed = new byte[]{56, 89, 34, 77, 12, 90};
        keyGen.initialize(new ECGenParameterSpec("secp256r1"), new SecureRandom(seed));
        keyPair = keyGen.generateKeyPair();
        demoMode = false;
    }

    public static void main(String[] args) throws Exception {
        Client mainClient = new Client("HQHJVipbsTGtqimNuqCgsFU2hUGHpUhALCs5tXhfPqpx", "YGpqa9F75ZWxvDAh1ZHVKTCWVUZHueYgVVFwA4ZenAq");
        mainClient.initMainBlockArray();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Blockchain parcel tracking system");
        System.out.println("Type \"help\" for all commands");

        while (true) {
            if (!mainClient.demoMode) System.out.print("> ");
            String ierr = null;
            switch (scanner.next()) {
                case "newtr":
                    String parcelTN = scanner.next();
                    String data = scanner.next();
                    mainClient.addTransaction(mainClient.generateSignedTransaction(parcelTN, data));
                    break;
                case "list":
                    parcelTN = scanner.next();
                    String mode = scanner.next();
                    boolean full;
                    boolean all = parcelTN.equalsIgnoreCase("all");
                    if (mode.equalsIgnoreCase("-f"))
                        full = true;
                    else if (mode.equalsIgnoreCase("-b"))
                        full = false;
                    else {
                        ierr = "Wrong attributes! Try again.";
                        break;
                    }

                    for (Block block : mainBlockArray) {
                        if (all || block.contains(parcelTN)) {
                            System.out.println(full ? block.getInfo() : block.getTruncInfo());
                            System.out.println(mainClient.divider);
                            for (Transaction transaction : block.getTransactions()) {
                                ParcelDataTransaction parcelDataTransaction = (ParcelDataTransaction) transaction;
                                String transactionParcelTN = null;
                                try {
                                    transactionParcelTN = parcelDataTransaction.getParcelTN();
                                } catch (NullPointerException e) {
                                    break;
                                }
                                if (all || transactionParcelTN.equals(parcelTN)) {
                                    System.out.println(full ? parcelDataTransaction.getInfo() : parcelDataTransaction.getTruncInfo());
                                }
                            }
                            System.out.println(mainClient.divider + '\n');
                        }

                    }
                    break;
                case "demo":
                    mainClient.demoMode = true;
                    scanner = new Scanner(new File("demo.txt"));
                    break;
                case "enddemo":
                    mainClient.demoMode = false;
                    System.out.println("demo successfully loaded");
                    scanner = new Scanner(System.in);
                    break;
                case "exit":
                    return;
                case "help":
                    System.out.println("Availiable commands:");
                    System.out.println("newtr *parcel_id* *status_update* - add new transaction");
                    System.out.println("list  (\"all\"|*parcel_id*) (-b|-f)- display transactions for \"all\" parcels or for particular one (-b for brief info, -f for full info)");
                    System.out.println("exit - exit the program");
                    break;
                case "trace":
                    String parcel = scanner.next();
                    System.out.println(mainClient.traceParcel(parcel));
                    break;
                default:
                    System.out.println("Unknown command! Try again.");
                    break;

            }
            if (ierr != null)
                System.out.println(ierr);
        }


    }


    Transaction generateSignedTransaction(String parcelTN, String data) throws Exception {
        Signature ecdsa = Signature.getInstance("SHA256withECDSA");
        ecdsa.initSign(keyPair.getPrivate());
        ecdsa.update((parcelTN + data).getBytes(StandardCharsets.UTF_8));
        byte[] signature = ecdsa.sign();
        return new ParcelDataTransaction(parcelTN, keyPair.getPublic(), data, (System.currentTimeMillis()) / 1000L, signature);

    }

    void addTransaction(Transaction transaction) throws Exception {
        Block lastBlock = mainBlockArray.get(mainBlockArray.size() - 1);
        if (lastBlock.isFull()) {
            lastBlock.pack(keyPair, mainBlockArray.size() == 1 ? null : mainBlockArray.get(mainBlockArray.size() - 2).getHashCode());
            lastBlock = new Block(lastBlock.getId() + 1, keyPair.getPublic());
            mainBlockArray.add(lastBlock);
        }
        lastBlock.addTransaction(transaction);
    }


    //TODO: Re-do this function as soon as DB added
    void initMainBlockArray() {
        mainBlockArray = new ArrayList<>();
        mainBlockArray.add(new Block(0, keyPair.getPublic()));
    }


    String traceParcel(String parcelTN) {
        String track = "";
        StringBuilder stringBuilder = new StringBuilder();
        for (Block block : mainBlockArray) {
            for (Transaction transaction : block.getTransactions()) {
                ParcelDataTransaction parcelDataTransaction = (ParcelDataTransaction) transaction;
                try {
                    if (parcelTN.equals(parcelDataTransaction.getParcelTN()))
                        stringBuilder.append(parcelDataTransaction.getData() + " ----> ");
                } catch (NullPointerException ignored) {
                }
            }
        }
        return stringBuilder.substring(0, stringBuilder.length() - 7);


    }
}
