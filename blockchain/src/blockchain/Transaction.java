package blockchain;

import dev.morphia.annotations.*;
import org.bson.types.ObjectId;

import javax.xml.bind.DatatypeConverter;
import java.security.*;
import java.security.spec.InvalidKeySpecException;

@Entity("transactions")
@Indexes(
        @Index(fields = @Field("hashCode"))
)
public class Transaction {

    @Id
    private ObjectId objectId;
    private String hashCode;
    private String senderPublicKey;
    private String recieverPublicKey;
    private String data;
    private byte code;
    private byte[] signature;
    private long timestamp;


    public Transaction(String data, String senderPublicKey, String recieverPublicKey, long timestamp, byte[] signature,byte code) {
        this.data = data;
        this.senderPublicKey = senderPublicKey;
        this.recieverPublicKey = recieverPublicKey;
        this.timestamp = timestamp;
        this.hashCode = Util.hash(senderPublicKey + data + recieverPublicKey);
        this.signature = signature;
        this.code = code;
    }

    public Transaction() {
    }

    private String truncate(String string) {
        return string.substring(0, 5) + "..." + string.substring(string.length() - 6, string.length() - 1);
    }

    boolean isValid() throws InvalidKeyException, SignatureException, NoSuchAlgorithmException, InvalidKeySpecException {
        Signature ecdsaVerify = Signature.getInstance("SHA256withECDSA");
        ecdsaVerify.initVerify(Util.stringToPubKey(senderPublicKey));
        ecdsaVerify.update(data.getBytes());
        ecdsaVerify.update(code);
        ecdsaVerify.update(recieverPublicKey.getBytes());
        return ecdsaVerify.verify(signature);
    }

    public String getData() { return data; }

    public String getHashCode() { return hashCode; }


    public String getInfo() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, InvalidKeySpecException {
        return "Hash: " + hashCode + "\tSenderPubK: " + senderPublicKey + "\tParcel TN: " + data + "\tRecieverPubK: " + recieverPublicKey + "\tTimestamp: " + timestamp + "\tSig: " + DatatypeConverter.printHexBinary(signature) + "\tisValid: " + isValid();
    }

    public String getTruncInfo() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, InvalidKeySpecException {
        return "Hash: " + truncate(hashCode) + "\tSenderPubK: " + truncate(senderPublicKey) + "\tParcel TN: " + data + "\tRecieverPubK: " + truncate(recieverPublicKey) + "\tTimestamp: " + timestamp + "\tSig: " + truncate(DatatypeConverter.printHexBinary(signature)) + "\tisValid: " + isValid();

    }

    public String getSenderPublicKey() {
        return senderPublicKey;
    }
}
