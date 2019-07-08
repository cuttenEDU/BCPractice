import com.google.common.base.Charsets;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;

class Test
{
    public static void main(String[] args) throws Exception
    {
        String password = "SHA-256";

        Hasher hasher = Hashing.sha256().newHasher();
        hasher.putString(password, Charsets.UTF_8);
        HashCode sha256 = hasher.hash();

        System.out.println(sha256);
    }
}