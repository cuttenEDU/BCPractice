import com.google.common.hash.HashCode;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Scanner;

public class Client {

    private static String publicKey;
    private static String privateKey;
    private static ArrayList<Block> mainBlockArray;

    public Client(String publicKey, String privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    public static void main(String[] args) throws Exception {
        Client mainClient = new Client("HQHJVipbsTGtqimNuqCgsFU2hUGHpUhALCs5tXhfPqpx", "YGpqa9F75ZWxvDAh1ZHVKTCWVUZHueYgVVFwA4ZenAq");
        mainClient.initMainBlockArray();
        while (true) {
            Scanner scanner = new Scanner(System.in);
            switch (scanner.next()) {
                case "newtr":
                    String parcelTN = scanner.next();
                    String data = scanner.next();
                    mainClient.addTransaction(new ParcelDataTransaction(parcelTN, publicKey, data, (System.currentTimeMillis()) / 1000L));
                    break;
                case "list":
                    parcelTN = scanner.next();
                        for (Block block:mainBlockArray) {
                            System.out.println(block.getInfo());
                            for (Transaction transaction:block.getTransactions()) {
                                ParcelDataTransaction parcelDataTransaction = (ParcelDataTransaction) transaction;
                                if (parcelDataTransaction == null)
                                    break;
                                if (parcelDataTransaction.getParcelTN().equalsIgnoreCase(parcelTN) || parcelTN.equalsIgnoreCase("all"))
                                System.out.println(parcelDataTransaction.getInfo());
                            }
                        }
                    }


            }


        }






    void addTransaction(Transaction transaction) {
        Block lastBlock = mainBlockArray.get(mainBlockArray.size() - 1);
        if (mainBlockArray.get(mainBlockArray.size() - 1).isFull()) {
            lastBlock = new Block();
            mainBlockArray.add(lastBlock);
        }
            lastBlock.addTransaction(transaction);
    }


    //TODO: Re-do this function as soon as DB added
    void initMainBlockArray(){
        mainBlockArray = new ArrayList<>();
        mainBlockArray.add(new Block());
    }




}
