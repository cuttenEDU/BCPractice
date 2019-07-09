import com.google.common.hash.HashCode;

public class Block {
    //TODO: determine how to make size dynamic
    //TODO: !!!signature!!!
    private final int BLOCK_SIZE = 4;


    private int id;
    private Transaction[] transactions;
    private int nextEmpty;

    public Block() {
        transactions = new Transaction[BLOCK_SIZE];
        id++;
        nextEmpty = 0;
    }

    void addTransaction(Transaction transaction){
        transactions[nextEmpty] = transaction;
        nextEmpty++;

    }

    boolean isFull(){
        return transactions[BLOCK_SIZE-1] != null;
    }

    public Transaction[] getTransactions() {
        return transactions;
    }

    String getInfo(){
        return "Block #" + id + ":";
    }
}
