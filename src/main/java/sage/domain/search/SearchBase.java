package sage.domain.search;

import static org.elasticsearch.index.query.QueryBuilders.queryString;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PreDestroy;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import sage.transfer.BlogData;
import sage.transfer.TweetCard;
import sage.web.context.JsonUtil;

@Component
public class SearchBase {
  public static final String BD = "bd";
  public static final String TC = "tc";
  private static final String INDEX = "sage";

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private static final Map<Class<?>, String> typeMap = new HashMap<>();
  static {
    typeMap.put(BlogData.class, BD);
    typeMap.put(TweetCard.class, TC);
  }

  private TransportClient client;

  SearchBase() {
    client = new TransportClient()
        .addTransportAddresses(new InetSocketTransportAddress("localhost", 9300));
    if (client.connectedNodes().isEmpty()) {
      logger.error("Cannot connect to search server!");
      client.close();
      client = null;
      return;
    }
    putMapping(getMappingSource("bd-mapping.json"), BD);
    putMapping(getMappingSource("tc-mapping.json"), TC);
  }

  private String getMappingSource(String filename) {
    try {
      return new String(Files.readAllBytes(Paths.get(getClass().getClassLoader()
          .getResource(filename).toURI())));
    }
    catch (IOException | URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  private void putMapping(String mappingSource, String type) {
    client.admin().indices().preparePutMapping(INDEX)
        .setType(type).setSource(mappingSource).execute();
  }

  @PreDestroy
  void shutdown() {
    client.close();
  }

  /**
   * Only accepts transfer object
   * 
   * @param id
   *          key
   * @param object
   *          a transfer object
   */
  public void index(long id, Object object) {
    if (client == null)
      return;
    if (object == null) {
      throw new IllegalArgumentException("object is null");
    }
    String json = JsonUtil.json(object);
    client.prepareIndex(INDEX, mapType(object.getClass()), String.valueOf(id))
        .setSource(json)
        .execute();
  }

  public void delete(Class<?> clazz, long id) {
    if (client == null)
      return;
    client.prepareDelete().setIndex(INDEX)
        .setType(mapType(clazz)).setId(String.valueOf(id))
        .execute();
  }

  public SearchResponse search(String q) {
    return client.prepareSearch(INDEX)
        .setTypes("bd", "tc")
        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
        .setQuery(queryString(q))
        .setFrom(0).setSize(60).setExplain(true)
        .execute()
        .actionGet();
  }

  private static String mapType(Class<?> clazz) {
    String type = typeMap.get(clazz);
    if (type == null) {
      throw new IllegalArgumentException(
          clazz.getName() + " is not indexable!");
    }
    return type;
  }
}
