import com.google.common.base.Charsets;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;

public class Hasher {

    static HashCode hash(String string){
        com.google.common.hash.Hasher hasher = Hashing.sha256().newHasher();
        hasher.putString(string, Charsets.UTF_8);
        return hasher.hash();
    }

}
