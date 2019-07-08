public class Block {

    private final int TRANSACTIONS_PER_BLOCK = 4;

    private Transaction[] transactions;
    private int nextEmpty;

    public Block() {
        transactions = new Transaction[TRANSACTIONS_PER_BLOCK];
        nextEmpty = 0;
    }

    void addTransaction(Transaction transaction){
        transactions[nextEmpty] = transaction;
    }
}
