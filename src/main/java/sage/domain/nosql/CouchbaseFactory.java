package sage.domain.nosql;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;

import com.couchbase.client.CouchbaseClient;

public class CouchbaseFactory {
  public static CouchbaseClient createClient(String bucketName) {
    try {
      return new CouchbaseClient(Arrays.asList(URI.create("http://127.0.0.1:8091/pools")), bucketName, "");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
