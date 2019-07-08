import java.sql.Time;

public class ParcelDataTransaction implements Transaction {
    String parcelTN;
    String senderPublicKey;
    String recieverPublicKey;
    String stateUpdate;
    Time timestamp;
    float weight;

    public ParcelDataTransaction(String parcelTN, String senderPublicKey, String recieverPublicKey, String stateUpdate, Time timestamp, float weight) {
        this.parcelTN = parcelTN;
        this.senderPublicKey = senderPublicKey;
        this.recieverPublicKey = recieverPublicKey;
        this.stateUpdate = stateUpdate;
        this.timestamp = timestamp;
        this.weight = weight;
    }
}
