import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import dev.morphia.Datastore;
import dev.morphia.Morphia;

public class Client {

    private final Morphia morphia = new Morphia();
    private ServerAddress addr = new ServerAddress("127.0.0.1", 27017);
    private String databaseName = "blockchain";
    private MongoClient mongoClient = new MongoClient(addr);
    private Datastore datastore = morphia.createDatastore(mongoClient, databaseName);

    public static void main(String[] args) {
        Client client = new Client();
        client.run();
    }

    void run(){

    }

}
