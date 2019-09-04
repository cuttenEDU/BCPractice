package blockchain;

import com.google.common.hash.HashCode;
import dev.morphia.annotations.Field;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Index;
import dev.morphia.annotations.Indexes;
import org.bson.types.ObjectId;

import java.security.*;
import java.security.spec.InvalidKeySpecException;


@Indexes(
        @Index(fields = @Field("hashCode"))
)
public class Transaction {

    @Id
    private ObjectId objectId;
    private String hashCode;
    private String senderPublicKey;
    private String recieverPublicKey;
    private String parcelTN;
    private byte code;
    private byte[] signature;
    private long timestamp;


    public Transaction(String parcelTN, String senderPublicKey, String recieverPublicKey, long timestamp, byte[] signature,byte code) {
        this.parcelTN = parcelTN;
        this.senderPublicKey = senderPublicKey;
        this.recieverPublicKey = recieverPublicKey;
        this.timestamp = timestamp;
        this.hashCode = Util.hash(senderPublicKey + parcelTN + recieverPublicKey);
        this.signature = signature;
        this.code = code;
    }

    private String truncate(String string) {
        return string.substring(0, 5) + "..." + string.substring(string.length() - 6, string.length() - 1);
    }

    boolean isValid() throws InvalidKeyException, SignatureException, NoSuchAlgorithmException, InvalidKeySpecException {
        Signature ecdsaVerify = Signature.getInstance("SHA256withECDSA");
        ecdsaVerify.initVerify((PublicKey)Util.stringToPubKey(senderPublicKey));
        ecdsaVerify.update(parcelTN.getBytes());
        ecdsaVerify.update(code);
        ecdsaVerify.update(recieverPublicKey.getBytes());
        return ecdsaVerify.verify(signature);
    }

    public String getParcelTN() { return parcelTN; }

    public String getHashCode() { return hashCode; }


//    public String getInfo() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
//        return "Hash: " + hashCode.toString() + "\tSenderPubK: " + Util.bytesToHex(senderPublicKey.getEncoded()) + "\tParcel TN: " + parcelTN + "\tRecieverPubK: " + Util.bytesToHex(recieverPublicKey.getEncoded()) + "\tTimestamp: " + timestamp + "\tSig: " + DatatypeConverter.printHexBinary(signature) + "\tisValid: " + String.valueOf(isValid());
//    }
//
//    public String getTruncInfo() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
//        return "Hash: " + truncate(hashCode.toString()) + "\tSenderPubK: " + truncate(Util.bytesToHex(senderPublicKey.getEncoded())) + "\tParcel TN: " + parcelTN + "\tRecieverPubK: " + truncate(Util.bytesToHex(recieverPublicKey.getEncoded())) + "\tTimestamp: " + timestamp + "\tSig: " + truncate(DatatypeConverter.printHexBinary(signature)) + "\tisValid: " + String.valueOf(isValid());
//
//    }

}
